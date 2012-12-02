package org.simplemodeling.SimpleModeler.entities.wadl

import scalaz._, Scalaz._
import scala.xml.Elem
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.relaxng._

/*
 * @version Dec.  2, 2012
 * @version Dec.  2, 2012
 * @author  ASAMI, Tomoharu
 */
case class WadlMaker(context: PEntityContext, ops: Seq[POperation]) {
  def application = <application xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://wadl.dev.java.net/2009/02 wadl.xsd"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:rng="http://relaxng.org/ns/structure/1.0"
    xmlns="http://wadl.dev.java.net/2009/02">{
      grammars
    }{
      resources
    }</application>

  def grammars = <grammars>{schemas}</grammars>

  def schemas = {
    val a = ops.map(MethodMaker(context, _).rngElements)
    a.flatten
  }
    
  def resources = <resources>{ops.map(resource)}</resources>
  def resource(op: POperation) = <resource path="">{
    MethodMaker(context, op).methods
  }</resource>
}

case class MethodMaker(context: PEntityContext, op: POperation) {
  def methods = (List(get, post, put, delete)).flatten

  def get: Option[Elem] = {
    <method name="GET" id={op.name}>{inout}</method>.some
  }

  def post: Option[Elem] = {
    None
  }

  def put: Option[Elem] = {
    None
  }

  def delete: Option[Elem] = {
    None
  }

  def inout: Seq[Elem] = {
    requests ++ responses
  }

  def requests: Seq[Elem] = {
    <request>{params}</request>.pure[List]
  }

  def params: Seq[Elem] = {
    op.in.map(params) | Nil
  }

  def params(doc: PDocumentType): Seq[Elem] = {
    require (doc.document != null, "Document should be resolved.")
    for (a <- doc.document.attributes) yield {
      a.attributeType match {
        case t => {
          <param name={a.name} type={xmlType(a.attributeType)} style="query"/>
        }
      }
    }
  }

  def xmlType(t: PObjectType) = "xsd:" + t.xmlDatatypeName

  def rngElements = {
    List(op.in.map(doc2rngElement),
         op.out.map(doc2rngElement)).flatten
  }

  def doc2rngElement(doc: PDocumentType) = {
    PRelaxngMaker(context, doc.document).element
  }

  def responses: Seq[Elem] = {
    List(response200, response400)
  }

  def response200 = {
    <response status="200">{outputDocumentRepresentation}</response>
  }

  def outputDocumentRepresentation = {
    outputDocumentTypeName.map(x => {
      <representation mediaType="application/json" element={x} />.pure[List]
    }) | Nil
  }

  def outputDocumentTypeName: Option[String] = {
    op.out.map(x => "app:" + x.xmlDatatypeName)
  }

  def response400 = {
    <response status="400"><representation mediaType="application/json" element="error" /></response>
  }
}
