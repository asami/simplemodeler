package org.simplemodeling.SimpleModeler.entities.wadl

import scalaz._, Scalaz._
import scala.xml.Elem
import org.smartdox._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.relaxng._

/*
 * @since   Dec.  2, 2012
 * @version Dec.  6, 2012
 * @author  ASAMI, Tomoharu
 */
case class WadlMaker(
  title: String,
  subtitle: String,
  entities: Seq[PDocumentEntity],
  events: Seq[PEntityEntity],
  services: Seq[PServiceEntity],
  author: String = null,
  date: String = null
)(implicit context: PEntityContext) {
  val entityMakers = entities.map(EntityMethodMaker(_))
  val eventMakers = events.map(EventMethodMaker(_))
  val serviceMakers = services.map(ServiceMethodMaker(_))

  def application = {
    <application xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://wadl.dev.java.net/2009/02 wadl.xsd"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:rng="http://relaxng.org/ns/structure/1.0"
    xmlns="http://wadl.dev.java.net/2009/02">{
      grammars ++ resources
    }</application>
  }

  def grammars = <grammars>{schemas}</grammars>

  def schemas = {
    val a = entityMakers.flatMap(_.schemas)
    val c = eventMakers.flatMap(_.schemas)
    val b = serviceMakers.flatMap(_.schemas)
    c ++ a ++ b // unify
  }

  def resources = <resources>{resourceElements}</resources>

  def resourceElements: Seq[Elem] = {
    val a = entityMakers.flatMap(_.resources)
    val c = eventMakers.flatMap(_.resources)
    val b = serviceMakers.flatMap(_.resources)
    c ++ a ++ b
  }

  def spec = SpecMaker(title, subtitle, this, entityMakers, eventMakers, serviceMakers, author, date).spec
}

abstract class MethodMaker(val context: PEntityContext) {
  def schemas: Seq[Elem]
  def resources: Seq[Elem]
/*
  def methods = (List(get, post, put, delete)).flatten
  def get: Option[Elem]
  def post: Option[Elem]
  def put: Option[Elem]
  def delete: Option[Elem]
*/
  protected def response200 = {
    <response status="200"/>
  }

  protected def response400 = {
    <response status="400"><representation mediaType="application/xml" element="error" /></response>
  }

  protected def doc2rng_element(doc: PDocumentType): Elem = {
    doc2rng_element(doc.document)
  }

  protected def doc2rng_element(doc: PDocumentEntity): Elem = {
    PRelaxngMaker(context, doc).element
  }

  protected def entity2rng_element(entity: PEntityEntity): Elem = {
   doc2rng_element(entity.documentEntity.get) // XXX
  }
}

case class EntityMethodMaker(entity: PDocumentEntity)(implicit context: PEntityContext) extends MethodMaker(context) {
  def schemaName = "rng:" + context.xmlName(entity)

  def schemas: List[Elem] = {
    doc2rng_element(entity).pure[List]
  }

  def resources: List[Elem] = {
    List(factory, resource)
  }

  def factory: Elem = {
    val attr = entity.idAttr
    val name = attr.name
    val desc = Nil // XXX
    val path = entity.uriName
    val xt = "xsd:" + attr.attributeType.xmlDatatypeName
    <resource path={path}><doc>{desc}</doc>{
      factoryMethods
    }</resource>
  }

  def factoryMethods = List(postMethod)

  def resource: Elem = {
    val attr = entity.idAttr
    val name = attr.name
    val doc = Nil // XXX
    val path = entity.uriName + "/{" + name + "}"
    val xt = "xsd:" + attr.attributeType.xmlDatatypeName
    <resource path={path}><doc>{doc}</doc><param
    name={attr.name}
    style="template" type={xt} required="true"/>{
      resourceMethods
    }</resource>
  }

  def postMethod = {
    val inout = {
      List(
        <request><representation mediaType="application/xml"
        element={schemaName}/></request>,
        response200,
        response400)
    }
    <method name="POST">{inout}</method>
  }

  def resourceMethods = {
    List(get, put, delete)
  }

  def get = {
    val inout = {
      List(
        <response status="200"><representation mediaType="application/xml"
        element={schemaName}/></response>,
        response400)
    }
    <method name="GET">{inout}</method>
  }

  def put = {
    val inout = {
      List(
        <request><representation mediaType="application/xml"
        element={schemaName}/></request>,
        response200,
        response400)
    }
    <method name="PUT">{inout}</method>
  }

  def delete = {
    val inout = {
      List(
        response200,
        response400)
    }
    <method name="DELETE">{inout}</method>
  }
}

case class EventMethodMaker(entity: PEntityEntity)(implicit context: PEntityContext) extends MethodMaker(context) {
  def schemaName = "rng:" + context.xmlName(entity)

  def schemas: List[Elem] = {
    entity2rng_element(entity).pure[List]
  }

  def resources: List[Elem] = {
    List(resource)
  }

  def resource: Elem = {
    val attr = entity.idAttr
    val name = attr.name
    val doc = Nil // XXX
    val path = entity.uriName + "/{" + name + "}"
    val xt = "xsd:" + attr.attributeType.xmlDatatypeName
    <resource path={path}><doc>{doc}</doc><param
    name={attr.name}
    style="template" type={xt} required="true"/>{
      postMethod
    }</resource>
  }

  def postMethod = {
    val inout = {
      List(
        <request><representation mediaType="application/xml"
        element={schemaName}/></request>,
        response200,
        response400)
    }
    <method name="POST">{inout}</method>
  }
}

case class ServiceMethodMaker(service: PServiceEntity)(implicit context: PEntityContext) extends MethodMaker(context) {
  val operations = service.operations.map(OperationMethodMaker(_))

  def schemas = operations.flatMap(_.schemas)

  def resources: List[Elem] = {
    List(resource)
  }

  def resource = {
    val path = service.uriName
    val desc = Nil
    val children = operations.flatMap(_.resources)
    <resource path={path}><doc>{desc}</doc>{children}</resource>
  }
}

case class OperationMethodMaker(op: POperation)(implicit context: PEntityContext) extends MethodMaker(context) {
  def schemas = {
    List(op.in.map(doc2rng_element),
         op.out.map(doc2rng_element)).flatten
  }

  def resources: List[Elem] = {
    List(resource)
  }

  def resource: Elem = {
    val path = op.uriName
    val desc = Nil
    <resource path={path}><doc>{desc}</doc>{methods}</resource>
  }

  def methods: Seq[Elem] = List(method)

  def method: Elem = {
    <method name="POST">{inout}</method>    
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

  def responses: Seq[Elem] = {
    List(response200, response400)
  }

  override def response200 = {
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

  def isQuery = op.isQuery
}
