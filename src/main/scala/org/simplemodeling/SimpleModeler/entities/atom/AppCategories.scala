package org.simplemodeling.SimpleModeler.entities.atom

import scala.collection.mutable.ArrayBuffer

/*
 * @since   May.  5, 2009
 * @version May.  5, 2009
 * @author  ASAMI, Tomoharu
 */
case class AppCategories() {
  var fixed: String = null
  var scheme: String = null
  val categories = new ArrayBuffer[AtomCategory]

  var href: String = null
}
