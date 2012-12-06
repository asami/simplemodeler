package org.simplemodeling.SimpleModeler.entities.wadl

import scalaz._, Scalaz._
import java.util.UUID
import java.net.URI
import scala.xml.Elem
import org.apache.commons.lang3.StringUtils
import org.smartdox._, Doxes._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.relaxng._

/*
 * @since   Dec.  4, 2012
 * @version Dec.  6, 2012
 * @author  ASAMI, Tomoharu
 */
case class SpecMaker(
  title: String,
  subtitle: String,
  wadl: WadlMaker,
  entities: Seq[EntityMethodMaker],
  events: Seq[EventMethodMaker],
  services: Seq[ServiceMethodMaker],
  author: String = null,
  date: String = null
)(implicit context: PEntityContext) {
  private var _section_depth = 1

  def spec = {
    Document(head, body)
  }

  protected def nonBlankString(s: String): Option[String] = {
    StringUtils.isNotBlank(s).option(s)
  }

  def head = {
    val t = List(title.some, nonBlankString(subtitle).map(_.mkString("(", "", ")"))).flatten.mkString(" ")
    val a = Option(author).toList
    val d = Option(date) | new java.util.Date().toString
    Head(dox_text(t), a.flatMap(dox_text), dox_text(d))
  }

  def body = {
    Body(List(
      prologue_section,
      wadl_section,
      service_section,
      event_section,
      resource_section
    ).flatten)
  }

  def prologue_section = {
    val wadluri = "http://wadl.java.net/"
    val relaxnguri = "http://relaxng.org/spec-20011203.html"
    section("仕様書諸元") {
      List(
        dox_p("本仕様書ではWADL(Web Application Description Language)を使用してRESTサービスの仕様を記述します。WADLの仕様は以下のページを参照してください。"),
        Ul(List(
          Li(List(Hyperlink(dox_text(wadluri), new URI(wadluri)))))),
        dox_p("RESTサービスで使用するXML文書のスキーマはRELAX NGを用いて定義しています。RELAX NGの仕様は以下のページを参照してください。"),
        Ul(List(
          Li(List(Hyperlink(dox_text(relaxnguri), new URI(relaxnguri))))))
      )
    }.some
  }

  def wadl_section = {
    section("WADL") {
      List(
        dox_p("全体のWADL定義は以下になります。個々のプロトコルの詳細は次章以降で説明します。"),
        dox_program("WADL定義", wadl.application)
      )
    }.some
  }

  def service_section = {
    if (services.isEmpty) None
    else {
      section("サービス") {
        services.flatMap(ServiceResourceSpecMaker(this, _).spec).toList
      }.some
    }
  }

  def event_section = {
    if (events.isEmpty) None
    else {
      section("イベント") {
        events.flatMap(EventResourceSpecMaker(this, _).spec).toList
      }.some
    }
  }

  def resource_section = {
    section("リソース") {
      entities.flatMap(EntityResourceSpecMaker(this, _).spec).toList
    }.some
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

trait ResourceSpecMaker {
  def idgen = UUID.randomUUID.toString
}

case class ServiceResourceSpecMaker(
  main: SpecMaker,
  maker: ServiceMethodMaker
)(implicit context: PEntityContext) extends ResourceSpecMaker {
  val service = maker.service
  val path = "service/" + service.uriName // XXX
  val resourceName = context.localeName(service.modelObject)
  val operationMakers = maker.operations.map(OperationResourceSpecMaker(main, this, _))

  def spec = {
    List(
      overview
    ).flatten
  }

  def overview: Option[Dox] = {
    val title = resourceName + " サービス"
    main.section(title) {
      List(
        sheet,
        description,
        operations
      ).flatten
    }.some
  }

  def sheet: List[Dox] = {
    dox_table_tuple(
      ("項目", "値"),
      List(
        ("パス", path)),
      "諸元",
      idgen).pure[List]
  }

  def description: List[Dox] = {
    main.section("説明") {
      List(
        dox_p("サービス%sのリソースです。プロトコルでは問合せ系操作はGETメソッド、更新系操作はPOSTメソッドを用います。", resourceName, resourceName)
      )
    }.pure[List]
  }

  def operations: List[Dox] = {
    operationMakers.flatMap(_.spec).toList
  }
}

case class OperationResourceSpecMaker(
  main: SpecMaker,
  service: ServiceResourceSpecMaker,
  maker: OperationMethodMaker
)(implicit context: PEntityContext) extends ResourceSpecMaker {
  val op = maker.op
  val path = service.path + "/" + op.uriName
  val resourceName = op.uriName // context.localeName(op)

  def spec: Option[Dox] = {
    if (maker.isQuery) get
    else post
  }

  def get: Option[Dox] = {
    main.section(context.uriName(op.model)) {
      List(
        get_sheet,
        get_resource,
        get_schema
      ).flatten
    }.some
  }

  def get_sheet: Option[Dox] = {
    dox_table_tuple(
      ("項目", "値"),
      List(
        ("メソッド", "GET")),
      "諸元",
      idgen).some
  }

  def get_resource: Option[Dox] = {
    main.section("リソース") {
      List(
        dox_p("サービス%sの操作%sに対するWADLリソース定義です。", resourceName),
        dox_program("WADLリソース定義", maker.resource)
      )
    }.some
  }

  def get_schema: Option[Dox] = {
    val schemas = maker.schemas
    main.section("スキーマ") {
      dox_p("サービス%sの操作%sで使用するXML文書のスキーマ定義です。", resourceName) +:
      schemas.map(dox_program("スキーマ定義", _))
    }.some
  }

  def post: Option[Dox] = {
    main.section(context.uriName(op.model)) {
      List(
        post_sheet,
        post_resource,
        post_schema
      ).flatten
    }.some
  }

  def post_sheet: Option[Dox] = {
    dox_table_tuple(
      ("項目", "値"),
      List(
        ("メソッド", "POST")),
      "諸元",
      idgen).some
  }

  def post_resource: Option[Dox] = {
    main.section("リソース") {
      List(
        dox_p("サービス%sの操作%sに対するWADLリソース定義です。", resourceName),
        dox_program("WADLリソース定義", maker.resource)
      )
    }.some
  }

  def post_schema: Option[Dox] = {
    val schemas = maker.schemas
    main.section("スキーマ") {
      dox_p("サービス%sの操作%sで使用するXML文書のスキーマ定義です。", resourceName) +:
      schemas.map(dox_program("スキーマ定義", _))
    }.some
  }
}

case class EventResourceSpecMaker(
  main: SpecMaker,
  maker: EventMethodMaker
)(implicit context: PEntityContext) extends ResourceSpecMaker {
  val entity = maker.entity
  val resourceName = context.localeName(entity.modelObject)
  val path = "event/" + entity.uriName // XXX

  def spec = {
    List(
      operation
    ).flatten
  }

  def operation: Option[Dox] = {
    val title = resourceName + " イベント"
    main.section(title) {
      List(
        operation_sheet,
        operation_path,
        operation_description,
        operation_get,
        operation_post,
        operation_put,
        operation_delete
      ).flatten
    }.some
  }

  def operation_sheet: Option[Dox] = {
    dox_table_tuple(
      ("項目", "値"),
      List(
        ("パス", path)),
      "諸元",
      idgen).some
  }

  def operation_path: Option[Dox] = None

  def operation_description: Option[Dox] = {
    main.section("説明") {
      List(
        dox_p("イベント%sを発行します。プロトコルではPOSTメソッドを用います。", resourceName, resourceName)
      )
    }.some
  }

  def operation_get: Option[Dox] = {
    none
  }

  def operation_post: Option[Dox] = {
    main.section("POST") {
      List(
        operation_post_sheet,
        operation_post_resource,
        operation_post_schema
      ).flatten
    }.some
  }

  def operation_post_sheet: Option[Dox] = {
    none
  }

  def operation_post_resource: Option[Dox] = {
    main.section("Resource") {
      List(
        dox_p("イベント%sに対するWADLリソース定義です。", resourceName),
        dox_program("WADLリソース定義", maker.resource)
      )
    }.some
  }

  def operation_post_schema: Option[Dox] = {
    val schemas = maker.schemas
    main.section("Schema") {
      dox_p("イベント%s発行時に使用するXML文書のスキーマ定義です。", resourceName) +:
      schemas.map(dox_program("スキーマ定義", _))
    }.some
  }

  def operation_put: Option[Dox] = {
    none
  }

  def operation_delete: Option[Dox] = {
    none
  }
}

case class EntityResourceSpecMaker(
  main: SpecMaker,
  maker: EntityMethodMaker
)(implicit context: PEntityContext) extends ResourceSpecMaker {
  val entity = maker.entity
  def spec = {
    List(
      factory,
      instance
    ).flatten
  }

  val resourceName = context.localeName(entity.modelObject)
  val path = "resource/" + entity.uriName // XXX
  val instancePath = path + "{" + entity.idAttr.name + "}"

  def factory: Option[Dox] = {
    val title = resourceName + " ファクトリ"
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
    val title = resourceName + " インスタンス"
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
