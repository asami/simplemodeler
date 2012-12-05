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
        dox_p("全体のWADL定義は以下になります。個々のプロトコルの詳細は次章以降で説明します。"),
        dox_program("WADL定義", wadl.application)
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

case class EntityResourceSpecMaker(main: SpecMaker, maker: EntityMethodMaker)(implicit context: PEntityContext) {
  val entity = maker.entity
  def spec = {
    List(
      factory,
      instance
    ).flatten
  }

  val resourceName = context.localeName(entity.modelObject)
  val path = entity.uriName
  val instancePath = path + "{" + entity.idAttr.name + "}"

  def factory: Option[Dox] = {
    val title = resourceName + "・ファクトリ"
    main.section(title) {
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

  def factory_sheet: Option[Dox] = {
    dox_table_tuple(
      ("項目", "値"),
      List(
        ("パス", path)),
      "諸元",
      idgen).some
  }

  def idgen = UUID.randomUUID.toString

  def factory_path: Option[Dox] = None

  def factory_description: Option[Dox] = {
    main.section("説明") {
      List(
        dox_p("リソース%sに対するファクトリです。リソース%sを新規作成します。プロトコルではPOSTメソッドを用います。", resourceName, resourceName)
      )
    }.some
  }

  def factory_get: Option[Dox] = {
    none
  }

  def factory_post: Option[Dox] = {
    main.section("POST") {
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
    main.section("Resource") {
      List(
        dox_p("リソース%sに対するファクトリのWADLリソース定義です。", resourceName),
        dox_program("WADLリソース定義", maker.factory)
      )
    }.some
  }

  def factory_post_schema: Option[Dox] = {
    val schemas = maker.schemas
    main.section("Schema") {
      dox_p("リソース%sへのアクセス時に使用するXML文書のスキーマ定義です。", resourceName) +:
      schemas.map(dox_program("スキーマ定義", _))
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
    val title = resourceName + "・インスタンス"
    main.section(title) {
      List(
        instance_sheet,
        instance_path,
        instance_description,
        instance_resource,
        instance_schema,
        instance_get,
        instance_post,
        instance_put,
        instance_delete
      ).flatten
    }.some
  }

  def instance_sheet: Option[Dox] = {
    dox_table_tuple(
      ("項目", "値"),
      List(
        ("パス", path)),
      "諸元",
      idgen).some
  }

  def instance_path: Option[Dox] = {
    none
  }

  def instance_description: Option[Dox] = {
    main.section("説明") {
      List(
        dox_p("リソース%sのインスタンスに参照・更新・削除アクセスします。プロトコルでは参照時にGETメソッド、更新時にPUTメソッド、削除時にDELETEメソッドを用います。", resourceName)
      )
    }.some
  }

  def instance_resource: Option[Dox] = {
    val resources = maker.resources
    main.section("WADLリソース定義") {
      List(
        dox_p("リソース%sの参照・更新・削除に使用するファクトリのWADLリソース定義です。関係部分のみ表示しています。", resourceName),
        dox_program("WADLリソース定義", maker.resource)
      )
    }.some
  }

  def instance_schema: Option[Dox] = {
    val schemas = maker.schemas
    main.section("スキーマ定義") {
      dox_p("リソース%sの参照・更新時に使用するXML文書のスキーマ定義です。", resourceName) +:
      schemas.map(dox_program("スキーマ定義", _))
    }.some
  }

  def instance_get: Option[Dox] = {
    main.section("GETメソッド") {
      List(
        instance_get_sheet,
        instance_get_description,
        instance_get_resource,
        instance_get_schema
      ).flatten
    }.some
  }


  def instance_get_sheet: Option[Dox] = {
    none
  }

  def instance_get_description: Option[Dox] = {
    main.section("説明") {
      List(
        dox_p("リソース%sのインスタンスを参照アクセスします。プロトコルではGETメソッドを用います。", resourceName)
      )
    }.some
  }

  def instance_get_resource: Option[Dox] = {
    main.section("Resource") {
      List(
        dox_p("リソース%sの参照に使用するファクトリのWADLリソース定義です。関係部分のみ表示しています。", resourceName),
        dox_program("WADLリソース定義", maker.resource)
      )
    }.some
  }

  def instance_get_schema: Option[Dox] = {
    val schemas = maker.schemas
    main.section("Schema") {
      dox_p("リソース%sへの参照結果として返されるXML文書のスキーマ定義です。", resourceName) +:
      schemas.map(dox_program("スキーマ定義", _))
    }.some
  }

  def instance_post: Option[Dox] = {
    none
  }

  def instance_put: Option[Dox] = {
    main.section("PUTメソッド") {
      List(
        instance_put_sheet,
        instance_put_description,
        instance_put_resource,
        instance_put_schema
      ).flatten
    }.some
  }

  def instance_put_sheet: Option[Dox] = {
    none
  }

  def instance_put_description: Option[Dox] = {
    main.section("説明") {
      List(
        dox_p("リソース%sのインスタンスに更新アクセスします。プロトコルではPUTメソッドを用います。", resourceName)
      )
    }.some
  }

  def instance_put_resource: Option[Dox] = {
    val resources = maker.resources
    main.section("Resource") {
      List(
        dox_p("リソース%sの更新に使用するファクトリのWADLリソース定義です。関係部分のみ表示しています。", resourceName),
        dox_program("WADLリソース定義", maker.resource)
      )
    }.some
  }

  def instance_put_schema: Option[Dox] = {
    val schemas = maker.schemas
    main.section("Schema") {
      dox_p("リソース%sの更新時に使用するXML文書のスキーマ定義です。", resourceName) +:
      schemas.map(dox_program("スキーマ定義", _))
    }.some
  }

  def instance_delete: Option[Dox] = {
    main.section("DELETEメソッド") {
      List(
        instance_delete_sheet,
        instance_delete_description,
        instance_delete_resource,
        instance_delete_schema
      ).flatten
    }.some
  }

  def instance_delete_sheet: Option[Dox] = {
    none
  }

  def instance_delete_description: Option[Dox] = {
    main.section("説明") {
      List(
        dox_p("リソース%sのインスタンスを削除します。プロトコルではDELETEメソッドを用います。", resourceName)
      )
    }.some
  }

  def instance_delete_resource: Option[Dox] = {
    val resources = maker.resources
    main.section("Resource") {
      dox_p("リソース%sの削除に使用するファクトリのWADLリソース定義です。", resourceName) +:
      resources.map(dox_program("WADLリソース定義", _))
    }.some
  }

  def instance_delete_schema: Option[Dox] = {
    none
  }
}
