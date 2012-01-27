package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entities.gaej._
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}

/*
 * @since   Apr. 16, 2009
 * @version Oct. 29, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtDomainServiceImplEntity(val gaejService: GaejDomainServiceEntity, val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource

  var packageName: String = ""
  val entities = gaejService.entities
  var basePackageName: String = ""
  var clientPackageName: String = ""
  var serviceInterfaceName: String = ""
  def domainServiceName: String = gaejService.name
  def queryPackageName = basePackageName + "." + domainServiceName

  override def is_Text_Output = true

/*
  // XXX unify GwtDocumentEntity
  final protected def java_element_type(anAttr: GaejAttribute) = {
    anAttr.elementTypeName match {
      case "User" => "String"
      case "Text" => "String"
      case "Category" => "String"
      case typeName => typeName
    }
  }
*/

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  private val service = """package %packageName%;

import java.util.*;
import java.math.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.*;
import %basePackageName%.*;
import %queryPackageName%.*;
import %clientPackageName%.*;

@SuppressWarnings("serial")
public class %serviceName% extends RemoteServiceServlet implements %serviceInterface% {
    private %domainService% service;

    public %serviceName%() {
      service = new %domainService%();
    }

    protected final String gwt_string(User user) {
      return user.getEmail();
    }

    protected final String gwt_string(Text text) {
      return text.getValue();
    }

    protected final String gwt_string(Category category) {
      return category.getCategory();
    }

    protected final User gwt_user(String string) {
        return new User(string, "gmail.com");
    }

    protected final Text gwt_text(String string) {
        return new Text(string);
    }

    protected final Category gwt_category(String string) {
        return new Category(string);
    }

%operations%
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val serviceName = name
    val buffer = new JavaTextMaker(
      service,
      Map("%packageName%" -> packageName,
          "%serviceName%" -> serviceName,
          "%basePackageName%" -> basePackageName,
          "%clientPackageName%" -> clientPackageName,
          "%queryPackageName%" -> queryPackageName,
          "%serviceInterface%" -> serviceInterfaceName,
          "%domainService%" -> domainServiceName
        ))
    buffer.replace("%operations%")(make_operations)
    out.append(buffer.toString)
    out.flush()
  }

  private def make_operations(buffer: JavaTextMaker) {
    def make_object_crud_operation(anEntity: GaejEntityEntity) {
      val entityName = anEntity.name
      val capitalizedTerm = gwtContext.gaejContext.entityBaseName(anEntity)
      val idName = anEntity.idName
      val gwtDocName = gwtContext.documentName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gwtContext.queryName(anEntity)

/* 2009-07-04
      def gwtDoc_to_gaejDoc(in: String, out: String) {
        for (attr <- anEntity.attributes) {
          doc_to_doc(attr, in, out);
        }
      }

      def gaejDoc_to_gwtDoc(in: String, out: String) {
        for (attr <- anEntity.attributes) {
          doc_to_doc(attr, in, out);
        }
      }

      def doc_to_doc(attr: GaejAttribute, in: String, out: String) {
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
          case _ => {
	    doc_to_doc_by_name(attr.name, in, out)
	  }
        }
      }

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

      def create_operation {
        buffer.println()
//        buffer.println("@Override")
        buffer.print("public ")
        buffer.print(gwtDocName)
        buffer.print(" create")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtDocName)
        buffer.println(" inDoc) {")
        buffer.indentUp
        buffer.print(gaejDocName)
        buffer.print(" reqDoc = new ")
        buffer.print(gaejDocName)
        buffer.println("();")
        gwtDoc_to_gaejDoc("inDoc", "reqDoc")
        buffer.print(gaejDocName)
        buffer.print(" respDoc = service.create")
        buffer.print(capitalizedTerm)
        buffer.println("(reqDoc);")
        buffer.print(gwtDocName)
        buffer.print(" outDoc = new ")
        buffer.print(gwtDocName)
        buffer.println("();")
        gaejDoc_to_gwtDoc("respDoc", "outDoc");
        buffer.println("return outDoc;")
        buffer.indentDown
        buffer.println("}")
      }

      def read_operation {
        buffer.println()
//        buffer.println("@Override")
        buffer.print("public ")
        buffer.print(gwtDocName)
        buffer.print(" read")
        buffer.print(capitalizedTerm)
        buffer.println("(String key) {")
        buffer.indentUp
        buffer.print(gaejDocName)
        buffer.print(" respDoc = service.read")
        buffer.print(capitalizedTerm)
        buffer.println("(key);")
        buffer.print(gwtDocName)
        buffer.print(" outDoc = new ")
        buffer.print(gwtDocName)
        buffer.println("();")
        gaejDoc_to_gwtDoc("respDoc", "outDoc")
        buffer.println("return outDoc;")
        buffer.indentDown
        buffer.println("}")
      }

      def update_operation {
        buffer.println()
//        buffer.println("@Override")
        buffer.print("public void update")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtDocName)
        buffer.println(" inDoc) {")
        buffer.indentUp
        buffer.print(gaejDocName)
        buffer.print(" reqDoc = new ")
        buffer.print(gaejDocName)
        buffer.println("();")
        gwtDoc_to_gaejDoc("inDoc", "reqDoc")
        buffer.print("service.update")
        buffer.print(capitalizedTerm)
        buffer.println("(reqDoc);")
        buffer.indentDown
        buffer.println("}")
      }

      def delete_operation {
        buffer.println()
//        buffer.println("@Override")
        buffer.print("public void delete")
        buffer.print(capitalizedTerm)
        buffer.println("(String key) {")
        buffer.indentUp
        buffer.print("service.delete")
        buffer.print(capitalizedTerm)
        buffer.println("(key);")
        buffer.indentDown
        buffer.println("}")
      }

      def query_operation {
        buffer.println()
//        buffer.println("@Override")
        buffer.print("public ")
	buffer.print("Collection<")
        buffer.print(gwtDocName)
	buffer.print(">");
        buffer.print(" query")
        buffer.print(capitalizedTerm)
        buffer.println("(GwtQuery gwtQuery) {")
        buffer.indentUp
        buffer.print(queryName)
        buffer.print(" query = service.query")
        buffer.print(capitalizedTerm)
        buffer.println("();")
        buffer.print("List<")
        buffer.print(gaejDocName)
        buffer.println("> docs = query.execute();")
        buffer.print("ArrayList<")
        buffer.print(gwtDocName)
        buffer.print("> result = new ArrayList<")
        buffer.print(gwtDocName)
        buffer.println(">();")
        buffer.print("for (")
        buffer.print(gaejDocName)
        buffer.println(" doc : docs) {")
        buffer.indentUp
        buffer.print(gwtDocName)
        buffer.print(" outDoc = new ")
        buffer.print(gwtDocName)
        buffer.println("();")
        gaejDoc_to_gwtDoc("doc", "outDoc")
        buffer.println("result.add(outDoc);")
        buffer.indentDown
        buffer.println("}")
        buffer.println("return result;")
        buffer.indentDown
        buffer.println("}")
      }
*/
      def make_gwt_document {
        def gaej_to_gwt(attr: GaejAttribute) {
          val attrName = gwtContext.gaejContext.attributeName(attr)
          val varName = gwtContext.gaejContext.variableName(attr)

          def make_one {
            val in = "model"
            val out = "gwt"
            attr.attributeType match {
              case v: GaejIntegerType => {
                buffer.print("gwt.")
                buffer.print(varName)
                buffer.print(" = model.")
                buffer.print(varName)
                buffer.print(" == null? null : model.")
                buffer.print(varName)
                buffer.println(".toString();")
              }
              case v: GaejDecimalType => {
                buffer.print("gwt.")
                buffer.print(varName)
                buffer.print(" = model.")
                buffer.print(varName)
                buffer.print(" == null? null : model.")
                buffer.print(varName)
                buffer.println(".toString();")
              }
              case e: GaejEntityType => {
                val refIdName = gwtContext.gaejContext.variableName4RefId(attr)
	        doc_to_doc_by_name(refIdName, in, out)
	      }
	      case e: GaejDateTimeType => {
	        doc_to_doc_by_name(varName, in, out)
	        doc_to_doc_by_name(varName + "_date", in, out)
	        doc_to_doc_by_name(varName + "_time", in, out)
	        doc_to_doc_by_name(varName + "_now", in, out)
	        doc_to_doc_by_name(varName + "_string", in, out)
	        doc_to_doc_by_name(varName + "_date_string", in, out)
	        doc_to_doc_by_name(varName + "_time_string", in, out)
	        doc_to_doc_by_name(varName + "_now_string", in, out)
	      }
              case p: GaejEntityPartType => {
                buffer.print("gwt.")
                buffer.print(varName)
                buffer.print(" = gwt_")
                buffer.print(gwtContext.documentName(p.part))
                buffer.print("(model.")
                buffer.print(varName)
                buffer.println(");")
              }
              case _ => {
                attr.attributeType.objectTypeName match {
                  case "User" => {
                    doc_to_doc_by_name_with_converter(varName, in, out, "gwt_string")
                  }
                  case "Text" => {
                    doc_to_doc_by_name_with_converter(varName, in, out, "gwt_string")
                  }
                  case "Category" => {
                    doc_to_doc_by_name_with_converter(varName, in, out, "gwt_string")
                  }
                  case _ => doc_to_doc_by_name(varName, in, out)
                }
	      }
            }
          }

          def make_many {
            val in = "model"
            val out = "gwt"
            attr.attributeType match {
              case v: GaejIntegerType => {
                buffer.makeFor("BigInteger", "elem", in + "." + varName) {
                  buffer.print("gwt.")
                  buffer.print(varName)
                  buffer.println(".add(elem.toString());")
                }
              }
              case v: GaejDecimalType => {
                buffer.makeFor("BigDecimal", "elem", in + "." + varName) {
                  buffer.print("gwt.")
                  buffer.print(varName)
                  buffer.println(".add(elem.toString());")
                }
              }
              case e: GaejEntityType => {
                val refIdName = gwtContext.gaejContext.variableName4RefId(attr)
                val refIdType = gwtContext.gaejContext.attributeTypeName4RefId(attr)
	        docs_to_docs_by_name(refIdName, refIdType, "elem", in, out)
	      }
	      case e: GaejDateTimeType => {
	        docs_to_docs_by_name(varName, "Date", "elem", in, out)
	        docs_to_docs_by_name(varName + "_date", "Date", "elem", in, out)
	        docs_to_docs_by_name(varName + "_time", "Date", "elem", in, out)
	        docs_to_docs_by_name(varName + "_now", "boolean", "elem", in, out)
	        docs_to_docs_by_name(varName + "_string", "String", "elem", in, out)
	        docs_to_docs_by_name(varName + "_date_string", "String", "elem", in, out)
	        docs_to_docs_by_name(varName + "_time_string", "String", "elem", in, out)
	        docs_to_docs_by_name(varName + "_now_string", "String", "elem", in, out)
	      }
              case p: GaejEntityPartType => {
                buffer.makeFor(p.part.documentName, "elem", in + "." + varName) {
                  buffer.print("gwt.")
                  buffer.print(varName)
                  buffer.print(".add(gwt_")
                  buffer.print(gwtContext.documentName(p.part))
                  buffer.println("(elem));")
                }
              }
              case _ => {
                attr.elementTypeName match {
                  case "User" => {
	            docs_to_docs_by_name_with_converter(varName, attr.elementTypeName, "elem", in, out, "gwt_string")
                  }
                  case "Text" => {
	            docs_to_docs_by_name_with_converter(varName, attr.elementTypeName, "elem", in, out, "gwt_string")
                  }
                  case "Category" => {
	            docs_to_docs_by_name_with_converter(varName, attr.elementTypeName, "elem", in, out, "gwt_string")
                  }
                  case _ => {
	            docs_to_docs_by_name(varName, attr.elementTypeName, "elem", in, out)
                  }
                }
	      }
            }
          }

          if (attr.isHasMany) {
            make_many
          } else {
            make_one
          }
        }

        def make_part_gwt_doc(obj: GaejEntityObjectEntity) {
          for (attr <- obj.attributes) {
            attr.attributeType match {
              case p: GaejEntityPartType => {
                val part = p.part
                val partGwtDocName = gwtContext.documentName(part)
                buffer.method("public " + partGwtDocName + " gwt_" + partGwtDocName + "(" + part.documentName + " model)") {
                  buffer.makeVar("gwt", partGwtDocName)
                  for (partAttr <- part.attributes) {
                    gaej_to_gwt(partAttr)
                  }
                  buffer.makeReturn("gwt")
                }
              }
              case _ => {}
            }
          }
          for (attr <- obj.attributes) {
            attr.attributeType match {
              case p: GaejEntityPartType => make_part_gwt_doc(p.part)
              case _ => {}
            }
          }
        }

        buffer.method("public " + gwtDocName + " gwt_" + gwtDocName + "(" + gaejDocName + " model)") {
          buffer.makeVar("gwt", gwtDocName)
          for (attr <- anEntity.attributes) {
            gaej_to_gwt(attr);
          }
          buffer.makeReturn("gwt")
        }
        make_part_gwt_doc(anEntity)
      }

      def make_model_document {
        val docName = anEntity.documentName

        def gwt_to_gaej(attr: GaejAttribute) {
          val attrName = gwtContext.gaejContext.attributeName(attr)
          val varName = gwtContext.gaejContext.variableName(attr)

          def make_one {
            val in = "gwt"
            val out = "model"
            attr.attributeType match {
              case v: GaejIntegerType => {
                buffer.print("model.")
                buffer.print(varName)
                buffer.print(" = ")
                buffer.print("new BigInteger(")
                buffer.print("gwt.")
                buffer.print(varName)
                buffer.println(");")
              }
              case v: GaejDecimalType => {
                buffer.print("model.")
                buffer.print(varName)
                buffer.print(" = ")
                buffer.print("new BigDecimal(")
                buffer.print("gwt.")
                buffer.print(varName)
                buffer.println(");")
              }
              case e: GaejEntityType => {
                val refIdName = gwtContext.gaejContext.variableName4RefId(attr)
	        doc_to_doc_by_name(refIdName, in, out)
	      }
	      case e: GaejDateTimeType => {
	        doc_to_doc_by_name(varName, in, out)
	        doc_to_doc_by_name(varName + "_date", in, out)
	        doc_to_doc_by_name(varName + "_time", in, out)
	        doc_to_doc_by_name(varName + "_now", in, out)
	        doc_to_doc_by_name(varName + "_string", in, out)
	        doc_to_doc_by_name(varName + "_date_string", in, out)
	        doc_to_doc_by_name(varName + "_time_string", in, out)
	        doc_to_doc_by_name(varName + "_now_string", in, out)
	      }
              case p: GaejEntityPartType => {
                buffer.print("model.")
                buffer.print(varName)
                buffer.print(" = ")
                buffer.print("model_")
                buffer.print(p.part.documentName)
                buffer.print("(gwt.")
                buffer.print(varName)
                buffer.println(");")
              }
              case _ => {
                attr.attributeType.objectTypeName match {
                  case "User" => {
                    doc_to_doc_by_name_with_converter(varName, in, out, "gwt_user")
                  }
                  case "Text" => {
                    doc_to_doc_by_name_with_converter(varName, in, out, "gwt_text")
                  }
                  case "Category" => {
                    doc_to_doc_by_name_with_converter(varName, in, out, "gwt_category")
                  }
                  case _ => doc_to_doc_by_name(varName, in, out)
                }
	      }
            }
          }

          def make_many {
            val in = "gwt"
            val out = "model"
            attr.attributeType match {
              case v: GaejIntegerType => {
                buffer.makeFor("String", "elem", "gwt." + varName) {
                  buffer.print("model.")
                  buffer.print(varName)
                  buffer.print(".add(new BigInteger(")
                  buffer.println("elem));")
                }
              }
              case v: GaejDecimalType => {
                buffer.makeFor("String", "elem", "gwt." + varName) {
                  buffer.print("model.")
                  buffer.print(varName)
                  buffer.print(".add(new BigDecimal(")
                  buffer.println("elem));")
                }
              }
              case e: GaejEntityType => {
                val refIdName = gwtContext.gaejContext.variableName4RefId(attr)
                val refIdType = gwtContext.gaejContext.attributeTypeName4RefId(attr)
	        docs_to_docs_by_name(refIdName, refIdType, "elem", in, out)
	      }
	      case e: GaejDateTimeType => {
	        docs_to_docs_by_name(varName, "Date", "elem", in, out)
	        docs_to_docs_by_name(varName + "_date", "Date", "elem", in, out)
	        docs_to_docs_by_name(varName + "_time", "Date", "elem", in, out)
	        docs_to_docs_by_name(varName + "_now", "boolean", "elem", in, out)
	        docs_to_docs_by_name(varName + "_string", "String", "elem", in, out)
	        docs_to_docs_by_name(varName + "_date_string", "String", "elem", in, out)
	        docs_to_docs_by_name(varName + "_time_string", "String", "elem", in, out)
	        docs_to_docs_by_name(varName + "_now_string", "String", "elem", in, out)
	      }
              case p: GaejEntityPartType => {
                buffer.makeFor(gwtContext.documentName(p.part), "elem", "gwt." + varName) {
                  buffer.print("model.")
                  buffer.print(varName)
                  buffer.print(".add(model_")
                  buffer.print(p.part.documentName)
                  buffer.println("(elem));")
                }
              }
              case _ => {
                attr.elementTypeName match {
                  case "User" => {
	            docs_to_docs_by_name_with_converter(varName, "String", "elem", in, out, "gwt_user")
                  }
                  case "Text" => {
	            docs_to_docs_by_name_with_converter(varName, "String", "elem", in, out, "gwt_text")
                  }
                  case "Category" => {
	            docs_to_docs_by_name_with_converter(varName, "String", "elem", in, out, "gwt_category")
                  }
                  case _ => {
	            docs_to_docs_by_name(varName, attr.elementTypeName, "elem", in, out)
                  }
                }
	      }
            }
          }

          if (attr.isHasMany) {
            make_many
          } else {
            make_one
          }
        }

        def make_part_model_doc(obj: GaejEntityObjectEntity) {
          for (attr <- obj.attributes) {
            attr.attributeType match {
              case p: GaejEntityPartType => {
                val part = p.part
                val partModelDocName = part.documentName
                buffer.method("public " + partModelDocName + " model_" + partModelDocName + "(" + gwtContext.documentName(part) + " gwt)") {
                  buffer.makeVar("model", partModelDocName)
                  for (partAttr <- part.attributes) {
                    gwt_to_gaej(partAttr)
                  }
                  buffer.makeReturn("model")
                }
              }
              case _ => {}
            }
          }
          for (attr <- obj.attributes) {
            attr.attributeType match {
              case p: GaejEntityPartType => make_part_model_doc(p.part)
              case _ => {}
            }
          }
        }

        buffer.method("public " + docName + " model_" + docName + "(" + gwtContext.documentName(anEntity)  + " gwt)") {
          buffer.makeVar("model", docName, "new " + docName + "()")
          for (attr <- anEntity.attributes) {
            gwt_to_gaej(attr)
          }
          buffer.makeReturn("model")
        }
        make_part_model_doc(anEntity)
      }

      def doc_to_doc_by_name(name: String, in: String, out: String) {
        buffer.print(out)
        buffer.print(".")
        buffer.print(name)
        buffer.print(" = ")
        buffer.print(in)
        buffer.print(".")
        buffer.print(name)
        buffer.println(";")
      }

      def doc_to_doc_by_name_with_converter(name: String, in: String, out: String, converter: String) {
        buffer.print(out)
        buffer.print(".")
        buffer.print(name)
        buffer.print(" = ")
        buffer.print(converter)
        buffer.print("(")
        buffer.print(in)
        buffer.print(".")
        buffer.print(name)
        buffer.print(")")
        buffer.println(";")
      }

      def docs_to_docs_by_name(attrName: String, typeName: String, expr: String, in: String, out: String) {
        buffer.makeFor(typeName, "elem", in + "." + attrName) {
          buffer.print(out)
          buffer.print(".")
          buffer.print(attrName)
          buffer.print(".add(")
          buffer.print(expr)
          buffer.println(");")
        }
      }

      def docs_to_docs_by_name_with_converter(attrName: String, typeName: String, expr: String, in: String, out: String, converter: String) {
        buffer.makeFor(typeName, "elem", in + "." + attrName) {
          buffer.print(out)
          buffer.print(".")
          buffer.print(attrName)
          buffer.print(".add(")
          buffer.print(converter)
          buffer.print("(")
          buffer.print(expr)
          buffer.print(")")
          buffer.println(");")
        }
      }

      def create_operation {
        buffer.println()
        buffer.print("public ")
        buffer.print(gwtDocName)
        buffer.print(" create")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtDocName)
        buffer.println(" inDoc) {")
        buffer.indentUp
        buffer.makeVar("reqDoc", gaejDocName, "model_" + gaejDocName + "(inDoc)")
        buffer.print(gaejDocName)
        buffer.print(" respDoc = service.create")
        buffer.print(capitalizedTerm)
        buffer.println("(reqDoc);")
        buffer.makeVar("outDoc", gwtDocName, "gwt_" + gwtDocName + "(respDoc)")
        buffer.println("return outDoc;")
        buffer.indentDown
        buffer.println("}")
      }

      def read_operation {
        buffer.println()
        buffer.print("public ")
        buffer.print(gwtDocName)
        buffer.print(" read")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtContext.keyTypeName(anEntity))
        buffer.println(" key) {")
        buffer.indentUp
        buffer.print(gaejDocName)
        buffer.print(" respDoc = service.read")
        buffer.print(capitalizedTerm)
        buffer.println("(key);")
        buffer.makeVar("outDoc", gwtDocName, "gwt_" + gwtDocName + "(respDoc)")
        buffer.println("return outDoc;")
        buffer.indentDown
        buffer.println("}")
      }

      def update_operation {
        buffer.println()
        buffer.print("public void update")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtDocName)
        buffer.println(" inDoc) {")
        buffer.indentUp
        buffer.print(gaejDocName)
        buffer.println(" reqDoc = model_" + gaejDocName + "(inDoc);")
        buffer.print("service.update")
        buffer.print(capitalizedTerm)
        buffer.println("(reqDoc);")
        buffer.indentDown
        buffer.println("}")
      }

      def delete_operation {
        buffer.println()
        buffer.print("public void delete")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtContext.keyTypeName(anEntity))
        buffer.println(" key) {")
        buffer.indentUp
        buffer.print("service.delete")
        buffer.print(capitalizedTerm)
        buffer.println("(key);")
        buffer.indentDown
        buffer.println("}")
      }

      def query_operation {
        buffer.println()
        buffer.print("public ")
	buffer.print("Collection<")
        buffer.print(gwtDocName)
	buffer.print(">");
        buffer.print(" query")
        buffer.print(capitalizedTerm)
        buffer.println("(GwtQuery gwtQuery) {")
        buffer.indentUp
        buffer.print(queryName)
        buffer.print(" query = service.query")
        buffer.print(capitalizedTerm)
        buffer.println("();")
        buffer.print("List<")
        buffer.print(gaejDocName)
        buffer.println("> docs = query.execute();")
        buffer.print("ArrayList<")
        buffer.print(gwtDocName)
        buffer.print("> result = new ArrayList<")
        buffer.print(gwtDocName)
        buffer.println(">();")
        buffer.print("for (")
        buffer.print(gaejDocName)
        buffer.println(" doc : docs) {")
        buffer.indentUp
        buffer.print(gwtDocName)
        buffer.print(" outDoc = gwt_")
        buffer.print(gwtDocName)
        buffer.println("(doc);")
        buffer.println("result.add(outDoc);")
        buffer.indentDown
        buffer.println("}")
        buffer.println("return result;")
        buffer.indentDown
        buffer.println("}")
      }

      buffer.indentUp
      create_operation
      read_operation
      update_operation
      delete_operation
      query_operation
      make_gwt_document
      make_model_document
      buffer.indentDown
    }

    entities.foreach(make_object_crud_operation)
  }
}
