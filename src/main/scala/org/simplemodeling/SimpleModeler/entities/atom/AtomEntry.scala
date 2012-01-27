package org.simplemodeling.SimpleModeler.entities.atom

import scala.collection.mutable.ArrayBuffer
import com.asamioffice.goldenport.text.XmlTextMaker

/*
 * @since   May.  4, 2009
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class AtomEntry() extends AtomNode with AtomCommonAttributes {
  var title: AtomTitle = null
  val links = new ArrayBuffer[AtomLink]
  var id: AtomId = null
  var updated: AtomUpdated = null
  var published: AtomPublished = null
  val categories = new ArrayBuffer[AtomCategory]
  val authors = new ArrayBuffer[AtomAuthor]
  val contributers = new ArrayBuffer[AtomContributer]
  var rights: AtomRights = null
  var source: AtomSource = null
  var summary: AtomSummary = null
  var content: AtomContent = null
  val extensionElements = new ArrayBuffer[AtomExtensionElement]

  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("entry") {
        if (title != null) title.writeXml(maker)
        links.foreach(_.writeXml(maker))
      }
    }
  }
}

case class AtomSource() extends AtomNode with AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("source") {
      }
    }
  }
}

case class AtomSummary() extends AtomNode with AtomTextConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("summary") {
      }
    }
  }
}

case class AtomContent() extends AtomNode with AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("content") {
      }
    }
  }
}

case class AtomPublished() extends AtomNode with AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("published") {
      }
    }
  }
}
