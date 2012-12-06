package org.simplemodeling.SimpleModeler.entities.relaxng

import scalaz._, Scalaz._
import scala.xml.Elem
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec.  2, 2012
 * @version Dec.  6, 2012
 * @author  ASAMI, Tomoharu
 */
case class PRelaxngMaker(context: PEntityContext, doc: PDocumentEntity) {
  def schema = {
    <rng:element rng:xmlns="http://relaxng.org/ns/structure/1.0" datatypeLibrary="http://www.w3.org/2001/XMLSchema-datatypes" name={doc.xmlElementName}>{attributes}</rng:element>
  }

  def element: Elem = {
    <rng:element name={doc.xmlElementName}>{attributes}</rng:element>
  }

  def attributes = {
    val b = for (a <- doc.wholeAttributes) yield {
      a.attributeType match {
        case v: PValueType => valueRef(a, v.value).some
        case d: PDocumentType => documentRef(a, d.document)
        case d: PDataType => datatypeRef(a, d).some
        case p: PPowertypeType => powertypeRef(a, p.powertype).some
        case sm: PStateMachineType => statemachineRef(a, sm.statemachine).some
        case e: PEntityType => documentRef(a, e.entity.documentEntity.get)
        case x => {
          context.record_warning("Illega object in document(%s) = %s".format(doc.name, x))
          none
        }
      }
    }
    b.flatten
  }

  def valueRef(attr: PAttribute, v: PValueEntity) = {
    refAttributeOrElement(attr)(<rng:text/>)
  }
  
  def documentRef(attr: PAttribute, doc: PDocumentEntity) = {
    // ref(attr)(PRelaxngMaker(context, doc).element)
    // TODO reference to document
    none
  }

  def datatypeRef(attr: PAttribute, dt: PDataType) = {
    refAttributeOrElement(attr) {
      dt match {
        case _: PTextType => <text/>
        case _ => <rng:data type={dt.xmlDatatypeName}/>
      }
    }
  }

  def powertypeRef(attr: PAttribute, dt: PPowertypeEntity) = {
    refAttributeOrElement(attr) {
      if (dt.isKnowledge) <rng:text/>
      else <rng:choice>{
        dt.kinds.map(x => <rng:value>{x.name}</rng:value>)
      }</rng:choice>
    }
  }

  def statemachineRef(attr: PAttribute, sm: PStateMachineEntity) = {
    ref(attr) {
      if (sm.isKnowledge) <rng:text/>
      else <rng:choice>{
        sm.states.map(x => <rng:value>{x.name}</rng:value>)
      }</rng:choice>
    }
  }

  def ref(attr: PAttribute)(elem: => Elem): Elem = {
    attr.multiplicity match {
      case POne => elem
      case PZeroOne => <rng:optional>{elem}</rng:optional>
      case POneMore => <rng:oneOrMore>{elem}</rng:oneOrMore>
      case PZeroMore => <rng:zeroOrMore>{elem}</rng:zeroOrMore>
    }
  }

  def refAttributeOrElement(attr: PAttribute)(elem: => Elem): Elem = {
    val name = context.xmlName(attr)
    if (isXmlAttribute(attr)) {
      <rng:attribute name={name}>{ref(attr)(elem)}</rng:attribute>
    } else {
      <rng:element name={name}>{ref(attr)(elem)}</rng:element>
    }
  }

  def isXmlAttribute(attr: PAttribute): Boolean = {
    attr.multiplicity match {
      case POne => true
      case _ => false
    }
  }
}
