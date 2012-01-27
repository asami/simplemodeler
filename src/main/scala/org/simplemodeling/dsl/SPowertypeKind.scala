package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._

/*
 * @since   Dec. 20, 2008
 * @version Oct. 25, 2009
 * @author  ASAMI, Tomoharu
 */
class SPowertypeKind(aName: String) extends SElement(aName) {
  def this(aSymbol: Symbol) = this(aSymbol.name)
}
