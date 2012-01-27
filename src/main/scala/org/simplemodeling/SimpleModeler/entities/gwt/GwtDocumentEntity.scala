package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej._
//import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}

/*
 * @since   Apr. 16, 2009
 *  version Nov.  6, 2009
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class GwtDocumentEntity(val gaejObject: GaejEntityObjectEntity, val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  var packageName = ""
  var basePackageName: String = ""
  def term = gaejObject.term
  def xmlNamespace = gaejObject.xmlNamespace
  def attributes = gaejObject.attributes

  protected final def id_var_name(attr: GaejAttribute) = {
    gwtContext.gaejContext.variableName4RefId(attr)
  }

  protected final def id_attr_type_name(attr: GaejAttribute) = {
    gwtContext.gaejContext.attributeTypeName4RefId(attr)
  }

  final protected def java_element_type(anAttr: GaejAttribute) = {
    anAttr.elementTypeName match {
      case "User" => "String"
      case "Text" => "String"
      case "Category" => "String"
      case typeName => typeName
    }
  }

  final protected def java_doc_element_type(anAttr: GaejAttribute) = {
    anAttr.attributeType match {
      case p: GaejEntityPartType => {
        p.part.documentName
      }
      case _ => error("Invalid type = " + anAttr.attributeType)
    }
  }


  override protected def write_Content(out: BufferedWriter) {
    val buffer = new JavaTextMaker

    def make_package {
      require (packageName != null)
      if (packageName != "") {
        buffer.print("package ")
        buffer.print(packageName)
        buffer.println(";")
        buffer.println()
      }
    }

    def make_imports {
      var isPrint = false

      def make_import_entity(anEntity: GaejEntityType) {
        if (packageName != anEntity.packageName) {
          make_import(anEntity.qualifiedName)
        }
      }

      def make_import(aName: String) {
          buffer.print("import ")
          buffer.print(aName)
          buffer.println(";")
          isPrint = true
      }

      make_import("java.util.*")
      make_import(basePackageName + ".*")
      val attrs = attributes.filter(_.attributeType.isEntity)
/*
      for (attr <- attrs) {
        val entity = attr.attributeType.asInstanceOf[GaejEntityType]
        make_import_entity(entity)
      }
*/
      if (isPrint)
        buffer.println()
    }

    make_package
    make_imports
    writeModel(buffer)
    out.append(buffer.toString)
    out.flush
  }

  final def writeModel(buffer: JavaTextMaker) {
    def get_type(anAttr: GaejAttribute) = {
      anAttr.objectTypeName match {
        case "User" => "String"
        case "Text" => "String"
        case "Category" => "String"
        case "List<User>" => "List<String>"
        case "List<Text>" => "List<String>"
        case "List<Category>" => "List<String>"
        case typeName => typeName
      }
    }

    def make_attributes {
      for (attr <- attributes) {
        if (attr.isHasMany) {
          make_multi_attribute_variable(attr)
        } else {
          make_attribute_variable(attr)
        }
        buffer.println()
      }
    }

    def make_attribute_variable(attr: GaejAttribute) {
      val attrName = gwtContext.gaejContext.attributeName(attr)
      val varName = gwtContext.gaejContext.variableName(attr)

      def make_key_long {
        buffer.print("public Long ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_unencoded_string {
        buffer.print("public String ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_key {
        buffer.print("public Key ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_as_encoded_string {
        buffer.print("public String ")
        buffer.print(varName)
        buffer.println(";")
      }

      if (attr.isId) {
/*
        attr.idPolicy match {
          case SMAutoIdPolicy => make_key_long
          case SMApplicationIdPolicy => make_key_unencoded_string
        }
*/
        buffer.print("public ")
        buffer.print(get_type(attr))
        buffer.print(" ")
        buffer.print(varName)
        buffer.println(";")
      } else {
	attr.attributeType match {
	  case v: GaejIntegerType => {
            buffer.print("public String ")
            buffer.print(varName)
            buffer.println(";")
          }
	  case v: GaejDecimalType => {
            buffer.print("public String ")
            buffer.print(varName)
            buffer.println(";")
          }
	  case t: GaejDateTimeType => {
            buffer.print("public ")
            buffer.print(get_type(attr))
            buffer.print(" ")
            buffer.print(varName)
            buffer.println(";")
	    buffer.print("public Date ")
	    buffer.print(varName)
	    buffer.println("_date = null;")
	    buffer.print("public Date ")
	    buffer.print(varName)
	    buffer.println("_time = null;")
	    buffer.print("public boolean ")
	    buffer.print(varName)
	    buffer.println("_now = false;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_string = null;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_date_string = null;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_time_string = null;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_now_string = null;")
	  }
	  case e: GaejEntityType => {
            val attrName = id_var_name(attr)
            val typeName = id_attr_type_name(attr)
            buffer.print("public %s ".format(typeName))
            buffer.print(attrName)
            buffer.println(";")
          }
	  case p: GaejEntityPartType => {
            val docName = gwtContext.documentName(p.part)
            buffer.print("public ")
            buffer.print(docName)
            buffer.print(" ")
            buffer.print(varName)
            buffer.println(";")
          }
	  case p: GaejPowertypeType => {
            buffer.print("public String ")
            buffer.print(varName)
            buffer.println(";")
          }
	  case _ => {
            buffer.print("public ")
            buffer.print(get_type(attr))
            buffer.print(" ")
            buffer.print(varName)
/*
            if (attr.isHasMany) {
              buffer.print(" = new ArrayList<")
              buffer.print(java_element_type(attr));
              buffer.print(">()")
            }
*/
            buffer.println(";")
          }
	}
      }
    }

    def make_multi_attribute_variable(attr: GaejAttribute) {
      val attrName = gwtContext.gaejContext.attributeName(attr)
      val varName = gwtContext.gaejContext.variableName(attr)

      def make_key_long {
        buffer.print("public Long ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_unencoded_string {
        buffer.print("public String ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_key {
        buffer.print("public Key ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_as_encoded_string {
        buffer.print("public String ")
        buffer.print(varName)
        buffer.println(";")
      }

      if (attr.isId) {
        attr.idPolicy match {
          case SMAutoIdPolicy => make_key_long
          case SMApplicationIdPolicy => make_key_unencoded_string
        }
      } else {
	attr.attributeType match {
	  case v: GaejIntegerType => {
            buffer.print("public List<String> ")
            buffer.print(varName)
            buffer.println(" = new ArrayList<String>();")
          }
	  case v: GaejDecimalType => {
            buffer.print("public List<String> ")
            buffer.print(varName)
            buffer.println(" = new ArrayList<String>();")
          }
	  case t: GaejDateTimeType => { // XXX
            buffer.print("public ")
            buffer.print(get_type(attr))
            buffer.print(" ")
            buffer.print(varName)
            buffer.println(";")
	    buffer.print("public Date ")
	    buffer.print(varName)
	    buffer.println("_date = null;")
	    buffer.print("public Date ")
	    buffer.print(varName)
	    buffer.println("_time = null;")
	    buffer.print("public boolean ")
	    buffer.print(varName)
	    buffer.println("_now = false;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_string = null;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_date_string = null;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_time_string = null;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_now_string = null;")
	  }
	  case e: GaejEntityType => {
            val idAttrName = id_var_name(attr)
            val idTypeName = id_attr_type_name(attr)
            buffer.println("public List<%s> %s = new ArrayList<%s>();".format(idTypeName, idAttrName, idTypeName))
          }
	  case p: GaejEntityPartType => {
            val docName = gwtContext.documentName(p.part)
            buffer.print("public List<")
            buffer.print(docName)
            buffer.print("> ")
            buffer.print(varName)
            buffer.print(" = new ArrayList<")
            buffer.print(docName)
            buffer.println(">();")
          }
	  case p: GaejPowertypeType => {
            buffer.print("public List<String> ")
            buffer.print(varName)
            buffer.println(" = new ArrayList<String>();")
          }
	  case _ => {
            buffer.print("public ")
            buffer.print(get_type(attr))
            buffer.print(" ")
            buffer.print(varName)
            buffer.print(" = new ArrayList<")
            buffer.print(java_element_type(attr))
            buffer.println(">();")
          }
	}
      }
    }

    def make_null_constructor {
      buffer.print("public ")
      buffer.print(name)
      buffer.println("() {")
      buffer.println("}")
    }

/*
    def make_doc_constructor {
      buffer.method("public " + name + "(" + gaejObject.documentName +  " doc)") {
        buffer.println("init_document(doc);")
      }
    }
*/

    def make_getters {
      for (attr <- attributes) {
        make_attribute_getters(attr)
      }
    }

    def make_attribute_getters(attr: GaejAttribute) {
      val attrName = gwtContext.gaejContext.attributeName(attr)
      val varName = gwtContext.gaejContext.variableName(attr)

      def make_key_long {
        buffer.method("public String get" + attr.name.capitalize + "_asString()") {
          buffer.print("return ")
          buffer.print(varName)
          buffer.print(".toString();")
        }
      }

      def make_key_unencoded_string {
        buffer.print("public String get")
        buffer.print(attr.name.capitalize)
        buffer.println("_asString() {");
        buffer.indentUp
        buffer.print("if (")
        buffer.print(varName)
        buffer.println(" == null) {")
        buffer.indentUp
        buffer.println("return \"\";")
        buffer.indentDown
        buffer.println("} else {")
        buffer.indentUp
        buffer.print("return ");
        buffer.print(varName)
        buffer.println(";")
        buffer.indentDown
        buffer.println("}")
        buffer.indentDown
        buffer.println("}")
      }

      def make_key_key {
        buffer.print("public Key ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_as_encoded_string {
        buffer.print("public String ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_single {
        def make_asString(attrName: String, varName: String, expr: String) {
          buffer.method("public String get" + attrName.capitalize + "_asString()") {
            buffer.makeIfElse(varName + " == null") {
              buffer.makeReturn("\"\"")
            }
            buffer.makeElse {
              buffer.makeReturn(expr)
            }
          }
        }

	attr.attributeType match {
	  case t: GaejDateTimeType => {
            make_asString(attrName, varName, "Util.dateTime2text(" + varName + ")")
/*            
            buffer.print("public String get")
            buffer.print(varName.capitalize)
            buffer.println("_asString() {");
            buffer.indentUp
            buffer.print("if (")
            buffer.print(varName)
            buffer.println(" == null) {")
            buffer.indentUp
            buffer.println("return \"\";")
            buffer.indentDown
            buffer.println("} else {")
            buffer.indentUp
            buffer.print("return Util.dateTime2text(");
            buffer.print(varName)
            buffer.println(");")
            buffer.indentDown
            buffer.println("}")
            buffer.indentDown
            buffer.println("}")
*/
            buffer.print("public String get")
            buffer.print(attr.name.capitalize)
            buffer.println("_date_asString() {");
            buffer.indentUp
            buffer.print("if (")
            buffer.print(varName)
            buffer.println(" == null) {")
            buffer.indentUp
            buffer.println("return \"\";")
            buffer.indentDown
            buffer.println("} else {")
            buffer.indentUp
            buffer.print("return Util.date2text(");
            buffer.print(varName)
            buffer.println(");")
            buffer.indentDown
            buffer.println("}")
            buffer.indentDown
            buffer.println("}")

            buffer.print("public String get")
            buffer.print(attr.name.capitalize)
            buffer.println("_time_asString() {");
            buffer.indentUp
            buffer.print("if (")
            buffer.print(varName)
            buffer.println(" == null) {")
            buffer.indentUp
            buffer.println("return \"\";")
            buffer.indentDown
            buffer.println("} else {")
            buffer.indentUp
            buffer.print("return Util.time2text(");
            buffer.print(varName)
            buffer.println(");")
            buffer.indentDown
            buffer.println("}")
            buffer.indentDown
            buffer.println("}")
          }
          case t: GaejDateType => make_asString(attrName, varName, "Util.date2text(" + varName + ")")
          case t: GaejTimeType => make_asString(attrName, varName, "Util.time2text(" + varName + ")")
	  case e: GaejEntityType => {
            val attrName = id_var_name(attr)
            val attrTypeName = id_attr_type_name(attr)
            buffer.print("public String get")
            buffer.print(attrName.capitalize)
            buffer.println("_asString() {")
            buffer.indentUp
            buffer.print("if (")
            buffer.print(gwtContext.gaejContext.variableName4RefId(attr))
            buffer.println(" == null) {")
            buffer.indentUp
            buffer.println("return \"\";")
            buffer.indentDown
            buffer.println("} else {")
            buffer.indentUp
            buffer.print("return ")
            if ("String".equals(attrTypeName)) {
              buffer.print(attrName)
            } else {
              buffer.print("Util.datatype2string(%s)".format(attrName))
            }
            buffer.println(";")
            buffer.indentDown
            buffer.println("}")
            buffer.indentDown
            buffer.println("}")
          }
	  case p: GaejEntityPartType => {
            buffer.print("public String get")
            buffer.print(attr.name.capitalize)
            buffer.println("_asString() {");
            buffer.indentUp
            buffer.print("if (")
            buffer.print(varName)
            buffer.println(" == null) {")
            buffer.indentUp
            buffer.println("return \"\";")
            buffer.indentDown
            buffer.println("} else {")
            buffer.indentUp
            buffer.print("return ");
            buffer.print(varName)
            buffer.println(".toString();")
            buffer.indentDown
            buffer.println("}")
            buffer.indentDown
            buffer.println("}")
          }
	  case _ => {
            buffer.print("public String get")
            buffer.print(attr.name.capitalize)
            buffer.println("_asString() {");
            buffer.indentUp
            if (attr.isDataType) {
              buffer.print("return Util.datatype2string(");
              buffer.print(varName)
              buffer.println(");")
            } else {
              buffer.print("if (")
              buffer.print(varName)
              buffer.println(" == null) {")
              buffer.indentUp
              buffer.println("return \"\";")
              buffer.indentDown
              buffer.println("} else {")
              buffer.indentUp
              buffer.print("return ");
              buffer.print(varName)
              buffer.println(".toString();")
              buffer.indentDown
              buffer.println("}")
            }
            buffer.indentDown
            buffer.println("}")
          }
	}
      }

      def make_multiple {
        def make_asString(attrName: String, varName: String, typeName: String, expr: String) {
          buffer.method("public String get" + attrName.capitalize + "_asString()") {
            buffer.makeIf(attrName + ".isEmpty()") {
              buffer.makeReturn("\"\"")
            }
            buffer.makeVar("buf", "StringBuilder")
            buffer.makeVar("last", typeName, attrName + ".get(" + attrName + ".size() - 1)")
            buffer.makeFor(typeName + " elem : " + attrName) {
              buffer.makeAppendExpr(expr)
              buffer.makeIf("elem != last") {
                buffer.makeAppendString(", ")
              }
            }
            buffer.makeReturn("buf.toString()")
          }
        }

        def make_asStringIndex(attrName: String, varName: String, typeName: String, expr: String) {
          buffer.method("public String get" + attrName.capitalize + "_asString(int index)") {
            buffer.makeIfElse(attrName + ".size() <= index") {
              buffer.makeReturn("\"\"")
            }
            buffer.makeElse {
              buffer.makeVar("elem", typeName, attrName + ".get(index)")
              if ("String".equals(typeName)) {
                buffer.makeReturn(expr)
              } else {
                buffer.makeReturn("Util.datatype2string(%s)".format(expr))
              }
            }
          }
        }

        def make_asStringList(attrName: String, varName: String, typeName: String, expr: String) {
          buffer.method("public List<String> get" + attrName.capitalize + "_asStringList()") {
            buffer.makeVar("list", "List<String>", "new ArrayList<String>()")
            buffer.makeFor(typeName + " elem : " + attrName) {
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

        def make_asString_methods(attrName: String, varName: String, typeName: String, expr: String) {
          make_asString(attrName, varName, typeName, expr)
          make_asStringIndex(attrName, varName, typeName, expr)
          make_asStringList(attrName, varName, typeName, expr)
        }

	attr.attributeType match {
	  case v: GaejIntegerType => make_asString_methods(attrName, varName, "String", "elem")
	  case v: GaejDecimalType => make_asString_methods(attrName, varName, "String", "elem")
	  case t: GaejDateTimeType => make_asString_methods(attrName, varName, "Date", "Util.dateTime2text(elem)")
	  case e: GaejEntityType => make_asString_methods(
            gwtContext.gaejContext.variableName4RefId(attr),
            gwtContext.gaejContext.variableName4RefId(attr),
            gwtContext.gaejContext.attributeTypeName4RefId(attr), "elem")
          case p: GaejEntityPartType => make_asString_methods(attrName, varName, gwtContext.documentName(p.part), "elem.toString()")
	  case _ => make_asString_methods(attrName, varName, java_element_type(attr), "elem.toString()")
	}
      }

      if (attr.isId) {
        attr.idPolicy match {
          case SMAutoIdPolicy => make_key_long
          case SMApplicationIdPolicy => make_key_unencoded_string
        }
      } else {
        if (attr.isHasMany) {
          make_multiple
        } else {
          make_single
        }
      }
    }

/*
    def make_init_document {
      def gaej_to_gwt(attr: GaejAttribute) {
        def make_one {
          val in = "doc"
          val out = "this"
          attr.attributeType match {
            case e: GaejEntityType => {
	      doc_to_doc_by_name(e.entity.idName, in, out)
	    }
	    case e: GaejDateTimeType => {
	      val name = attr.name;
	      doc_to_doc_by_name(name, in, out)
	      doc_to_doc_by_name(name + "_date", in, out)
	      doc_to_doc_by_name(name + "_time", in, out)
	      doc_to_doc_by_name(name + "_now", in, out)
	      doc_to_doc_by_name(name + "_string", in, out)
	      doc_to_doc_by_name(name + "_date_string", in, out)
	      doc_to_doc_by_name(name + "_time_string", in, out)
	      doc_to_doc_by_name(name + "_now_string", in, out)
	    }
            case p: GaejEntityPartType => {
	      val name = attr.name;
              buffer.print("doc.")
              buffer.print(name)
              buffer.print(" = new ")
              buffer.print(gwtContext.documentName(p.part))
              buffer.print("(this.")
              buffer.print(name)
              buffer.println(");")
            }
            case _ => {
	      doc_to_doc_by_name(attr.name, in, out)
	    }
          }
        }

        def make_many {
          val in = "doc"
          val out = "this"
          attr.attributeType match {
            case e: GaejEntityType => {
	      docs_to_docs_by_name(e.entity.idName, "String", "elem", in, out)
	    }
	    case e: GaejDateTimeType => {
	      val name = attr.name;
	      docs_to_docs_by_name(name, "Date", "elem", in, out)
	      docs_to_docs_by_name(name + "_date", "Date", "elem", in, out)
	      docs_to_docs_by_name(name + "_time", "Date", "elem", in, out)
	      docs_to_docs_by_name(name + "_now", "boolean", "elem", in, out)
	      docs_to_docs_by_name(name + "_string", "String", "elem", in, out)
	      docs_to_docs_by_name(name + "_date_string", "String", "elem", in, out)
	      docs_to_docs_by_name(name + "_time_string", "String", "elem", in, out)
	      docs_to_docs_by_name(name + "_now_string", "String", "elem", in, out)
	    }
            case p: GaejEntityPartType => {
	      val name = attr.name;
              buffer.makeFor(p.part.documentName, "elem", "doc." + name) {
                buffer.print("this.")
                buffer.print(name)
                buffer.print(".add(new ")
                buffer.print(gwtContext.documentName(p.part))
                buffer.println("(elem));")
              }
            }
            case _ => {
	      docs_to_docs_by_name(attr.name, attr.elementTypeName, "elem", in, out)
	    }
          }
        }

        if (attr.isHasMany) {
          make_many
        } else {
          make_one
        }
      }

      buffer.method("public void init_document(" + gaejObject.documentName + " doc)") {
        for (attr <- attributes) {
          gaej_to_gwt(attr);
        }
      }
    }
*/

/*
    def make_make_document {
      val docName = gaejObject.documentName

      def gwt_to_gaej(attr: GaejAttribute) {
        def make_one {
          val in = "this"
          val out = "doc"
          attr.attributeType match {
            case e: GaejEntityType => {
	      doc_to_doc_by_name(e.entity.idName, in, out)
	    }
	    case e: GaejDateTimeType => {
	      val name = attr.name;
	      doc_to_doc_by_name(name, in, out)
	      doc_to_doc_by_name(name + "_date", in, out)
	      doc_to_doc_by_name(name + "_time", in, out)
	      doc_to_doc_by_name(name + "_now", in, out)
	      doc_to_doc_by_name(name + "_string", in, out)
	      doc_to_doc_by_name(name + "_date_string", in, out)
	      doc_to_doc_by_name(name + "_time_string", in, out)
	      doc_to_doc_by_name(name + "_now_string", in, out)
	    }
            case p: GaejEntityPartType => {
	      val name = attr.name;
              buffer.print("doc.")
              buffer.print(name)
              buffer.print(" = ")
              buffer.print("this.")
              buffer.print(name)
              buffer.println(".make_document();")
            }
            case _ => {
	      doc_to_doc_by_name(attr.name, in, out)
	    }
          }
        }

        def make_many {
          val in = "doc"
          val out = "this"
          attr.attributeType match {
            case e: GaejEntityType => {
	      docs_to_docs_by_name(e.entity.idName, "String", "elem", in, out)
	    }
	    case e: GaejDateTimeType => {
	      val name = attr.name;
	      docs_to_docs_by_name(name, "Date", "elem", in, out)
	      docs_to_docs_by_name(name + "_date", "Date", "elem", in, out)
	      docs_to_docs_by_name(name + "_time", "Date", "elem", in, out)
	      docs_to_docs_by_name(name + "_now", "boolean", "elem", in, out)
	      docs_to_docs_by_name(name + "_string", "String", "elem", in, out)
	      docs_to_docs_by_name(name + "_date_string", "String", "elem", in, out)
	      docs_to_docs_by_name(name + "_time_string", "String", "elem", in, out)
	      docs_to_docs_by_name(name + "_now_string", "String", "elem", in, out)
	    }
            case p: GaejEntityPartType => {
	      val name = attr.name;
              buffer.makeFor(gwtContext.documentName(p.part), "elem", "this." + name) {
                buffer.print("doc.")
                buffer.print(name)
                buffer.print(".add(elem.make_document());")
              }
            }
            case _ => {
	      docs_to_docs_by_name(attr.name, attr.elementTypeName, "elem", in, out)
	    }
          }
        }

        if (attr.isHasMany) {
          make_many
        } else {
          make_one
        }
      }

      buffer.method("public " + docName + " make_document()") {
        buffer.makeVar("doc", docName, "new " + docName + "()")
        for (attr <- attributes) {
          gwt_to_gaej(attr)
        }
        buffer.makeReturn("doc")
      }
    }
*/

    def doc_to_doc_by_name(name: String, in: String, out: String) {
      buffer.print(out);
      buffer.print(".");
      buffer.print(name);
      buffer.print(" = ");
      buffer.print(in);
      buffer.print(".");
      buffer.print(name);
      buffer.println(";")
    }

    def docs_to_docs_by_name(attrName: String, typeName: String, expr: String, in: String, out: String) {
      buffer.makeFor(typeName, "elem", in + "." + attrName) {
        buffer.print(out);
        buffer.print(".");
        buffer.print(attrName);
        buffer.print(".add(");
        buffer.print(expr);
        buffer.println(");")
      }
    }

    //
    buffer.println("""@SuppressWarnings({ "unused", "serial" })""")
    buffer.print("public class ")
    buffer.print(name)
    gaejObject.getBaseObject match {
      case Some(base) => {
        val baseName = gwtContext.documentName(base)
        buffer.print(" extends ")
        buffer.print(baseName)
        buffer.print(" {")
      }
      case _ => {
        buffer.print(" implements java.io.Serializable {")
      }
    }
    buffer.println()
    buffer.indentUp
    make_attributes
    make_null_constructor
//    make_doc_constructor // GWT can't refer external sources.
    make_getters
//    make_init_document // GWT can't refer external sources.
//    make_make_document // GWT can't refer external sources.
    make_entity_title_asString(buffer)
    make_entity_asXmlString(buffer)
    make_build_xml_element(buffer)
    make_toString(buffer)
    buffer.indentDown
    buffer.println("}")
    buffer.println()
  }

  final protected def make_toString(buffer: JavaTextMaker) {
    buffer.method("public String toString()") {
      buffer.makeVar("buf", "StringBuilder");
      buffer.println("entity_asXmlString(buf);")
      buffer.println("return buf.toString();");
    }
  }

  def make_entity_title_asString(buffer: JavaTextMaker) {
    buffer.method("public String entity_title_asString()") {
      buffer.makeStringBuilderVar
      gaejObject.getTitleName match {
        case Some(name) => {
          buffer.makeAppendExpr(make_get_string_property(name))
        }
        case None => gaejObject.getNameName match {
          case Some(name) => {
            buffer.makeAppendExpr(make_get_string_property(name))
            if (gaejObject.isId) {
              buffer.makeAppendString("(")
              buffer.makeAppendExpr(make_get_string_property(gaejObject.idName))
              buffer.makeAppendString(")")
            }
          }
          case None => {
            if (gaejObject.isId) {
              buffer.makeAppendExpr(make_get_string_property(gaejObject.idName))
            }
          }
        }
      }
      buffer.makeStringBuilderReturn
    }
  }

  // see GaejObjectEntity
  final protected def make_entity_asXmlString(buffer: JavaTextMaker) {
    buffer.println()
    buffer.println("public void entity_asXmlString(StringBuilder buf) {")
    buffer.indentUp
    buffer.makeAppendString("<" + term + " xmlns=\"" + xmlNamespace + "\">")
    for (attr <- attributes) {
      buffer.println(make_get_string_element(attr) + ";")
    }
    buffer.makeAppendString("</" + term + ">")
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
        make_get_string_property(gwtContext.gaejContext.variableName4RefId(attr))
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
        make_get_string_list_property(gwtContext.gaejContext.variableName4RefId(attr))
      }
      case _ => make_get_string_list_property(attr.name)
    }
  }

/*
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
      buffer.makeFor(typeName + " elem : " + varName) {
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
        buffer.makeReturn(expr)
      }
    }
  }

  final protected def make_multi_get_asStringList(attrName: String, varName: String, typeName: String, expr: String, buffer: JavaTextMaker) {
    buffer.method("public List<String> get" + attrName.capitalize + "_asStringList()") {
      buffer.makeVar("list", "List<String>", "new ArrayList<String>()")
      buffer.makeFor(typeName + " elem : " + attrName) {
        buffer.print("list.add(")
        buffer.print(expr)
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
*/

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
