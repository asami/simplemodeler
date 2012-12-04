package org.simplemodeling.SimpleModeler.entities.wadl

import scalaz._, Scalaz._
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
    Section(
      List(Text("WADL")),
      List(
        dox_program(wadl.application)
      )
    )
  }

  def resource_section = {
    Section(
      dox_text("リソース"), // XXX
      entities.flatMap(EntityResourceSpecMaker(_).spec).toList
    )
  }
}

case class EntityResourceSpecMaker(maker: EntityMethodMaker) {
  val entity = maker.entity
  def spec = {
    List(
      factory,
      instance
    ).flatten
  }

  def factory: Option[Dox] = {
    Section(
      factory_name,
      List(
        factory_sheet,
        factory_path,
        factory_description,
        factory_get,
        factory_post,
        factory_put,
        factory_delete
      ).flatten
    ).some
  }

  def factory_name = {
    List(Text(entity.name))
  }

  def factory_sheet: Option[Dox] = {
    dox_table(
      ("項目", "値"),
      List(
        ("パス", "???"))).some
  }

  def factory_path: Option[Dox] = None

  def factory_description: Option[Dox] = {
    dox_section(
      "説明",
      List()
    ).some
  }

  def factory_get: Option[Dox] = {
    none
  }

  def factory_post: Option[Dox] = {
    dox_section(
      "POST",
      List(
        factory_post_sheet,
        factory_post_resource,
        factory_post_schema
      ).flatten
    ).some
  }

  def factory_post_sheet: Option[Dox] = {
    none
  }

  def factory_post_resource: Option[Dox] = {
    val resources = maker.resources
    dox_section(
      "Resource",
      resources.map(dox_program)
    ).some
  }

  def factory_post_schema: Option[Dox] = {
    val schemas = maker.schemas
    dox_section(
      "Schema",
      schemas.map(dox_program)
    ).some
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
    Section(
      factory_name,
      List(
        instance_sheet,
        instance_path,
        instance_description,
        instance_get,
        instance_post,
        instance_put,
        instance_delete
      ).flatten
    ).some
  }

  def instance_name = dox_text(entity.name)

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
