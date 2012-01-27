package org.simplemodeling.SimpleModeler.entities.atom

import scala.collection.mutable.ArrayBuffer
import java.net.URI

/*
 * @since   May.  5, 2009
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
trait AtomPersonConstruct extends AtomCommonAttributes {
  var name: String = null
  var uri: URI = null
  var email: AtomEmailAddress = null
}

case class AtomEmailAddress() {
  var content: String = null
}
