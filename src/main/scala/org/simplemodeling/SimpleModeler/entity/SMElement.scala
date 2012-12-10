package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.{Buffer, ArrayBuffer}
import java.util.UUID
import org.simplemodeling.dsl._
import org.goldenport.value._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler._
import org.simplemodeling.SimpleModeler.sdoc.SMObjectRef

/**
 * @since   Sep. 15, 2008
 *  version Dec. 18, 2010
 *  version Feb. 22, 2012
 *  version Nov. 26, 2012
 * @version Dec. 10, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SMElement(val dslElement: SElement) extends GTreeNodeBase[SMElement] {
  type TreeNode_TYPE = SMElement
//  require (dslElement != null &&
//	   (!this.isInstanceOf[SMRoot] && dslElement.name != null))
  content = this
  set_name(dslElement.name)
//  dslElement.modelElement = this

  private val _features = new ArrayBuffer[SMFeature]
//  private val _anchors = new ArrayBuffer[SIAnchor]

  protected final def add_feature(key: GKey, value: SDoc): SMFeature = {
    val feature = new SMFeature(key, value)
    _features += feature
    feature
  }

  protected final def add_feature(aKey: GKey)(aFunction: () => SDoc): SMFeature = {
    val feature = new SMFeature(aKey)(aFunction)
    _features += feature
    feature
  }

/* 2008-10-15
  protected final def add_anchor(anchor: SIAnchor): SIAnchor = {
    _anchors += anchor
    anchor
  }
*/

  final def name_en = dslElement.name_en
  final def name_ja = dslElement.name_ja
  final def term = dslElement.term
  final def term_en = dslElement.term_en
  final def term_ja = dslElement.term_ja
  // XXX another name stuff
  /**
   * Name title is conflict with GTreeNode#title.
   */
  final def title_sdoc = dslElement.title
  final def subtitle = dslElement.subtitle
  final def label = dslElement.label
  final def caption = dslElement.caption
  final def brief = dslElement.brief
  final def summary = dslElement.summary
  final def resume = dslElement.resume
  final def description = dslElement.description
  final def note = dslElement.note
  final def history = dslElement.history

  /*
   * XML
   */
  def xmlName = dslElement.xmlName

  /*
   * GUI
   */
  def naviLabel = dslElement.naviLabel
  def tabLabel = dslElement.tabLabel

  /*
   * SQL
   */
  def sqlTableName = dslElement.sqlTableName
  def sqlColumnName = dslElement.sqlColumnName
//  def sqlDataType = dslElement.sqlDataType
  def sqlAutoId = dslElement.sqlAutoId
  def sqlReadOnly = dslElement.sqlReadOnly
  def sqlAutoCreate = dslElement.sqlAutoCreate
  def sqlAutoUpdate = dslElement.sqlAutoUpdate

  protected def new_Node(aName: String): SMElement = {
    val dslPackage = new SPackage(aName)
    // XXX more setup
    new SMPackage(dslPackage)
  }

  final def qualifiedName: String = {
    qualified_Name match {
      case Some(qName) => qName
      case None => UJavaString.pathname2className(pathname)
    }
  }

  protected def qualified_Name: Option[String] = None

  final def features: Seq[SMFeature] = _features.toList

  def getProperty(key: String): Option[String] = {
    dslElement.properties.find(_.isMatch(key)).flatMap(_.value)
  }

/*
  final def features: Seq[SMFeature] = {
    val buf = new ArrayBuffer[SMFeature]
    build_Element_Feature(buf)
    buf
  }

  protected def build_Element_Feature(aBuf: Buffer[SMFeature]) {
  }
*/

  // utilities
  protected final def objects_literal(theObjects: Seq[SMObject]): SDoc = {
    if (theObjects.isEmpty) return "-"
    val fragment = SFragment()
    for (obj <- theObjects) {
      val anchor = new SMObjectRef(obj)
      fragment.addChild(anchor)
      if (theObjects.last != obj) {
	fragment.addChild(SText(", "))
      }
    }
    fragment
  }

  protected final def syntax_error(message: String) {
    error("syntax error: " + message)
  }
}
