package org.simplemodeling.SimpleModeler.entities

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity.SMPackage

/*
 * @since   Jun.  6, 2011
 *  version Aug. 13, 2011
 *  version Oct. 30, 2012
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
class JavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity,
  maker: JavaMaker = null
) extends GenericClassDefinition(pContext, aspects, pobject) with JavaMakerHolder with JavaClassCodeUtils {
  type ATTR_DEF = JavaClassAttributeDefinition
  type OP_DEF = JavaClassOperationDefinition

  /**
   * References to entity are converted to documents of entity.
   */
  def isDocument = false

  if (maker == null) {
    jm_open(aspects)
  } else {
    jm_open(maker, aspects)
  }
  aspects.foreach(_.openJavaClass(this))

  private lazy val _builder = new BuilderJavaClassDefinition(pContext, Nil, pobject, jm_maker)

  override def toText = {
    jm_to_text
  }

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new JavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker)
  }

  override protected def operation(op: POperation): OP_DEF = {
    new JavaClassOperationDefinition(pContext, aspects, op, this, jm_maker)
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
    jm_import("org.simplemodeling.SimpleModeler.runtime.USimpleModeler")
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
    jm_p(classifierKind.keyword)
    jm_p(" ")
    jm_p(name)
    for (base <- customBaseName orElse baseObject.map(_.name)) {
      jm_p(" extends ")
      jm_p(base)
    }
    val is = implements_interfaces
    if (!is.isEmpty) {
      jm_p(" implements ")
      jm_p(is.mkString(", "))
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

  protected def implements_interfaces: Seq[String] = {
//    println("JavaClassDefinition#_implements_interfaces: " + pobject.getTraitNames)
    customImplementNames ++ pobject.getTraitNames
  }

  override protected def class_close_body {
    jm_indent_down
    jm_pln("}")
  }

  override protected def constructors_null_constructor {
    val attrs = not_derived_whole_attribute_definitions
    if (!attrs.filter(!_.isInject).isEmpty) {
      jm_public_constructor("%s()", name) {
        if (hasBaseObject) {
          jm_pln("super();")
        }
      }
    }
  }

  override protected def constructors_copy_constructor {
    jm_public_constructor("%s(%s o)", name, name) {
      if (hasBaseObject) {
        jm_pln("super(o);")
      }
      for (a <- not_derived_implements_attribute_definitions) {
        record_trace("JavaClassDefinition#constructors_copy_constructor(%s): %s".format(name, a.attr.name))
        if (aspects.find(_.weaveCopyConstructorAttributeBlock(a.attr, a.varName, "o")).isDefined) {}
        else jm_assign_this(a.varName, "o." + a.varName)
      }
    }
  }

  override protected def constructors_plain_constructor {
//    println("JavaClassDefinition#constructors_plain_constructor(%s) = %s".format(this.name, not_derived_whole_attribute_definitions.map(_.attr.name)))
    val params = not_derived_whole_attribute_definitions.filter(!_.isInject).
    map(a => a.javaType + " " + a.paramName).mkString(", ")
    jm_public_constructor("%s(%s)", name, params) {
      if (hasBaseObject) {
        val vs = not_derived_parent_attribute_definitions.filter(!_.isInject).
          map(a => a.paramName).mkString(", ")
        jm_pln("super(%s);", vs)
      }
      for (a <- not_derived_implements_attribute_definitions) {
        if (aspects.find(_.weavePlainConstructorAttributeBlock(a.attr, a.varName, a.paramName)).isDefined) {}
        else jm_assign_this(a.varName, a.paramName)
      }
    }
  }

  override protected def constructors_doc_constructor {
    jm_public_constructor("%s(%s o)", name, documentName) {
      if (hasBaseObject) {
        jm_pln("super(o);")
      }
      for (a <- not_derived_implements_attribute_definitions) {
        if (aspects.find(_.weaveDocConstructorAttributeBlock(a.attr, a.varName, "o")).isDefined) {}
        else {
          val in = "o." + a.varName
          if (a.attr.isMulti) {
            a.attr.attributeType match {
              case t: PEntityType => {
                jm_for(t.entity.documentName, "elem", in) {
                  jm_pln("%s.add(new %s(elem));", a.varName, t.name)
                }
              }
              case _ => jm_assign_this(a.varName, in)
            }
          } else {
            val rhs = a.attr.attributeType match {
              case t: PEntityType => {
                "%s != null ? new %s(%s) : null".format(in, t.name, in)
              }
              case _ => in
            }
            jm_assign_this(a.varName, rhs)
          }
        }
      }
    }
  }

  protected final def constructors_plain_constructor_for_document {
    val params = not_derived_whole_attribute_definitions.filter(!_.isInject).
    map(a => {
      val typename = if (a.isMulti) {
        a.attr.attributeType match {
          case t: PEntityType => {
            val b = pContext.entityDocumentName(t.entity.modelEntity)
            "List<" + b + ">"
          }
          case _ => a.javaType
        }
      } else {
        a.attr.attributeType match {
          case t: PEntityType => {
            pContext.entityDocumentName(t.entity.modelEntity)
          }
          case _ => a.javaType
        }
      }
      typename + " " + a.paramName 
    }).mkString(", ")
    jm_mark("// JavaClassDefinition#constructors_plain_constructor_for_document")
    jm_public_constructor("%s(%s)", name, params) {
      if (hasBaseObject) {
        val vs = not_derived_parent_attribute_definitions.filter(!_.isInject).
          map(a => a.paramName).mkString(", ")
        jm_pln("super(%s);", vs)
      }
      for (a <- not_derived_implements_attribute_definitions) {
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
   * operation
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
      jm_pln("toXmlElement(buf);")
      jm_return_StringBuilder
    }
    jm_public_method("void toXmlElement(StringBuilder buf)") {
      jm_append_String("""<%s xmlns="%s">""", xmlElementName, xmlNamespace)
      jm_pln("toXmlContent(buf);")
      jm_append_String("</%s>", xmlElementName)
    }
    jm_public_method("void toXmlContent(StringBuilder buf)") {
      if (hasBaseObject) {
        jm_pln("super.toXmlContent(buf);")
      }
      attributeDefinitions.foreach(_.ext_to_xml)
    }
  }

  override protected def to_methods_json {
    jm_public_method("String toJson()") {
      jm_var_new_StringBuilder
      jm_pln("toJsonElement(buf);")
      jm_return_StringBuilder
    }
    jm_public_method("void toJsonElement(StringBuilder buf)") {
      jm_append_String("{", term)
      jm_pln("toJsonContent(buf);")
      jm_append_String("}", term)
    }
    jm_public_method("void toJsonContent(StringBuilder buf)") {
      if (hasBaseObject) {
        jm_pln("super.toJsonContent(buf);")
      }
      var cont = false
      for (a <- attributeDefinitions) {
        if (cont) jm_append_String(", ")
        else cont = true
        a.ext_to_json
      }
    }
  }

  override protected def to_methods_csv {
    jm_public_method("String toCsv()") {
      jm_var_new_StringBuilder
      jm_pln("toCsvElement(buf);")
      jm_return_StringBuilder
    }
    jm_public_method("void toCsvElement(StringBuilder buf)") {
      jm_pln("toCsvContent(buf);")
      jm_append_String("\n", term)
    }
    jm_public_method("void toCsvContent(StringBuilder buf)") {
      if (hasBaseObject) {
        jm_pln("super.toCsvContent(buf);")
      }
      var cont = false
      for (a <- attributeDefinitions) {
        if (cont) jm_append_String(", ")
        else cont = true
        a.ext_to_csv
      }
    }
  }

  override protected def to_methods_yaml {
    jm_public_method("String toYaml()") {
      jm_var_new_StringBuilder
      jm_pln("toYamlElement(buf);")
      jm_return_StringBuilder
    }
    jm_public_method("void toYamlElement(StringBuilder buf)") {
      jm_append_String("{", term)
      jm_pln("toYamlContent(buf);")
      jm_append_String("}", term)
    }
    jm_public_method("void toYamlContent(StringBuilder buf)") {
      if (hasBaseObject) {
        jm_pln("super.toYamlContent(buf);")
      }
      var cont = false
      for (a <- attributeDefinitions) {
        if (cont) jm_append_String(", ")
        else cont = true
        a.ext_to_yaml
      }
    }
  }

  override protected def to_methods_map {
    jm_public_method("Map<String, Object> toMap()") {
      jm_var("Map<String, Object>", "r", "new LinkedHashMap<String, Object>()")
      jm_pln("toMapContent(r);")
      jm_return("r")
    }
    jm_public_method("void toMapContent(Map r)") {
      if (hasBaseObject) {
        jm_pln("super.toMapContent(r);")
      }
      for (a <- attributeDefinitions) {
        jm_pln("""USimpleModeler.toMap(r, %s, %s);""", a.propertyConstantName, code_get_value(a)) 
      }
    }
    jm_public_method("Map<String, String> toStringMap()") {
      jm_var("Map<String, String>", "r", "new LinkedHashMap<String, String>()")
      jm_pln("toStringMapContent(r);")
      jm_return("r")
    }
    jm_public_method("void toStringMapContent(Map r)") {
      if (hasBaseObject) {
        jm_pln("super.toMapContent(r);")
      }
      for (a <- attributeDefinitions) {
        a.ext_to_map
      }
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
        case List(a) => jm_pln("USimpleModeler.toHashCode(%s);", code_get_value(a))
        case a :: tail => {
          jm_p("USimpleModeler.toHashCode(%s)", code_get_value(a))
          for (aa <- tail) {
            jm_pln
            jm_p("+ USimpleModeler.toHashCode(%s)", code_get_value(aa))
          }
          jm_pln(";")
        }
      }
    }
  }

  override protected def object_methods_equals {
    def isequals(a: GenericClassAttributeDefinition) {
      jm_p("USimpleModeler.isEquals(oo.%s, this.%s)", code_get_value(a), code_get_value(a))      
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
        if (isId) { // e.g. except Part
          jm_pln("doc.%s = get%s();", idName, idName.capitalize)
        }
        for (a <- wholeAttributeDefinitions if !a.attr.isId) {
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
/* XXX inheritance issue
      if (isId) { // e.g. except Part
        jm_if_not_null(idName) {
          jm_if_not_equals_expr("doc.%s", idName)("get%s()", idName.capitalize) {
            jm_pln("throw new IllegalArgumentException(\"Invalid id\");")
          }
        }
      }
*/
      for (a <- attributeDefinitions if !a.attr.isId) {
        a.document_methods_update_attribute
      }
    }
  }

/*
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
*/
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
}
