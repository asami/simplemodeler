package org.simplemodeling.SimpleModeler.entities.wadl

import scalaz._, Scalaz._
import java.util.UUID
import scala.xml.Elem
import org.smartdox._
import Doxes._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.relaxng._

/*
 * @since   Dec.  4, 2012
 * @version Dec.  5, 2012
 * @author  ASAMI, Tomoharu
 */
case class SpecMaker(
  wadl: WadlMaker,
  entities: Seq[EntityMethodMaker],
  services: Seq[ServiceMethodMaker]
)(implicit context: PEntityContext) {
  private var _section_depth = 1

  def spec = {
    Document(head, body)
  }

  def head = {
    Head()
  }

  def body = {
    Body(List(
      wadl_section,
      resource_section
    ))
  }

  def wadl_section = {
    section("WADL") {
      List(
        dox_program(wadl.application)
      )
    }
  }

  def resource_section = {
    section("リソース") {
      entities.flatMap(EntityResourceSpecMaker(this, _).spec).toList
    }
  }

  //
  def section(title: String)(body: => List[Dox]): Section = {
    val d = _section_depth
    _section_depth += 1
    val r = dox_section(d, title, body)
    _section_depth -= 1
    r
  }
}

case class EntityResourceSpecMaker(context: SpecMaker, maker: EntityMethodMaker) {
  val entity = maker.entity
  def spec = {
    List(
      factory,
      instance
    ).flatten
  }

  def factory: Option[Dox] = {
    context.section(factory_name) {
      List(
        factory_sheet,
        factory_path,
        factory_description,
        factory_get,
        factory_post,
        factory_put,
        factory_delete
      ).flatten
    }.some
  }

  def factory_name = entity.name

  def factory_sheet: Option[Dox] = {
    dox_table_tuple(
      ("項目", "値"),
      List(
        ("パス", "???")),
      "諸元",
      idgen).some
  }

  def idgen = UUID.randomUUID.toString

  def factory_path: Option[Dox] = None

  def factory_description: Option[Dox] = {
    context.section("説明") {
      List()
    }.some
  }

  def factory_get: Option[Dox] = {
    none
  }

  def factory_post: Option[Dox] = {
    context.section("POST") {
      List(
        factory_post_sheet,
        factory_post_resource,
        factory_post_schema
      ).flatten
    }.some
  }

  def factory_post_sheet: Option[Dox] = {
    none
  }

  def factory_post_resource: Option[Dox] = {
    val resources = maker.resources
    context.section("Resource") {
      resources.map(dox_program)
    }.some
  }

  def factory_post_schema: Option[Dox] = {
    val schemas = maker.schemas
    context.section("Schema") {
      schemas.map(dox_program)
    }.some
  }

  def factory_put: Option[Dox] = {
    none
  }

  def factory_delete: Option[Dox] = {
    none
  }

  /*
   * Instance
   */
  def instance: Option[Dox] = {
    context.section(instance_name) {
      List(
        instance_sheet,
        instance_path,
        instance_description,
        instance_get,
        instance_post,
        instance_put,
        instance_delete
      ).flatten
    }.some
  }

  def instance_name = entity.name

  def instance_sheet: Option[Dox] = {
    none
    
  }

  def instance_path: Option[Dox] = {
    none
    
  }

  def instance_description: Option[Dox] = {
    none
    
  }

  def instance_get: Option[Dox] = {
    none
    
  }

  def instance_post: Option[Dox] = {
    none
    
  }

  def instance_put: Option[Dox] = {
    none
    
  }

  def instance_delete: Option[Dox] = {
    none
    
  }
}
