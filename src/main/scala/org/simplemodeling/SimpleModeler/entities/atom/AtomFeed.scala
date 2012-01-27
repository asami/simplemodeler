package org.simplemodeling.SimpleModeler.entities.atom

import scala.collection.mutable.ArrayBuffer
import java.net.URI
import com.asamioffice.goldenport.text.XmlTextMaker

/*
 * @since   May.  4, 2009
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class AtomFeed(val id: AtomId, val title: AtomTitle) extends AtomCommonAttributes {
  var subtitle: AtomSubtitle = null
  val updated: AtomUpdated = null
  val categories = new ArrayBuffer[AtomCategory]
  val authors = new ArrayBuffer[AtomAuthor]
  val contributers = new ArrayBuffer[AtomContributer]
  var rights: AtomRights = null
  var icon: AtomIcon = null
  var logo: AtomLogo = null
  val links = new ArrayBuffer[AtomLink]
  var generator: AtomGenerator = null
  val extensionElements = new ArrayBuffer[AtomExtensionElement]
  val entries = new ArrayBuffer[AtomEntry]

  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("source") {
      }
    }
  }
}

case class AtomId() extends AtomCommonAttributes {
  var content: URI = null
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("id") {
      }
    }
  }
}

case class AtomTitle() extends AtomTextConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("title") {
      }
    }
  }
}

case class AtomSubtitle() extends AtomTextConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("subtitle") {
      }
    }
  }
}

case class AtomUpdated() extends AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("updated") {
      }
    }
  }
}

case class AtomCategory() extends AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("category") {
      }
    }
  }
}

case class AtomAuthor() extends AtomPersonConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("author") {
      }
    }
  }
}

case class AtomContributer() extends AtomPersonConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("contributer") {
      }
    }
  }
}

case class AtomRights() extends AtomTextConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("rights") {
      }
    }
  }
}

case class AtomIcon() extends AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("icon") {
      }
    }
  }
}

case class AtomLogo() extends AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("logo") {
      }
    }
  }
}

case class AtomLink() extends AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("link") {
      }
    }
  }
}

case class AtomGenerator() extends AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("generator") {
      }
    }
  }
}

case class AtomExtensionElement() extends AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("???") {
      }
    }
  }
}

/*
case class AtomFeed(val id: AtomId, val title: AtomTitle) with AtomCommonAttributes {
  private var _subtitle: AtomSubtitle = _
  def setSubtitle(subtitle: AtomSubtitle): AtomSubtitle = {
    _subtitle = subtitle
    _subtitle
  }
  def getSubtitle: Option[AtomSubtitle] = {
    if (_subtitle != null)
      Some(_subtitle)
    else
      None
  }

  val updated: AtomUpdated = _
  def setUpdated(updated: AtomUpdated): AtomUpdated = {
    _updated = updated
    _updated
  }
  def getUpdated: Option[AtomUpdated] = {
    if (_updated != null)
      Some(_updated)
    else
      None
  }

  val categories = new ArrayBuffer[AtomCategory]
  val authors = new ArrayBuffer[AtomAuthor]
  val contributers = new ArrayBuffer[AtomContributer]

  private var _rights: AtomRights = _
  def setRights(rights: AtomRights): AtomRights = {
    _rights = rights
    _rights
  }
  def getRights: Option[AtomRights] = {
    if (_rights != null)
      Some(_rights)
    else
      None
  }

  private var _icon: AtomIcon = _
  def setIcon(icon: AtomIcon): AtomIcon = {
    _icon = icon
    _icon
  }
  def getIcon: Option[AtomIcon] = {
    if (_icon != null)
      Some(_icon)
    else
      None
  }

  private var _logo: AtomLogo = _
  def setIcon(icon: AtomIcon): AtomIcon = {
    _icon = icon
    _icon
  }
  def getIcon: Option[AtomIcon] = {
    if (_icon != null)
      Some(_icon)
    else
      None
  }

  val links = new ArrayBuffer[AtomLink]

  private var _generator: AtomGenerator = _
  def setGenerator(generator: AtomGenerator): AtomGenerator = {
    _generator = generator
    _generator
  }
  def getGenerator: Option[AtomGenerator] = {
    if (_generator != null)
      Some(_generator)
    else
      None
  }

  val extensionElements = new ArrayBuffer[AtomExtensionElement]
  val entries = new ArrayBuffer[AtomEntry]
}

case class AtomFeed2 with AtomCommonAttributes {
  private val _metadatas = new ArrayBuffer[AtomFeedMetaData]
  private val _entries = new ArrayBuffer[AtomEntry]

  def authors: Seq[AtomAuthor] = {
    metadatas.filter(_.isInstanceOf[AtomAuthor]).asInstanceOf[Seq[AtomAuthor]]
  }

  def categories : Seq[AtomCategory] = {
    metadatas.filter(_.isInstanceOf[AtomCategory]).asInstanceOf[Seq[AtomCategory]]
  }

  def contributers : Seq[AtomContributer] = {
    metadatas.filter(_.isInstanceOf[AtomContributer]).asInstanceOf[Seq[AtomContributer]]
  }

  def getGenerator : Option[AtomGenerator] = {
    metadatas.find(_.isInstanceOf[AtomGenerator]).asInstanceOf[Option[AtomGenerator]]
  }

  def generator : AtomGenerator = {
    getGenerator match {
      case Some(generator) => generator
      case None => error("no generator in atom feed")
    }
  }

  def getIcon : Option[AtomIcon] = {
    metadatas.find(_.isInstanceOf[AtomIcon]).asInstanceOf[Option[AtomIcon]]
  }

  def icon : AtomIcon = {
    getIcon match {
      case Some(icon) => icon
      case None => error("no icon in atom feed")
    }
  }

  def id : AtomId = {
    metadatas.find(_.isInstanceOf[AtomId]) match {
      case Some(atomId: AtomId) => atomId.asInstanceOf[Option[AtomIcon]]
  }

  def links : Seq[AtomLink] = {
  }

  def getLogo : Option[AtomLogo] = {
    metadatas.find(_.isInstanceOf[AtomLog]).asInstanceOf[Option[AtomLog]]
  }

  def logo : AtomLogo = {
  }

  def getRights : Option[AtomRights] = {
    metadatas.find(_.isInstanceOf[AtomGenerator]).asInstanceOf[Option[AtomGenerator]]
  }

  def rights : AtomRights = {
  }

  def title : AtomTitle = {
  }

  def updated : AtomUpdated = {
  }

  def extensionElements : Seq[AtomExtensionElement] = {
    metadatas.filter(_.isInstanceOf[AtomAuthor]).asInstanceOf[Seq[AtomAuthor]]
  }

  def entries : Seq[AtomEntry] = {
  }
}

trait AtomFeedMetaData {
}

object AtomFeed {
  def apply(aFeed: AtomFeed): Node = {
    error("???")
  }

  def unapply(aNode: Node) : Option(AtomFeed) = {
    error("???")
  }
}
*/
