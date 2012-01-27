package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker}

/*
 * @since   Oct.  2, 2009
 * @version Oct. 10, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejEventEntity(aContext: GaejEntityContext) extends GaejObjectEntity(aContext) {
  val services = new ArrayBuffer[GaejServiceEntity]

  private val code = """package %packageName%;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class %eventName% {
    @PrimaryKey
    protected String id;

    @Persistent
    protected Date created;

    @Persistent
    protected User user;

    @Persistent
    protected String status; // XXX

    @Persistent
    protected Text event;

    public %eventName%(String id, String eventXml) {
        this.id = id;
        UserService userService = UserServiceFactory.getUserService();
        created = new Date();
        user = userService.getCurrentUser();
        this.event = new Text(eventXml);
    }

    public final String getEventXml() {
        return event.getValue();
    }
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val maker = new JavaTextMaker(
      code,
      Map("%packageName%" -> packageName,
          "%eventName%" -> name))
    out.append(maker.toString)
    out.flush()
  }
}
