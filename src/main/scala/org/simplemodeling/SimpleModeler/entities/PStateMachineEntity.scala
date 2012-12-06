package org.simplemodeling.SimpleModeler.entities

import java.io._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov. 14, 2012
 * @version Dec.  2, 2012
 * @author  ASAMI, Tomoharu
 */
trait PStateMachineEntity extends PObjectEntity {
  var modelStateMachine: SMStateMachine = null

  lazy val states = modelStateMachine.states.map(PState.create)

  private def first_state = modelStateMachine.states.firstOption match {
    case Some(state) => state.name
    case _ => "null"
  }

  def isKnowledge: Boolean = false // modelStateMachine.isKnowledge

  /*
   * Obsolated
   */
  override protected def write_Content(out: BufferedWriter) {
    // println("is_editable = " + is_editable)
      out.append(new EnumStateMachineCode(pContext).code())
    out.flush()
  }

  class EnumStateMachineCode(context: PEntityContext) extends PObjectCode(context) {
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
        return %firstState%;
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
    coder("firstState", first_state)
    coder("declaration", make_declaration)
    coder("getEnum", make_get_enum)

    private def make_declaration = new Coder {
      override def code() {
        if (modelStateMachine.states.size > 0) {
          cIndentUp
          val last = modelStateMachine.states.last
          for (state <- modelStateMachine.states) {
            val key = state.name
            val label = state.term
            cAppend("""%s("%s", "%s")""".format(key, key, label))
//             println("state = %s, last = %s, result = %s".format(state, last, (state eq last))) // 2009-10-28
            if (!(state eq last)) {
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
        cMatchElse(modelStateMachine.states) {
          element => "\"%s\".equals(key)".format(element.name)
        } {
          element => cReturn(element.name)
        } {
          cReturnNull
        }
      }
    }
  }
}
