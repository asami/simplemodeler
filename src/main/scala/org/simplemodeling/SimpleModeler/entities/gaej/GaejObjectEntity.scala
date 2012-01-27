package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

/*
 * @since   Apr. 10, 2009
 * @version Nov. 12, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class GaejObjectEntity(val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  protected var _baseObject: GaejObjectReferenceType = null
//  protected var _belongsTo: GaejEntityType = null

  var packageName = ""
  var xmlNamespace = ""
  def qualifiedName = if (packageName != "") packageName + "." + name else name
  val attributes = new ArrayBuffer[GaejAttribute]
  val operations = new ArrayBuffer[GaejOperation]
  var name_en = ""
  var name_ja = ""
  var term = ""
  var term_en = ""
  var term_ja = ""
  // URL (term_name)
  var termName = ""
  // Class Name base (TermName)
  var termNameBase = ""
  var modelObject: SMObject = SMNullObject
  lazy val xmlElementName = termName
  lazy val factoryName = gaejContext.factoryName(packageName)
  lazy val contextName = gaejContext.contextName(packageName)

  def getBaseObject: Option[GaejObjectEntity] = {
    if (_baseObject != null) Some(_baseObject.reference) else None
  }

  def setBaseObjectType(className: String, packageName: String) {
    _baseObject = new GaejObjectReferenceType(className, packageName)
  }

  def getBaseObjectType = {
    if (_baseObject != null) Some(_baseObject) else None
  }

  lazy val wholeAttributes: List[GaejAttribute] = {
    if (_baseObject != null) {
      _baseObject.reference.wholeAttributes ::: attributes.toList
    } else {
      attributes.toList
    }
  }

  lazy val isId = wholeAttributes.find(_.isId).isDefined
  lazy val idAttr = wholeAttributes.find(_.isId) match {
      case Some(attr) => attr
      case None => throw new UnsupportedOperationException("no id:" + name)
  }
  def idName = idAttr.name
  def idPolicy = idAttr.idPolicy

  def nameName: String = {
    getNameName match {
      case Some(name) => name
      case None => ""
    }
  }

  def getNameName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isName) return Some(attr.name)
    }
    for (attr <- attributes if !attr.isId) {
      if (attr.attributeType.isInstanceOf[GaejStringType]) {
        return Some(attr.name)
      }
    }
    None
  }

  //
  def isUser: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isUser) return true
    }
    false
  }

  def getUserName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isUser) return Some(attr.name)
    }
    None
  }

  def isTitle: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isTitle) return true
    }
    false
  }

  def getTitleName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isTitle) return Some(attr.name)
    }
    None
  }

  def isSubTitle: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isSubTitle) return true
    }
    false
  }

  def getSubTitleName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isSubTitle) return Some(attr.name)
    }
    None
  }

  def isSummary: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isSummary) return true
    }
    false
  }

  def getSummaryName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isSummary) return Some(attr.name)
    }
    None
  }

  def isCategory: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isCategory) return true
    }
    false
  }

  def getCategoryName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isCategory) return Some(attr.name)
    }
    None
  }

  def isAuthor: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isAuthor) return true
    }
    false
  }

  def getAuthorName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isAuthor) return Some(attr.name)
    }
    None
  }

  def isIcon: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isIcon) return true
    }
    false
  }

  def getIconName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isIcon) return Some(attr.name)
    }
    None
  }

  def isLogo: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isLogo) return true
    }
    false
  }

  def getLogoName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isLogo) return Some(attr.name)
    }
    None
  }

  def isLink: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isLink) return true
    }
    false
  }

  def getLinkName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isLink) return Some(attr.name)
    }
    None
  }

  def isContent: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isContent) return true
    }
    false
  }

  def getContentName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isContent) return Some(attr.name)
    }
    None
  }

  def isCreated: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isCreated) return true
    }
    false
  }

  def getCreatedName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isCreated) return Some(attr.name)
    }
    None
  }

  def isUpdated: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isUpdated) return true
    }
    false
  }

  def getUpdatedName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isUpdated) return Some(attr.name)
    }
    None
  }

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

/*
  final protected def java_type(anAttr: GaejAttribute) = {
    anAttr.kind match {
      case NullAttributeKind => anAttr.typeName
      case IdAttributeKind => {
        anAttr.idPolicy match {
          case SMAutoIdPolicy => "Long"
          case SMUserIdPolicy => "String"
        }
      }
      case NameAttributeKind => "String"
      case UserAttributeKind => "User"
      case TitleAttributeKind => "String"
      case CreatedAttributeKind => "Date"
      case UpdatedAttributeKind => "Date"
    }
  }
*/

  //
  final protected def attr_name(attr: GaejAttribute) = {
    gaejContext.attributeName(attr)
  }

  final protected def var_name(attr: GaejAttribute) = {
    gaejContext.variableName(attr)
  }

  final protected def doc_attr_name(attr: GaejAttribute) = {
    gaejContext.documentAttributeName(attr)
  }

  final protected def doc_var_name(attr: GaejAttribute) = {
    gaejContext.documentVariableName(attr)
  }

  final protected def entity_ref_assoc_var_name(attr: GaejAttribute) = {
    var_name(attr) + "_association"
  }

  final protected def entity_ref_part_var_name(attr: GaejAttribute) = {
    var_name(attr) + "_part"
  }

  final protected def entity_ref_powertype_var_name(attr: GaejAttribute) = {
    var_name(attr) + "_powertype"
  }

  //
  final protected def entity_ref_updated_var_name(attr: GaejAttribute) = {
    var_name(attr) + "_updated"
  }

  final protected def entity_ref_label_var_name(attr: GaejAttribute) = {
    var_name(attr) + "_label"
  }

  final protected def entity_ref_cache_var_name(attr: GaejAttribute) = {
    var_name(attr) + "_cache"
  }

  final protected def entity_ref_cache_timestamp_var_name(attr: GaejAttribute) = {
    var_name(attr) + "_cache_timestamp"
  }

/*
  final protected def java_type(anAttr: GaejAttribute) = {
    anAttr.typeName
  }

  final protected def java_element_type(anAttr: GaejAttribute) = {
    anAttr.elementTypeName
  }

  final protected def jdo_type(anAttr: GaejAttribute) = {
    anAttr.jdoTypeName
  }

  final protected def jdo_element_type(anAttr: GaejAttribute) = {
    anAttr.jdoElementTypeName
  }

  final protected def java_doc_type(anAttr: GaejAttribute) = {
    if (anAttr.isHasMany) {
      "List<" + java_doc_element_type(anAttr) + ">"
    } else {
      java_doc_element_type(anAttr)
    }
  }

  final protected def java_doc_element_type(anAttr: GaejAttribute) = {
    anAttr.attributeType match {
      case p: GaejEntityPartType => {
        p.part.documentName
      }
      case p: GaejPowertypeType => "String"
      case _ => java_element_type(anAttr)
    }
  }
*/

  final protected def make_entity_xmlString(buffer: JavaTextMaker) {
    buffer.method("public String entity_xmlString()") {
      buffer.makeStringBuilderVar();
      buffer.println("entity_asXmlString(buf);")
      buffer.makeStringBuilderReturn();
    }
  }

  final protected def make_entity_asXmlString(buffer: JavaTextMaker) {
    buffer.println()
    buffer.println("public void entity_asXmlString(StringBuilder buf) {")
    buffer.indentUp
    buffer.makeAppendString("<" + xmlElementName + " xmlns=\"" + xmlNamespace + "\">")
    for (attr <- attributes) {
      buffer.println(make_get_string_element(attr) + ";")
    }
    buffer.makeAppendString("</" + xmlElementName + ">")
    buffer.indentDown
    buffer.println("}")
  }

  final protected def make_get_string_element(attr: GaejAttribute): String = {
    if (attr.isHasMany) {
      "build_xml_element(\"" + attr.name + "\", " + make_get_string_list_property(attr) + ", buf)"
    } else {
      "build_xml_element(\"" + attr.name + "\", " + make_get_string_property(attr) + ", buf)"
    }
  }

  final protected def make_get_string_property(name: String) = {
    "get" + name.capitalize + "_asString()"
  }

  final protected def make_get_xml_string_property(name: String) = {
    "Util.escapeXmlText(" + make_get_string_property(name) + ")"
  }

  final protected def make_get_string_property(attr: GaejAttribute): String = {
    attr.attributeType match {
      case e: GaejEntityType => {
        make_get_string_property(gaejContext.variableName4RefId(attr))
      }
      case _ => make_get_string_property(attr.name)
    }
  }

  final protected def make_get_string_list_property(name: String) = {
    "get" + name.capitalize + "_asStringList()"
  }

  final protected def make_get_string_list_property(attr: GaejAttribute): String = {
    attr.attributeType match {
      case e: GaejEntityType => {
        make_get_string_list_property(gaejContext.variableName4RefId(attr))
      }
      case _ => make_get_string_list_property(attr.name)
    }
  }

  final protected def make_single_datatype_get_asString(attrName: String, expr: String, buffer: JavaTextMaker) {
    make_single_datatype_get_asString(attrName, attrName, expr, buffer)
  }

  final protected def make_single_datatype_get_asString(attrName: String, varName: String, expr: String, buffer: JavaTextMaker) {
    buffer.method("public String get" + attrName.capitalize + "_asString()") {
      buffer.makeReturn(expr)
    }
  }

  final protected def make_single_object_get_asString(attrName: String, expr: String, buffer: JavaTextMaker) {
    make_single_object_get_asString(attrName, attrName, expr, buffer)
  }

  final protected def make_single_object_get_asString(attrName: String, varName: String, expr: String, buffer: JavaTextMaker) {
    buffer.method("public String get" + attrName.capitalize + "_asString()") {
      buffer.makeIfElse(varName + " == null") {
        buffer.makeReturn("\"\"")
      }
      buffer.makeElse {
        buffer.makeReturn(expr)
      }
    }
  }

  final protected def make_multi_get_asString(attrName: String, varName: String, typeName: String, expr: String, buffer: JavaTextMaker) {
    buffer.method("public String get" + attrName.capitalize + "_asString()") {
      buffer.makeIf(varName + ".isEmpty()") {
        buffer.makeReturn("\"\"")
      }
      buffer.makeVar("buf", "StringBuilder")
      buffer.makeVar("last", typeName, varName + ".get(" + varName + ".size() - 1)")
      buffer.makeFor(typeName + " elem: " + varName) {
        buffer.makeAppendExpr(expr)
        buffer.makeIf("elem != last") {
          buffer.makeAppendString(", ")
        }
      }
      buffer.makeReturn("buf.toString()")
    }
  }

  final protected def make_multi_get_asStringIndex(attrName: String, varName: String, typeName: String, expr: String, buffer: JavaTextMaker) {
    buffer.method("public String get" + attrName.capitalize + "_asString(int index)") {
      buffer.makeIfElse(varName + ".size() <= index") {
        buffer.makeReturn("\"\"")
      }
      buffer.makeElse {
        buffer.makeVar("elem", typeName, varName + ".get(index)")
        if ("String".equals(typeName)) {
          buffer.makeReturn(expr)
        } else {
          buffer.makeReturn("Util.datatype2string(%s)".format(expr))
        }
      }
    }
  }

  final protected def make_multi_get_asStringList(attrName: String, varName: String, typeName: String, expr: String, buffer: JavaTextMaker) {
    buffer.method("public List<String> get" + attrName.capitalize + "_asStringList()") {
      buffer.makeVar("list", "List<String>", "new ArrayList<String>()")
      buffer.makeFor(typeName + " elem: " + varName) {
        buffer.print("list.add(")
        if ("String".equals(typeName)) {
          buffer.print(expr)
        } else {
          buffer.print("Util.datatype2string(%s)".format(expr))
        }
        buffer.println(");")
      }
      buffer.println("return list;")
    }
  }

  final protected def make_multi_get_asString_methods(attrName: String, typeName: String, expr: String, buffer: JavaTextMaker) {
    make_multi_get_asString_methods(attrName, attrName, typeName, expr, buffer)
  }

  final protected def make_multi_get_asString_methods(attrName: String, varName: String, typeName: String, expr: String, buffer: JavaTextMaker) {
    make_multi_get_asString(attrName, varName, typeName, expr, buffer)
    make_multi_get_asStringIndex(attrName, varName, typeName, expr, buffer)
    make_multi_get_asStringList(attrName, varName, typeName, expr, buffer)
  }

  final protected def make_build_xml_element(buffer: JavaTextMaker) {
    buffer.println
    buffer.method("private void build_xml_element(String name, String value, StringBuilder buf)") {
      buffer.makeAppendString("<")
      buffer.makeAppendExpr("name")
      buffer.makeAppendString(">")
      buffer.makeAppendExpr("Util.escapeXmlText(value)")
      buffer.makeAppendString("</")
      buffer.makeAppendExpr("name")
      buffer.makeAppendString(">")
    }
    buffer.println
    buffer.method("private void build_xml_element(String name, List<String> values, StringBuilder buf)") {
      buffer.makeFor("String value: values") {
        buffer.println("build_xml_element(name, value, buf);")
      }
    }
    buffer.println
    buffer.method("private void build_xml_element(String name, String[] values, StringBuilder buf)") {
      buffer.makeFor("String value: values") {
        buffer.println("build_xml_element(name, value, buf);")
      }
    }
  }
}
