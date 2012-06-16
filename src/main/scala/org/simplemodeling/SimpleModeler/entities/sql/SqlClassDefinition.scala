package org.simplemodeling.SimpleModeler.entities.sql

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import com.asamioffice.goldenport.text.UString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   May.  3, 2012
 * @version Jun. 16, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SqlClassDefinition(
  val sqlContext: SqlEntityContext,
  anAspects: Seq[SqlAspect],
  val sqlObject: SqlObjectEntity,
  maker: JavaMaker = null
) extends GenericClassDefinition(sqlContext, anAspects, sqlObject) with JavaMakerHolder {
  type ATTR_DEF = SqlClassAttributeDefinition

  require (pobject != null, "SqlClassDefinition: sql object should not be null.")
  require (UString.notNull(sqlObject.name), "SqlClassDefinition: SqlObjectEntity.name should not be null.")

  if (maker == null) {
    jm_open(Nil) // XXX SqlAspect
  } else {
    jm_open(maker, Nil) // XXX SqlAspect
  }
  anAspects.foreach(_.openSqlClass(this))

  protected def pln() {
    jm_pln()
  }

  override def toText = {
    jm_to_text
  }

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new NullSqlClassAttributeDefinition(sqlContext, Nil, attr, this, maker) // XXX SqlAspect
  }

  override protected def class_open_body {
  }

  override protected def class_close_body {
  }

  override protected def attribute_variables_Prologue {
  }

  override protected def attribute_variables_Epilogue {
    jm_pln("CREATE TABLE %s (", sqlContext.sqlName(sqlObject))
    jm_indent_up
    wholeAttributeDefinitions.map { x =>
      () => x.ddl
    } intersperse {
      () => jm_pln(",")
    } map (_())
    jm_pln
    jm_indent_down
    jm_pln(");")
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
  }

  /*
   * to_methods
   */
  override protected def to_methods_string {
  }

  override protected def to_methods_xml {
  }

  private def _var_name(attr: GenericClassAttributeDefinition): String = {
    _var_name(attr.attr, attr.varName)
  }

  private def _var_name(attr: PAttribute, varName: String): String = {
    aspects.flatMap(_.objectVarName(attr, varName)).headOption getOrElse varName
  }

  override protected def to_methods_json {
  }

  override protected def to_methods_csv {
  }

  override protected def to_methods_yaml {
  }

  override protected def to_methods_map {
  }

  /*
   * update_methods
   */
  override protected def update_methods_string {
  }

  override protected def update_methods_xml {
  }

  override protected def update_methods_json {
  }

  override protected def update_methods_csv {
  }

  override protected def update_methods_yaml {
  }

  override protected def update_methods_urlencode {
  }

  override protected def update_methods_map {
  }

  /*
   * object_methods
   */
  override protected def object_methods_hashcode {
  }

  override protected def object_methods_equals {
  }

  override protected def document_methods_make {
  }

  override protected def document_methods_update {
  }

  override protected def builder_copy_factory {
  }

  override protected def builder_new_factory {
  }

  override protected def builder_class {
  }

  override protected def builder_auxiliary {
  }

  /**
   * Utility for subclass.
   */
}
