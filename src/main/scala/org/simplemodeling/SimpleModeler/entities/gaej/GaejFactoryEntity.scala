package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker}

/*
 * @since   Sep. 16, 2009
 * @version Oct.  2, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejFactoryEntity(aContext: GaejEntityContext) extends GaejObjectEntity(aContext) {
  val services = new ArrayBuffer[GaejServiceEntity]

  private val code = """package %packageName%;

import java.util.*;

@SuppressWarnings("unused")
public class %factoryName% {
    private static %factoryName% factory = null; 

    public static %factoryName% getFactory() {
        if (factory == null) {
            factory = get_impl();
        }
        if (factory == null) {
            factory = new %factoryName%();
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    private static %factoryName% get_impl() {
        try {
            Class<%factoryName%> klass = (Class<%factoryName%>)Class.forName("%packageName%.impl.%factoryName%Impl");
            return klass.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public %contextName% createContext() {
        return new %contextName%(this);
    }

%creators%
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val maker = new JavaTextMaker(
      code,
      Map("%packageName%" -> packageName,
          "%factoryName%" -> name,
          "%contextName%" -> gaejContext.contextName(packageName)))
    maker.replace("%creators%")(make_creators)
    out.append(maker.toString)
    out.flush()
  }

  private def make_creators(code: JavaTextMaker) {
    def make_service_creator(aService: GaejServiceEntity) {
      code.method("public " + aService.name + " create" + gaejContext.serviceBaseName(aService) + "()") {
        code.makeReturn("new " + aService.name + "()")
      }
    }

    services.foreach(make_service_creator)
  }
}
