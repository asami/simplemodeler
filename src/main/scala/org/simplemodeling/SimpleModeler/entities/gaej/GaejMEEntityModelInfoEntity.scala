package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, JavaTextMaker}

/*
 * @since   May. 13, 2009
 * @version Oct.  6, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejMEEntityModelInfoEntity(val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

import java.io.Serializable;
import java.util.List;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class MEEntityModelInfo implements Serializable {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String name;

    @Persistent
    private String version;

    @Persistent
    private String build;

    @Persistent
    private Date created;

    @Persistent
    private Date updated;

    @Persistent
    private Date removed;

    @Persistent
    private long count;

    @Persistent
    private String modelName;

    @Persistent
    private String modelVersion;

    @Persistent
    private String modelBuild;

    public MEEntityModelInfo(String name, String version, String build) {
        this.name = name;
        this.version = version;
        this.build = build;
    }

    public MEEntityModelInfo(String name, String version, String build, MEEntityModelInfo source) {
        this.name = name;
        this.version = version;
        this.build = build;
        this.created = source.created;
        this.updated = source.updated;
        this.removed = source.removed;
        this.count = source.count;
        this.modelName = source.modelName;
        this.modelVersion = source.modelVersion;
        this.modelBuild = source.modelBuild;
    }

    public long getCount() {
        return count;
    }

    public void countUp() {
    	count++;
    }

    public void countDown() {
    	count--;
    }

    public void setUpdated() {
        if (created == null) {
            created = new Date();
        } else {
            updated = new Date();
        }
    }

    public static void updateEntityModel(PersistenceManager pm, MEEntityModelInfo info) {
        int retryCount = 10; // XXX
        int waitMillSec = 500; // XXX
        while (retryCount-- > 0) {
            try {
                pm.makePersistent(info);
                return;
            } catch (Exception e) {
                try {
                  Thread.sleep(waitMillSec);
                } catch (InterruptedException e2) {}
            }
        }
        pm.makePersistent(info);
    }

    @SuppressWarnings("unchecked")
    public static MEEntityModelInfo getEntityModelInfo(String name, String version, String build) {
        PersistenceManager pm = Util.getPersistenceManager();
        try {
            Query query = pm.newQuery(MEEntityModelInfo.class);
            query.setFilter("name == nameParam");
            query.setOrdering("created desc");
            query.declareParameters("String nameParam");
            List<MEEntityModelInfo> infos = (List<MEEntityModelInfo>)query.execute(name);
            for (MEEntityModelInfo info : infos) {
                if (version.equals(info.version) && build.equals(info.build)) {
                    return info;
                }
            }
            if (infos.size() > 0) {
                MEEntityModelInfo oldInfo = infos.get(0);
                return new MEEntityModelInfo(name, version, build, oldInfo);
            } else {
                return new MEEntityModelInfo(name, version, build);
            }
        } finally {
            pm.close();
        }
    }

    /// dprecated belows
    public static String getEntityLastUpdated(String name) {
        SimpleDateFormat df = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        df.setTimeZone(utc);
        PersistenceManager pm = Util.getPersistenceManager();
        try {
            Query query = pm.newQuery(MEEntityModelInfo.class);
            query.setFilter("name == nameParam");
            query.setOrdering("created desc");
            query.declareParameters("String nameParam");
            List<MEEntityModelInfo> infos = (List<MEEntityModelInfo>) query
                    .execute(name);
            if (infos.isEmpty()) {
                return df.format(new Date());
            } else {
                MEEntityModelInfo info = infos.get(0);
                if (info.updated != null) {
                    return df.format(info.updated);
                } else {
                    return df.format(info.created);
                }
            }
        } finally {
            pm.close();
        }
    }

    /*
     * public static MEEntityModelInfo getLatestInfo(String name) {
     * PersistenceManager pm = Util.getPersistenceManager(); try { Query query =
     * pm.newQuery(MEEntityModelInfo.class);
     * query.setFilter("name = nameParam"); query.setOrdering("created desc");
     * query.declareParameters("String nameParam"); List<MEEntityModelInfo>
     * result = (List<MEEntityModelInfo>)query.execute(name); if
     * (result.isEmpty()) { return null; } else { return result.get(0); } }
     * finally { pm.close(); } }
     * 
     * public static void createInfo(String name, String version, String build)
     * { PersistenceManager pm = Util.getPersistenceManager(); try {
     * MEEntityModelInfo info = new MEEntityModelInfo(name, version, build);
     * info.created = new Date(); pm.makePersistent(info); } finally {
     * pm.close(); } }
     * 
     * public static void updateInfo(MEEntityModelInfo info) {
     * PersistenceManager pm = Util.getPersistenceManager(); try { info.updated
     * = new Date(); pm.makePersistent(info); } finally { pm.close(); } }
     */

    public static void updateEntityModel(PersistenceManager pm, String name,
            String version, String build, String modelName,
            String modelVersion, String modelBuild) {
        Query query = pm.newQuery(MEEntityModelInfo.class);
        query.setFilter("name == nameParam");
        query.declareParameters("String nameParam");
        List<MEEntityModelInfo> infos = (List<MEEntityModelInfo>) query
                .execute(name);
        for (MEEntityModelInfo info : infos) {
            if (version.equals(info.version)) {
                update_entity_model(pm, info, name, version, build, modelName,
                        modelVersion, modelBuild);
                return;
            }
        }
        update_entity_model(pm, name, version, build, modelName, modelVersion,
                modelBuild);
    }

    private static void update_entity_model(PersistenceManager pm,
            MEEntityModelInfo info, String name, String version, String build,
            String modelName, String modelVersion, String modelBuild) {
        if (!info.name.equals(name)) {
            throw new IllegalArgumentException();
        }
        if (!info.version.equals(version)) {
            throw new IllegalArgumentException();
        }
        info.build = build;
        info.modelName = modelName;
        info.modelVersion = modelVersion;
        info.modelBuild = modelBuild;
        info.updated = new Date();
        pm.makePersistent(info);
    }

    private static void update_entity_model(PersistenceManager pm, String name,
            String version, String build, String modelName,
            String modelVersion, String modelBuild) {
        MEEntityModelInfo info = new MEEntityModelInfo(name, version, build);
        info.modelName = modelName;
        info.modelVersion = modelVersion;
        info.modelBuild = modelBuild;
        info.created = new Date();
        pm.makePersistent(info);
    }
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val generator_version = gaejContext.simplemodelerVersion
    val generator_build = gaejContext.simplemodelerBuild

    val maker = new JavaTextMaker(
      code,
      Map("%packageName%" -> packageName,
          "%format_version%" -> "0.1",
          "%generator_version%" -> generator_version,
          "%generator_build%" -> generator_build))
    out.append(maker.toString)
    out.flush()
  }
}
