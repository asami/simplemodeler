package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._

/**
 * @since   Apr.  8, 2012
 * @version Apr.  8, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class JavaScriptObjectEntityBase(val jsContext: JavaScriptEntityContextBase) extends PObjectEntity(jsContext) {
  val fileSuffix = "js"
  def modelEntity = modelObject.asInstanceOf[SMEntity]
  var documentName: String = ""
}
