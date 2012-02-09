package org.simplemodeling.SimpleModeler.entities

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity.SMPackage

/*
 * @since   Jun.  6, 2011
 *  version Aug. 13, 2011
 * @version Feb.  9, 2012
 * @author  ASAMI, Tomoharu
 */
class JavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity,
  maker: JavaMaker = null
) extends GenericClassDefinition(pContext, aspects, pobject) with JavaMakerHolder {
  if (maker == null) {
    jm_open(aspects)
  } else {
    jm_open(maker, aspects)
  }

  private lazy val _builder = new BuilderJavaClassDefinition(pContext, Nil, pobject, jm_maker)

  override def toText = {
    jm_to_text
  }

  override protected def attribute(attr: PAttribute): GenericClassAttributeDefinition = {
    new JavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker)
  }

  override protected def pln() {
    jm_pln()
  }

  override protected def head_package {
    require(packageName != null)
    if (packageName != "") {
      jm_package(packageName)
    }
    jm_end_package_section()
  }

  override protected def head_imports_Prologue {
    jm_import("java.util.*")
    jm_import("java.math.*")
    if (isSingleton) {
      head_imports_Inject_Singleton
    }
  }

  override protected def head_imports_Object(anObject: PObjectReferenceType) {
    if (packageName != anObject.packageName) {
      jm_import(anObject.qualifiedName)
    }
  }

  override protected def head_imports_Entity(anEntity: PEntityType) {
    if (packageName != anEntity.packageName) {
      jm_import(anEntity.qualifiedName)
    }
  }

  override protected def head_imports_Epilogue() {
    jm_end_import_section()
  }

  protected def head_imports_Inject_Singleton {
    jm_import("javax.inject.Singleton")
    // jm_import("com.google.inject.Singleton")
  }

  protected def head_imports_Inject_Inject {
    jm_import("javax.inject.Inject")
    // jm_import("com.google.inject.Inject")
  }

  override protected def head_imports_Builder {
    _builder.includeImports
  }

  override protected def class_open_body {
    if (isSingleton) {
      jm_pln("@Singleton")
    }
    jm_p("public ")
    if (isStatic) {
      jm_p("static ")
    }
    if (isInterface) {
      jm_p("interface ")
    } else {
      jm_p("class ")
    }
    jm_p(name)
    for (base <- customBaseName orElse baseObject.map(_.name)) {
      jm_p(" extends ")
      jm_p(base)
    }
    if (!customImplementNames.isEmpty) {
      jm_p(" implements ")
      jm_p(customImplementNames.mkString(", "))
    }
/*
    baseObject match {
      case Some(base) => {
        jm_p(" extends ")
        jm_p(base.name)
      }
      case None => {}
    }
*/
    jm_pln(" {")
    jm_indent_up
  }

  override protected def class_close_body {
    jm_indent_down
    jm_pln("}")
  }

  override protected def constructors_null_constructor {
    if (!attributeDefinitions.filter(!_.isInject).isEmpty) {
      jm_public_constructor("%s()", name) {
      }
    }
  }

  override protected def constructors_copy_constructor {
    jm_public_constructor("%s(%s o)", name, name) {
      for (a <- attributeDefinitions) {
        if (aspects.find(_.weaveCopyConstructorAttributeBlock(a.attr, a.varName, "o")).isDefined) {}
        else jm_assign_this(a.varName, "o." + a.varName)
      }
    }
  }

  override protected def constructors_plain_constructor {
    val params = attributeDefinitions.filter(!_.isInject).
      map(a => a.javaType + " " + a.paramName).mkString(", ")
    jm_public_constructor("%s(%s)", name, params) {
      for (a <- attributeDefinitions) {
        if (aspects.find(_.weavePlainConstructorAttributeBlock(a.attr, a.varName, a.paramName)).isDefined) {}
        else jm_assign_this(a.varName, a.paramName)
      }
    }
  }

  protected final def constructors_plain_constructor_for_document {
    val params = attributeDefinitions.filter(!_.isInject).
      map(a => {
        val typename = a.attr.attributeType match {
          case t: PEntityType => {
            pContext.entityDocumentName(t.entity.modelEntity)
          }
          case _ => a.javaType
        }
        typename + " " + a.paramName 
      }).mkString(", ")
    jm_public_constructor("%s(%s)", name, params) {
      for (a <- attributeDefinitions) {
        jm_assign_this(a.varName, a.paramName)
      }
    }
  }

/*
  override protected def constructors_plain_constructor {
    val params = attributeDefinitions.filter(!_.isInject).
      map(a => a.javaType + " " + a.paramName).mkString(", ")
    if (!params.isEmpty) {
      jm_public_constructor("%s(%s)", name, params) {
        for (a <- attributeDefinitions) {
          if (aspects.find(_.weavePlainConstructorAttributeBlock(a.attr, a.varName, a.paramName)).isDefined) {}
          else jm_assign_this(a.varName, a.paramName)
        }
      }
    }
  }

  protected final def constructors_plain_constructor_for_document {
    val params = attributeDefinitions.filter(!_.isInject).
      map(a => {
        val typename = a.attr.attributeType match {
          case t: PEntityType => {
            pContext.entityDocumentName(t.entity.modelEntity)
          }
          case _ => a.javaType
        }
        typename + " " + a.paramName 
      }).mkString(", ")
    if (!params.isEmpty) {
      jm_public_constructor("%s(%s)", name, params) {
        for (a <- attributeDefinitions) {
          jm_assign_this(a.varName, a.paramName)
        }
      }
    }
  }
*/

  /*
   * to_methods
   */
  override protected def to_methods_string {
    jm_public_method("String toString()") {
      jm_return("toJson()")
    }
  }

  override protected def to_methods_xml {
    jm_public_method("String toXml()") {
      jm_var_new_StringBuilder
      jm_pln("toXml(buf);")
      jm_return_StringBuilder
    }
    jm_public_method("void toXml(StringBuilder buf)") {
      jm_append_String("""<%s xmlns="%s">""", xmlElementName, xmlNamespace)
      for (a <- attributeDefinitions) {
        if (a.isSystemType) {
          jm_pln("""to_xml(buf, "%s", %s);""", a.xmlElementName, a.varName)
        } else {
          jm_if_not_null(a.varName) {
            jm_pln("%s.toXml(buf);", _var_name(a))
          }
        }
      }
      jm_append_String("</%s>", xmlElementName)
    }
  }

  private def _var_name(attr: GenericClassAttributeDefinition): String = {
    _var_name(attr.attr, attr.varName)
  }

  private def _var_name(attr: PAttribute, varName: String): String = {
    aspects.flatMap(_.objectVarName(attr, varName)).headOption getOrElse varName
  }

  override protected def to_methods_json {
    jm_public_method("String toJson()") {
      jm_var_new_StringBuilder
      jm_pln("toJson(buf);")
      jm_return_StringBuilder
    }
    jm_public_method("void toJson(StringBuilder buf)") {
      jm_append_String("{", term)
      var cont = false
      for (a <- attributeDefinitions) {
        if (cont) jm_append_String(", ")
        else cont = true
        if (a.isSystemType) {
          jm_pln("""to_json(buf, %s, %s);""", a.propertyConstantName, a.varName)
        } else {
          jm_pln("""to_json(buf, %s, %s.toJson());""", a.propertyConstantName, _var_name(a))
        }
      }
      jm_append_String("}", term)
    }
  }

  override protected def to_methods_csv {
    jm_public_method("String toCsv()") {
      jm_var_new_StringBuilder
      jm_pln("toCsv(buf);")
      jm_return_StringBuilder
    }
    jm_public_method("void toCsv(StringBuilder buf)") {
      jm_append_String("{", term)
      var cont = false
      for (a <- attributeDefinitions) {
        if (cont) jm_append_String(", ")
        else cont = true
        if (a.isSystemType) {
          jm_pln("""to_json(buf, %s, %s);""", a.propertyConstantName, a.varName)
        } else {
          jm_pln("""to_json(buf, %s, %s.toJson());""", a.propertyConstantName, _var_name(a))
        }
      }
      jm_append_String("}", term)
    }
  }

  override protected def to_methods_yaml {
    jm_public_method("String toYaml()") {
      jm_var_new_StringBuilder
      jm_pln("toYaml(buf);")
      jm_return_StringBuilder
    }
    jm_public_method("void toYaml(StringBuilder buf)") {
      jm_append_String("{", term)
      var cont = false
      for (a <- attributeDefinitions) {
        if (cont) jm_append_String(", ")
        else cont = true
        if (a.isSystemType) {
          jm_pln("""to_json(buf, %s, %s);""", a.propertyConstantName, a.varName)
        } else {
          jm_pln("""to_json(buf, %s, %s.toJson());""", a.propertyConstantName, _var_name(a))
        }
      }
      jm_append_String("}", term)
    }
  }

  override protected def to_methods_map {
    jm_public_method("Map<String, Object> toMap()") {
      jm_var("Map<String, Object>", "r", "new LinkedHashMap<String, Object>()")
      for (a <- attributeDefinitions) {
        jm_pln("""to_map(r, %s, %s);""", a.propertyConstantName, a.varName) 
      }
      jm_return("r")
    }
    jm_public_method("Map<String, String> toStringMap()") {
      jm_var("Map<String, String>", "r", "new LinkedHashMap<String, String>()")
      for (a <- attributeDefinitions) {
        if (a.isSystemType) {
          jm_pln("""to_string_map(r, %s, %s);""", a.propertyConstantName, a.varName)
        } else {
          jm_pln("""to_string_map(r, %s, %s.toJson());""", a.propertyConstantName, _var_name(a))
        }
      }
      jm_return("r")
    }
  }

  /*
   * update_methods
   */
  override protected def update_methods_string {
    jm_public_method("void update_string(String string)") {
    }
  }

  override protected def update_methods_xml {
    jm_public_method("void update_xml(String xml)") {
    }
  }

  override protected def update_methods_json {
    jm_public_method("void update_json(String json)") {
    }
  }

  override protected def update_methods_csv {
    jm_public_method("void update_csv(String csv)") {
    }
  }

  override protected def update_methods_yaml {
    jm_public_method("void update_yaml(String yaml)") {
    }
  }

  override protected def update_methods_urlencode {
    jm_public_method("void update_urlencode(String url)") {
    }
  }

  override protected def update_methods_map {
    jm_public_method("void update_map(Map<String, Object> map)") {
    }
  }

  /*
   * object_methods
   */
  override protected def object_methods_hashcode {
    jm_public_method("int hashCode()") {
      jm_p("return ")
      var cont = false
      attributeDefinitions match {
        case Nil => jm_pln("0;")
        case List(a) => jm_pln("hash_code(%s);", a.varName)
        case a :: tail => {
          jm_p("hash_code(%s)", a.varName)
          for (aa <- tail) {
            jm_pln
            jm_p("+ hash_code(%s)", aa.varName)
          }
          jm_pln(";")
        }
      }
    }
  }

  override protected def object_methods_equals {
    def isequals(a: GenericClassAttributeDefinition) {
      jm_p("is_equals(oo.%s, this.%s)", a.varName, a.varName)      
//      if (a.isSystemType) {
//          jm_p("is_equals(oo.%s, this.%s)", a.varName, a.varName)
//        } else {
//          jm_p("oo.%s == null ? this.%s == null : oo.%s.equals(this.%s)", a.varName, a.varName, a.varName, a.varName)
//        }      
    }

    jm_public_method("boolean equals(Object o)") {
      jm_if_return_expr("!(o instanceof %s)", name)("false")
      jm_var(name, "oo", "(%s)o", name)
      jm_p("return ")
      var cont = false
      attributeDefinitions match {
        case Nil => jm_pln("true;")
        case List(a) => {
          isequals(a)
          jm_pln(";")
        }
        case a :: tail => {
          isequals(a)
          for (aa <- tail) {
            jm_pln
            jm_p("&& ")
            isequals(aa)
          }
          jm_pln(";")
        }
      }
    }
  }

  override protected def document_methods_make {
    def attribute_plain(attr: GenericClassAttributeDefinition) {
      attr.method_document_plain
    }

    def attribute_shallow(attr: GenericClassAttributeDefinition) {
      attr.method_document_shallow
    }

    def document_common(methodName: String, makeAttr: GenericClassAttributeDefinition => Unit) {
      jm_public_method("%s %s()".format(documentName, methodName)) {
        jm_var(documentName + ".Builder", "doc", "%s.Builder()", documentName)
        jm_pln("doc.%s = get%s();", idName, idName.capitalize)
        for (a <- attributeDefinitions if !a.attr.isId) {
          makeAttr(a)
        }
        jm_return("doc.build()")
      }
    }

    def document_plain {
      document_common("make_document", attribute_plain)
    }

    def document_shallow {
      document_common("make_document_shallow", attribute_shallow)
    }

    document_plain
    document_shallow
  }

  override protected def document_methods_update {
    jm_public_void_method("update_document(%s doc)", documentName) {
      jm_if_not_null(idName) {
        jm_if_not_equals_expr("doc.%s", idName)("get%s()", idName.capitalize) {
          jm_pln("throw new IllegalArgumentException(\"Invalid id\");")
        }
      }
      for (a <- attributeDefinitions if !a.attr.isId) {
        a.document_methods_update_attribute
      }
    }
  }

  override protected def utilities {
    if (isValueEquality || isData) { 
      jm_code("""// Utility methods
protected final int hash_code(Object value) {
    return value == null ? 0 : value.hashCode();
}

protected final boolean is_equals(boolean lhs, boolean rhs) {
    return lhs == rhs;
}

protected final boolean is_equals(byte lhs, byte rhs) {
    return lhs == rhs;
}

protected final boolean is_equals(short lhs, short rhs) {
    return lhs == rhs;
}

protected final boolean is_equals(int lhs, int rhs) {
    return lhs == rhs;
}

protected final boolean is_equals(long lhs, long rhs) {
    return lhs == rhs;
}

protected final boolean is_equals(float lhs, float rhs) {
    return lhs == rhs;
}

protected final boolean is_equals(double lhs, double rhs) {
    return lhs == rhs;
}

protected final boolean is_equals(Object lhs, Object rhs) {
    if (lhs == null) return rhs == null;
    else return lhs.equals(rhs);
}

protected final void to_string(StringBuilder buf, String value) {
    buf.append(value);
}

protected final void to_string(StringBuilder buf, Object value) {
    buf.append(to_string(value));
}

protected final void to_string(StringBuilder buf, Date value) {
    buf.append(to_string(value));
}

private String to_string(Object value) {
    return value.toString();
}

protected final void to_xml(StringBuilder buf, String name, String value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
}
/*
protected final void to_xml(StringBuilder buf, String name, boolean value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
} 

protected final void to_xml(StringBuilder buf, String name, byte value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
} 

protected final void to_xml(StringBuilder buf, String name, short value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
} 

protected final void to_xml(StringBuilder buf, String name, int value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
} 

protected final void to_xml(StringBuilder buf, String name, long value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
} 

protected final void to_xml(StringBuilder buf, String name, float value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
} 

protected final void to_xml(StringBuilder buf, String name, double value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
} 
*/
protected final void to_xml(StringBuilder buf, String name, Date value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
} 

protected final void to_xml(StringBuilder buf, String name, Object value) {
    if (value == null) return;
    buf.append("<"); 
    buf.append(name); 
    buf.append(">"); 
    to_string(buf, value);
    buf.append("</"); 
    buf.append(name); 
    buf.append(">");
} 
    
protected final void to_xml(StringBuilder buf, String name, List<String> values) {
    if (values == null) return;
    for (String v: values) {
        to_xml(buf, name, v);
    }
} 

protected final void to_json(StringBuilder buf, String name, String value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":"); 
    buf.append("\""); 
    to_string(buf, value); 
    buf.append("\""); 
} 

/*
protected final void to_json(StringBuilder buf, String name, boolean value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":"); 
    buf.append("\""); 
    to_string(buf, value); 
    buf.append("\""); 
} 

protected final void to_json(StringBuilder buf, String name, byte value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":"); 
    buf.append("\""); 
    to_string(buf, value); 
    buf.append("\""); 
} 

protected final void to_json(StringBuilder buf, String name, short value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":"); 
    buf.append("\""); 
    to_string(buf, value); 
    buf.append("\""); 
} 

protected final void to_json(StringBuilder buf, String name, int value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":"); 
    buf.append("\""); 
    to_string(buf, value); 
    buf.append("\""); 
} 

protected final void to_json(StringBuilder buf, String name, long value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":"); 
    buf.append("\""); 
    to_string(buf, value); 
    buf.append("\""); 
} 

protected final void to_json(StringBuilder buf, String name, float value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":"); 
    buf.append("\""); 
    to_string(buf, value); 
    buf.append("\""); 
} 

protected final void to_json(StringBuilder buf, String name, double value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":"); 
    buf.append("\""); 
    to_string(buf, value); 
    buf.append("\""); 
} 
*/
protected final void to_json(StringBuilder buf, String name, Object value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":"); 
    buf.append("\""); 
    to_string(buf, value); 
    buf.append("\""); 
} 

protected final void to_json(StringBuilder buf, String name, Date value) {
    if (value == null) return;
    buf.append(name); 
    buf.append(":");
    buf.append("\"");
    to_string(buf, value); 
    buf.append("\""); 
} 
    
protected final void to_json(StringBuilder buf, String name, List<String> values) {
    if (values == null) return;
    buf.append(name);
    buf.append(": [");
    boolean first = true;
    for (String v: values) {
        if (!first) {
            first = false;
            buf.append(", ");
        }
        buf.append("\"");
        to_string(buf, v);
        buf.append("\"");
    }
    buf.append("]");
} 

protected final void to_map(Map<String, Object> map, String name, Object value) {
    if (value == null) return;
    map.put(name, value);
}

protected final void to_string_map(Map<String, String> map, String name, Object value) {
    if (value == null) return;
    map.put(name, to_string(value));
}
""")
    }
  }

  override protected def builder_copy_factory {
    jm_public_method("Builder builder()") {
      jm_return("new Builder(this)");
    }
  }

  override protected def builder_new_factory {
    jm_public_static_method("Builder Builder()") {
      jm_return("new Builder()");
    }
  }

  override protected def builder_class {
    _builder.build
  }

  override protected def builder_auxiliary {
  }

  //

  // XXX Java
  /*
   * Utility methods
   */
  final protected def util_entity_xmlString {
    jm_public_method("String entity_xmlString()") {
      jm_var_new_StringBuilder
      jm_pln("entity_asXmlString(buf);")
      jm_return_StringBuilder
    }
  }

  final protected def util_entity_asXmlString {
    jm_public_method("void entity_asXmlString(StringBuilder buf)") {
      jm_append_String("<" + xmlElementName + " xmlns=\"" + xmlNamespace + "\">")
      for (attr <- attributes) {
        jm_pln(util_get_string_element(attr) + ";")
      }
      jm_append_String("</" + xmlElementName + ">")
    }
  }

  final protected def util_get_string_element(attr: PAttribute): String = {
    if (attr.isHasMany) {
      "build_xml_element(\"" + attr.name + "\", " + util_get_string_list_property(attr) + ", buf)"
    } else {
      "build_xml_element(\"" + attr.name + "\", " + util_get_string_property(attr) + ", buf)"
    }
  }

  final protected def util_get_string_property(name: String) = {
    "get" + name.capitalize + "_asString()"
  }

  final protected def util_get_xml_string_property(name: String) = {
    "Util.escapeXmlText(" + util_get_string_property(name) + ")"
  }

  final protected def util_get_string_property(attr: PAttribute): String = {
    attr.attributeType match {
      case e: PEntityType => {
        util_get_string_property(pContext.variableName4RefId(attr))
      }
      case _ => util_get_string_property(attr.name)
    }
  }

  final protected def util_get_string_list_property(name: String) = {
    "get" + name.capitalize + "_asStringList()"
  }

  final protected def util_get_string_list_property(attr: PAttribute): String = {
    attr.attributeType match {
      case e: PEntityType => {
        util_get_string_list_property(pContext.variableName4RefId(attr))
      }
      case _ => util_get_string_list_property(attr.name)
    }
  }

  final protected def util_single_datatype_get_asString(attrName: String, expr: String) {
    util_single_datatype_get_asString(attrName, attrName, expr)
  }

  final protected def util_single_datatype_get_asString(attrName: String, varName: String, expr: String) {
    jm_method("public String get" + attrName.capitalize + "_asString()") {
      jm_return(expr)
    }
  }

  final protected def util_single_object_get_asString(attrName: String, expr: String) {
    util_single_object_get_asString(attrName, attrName, expr)
  }

  final protected def util_single_object_get_asString(attrName: String, varName: String, expr: String) {
    jm_public_method("String get" + attrName.capitalize + "_asString()") {
      jm_if_else_null(varName) {
        jm_return("\"\"")
      }
      jm_else {
        jm_return(expr)
      }
    }
  }

  final protected def util_multi_get_asString(attrName: String, varName: String, typeName: String, expr: String) {
    jm_public_method("String get" + attrName.capitalize + "_asString()") {
      jm_if(varName + ".isEmpty()") {
        jm_return("\"\"")
      }
      jm_var_new_StringBuilder
      jm_var(typeName, "last", varName + ".get(" + varName + ".size() - 1)")
      jm_for(typeName + " elem: " + varName) {
        jm_append_expr(expr)
        jm_if("elem != last") {
          jm_append_String(", ")
        }
      }
      jm_return_StringBuilder
    }
  }

  final protected def util_multi_get_asStringIndex(attrName: String, varName: String, typeName: String, expr: String) {
    jm_public_method("String get" + attrName.capitalize + "_asString(int index)") {
      jm_if_else(varName + ".size() <= index") {
        jm_return("\"\"")
      }
      jm_else {
        jm_var(typeName, "elem", varName + ".get(index)")
        if ("String".equals(typeName)) {
          jm_return(expr)
        } else {
          jm_return("Util.datatype2string(%s)".format(expr))
        }
      }
    }
  }

  final protected def util_multi_get_asStringList(attrName: String, varName: String, typeName: String, expr: String) {
    jm_public_method("List<String> get" + attrName.capitalize + "_asStringList()") {
      jm_var_List_new_ArrayList("String", "list")
      jm_for(typeName + " elem: " + varName) {
        jm_p("list.add(")
        if ("String".equals(typeName)) {
          jm_p(expr)
        } else {
          jm_p("Util.datatype2string(%s)".format(expr))
        }
        jm_pln(");")
      }
      jm_pln("return list;")
    }
  }

  final protected def util_multi_get_asString_methods(attrName: String, typeName: String, expr: String) {
    util_multi_get_asString_methods(attrName, attrName, typeName, expr)
  }

  final protected def util_multi_get_asString_methods(attrName: String, varName: String, typeName: String, expr: String) {
    util_multi_get_asString(attrName, varName, typeName, expr)
    util_multi_get_asStringIndex(attrName, varName, typeName, expr)
    util_multi_get_asStringList(attrName, varName, typeName, expr)
  }

  final protected def util_build_xml_element() {
    jm_private_method("void build_xml_element(String name, String value, StringBuilder buf)") {
      jm_append_String("<")
      jm_append_expr("name")
      jm_append_String(">")
      jm_append_expr("Util.escapeXmlText(value)")
      jm_append_String("</")
      jm_append_expr("name")
      jm_append_String(">")
    }
    jm_private_method("void build_xml_element(String name, List<String> values, StringBuilder buf)") {
      jm_for("String value: values") {
        jm_pln("build_xml_element(name, value, buf);")
      }
    }
    jm_private_method("void build_xml_element(String name, String[] values, StringBuilder buf)") {
      jm_for("String value: values") {
        jm_pln("build_xml_element(name, value, buf);")
      }
    }
  }
}
