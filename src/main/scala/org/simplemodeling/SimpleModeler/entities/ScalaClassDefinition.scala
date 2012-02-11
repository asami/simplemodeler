package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity.SMPackage

/*
 * @since   Aug. 19, 2011
 *  version Aug. 19, 2011
 * @version Feb. 11, 2012
 * @author  ASAMI, Tomoharu
 */
class ScalaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  pobject: PObjectEntity,
  maker: ScalaMaker = null
) extends GenericClassDefinition(pContext, aspects, pobject) with ScalaMakerHolder {
  type ATTR_DEF = ScalaClassAttributeDefinition

  if (maker == null) {
    sm_open(aspects)
  } else {
    sm_open(maker, aspects)
  }

  override def toText = {
    sm_to_text
  }

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new ScalaClassAttributeDefinition(pContext, aspects, attr, this, sm_maker)
  }

  override protected def pln() {
    sm_pln()
  }

  override protected def head_package {
    require(packageName != null)
    if (packageName != "") {
      sm_package(packageName)
    }
    sm_end_package_section()
  }

  override protected def head_imports_Prologue {
//    sm_import("scala.util.*")
//    sm_import("scala.math.*")
    if (isSingleton) {
      sm_import("com.google.inject.Singleton")
    }
  }

  override protected def head_imports_Object(anObject: PObjectReferenceType) {
    if (packageName != anObject.packageName) {
      sm_import(anObject.qualifiedName)
    }
  }

  override protected def head_imports_Entity(anEntity: PEntityType) {
    if (packageName != anEntity.packageName) {
      sm_import(anEntity.qualifiedName)
    }
  }

  override protected def head_imports_Epilogue() {
    sm_end_import_section()
  }

  override protected def class_open_body {
    if (isSingleton) {
      sm_pln("@Singleton")
    }
    sm_p("class ")
    sm_p(name)
    for (base <- customBaseName orElse baseObject.map(_.name)) {
      sm_p(" extends ")
      sm_p(base)
    }
    if (!customImplementNames.isEmpty) {
      sm_p(" with ")
      sm_p(customImplementNames.mkString(" with "))
    }
/*
    baseObject match {
      case Some(base) => {
        sm_p(" extends ")
        sm_p(base.name)
      }
      case None => {}
    }
*/
    sm_pln(" {")
    sm_indent_up
  }

  override protected def class_close_body {
    sm_indent_down
    sm_pln("}")
  }

  override protected def constructors_null_constructor {
    sm_public_constructor("%s()", name) {
    }
  }

  override protected def constructors_copy_constructor {
    sm_public_constructor("%s(%s o)", name, name) {
      for (a <- attributeDefinitions) {
        sm_assign_this(a.varName, "o." + a.varName)
      }
    }
  }

  override protected def constructors_plain_constructor {
    val params = attributeDefinitions.filter(!_.isInject).
      map(a => a.javaType + " " + a.paramName).mkString(", ")
    if (!params.isEmpty) {
      sm_public_constructor("%s(%s)", name, params) {
        for (a <- attributeDefinitions) {
          sm_assign_this(a.varName, a.paramName)
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
      sm_public_constructor("%s(%s)", name, params) {
        for (a <- attributeDefinitions) {
          sm_assign_this(a.varName, a.paramName)
        }
      }
    }
  }

  /*
   * to_methods
   */
  override protected def to_methods_string {
    sm_public_method("String toString()") {
      sm_return("toJson()")
    }
  }

  override protected def to_methods_xml {
    sm_public_method("String toXml()") {
      sm_var_new_StringBuilder
      sm_pln("toXml(buf);")
      sm_return_StringBuilder
    }
    sm_public_method("void toXml(StringBuilder buf)") {
      sm_append_String("""<%s xmlns="%s">""", xmlElementName, xmlNamespace)
      for (a <- attributeDefinitions) {
        if (a.isSystemType) {
          sm_pln("""to_xml(buf, "%s", %s);""", a.xmlElementName, a.varName)
        } else {
          sm_if_not_null(a.varName) {
            sm_pln("%s.toXml(buf);", a.varName)
          }
        }
      }
      sm_append_String("</%s>", xmlElementName)
    }
  }

  override protected def to_methods_json {
    sm_public_method("String toJson()") {
      sm_var_new_StringBuilder
      sm_pln("toJson(buf);")
      sm_return_StringBuilder
    }
    sm_public_method("void toJson(StringBuilder buf)") {
      sm_append_String("{", term)
      var cont = false
      for (a <- attributeDefinitions) {
        if (cont) sm_append_String(", ")
        else cont = true
        if (a.isSystemType) {
          sm_pln("""to_json(buf, %s, %s);""", a.propertyConstantName, a.varName)
        } else {
          sm_pln("""to_json(buf, %s, %s.toJson());""", a.propertyConstantName, a.varName)
        }
      }
      sm_append_String("}", term)
    }
  }

  override protected def to_methods_csv {
    sm_public_method("String toCsv()") {
      sm_var_new_StringBuilder
      sm_pln("toCsv(buf);")
      sm_return_StringBuilder
    }
    sm_public_method("void toCsv(StringBuilder buf)") {
      sm_append_String("{", term)
      var cont = false
      for (a <- attributeDefinitions) {
        if (cont) sm_append_String(", ")
        else cont = true
        if (a.isSystemType) {
          sm_pln("""to_json(buf, %s, %s);""", a.propertyConstantName, a.varName)
        } else {
          sm_pln("""to_json(buf, %s, %s.toJson());""", a.propertyConstantName, a.varName)
        }
      }
      sm_append_String("}", term)
    }
  }

  override protected def to_methods_yaml {
    sm_public_method("String toYaml()") {
      sm_var_new_StringBuilder
      sm_pln("toYaml(buf);")
      sm_return_StringBuilder
    }
    sm_public_method("void toYaml(StringBuilder buf)") {
      sm_append_String("{", term)
      var cont = false
      for (a <- attributeDefinitions) {
        if (cont) sm_append_String(", ")
        else cont = true
        if (a.isSystemType) {
          sm_pln("""to_json(buf, %s, %s);""", a.propertyConstantName, a.varName)
        } else {
          sm_pln("""to_json(buf, %s, %s.toJson());""", a.propertyConstantName, a.varName)
        }
      }
      sm_append_String("}", term)
    }
  }

  override protected def to_methods_map {
    sm_public_method("Map<String, Object> toMap()") {
      sm_var("Map<String, Object>", "r", "new LinkedHashMap<String, Object>()")
      for (a <- attributeDefinitions) {
        sm_pln("""to_map(r, %s, %s);""", a.propertyConstantName, a.varName) 
      }
      sm_return("r")
    }
    sm_public_method("Map<String, String> toStringMap()") {
      sm_var("Map<String, String>", "r", "new LinkedHashMap<String, String>()")
      for (a <- attributeDefinitions) {
        if (a.isSystemType) {
          sm_pln("""to_string_map(r, %s, %s);""", a.propertyConstantName, a.varName)
        } else {
          sm_pln("""to_string_map(r, %s, %s.toJson());""", a.propertyConstantName, a.varName)
        }
      }
      sm_return("r")
    }
  }

  /*
   * update_methods
   */
  override protected def update_methods_string {
    sm_public_method("void update_string(String string)") {
    }
  }

  override protected def update_methods_xml {
    sm_public_method("void update_xml(String xml)") {
    }
  }

  override protected def update_methods_json {
    sm_public_method("void update_json(String json)") {
    }
  }

  override protected def update_methods_csv {
    sm_public_method("void update_csv(String csv)") {
    }
  }

  override protected def update_methods_yaml {
    sm_public_method("void update_yaml(String yaml)") {
    }
  }

  override protected def update_methods_urlencode {
    sm_public_method("void update_urlencode(String url)") {
    }
  }

  override protected def update_methods_map {
    sm_public_method("void update_map(Map<String, Object> map)") {
    }
  }

  /*
   * object_methods
   */
  override protected def object_methods_hashcode {
    sm_public_method("int hashCode()") {
      sm_p("return ")
      var cont = false
      attributeDefinitions match {
        case Nil => sm_pln("0;")
        case List(a) => sm_pln("hash_code(%s);", a.varName)
        case a :: tail => {
          sm_p("hash_code(%s)", a.varName)
          for (aa <- tail) {
            sm_pln
            sm_p("+ hash_code(%s)", aa.varName)
          }
          sm_pln(";")
        }
      }
    }
  }

  override protected def object_methods_equals {
    def isequals(a: GenericClassAttributeDefinition) {
      sm_p("is_equals(oo.%s, this.%s)", a.varName, a.varName)      
//      if (a.isSystemType) {
//          sm_p("is_equals(oo.%s, this.%s)", a.varName, a.varName)
//        } else {
//          sm_p("oo.%s == null ? this.%s == null : oo.%s.equals(this.%s)", a.varName, a.varName, a.varName, a.varName)
//        }      
    }

    sm_public_method("boolean equals(Object o)") {
      sm_if_return_expr("!(o instanceof %s)", name)("false")
      sm_var(name, "oo", "(%s)o", name)
      sm_p("return ")
      var cont = false
      attributeDefinitions match {
        case Nil => sm_pln("true;")
        case List(a) => {
          isequals(a)
          sm_pln(";")
        }
        case a :: tail => {
          isequals(a)
          for (aa <- tail) {
            sm_pln
            sm_p("&& ")
            isequals(aa)
          }
          sm_pln(";")
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
      sm_public_method("%s %s()".format(documentName, methodName)) {
        sm_var(documentName + ".Builder", "doc", "%s.Builder()", documentName)
        sm_pln("doc.%s = get%s();", idName, idName.capitalize)
        for (a <- attributeDefinitions if !a.attr.isId) {
          makeAttr(a)
        }
        sm_return("doc.build()")
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
    sm_public_void_method("update_document(%s doc)", documentName) {
      sm_if_not_null(idName) {
        sm_if_not_equals_expr("doc.%s", idName)("get%s()", idName.capitalize) {
          sm_pln("throw new IllegalArgumentException(\"Invalid id\");")
        }
      }
      for (a <- attributeDefinitions if !a.attr.isId) {
        a.document_methods_update_attribute
      }
    }
  }

  override protected def utilities {
    if (isValueEquality || isData) { 
      sm_code("""
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

protected final void to_json(StringBuilder buf, String name, String value) {
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
    sm_public_method("Builder builder()") {
      sm_return("new Builder(this)");
    }
  }

  override protected def builder_new_factory {
    sm_public_static_method("Builder Builder()") {
      sm_return("new Builder()");
    }
  }

  override protected def builder_class {
    val builder = new BuilderScalaClassDefinition(pContext, Nil, pobject, sm_maker)
    builder.build
  }

  override protected def builder_auxiliary {
  }
}
