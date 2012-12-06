package org.simplemodeling.SimpleModeler.entities.relaxng

import scalaz._, Scalaz._
import scala.xml.Elem
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @version Dec.  2, 2012
 * @version Dec.  2, 2012
 * @author  ASAMI, Tomoharu
 */
case class RelaxngMaker(doc: SMDocument) {
  def schema = {
    <rng:element rng:xmlns="http://relaxng.org/ns/structure/1.0" name={doc.xmlElementName}>attributes</rng:element>
  }

  def element: Elem = {
    <rng:element name={doc.xmlElementName}>{attributes}</rng:element>
  }

  // TODO implement using PRelaxngMaker logic.
  def attributes = {
    val b = for (a <- doc.attributes) yield {
      a.attributeType.typeObject match {
        case d: SMDatatype => <text/>.some
        case d: SMValue => <text/>.some
        case d: SMDocument => documentRef(a, d).some
        case _ => none
      }
    }
    b.flatten
  }

  def documentRef(attr: SMAttribute, doc: SMDocument) = {
    val e = RelaxngMaker(doc).element
    attr.multiplicity.kind match {
      case SMMultiplicityOne => e
      case SMMultiplicityZeroOne => <optional>{e}</optional>
      case SMMultiplicityOneMore => <oneOrMore>{e}</oneOrMore>
      case SMMultiplicityZeroMore => <zeroOrMore>{e}</zeroOrMore>
    }
  }
}
