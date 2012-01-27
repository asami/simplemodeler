package org.simplemodeling.SimpleModeler.entities.atom

import scala.collection.mutable.ArrayBuffer
import java.net.URI
import com.asamioffice.goldenport.text.XmlTextMaker

/*
 * @since   May.  5, 2009
 * @version May.  6, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class AtomNode {
  def writeXml(maker: XmlTextMaker)
}
