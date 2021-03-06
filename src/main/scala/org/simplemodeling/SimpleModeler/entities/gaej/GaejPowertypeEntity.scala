package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

/*
 * @since   Oct. 25, 2009
 *  version Oct. 28, 2009
 * @version Nov. 18, 2012
 * @author  ASAMI, Tomoharu
 */
class GaejPowertypeEntity(aContext: GaejEntityContext) extends GaejObjectEntity(aContext) {
  var modelPowertype: SMPowertype = null

  private def first_kind = modelPowertype.kinds.firstOption match {
    case Some(kind) => kind.name
    case _ => "null"
  }

  private def is_knowledge = modelPowertype.isKnowledge

  override protected def write_Content(out: BufferedWriter) {
    println("is_knowledge = " + is_knowledge)
    if (is_knowledge)
      out.append(new EntityPowertypeCode(gaejContext).code())
    else
      out.append(new EnumPowertypeCode(gaejContext).code())
    out.flush()
  }

  class EnumPowertypeCode(context: GaejEntityContext) extends GaejObjectCode(context) {
    val template = """package %packageName%;

import java.util.Date;
import javax.jdo.PersistenceManager;

public enum %enumName% {
%declaration%

    final private String key;
    final private String label;
    final private Date updated = new Date(); // XXX tool build time

    %enumName%(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public static %enumName% getDefault() {
        return %firstKind%;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public Date getUpdated() {
        return updated;
    }

    public static %enumName% get(String key) {
%getEnum%
    }

    public static %enumName% get(String key, String label, Date updated) {
%getEnum%
    }

    public static %enumName% get(String key, PersistenceManager pm) {
%getEnum%
    }
}
"""
    coder("packageName", packageName)
    coder("enumName", name)
    coder("firstKind", first_kind)
    coder("declaration", make_declaration)
    coder("getEnum", make_get_enum)

    private def make_declaration = new Coder {
      override def code() {
        if (modelPowertype.kinds.size > 0) {
          cIndentUp
          val last = modelPowertype.kinds.last
          for (kind <- modelPowertype.kinds) {
            val key = kind.name
            val label = kind.term
            cAppend("""%s("%s", "%s")""".format(key, key, label))
//             println("kind = %s, last = %s, result = %s".format(kind, last, (kind eq last))) // 2009-10-28
            if (!(kind eq last)) {
              cAppendln(",")
            } else {
              cAppendln(";")
            }
          }
          cIndentDown
        }
      }
    }

    private def make_get_enum = new Coder {
      override def code() {
        cMatchElse(modelPowertype.kinds) {
          element => "\"%s\".equals(key)".format(element.name)
        } {
          element => cReturn(element.name)
        } {
          cReturnNull
        }
      }
    }
  }

  class EntityPowertypeCode(context: GaejEntityContext) extends GaejObjectCode(context) {
    val template = """package %packageName%;

import java.util.Date;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType=IdentityType.APPLICATION, detachable="true")
public class %enumName% {
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @PrimaryKey
    private Long id;
    
    @Persistent
    private String key;

    @Persistent
    private String label;

    @Persistent
    private Date updated;

    public %enumName%() {
    }

    public %enumName%(String key, String label) {
        this.key = key;
        this.label = label;
        updated = new Date();
    }

    public %enumName%(String key, String label, Date updated) {
        this.key = key;
        this.label = label;
        updated = updated;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public Date getCreated() {
        return updated;
    }

    public Date getUpdated() {
        return updated;
    }

    private static %enumName% defaultKind;

    public static %enumName% getDefault() {
        if (defaultKind == null) {
            PersistenceManager pm = Util.getPersistenceManager();
            try {
                defaultKind = (%enumName%)pm.getObjectById(new Long(0)); 
            } finally {
                pm.close();
            }
        }
        return defaultKind;
    }

    public static %enumName% get(String key) {
        PersistenceManager pm = Util.getPersistenceManager();
        try {
            return get(key, pm);
        } finally {
            pm.close();
        }
    }

    public static %enumName% get(String key, String label, Date updated) {
      return new %enumName%(key, label, updated);
    }

    @SuppressWarnings("unchecked")
    public static %enumName% get(String key, PersistenceManager pm) {
        Query query = pm.newQuery(%enumName%.class);
        query.setFilter("key == keyParam");
        query.declareParameters("String keyParam");
        List<%enumName%> infos = (List<%enumName%>)query.execute(key);
        return infos.get(0);
    }
}
"""
    coder("packageName", packageName)
    coder("enumName", name)
  }
}
