package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 14, 2012
 * @version Jun. 10, 2012
 * @author  ASAMI, Tomoharu
 */
class MainControllerExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  pobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ControllerExtjsClassDefinition(context, aspects, pobject, maker) {

  override def view_QNames: Seq[String] = {
    def views(e: PEntityEntity) = {
      List(
        extjsContext.entityGridViewQualifiedName(e),
        extjsContext.entityFormViewQualifiedName(e),
        extjsContext.entityEditFormViewQualifiedName(e))
    }
    entities_in_module.flatMap(views)
  }

  override def model_QNames: Seq[String] = {
    entities_in_module.map(extjsContext.entityModelQualifiedName)
  }

  override def store_QNames: Seq[String] = {
    "app.store.NavigationStore" +: entities_in_module.map(extjsContext.entityStoreQualifiedName)
  }
}
