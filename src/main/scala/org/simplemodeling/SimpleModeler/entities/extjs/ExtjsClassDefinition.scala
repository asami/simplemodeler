package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import com.asamioffice.goldenport.text.UString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr.  4, 2012
 * @version Apr. 16, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class ExtjsClassDefinition(
  val extjsContext: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  extjsobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends GenericClassDefinition(extjsContext, aspects, extjsobject) with ExtjsMakerHolder {
  type ATTR_DEF = ExtjsClassAttributeDefinition

  require (pobject != null, "ExtjsClassDefinition: extjs object should not be null.")
  require (UString.notNull(extjsobject.name), "ExtjsClassDefinition: ExtjsObjectEntity.name should not be null.")
  require (UString.notNull(extjsobject.kindName), "ExtjsClassDefinition: extjsObjectEntity.kindName should not be null.")
  val applicationName: String = extjsContext.applicationName(pobject)
  val kindName: String = extjsobject.kindName
  var baseName: Option[String] = None // XXX
  var alias: Option[String] = None

  if (maker == null) {
    ej_open(aspects)
  } else {
    ej_open(maker, aspects)
  }
  aspects.foreach(_.openExtjsClass(this))

//  private lazy val _builder = new BuilderJavaClassDefinition(pContext, Nil, pobject, ej_maker)

  override def toText = {
    jm_to_text
  }

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new ExtjsClassAttributeDefinition(pContext, aspects, attr, this, ejmaker)
  }

  override protected def pln() {
    jm_pln()
  }

  override protected def head_package {
  }

  override protected def head_imports_Prologue {
  }

  override protected def head_imports_Object(anObject: PObjectReferenceType) {
  }

  override protected def head_imports_Entity(anEntity: PEntityType) {
  }

  override protected def head_imports_Epilogue() {
    jm_end_import_section()
  }

  override protected def head_imports_Builder {
  }

  override protected def class_open_body {
    jm_pln("Ext.define('%s', {", qualifiedName)
    jm_indent_up
    for (n <- baseName) {
      jm_pln("extend: '%s',", n)
    }
    for (a <- alias) {
      jm_pln("alias: '%s',", a)
    }
  }

  override protected def class_close_body {
    jm_indent_down
    jm_pln("});")
  }

  override protected def attribute_variables_Prologue {
  }

  override protected def attribute_variables_Epilogue {
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
}
