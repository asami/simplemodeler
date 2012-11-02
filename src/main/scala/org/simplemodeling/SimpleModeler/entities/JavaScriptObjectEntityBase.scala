package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._

/**
 * @since   Apr.  8, 2012
 *  version May.  5, 2012
 * @version Nov.  1, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class JavaScriptObjectEntityBase(val jsContext: JavaScriptEntityContextBase) extends PObjectEntity(jsContext) {
  val fileSuffix = "js"
}
