package org.simplemodeling.SimpleModeler.entities.atom

import scala.collection.mutable.ArrayBuffer

/*
 * @since   May.  5, 2009
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
abstract class AtomText {
}

case class AtomTextText() extends AtomText {
}

case class AtomHtmlText() extends AtomText {
}

case class AtomXHtmlText() extends AtomText {
}
