package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

/*
 * @since   Jun. 20, 2009
 *  version Nov.  9, 2009
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class GaejEntityPartEntity(aContext: GaejEntityContext) extends GaejEntityObjectEntity(aContext) {
  var modelEntityPart: SMEntityPart = null

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

      def make_import_object(anObject: GaejObjectReferenceType) {
        if (packageName != anObject.packageName) {
          make_import(anObject.qualifiedName)
        }
      }

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
      make_import("java.math.*")
      make_import("javax.jdo.*")
      make_import("javax.jdo.annotations.*")
      make_import("com.google.appengine.api.datastore.*")
      make_import("com.google.appengine.api.users.User")
      make_import("javax.xml.parsers.*")
      make_import("javax.xml.namespace.NamespaceContext")
      make_import("javax.xml.xpath.*")
      make_import("org.w3c.dom.Document")
      make_import("org.w3c.dom.Node")
      make_import("org.xml.sax.InputSource")
      make_import("java.io.StringReader")
      if (_baseObject != null) {
        make_import_object(_baseObject)
      }
      val attrs = attributes.filter(_.attributeType.isEntity)
      for (attr <- attrs) {
        val entity = attr.attributeType.asInstanceOf[GaejEntityType]
        make_import_entity(entity)
      }
      if (isPrint)
        buffer.println()
    }

    make_package
    make_imports
    writeModel(buffer)
    out.append(buffer.toString)
    out.flush()
  }

  final def writeModel(buffer: JavaTextMaker) {
/*
    def get_type(anAttr: GaejAttribute) = {
      anAttr.typeName
    }
*/

    def make_attribute_variables {
      for (attr <- attributes) {
        make_attribute_variable(attr)
        buffer.println()
      }
/*
      buffer.println("@Persistent")
      buffer.println("private Date _entity_updated = null;")
      buffer.println("@Persistent")
      buffer.println("private Text _entity_info = null;")
      buffer.println("@NotPersistent")
      buffer.println("private transient MDEntityInfo entity_info;")
      buffer.println()
*/
    }

    def make_attribute_variable(attr: GaejAttribute) {
      def make_persistent_annotation {
        attr.isPersistentOption match {
          case Some(true) => buffer.println("@Persistent")
          case Some(false) => buffer.println("@NotPersistent")
          case None => // do nothing
        }
      }

      def make_key_long {
        buffer.println("@PrimaryKey")
        buffer.println("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
        buffer.print("private Long ")
        buffer.print(attr.name)
        buffer.println(";")
      }

      def make_key_unencoded_string {
        buffer.println("@PrimaryKey")
        buffer.print("private String ")
        buffer.print(attr.name)
        buffer.println(";")
      }

      def make_key_key {
        buffer.println("@PrimaryKey")
        buffer.println("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
        buffer.print("private Key ")
        buffer.print(attr.name)
        buffer.println(";")
      }

      def make_key_as_encoded_string {
        buffer.println("@PrimaryKey")
        buffer.println("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
        buffer.println("@Extension(vendorName=\"datanucleus\", key=\"gae.encoded-pk\", value=\"true\")")
        buffer.print("private String ")
        buffer.print(attr.name)
        buffer.println(";")
      }

      def make_reference_key_long(entityType: GaejEntityType) { // XXX
        val idType = if (attr.isHasMany) {
          "List<" + entityType.entity.idAttr.typeName + ">"
        } else {
          entityType.entity.idAttr.typeName
        }
//        make_persistent_annotation
        buffer.print("private ")
        buffer.print(idType)
        buffer.print(" ")
        buffer.print(attr.name)
        buffer.print("_")
        buffer.print(entityType.entity.idName)
        if (attr.isHasMany) {
          buffer.print(" = new ArrayList<")
          buffer.print(entityType.entity.idAttr.typeName)
          buffer.print(">()")
        }
        buffer.println(";")
        buffer.println("@NotPersistent")
        buffer.print("private transient ")
        buffer.print(java_type(attr))
        buffer.print(" ")
        buffer.print(attr.name)
        if (attr.isHasMany) {
          buffer.print(" = new ArrayList<")
          buffer.print(java_element_type(attr))
          buffer.print(">()")
        } else {
          buffer.print(" = null")
        }
        buffer.println(";")
      }

      def make_reference_key_unencoded_string(entityType: GaejEntityType) {
        val idType = if (attr.isHasMany) {
          "List<" + entityType.entity.idAttr.typeName + ">"
        } else {
          entityType.entity.idAttr.typeName
        }
        make_persistent_annotation
        buffer.print("private ")
        buffer.print(idType)
        buffer.print(" ")
        buffer.print(attr.name)
        buffer.print("_")
        buffer.print(entityType.entity.idName)
        if (attr.isHasMany) {
          buffer.print(" = new ArrayList<")
          buffer.print(entityType.entity.idAttr.typeName)
          buffer.print(">()")
        }
        buffer.println(";")
        buffer.println("@NotPersistent")
        buffer.print("private transient ")
        buffer.print(java_type(attr))
        buffer.print(" ")
        buffer.print(attr.name)
        if (attr.isHasMany) {
          buffer.print(" = new ArrayList<")
          buffer.print(java_element_type(attr))
          buffer.print(">()")
        } else {
          buffer.print(" = null")
        }
        buffer.println(";")
      }

      if (attr.isId) {
        attr.idPolicy match {
          case SMAutoIdPolicy => make_key_long
          case SMApplicationIdPolicy => make_key_unencoded_string
        }
      } else {
        attr.attributeType match {
          case e: GaejEntityType => {
            e.entity.idPolicy match {
              case SMAutoIdPolicy => make_reference_key_long(e)
              case SMApplicationIdPolicy => make_reference_key_unencoded_string(e)
            }
          }
          case _ => {
            make_persistent_annotation
            buffer.print("private ")
            buffer.print(java_type(attr))
            buffer.print(" ")
            buffer.print(attr.name)
            if (attr.isHasMany) {
              buffer.print(" = new ArrayList<")
              buffer.print(java_element_type(attr));
              buffer.print(">()")
            }
            buffer.println(";")
          }
        }
      }
    }

    def make_attribute_methods {
      for (attr <- attributes) {
        make_attribute_method(attr)
        buffer.println()
      }
    }

    def make_attribute_method(attr: GaejAttribute) {
      def make_reference_attribute_method(entityType: GaejEntityType) {
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attr.name.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.print("return ")
        buffer.print(attr.name)
        buffer.println(";")
        buffer.indentDown
        buffer.println("}")
        if (!attr.isId || attr.isId) { // XXX
          buffer.println()
          buffer.print("public void set")
          buffer.print(attr.name.capitalize)
          buffer.print("(")
          buffer.print(java_type(attr))
          buffer.print(" ")
          buffer.print(attr.name)
          buffer.println(") {")
          buffer.indentUp
          buffer.print("this.")
          buffer.print(attr.name)
          buffer.print(" = ")
          buffer.print(attr.name)
          buffer.println(";")
          buffer.print("this.")
          buffer.print(attr.name)
          buffer.print("_")
          buffer.print(entityType.entity.idName)
          buffer.print(" = ")
          buffer.print(attr.name)
          buffer.print(".")
          buffer.print("get")
          buffer.print(entityType.entity.idName.capitalize)
          buffer.println("();")
          buffer.indentDown
          buffer.println("}")
        }
      }

      def make_value_attribute_method {
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attr.name.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.print("return ")
        buffer.print(attr.name)
        buffer.println(";")
        buffer.indentDown
        buffer.println("}")
        if (attr.attributeType == GaejBooleanType) {
          buffer.method("public boolean is" + attr.name.capitalize + "()") {
            buffer.makeReturn(attr.name)
          }
        }
        if (!attr.isId || attr.isId) { // XXX
          buffer.println()
          buffer.print("public void set")
          buffer.print(attr.name.capitalize)
          buffer.print("(")
          buffer.print(java_type(attr))
          buffer.print(" ")
          buffer.print(attr.name)
          buffer.println(") {")
          buffer.indentUp
          buffer.print("this.")
          buffer.print(attr.name)
          buffer.print(" = ")
          buffer.print(attr.name)
          buffer.println(";")
          buffer.indentDown
          buffer.println("}")
        }
      }

      def make_multi_reference_attribute_method(entityType: GaejEntityType) {
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attr.name.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.makeIfElse(attr.name + " != null") {
          buffer.print("return Collections.unmodifiableList(")
          buffer.print(attr.name)
          buffer.println(");")
        }
        buffer.makeElse {
          buffer.println("return Collections.emptyList();")
        }
        buffer.indentDown
        buffer.println("}")
        if (!attr.isId || attr.isId) { // XXX
          buffer.println()
          buffer.print("public void set")
          buffer.print(attr.name.capitalize)
          buffer.print("(")
          buffer.print(java_type(attr))
          buffer.print(" ")
          buffer.print(attr.name)
          buffer.println(") {")
          buffer.indentUp
          buffer.print("this.")
          buffer.print(attr.name)
          buffer.print(" = new ArrayList<")
          buffer.print(java_element_type(attr))
          buffer.print(">(")
          buffer.print(attr.name)
          buffer.println(");")
          buffer.makeFor(java_element_type(attr) + " element: this." + attr.name) {
            buffer.print("this.")
            buffer.print(attr.name)
            buffer.print("_")
            buffer.print(entityType.entity.idName)
            buffer.print(".add(element.get")
            buffer.print(entityType.entity.idName.capitalize)
            buffer.println("());")
          }
          buffer.indentDown
          buffer.println("}")
        }
      }

      def make_multi_value_attribute_method {
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attr.name.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.makeIfElse(attr.name + " != null") {
          buffer.print("return Collections.unmodifiableList(")
          buffer.print(attr.name)
          buffer.println(");")
        }
        buffer.makeElse {
          buffer.println("return Collections.emptyList();")
        }
        buffer.indentDown
        buffer.println("}")
        buffer.println()
        buffer.method("public void set" + attr.name.capitalize + "(" + java_type(attr) + " " + attr.name + ")") {
          buffer.print("this.")
          buffer.print(attr.name)
          buffer.print(" = new ArrayList<")
          buffer.print(java_element_type(attr))
          buffer.print(">(")
          buffer.print(attr.name)
          buffer.println(");")
        }
        buffer.method("public void add"  + attr.name.capitalize + "(" + java_element_type(attr) + " " + attr.name + ")") {
          buffer.makeIf("this." + attr.name + " == null") {
            buffer.print("this.")
            buffer.print(attr.name)
            buffer.print(" = new ArrayList<")
            buffer.print(java_element_type(attr))
            buffer.println(">();")
          }
          buffer.print("this.")
          buffer.print(attr.name)
          buffer.print(".add(")
          buffer.print(attr.name)
          buffer.println(");")
        }
      }

      if (attr.isHasMany) {
        attr.attributeType match {
          case e: GaejEntityType => {
            val entityRefName = attr.name + "_" + e.entity.idName
            make_multi_reference_attribute_method(e)
            make_multi_get_asString_methods(e.entity.idName, entityRefName, "String", "elem", buffer)
          }
          case p: GaejEntityPartEntity => {
            make_multi_get_asString_methods(attr.name, java_doc_element_type(attr), "elem.toString()", buffer)
          }
          case _ => {
            make_multi_value_attribute_method
            make_multi_get_asString_methods(attr.name, java_element_type(attr), "elem.toString()", buffer)
          }
        }
      } else {
        attr.attributeType match {
          case e: GaejEntityType => {
            val entityRefName = attr.name + "_" + e.entity.idName
            make_reference_attribute_method(e)
            make_single_object_get_asString(e.entity.idName, entityRefName, entityRefName, buffer)
          }
          case p: GaejEntityPartEntity => {
            make_single_object_get_asString(attr.name, attr.name + ".toString()", buffer)
          }
          case _ => {
            make_value_attribute_method
            if (attr.isDataType) {
              make_single_datatype_get_asString(attr.name, "Util.datatype2string(" + attr.name + ")", buffer)
            } else {
              make_single_object_get_asString(attr.name, attr.name + ".toString()", buffer)
            }
          }
        }
      }
    }

    def make_null_constructor {
      buffer.method("public " + name + "()") {
      }
    }

    def make_init_constructor {
      buffer.method("public " + name + "(" + documentName + " doc, PersistenceManager pm)") {
        buffer.println("init_document(doc, pm);")
      }
    }

    def build_attributes_from_xml = {
      val buf = new JavaTextMaker
      buf.println("int length;")
      for (attr <- attributes) {
        def build_attribute_datatype_one {
          attr.attributeType match {
            case e: GaejEntityType => error("don't route")
            case p: GaejEntityPartType => error("don't route")
            case _ => {
              buf.print(attr.name)
              buf.print(" = Util.string2")
              buf.print(attr.typeName)
              buf.print("((String)xpath.evaluate(\"")
              buf.print("/ns:")
              buf.print(xmlElementName)
              buf.print("/ns:")
              buf.print(attr.name)
              buf.println("\", doc, XPathConstants.STRING));")
            }
          }
        }

        def build_attribute_object_one {
          attr.attributeType match {
            case t: GaejDateTimeType => {
              buf.print(attr.name)
              buf.print(" = Util.string2dateTime")
              buf.print("((String)xpath.evaluate(\"")
              buf.print("/ns:")
              buf.print(xmlElementName)
              buf.print("/ns:")
              buf.print(attr.name)
              buf.println("\", doc, XPathConstants.STRING));")
            }
            case t: GaejUserType => {
              buf.print(attr.name)
              buf.println(" = new User(")
              buf.print("(String)xpath.evaluate(\"")
              buf.print("/ns:")
              buf.print(xmlElementName)
              buf.print("/ns:")
              buf.print(attr.name)
              buf.print("/gaau:user/gaau:email\", doc, XPathConstants.STRING)")
              buf.println(", ")
              buf.print("(String)xpath.evaluate(\"")
              buf.print("/ns:")
              buf.print(xmlElementName)
              buf.print("/ns:")
              buf.print(attr.name)
              buf.print("/gaau:user/gaau:authDomain\", doc, XPathConstants.STRING)")
              buf.println(", ")
              buf.print("(String)xpath.evaluate(\"")
              buf.print("/ns:")
              buf.print(xmlElementName)
              buf.print("/ns:")
              buf.print(attr.name)
              buf.print("/gaau:user/gaau:userId\", doc, XPathConstants.STRING)")
              buf.println(");")
            }
            case e: GaejEntityType => {
              buf.print(attr.name)
              buf.print("_")
              buf.print(e.entity.idName)
              buf.print(" = (String)xpath.evaluate(\"")
              buf.print("/ns:")
              buf.print(xmlElementName)
              buf.print("/ns:")
              buf.print(attr.name)
              buf.println("\", doc, XPathConstants.STRING);")
              buf.print(attr.name)
              buf.print(" = ")
              buf.print(e.entity.name)
              buf.print(".get_entity(")
              buf.print(attr.name)
              buf.print("_")
              buf.print(e.entity.idName)
              buf.print(", pm);")
              buf.println()
            }
            case p: GaejEntityPartType => error("not implemented yet")
            case _ => {
              buf.print(attr.name)
              buf.print(" = Util.string2")
              buf.print(attr.typeName)
              buf.print("((String)xpath.evaluate(\"")
              buf.print("/ns:")
              buf.print(xmlElementName)
              buf.print("/ns:")
              buf.print(attr.name)
              buf.println("\", doc, XPathConstants.STRING));")
            }
          }
        }

        def build_attribute_object_many {
          def build_body(attrName: String, typeName: String)  {
            val nodesVarName = attrName + "Nodes"
            buf.print(attrName)
            buf.println(".clear();");
            buf.print("NodeList ")
            buf.print(nodesVarName)
            buf.print(" = (NodeList)xpath.evaluate(\"")
            buf.print("/ns:")
            buf.print(xmlElementName)
            buf.print("/ns:")
            buf.print(attrName)
            buf.println("\", doc, XPathConstants.NODESET);")
            buf.print("length = ")
            buf.print(nodesVarName)
            buf.println(".getLength();")
            buf.makeFor("int i = 0;i < length;i++") {
              buf.makeVar("node", "Node", nodesVarName + ".item(i)")
              buf.print(attrName)
              buf.print(".add(Util.string2")
              buf.print(typeName)
              buf.println("(node.getTextContent()));")
            }
          }

          attr.attributeType match {
            case e: GaejEntityType => build_body(attr.name + "_" + e.entity.idName, "String")
            case p: GaejEntityPartType => error("not implemented yet")
            case _ => build_body(attr.name, attr.elementTypeName)
            }
          }

        if (attr.isDataType) {
          build_attribute_datatype_one
        } else if (!attr.isHasMany) {
          build_attribute_object_one
        } else {
          build_attribute_object_many
        }
      }
      buf.toString
    }

    def make_string_constructor {
      val code = """    try {
      DocumentBuilderFactory xdoc_factory = DocumentBuilderFactory.newInstance();
      xdoc_factory.setNamespaceAware(true);
      DocumentBuilder builder = xdoc_factory.newDocumentBuilder();
      InputSource is = new InputSource(new StringReader(string));
      Document doc = builder.parse(is);
      XPathFactory xpath_factory = XPathFactory.newInstance();
      XPath xpath = xpath_factory.newXPath();
      xpath.setNamespaceContext(ns_context);
%build%
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
"""
      buffer.method("public " + name + "(String string, PersistenceManager pm)") {
        buffer.append(code, Map("%build%" -> build_attributes_from_xml))
      }
    }

    def make_xml_element_constructor {
      val code = """    try {
      XPathFactory xpath_factory = XPathFactory.newInstance();
      XPath xpath = xpath_factory.newXPath();
      xpath.setNamespaceContext(ns_context);
%build%
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
"""
      buffer.method("public " + name + "(Node doc, PersistenceManager pm)") {
        buffer.append(code, Map("%build%" -> build_attributes_from_xml))
      }
    }

/*
      MDEntityInfo info = new MDEntityInfo();
      info.set_created(xpath.evaluate("/ns:info/@created", doc, XPathConstants.STRING));
      info.set_updated(xpath.evaluate("/ns:info/@updated", doc, XPathConstants.STRING));
      info.set_removed(xpath.evaluate("/ns:info/@removed", doc, XPathConstants.STRING));
      info.set_authors((NodeList)xpath.evaluate("/ns:info/author", doc, XPathConstants.NODESET));
      return info;
*/

    def make_ns_context {
      val code = """
   private static NamespaceContext ns_context = new NamespaceContext() {
    @Override
    public String getNamespaceURI(String prefix) {
      if (prefix == null) {
        throw new IllegalArgumentException();
      } else if ("%prefix%".equals(prefix)) {
        return "%uri%";
      } else if ("%gaadPrefix%".equals(prefix)) {
        return "%gaadUri%";
      } else if ("%gaauPrefix%".equals(prefix)) {
        return "%gaauUri%";
      } else {
        return null;
      }
    }

    @Override
    public String getPrefix(String namespaceURI) {
      if (namespaceURI == null) {
	throw new IllegalArgumentException();
      } else if ("%uri%".equals(namespaceURI)) {
        return "%prefix%";
      } else if ("%gaadUri".equals(namespaceURI)) {
        return "%gaadPrefix%%";
      } else if ("%gaauUri%".equals(namespaceURI)) {
        return "%gaauPrefix%";
      } else {
        return null;
      }
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
      if (namespaceURI == null) {
        throw new IllegalArgumentException();
      } else if ("%uri%".equals(namespaceURI)) {
        return Arrays.asList("%prefix%").iterator();
      } else if ("%gaadUri".equals(namespaceURI)) {
        return Arrays.asList("%gaadPrefix%%").iterator();
      } else if ("%gaauUri%".equals(namespaceURI)) {
        return Arrays.asList("%gaauPrefix%").iterator();
      } else {
        return null;
      }
    }
  };
"""
      buffer.append(code, Map("%uri%" -> xmlNamespace,
                              "%prefix%" -> "ns",
                              "%gaadUri%" -> "http://appengine.google.com/api/datastore/",
                              "%gaadPrefix%" -> "gaad", 
                              "%gaauUri%" -> "http://appengine.google.com/api/users/",
                              "%gaauPrefix%" -> "gaau"))
    }

    def make_toString_method {
      buffer.method("public String toString()") {
        buffer.makeStringBuilderVar
        buffer.println("entity_asXmlString(buf);")
        buffer.makeStringBuilderReturn
      }
    }

    def make_init_method {
      buffer.println()
      buffer.print("public void init_document(")
      buffer.print(documentName)
      buffer.println(" doc, PersistenceManager pm) {")
      buffer.indentUp
      for (attr <- attributes) {
        make_update_attribute(attr)
      }
      buffer.indentDown
      buffer.println("}")
    }

    def make_update_method {
      buffer.println()
      buffer.print("public void update_document(")
      buffer.print(documentName)
      buffer.println(" doc, PersistenceManager pm) {")
      buffer.indentUp
      buffer.print("if (doc.")
      buffer.print(idName)
      buffer.println(" != null) {")
      buffer.indentUp
      buffer.print("if (!doc.")
      buffer.print(idName)
      buffer.print(".equals(")
      buffer.print(idName)
      buffer.println(")) {")
      buffer.indentUp
      buffer.println("throw new IllegalArgumentException(\"XXX\");")
      buffer.indentDown
      buffer.println("}")
      buffer.indentDown
      buffer.println("}")
      for (attr <- attributes if !attr.isId) {
        make_update_attribute(attr)
      }
      buffer.indentDown
      buffer.println("}")
    }

    def make_update_attribute(attr: GaejAttribute) {
      def make_update_attribute_one(attr: GaejAttribute) {
        attr.attributeType match { // sync GaejDomainServiceEntity
          case t: GaejDateTimeType => {
            buffer.print("if (doc.")
            buffer.print(attr.name)
            buffer.print(" != null || doc.")
            buffer.print(attr.name)
            buffer.print("_date != null || doc.")
            buffer.print(attr.name)
            buffer.print("_time != null || doc.")
            buffer.print(attr.name)
            buffer.print("_now")
            buffer.println(") {")
            buffer.indentUp
            buffer.print("this.")
            buffer.print(attr.name)
            buffer.print(" = ")
            buffer.print("Util.makeDateTime(")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print(", ")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_date, ")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_time, ")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_now")
            buffer.print(");")
            buffer.println()
            buffer.indentDown
            buffer.println("}")
          }
          case e: GaejEntityType => {
            buffer.print("if (doc.")
            buffer.print(e.entity.idName)
            buffer.println(" != null) {")
            buffer.indentUp
            buffer.print("this.") // make_reference_key_unencoded_string
            buffer.print(attr.name)
            buffer.print("_")
            buffer.print(e.entity.idName)
            buffer.print(" = ")
            buffer.print("doc.")
            buffer.print(e.entity.idName)
            buffer.println(";")
            buffer.print("this.")
            buffer.print(attr.name)
            buffer.print(" = ")
            buffer.print(e.entity.name)
            buffer.print(".get_entity(doc.")
            buffer.print(e.entity.idName)
            buffer.print(", pm);")
            buffer.println()
            buffer.indentDown
            buffer.println("}")
          }
          case p: GaejEntityPartType => error("not implemented yet")
          case _ => {
            if (attr.isDataType) {
              buffer.print("this.")
              buffer.print(attr.name)
              buffer.print(" = ")
              buffer.print("doc.")
              buffer.print(attr.name)
              buffer.print(";")
              buffer.println()
            } else {
              buffer.print("if (doc.")
              buffer.print(attr.name)
              buffer.println(" != null) {")
              buffer.indentUp
              buffer.print("this.")
              buffer.print(attr.name)
              buffer.print(" = ")
              buffer.print("doc.")
              buffer.print(attr.name)
              buffer.print(";")
              buffer.println()
              buffer.indentDown
              buffer.println("}")
            }
          }
        }
      }

      def make_update_attribute_many(attr: GaejAttribute) {
        attr.attributeType match { // sync GaejDomainServiceEntity
          case t: GaejDateTimeType => { // XXX
            buffer.print("if (doc.")
            buffer.print(attr.name)
            buffer.print(" != null || doc.")
            buffer.print(attr.name)
            buffer.print("_date != null || doc.")
            buffer.print(attr.name)
            buffer.print("_time != null || doc.")
            buffer.print(attr.name)
            buffer.print("_now")
            buffer.println(") {")
            buffer.indentUp
            buffer.print("this.")
            buffer.print(attr.name)
            buffer.print(" = ")
            buffer.print("Util.makeDateTime(")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print(", ")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_date, ")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_time, ")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_now")
            buffer.print(");")
            buffer.println()
            buffer.indentDown
            buffer.println("}")
          }
          case e: GaejEntityType => {
            // make_reference_key_unencoded_string
            val idVarName = "this." + attr.name + "_" + e.entity.idName
            buffer.print(idVarName)
            buffer.print(" = new ArrayList<")
            buffer.print(e.entity.idAttr.elementTypeName)
            buffer.print(">(")
            buffer.print("doc.")
            buffer.print(e.entity.idName)
            buffer.println(");")
            buffer.print("this.")
            buffer.print(attr.name)
            buffer.print(" = new ArrayList<")
            buffer.print(java_element_type(attr))
            buffer.println(">();")
            buffer.makeFor("String id : " + idVarName) {
              buffer.print("this.")
              buffer.print(attr.name)
              buffer.print(".add(")
              buffer.print(e.entity.name)
              buffer.println(".get_entity(id, pm));")
            }
            buffer.println()
          }
          case p: GaejEntityPartType => error("not implemented yet")
          case _ => {
            buffer.print("this.")
            buffer.print(attr.name)
            buffer.print(" = new ArrayList<")
            buffer.print(java_element_type(attr))
            buffer.print(">(doc.")
            buffer.print(attr.name)
            buffer.print(");")
            buffer.println()
          }
        }
      }

      if (attr.isHasMany) {
        make_update_attribute_many(attr)
      } else {
        make_update_attribute_one(attr)
      }
    }

    def make_document_method {
      def make_one(attr: GaejAttribute) {
        attr.attributeType match {
          case t: GaejDateTimeType => {
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print(" = this.")
            buffer.print(attr.name)
            buffer.println(";")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_date = Util.makeDate(this.")
            buffer.print(attr.name)
            buffer.print(");")
            buffer.println()
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_time = Util.makeTime(this.")
            buffer.print(attr.name)
            buffer.print(");")
            buffer.println()
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_now = false;")
            buffer.println()
          }
          case e: GaejEntityType => {
            buffer.print("doc.")
            buffer.print(e.entity.idName)
            buffer.print(" = this.")
            buffer.print(attr.name)
            buffer.print("_")
            buffer.print(e.entity.idName)
            buffer.println(";")
/*
            buffer.print("doc.")
            buffer.print(e.entity.idName)
            buffer.print(" = ")
            buffer.print(attr.name)
            buffer.print(".")
            buffer.print("get")
            buffer.print(e.entity.idName.capitalize)
            buffer.println("();")
*/
          }
          case p: GaejEntityPartType => error("not implemented yet")
          case _ => {
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print(" = this.")
            buffer.print(attr.name)
            buffer.println(";")
          }
        }
      }        

      def make_many(attr: GaejAttribute) {
        attr.attributeType match {
          case t: GaejDateTimeType => { // XXX
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print(" = ")
            buffer.print(attr.name)
            buffer.println(";")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_date = Util.makeDate(")
            buffer.print(attr.name)
            buffer.print(");")
            buffer.println()
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_time = Util.makeTime(")
            buffer.print(attr.name)
            buffer.print(");")
            buffer.println()
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_now = false;")
            buffer.println()
          }
          case e: GaejEntityType => {
            buffer.makeIf("this." + attr.name + "_" + e.entity.idName + " != null") {
              buffer.print("doc.")
              buffer.print(e.entity.idName)
              buffer.print(".addAll(this.")
              buffer.print(attr.name)
              buffer.print("_")
              buffer.print(e.entity.idName)
              buffer.println(");")
            }
/*
            buffer.print("doc.")
            buffer.print(e.entity.idName)
            buffer.print(" = ")
            buffer.print(attr.name)
            buffer.print(".")
            buffer.print("get")
            buffer.print(e.entity.idName.capitalize)
            buffer.println("();")
*/
          }
          case p: GaejEntityPartType => error("not implemented yet")
          case _ => {
            buffer.makeIf("this." + attr.name + " != null") {
              buffer.print("doc.")
              buffer.print(attr.name)
              buffer.print(".addAll(this.")
              buffer.print(attr.name)
              buffer.println(");")
            }
          }
        }
      }

      buffer.println()
      buffer.print("public ")
      buffer.print(documentName)
      buffer.println(" make_document() {")
      buffer.indentUp
      buffer.print(documentName)
      buffer.print(" doc = new ")
      buffer.print(documentName)
      buffer.println("();")
      for (attr <- attributes) {
        if (attr.isHasMany) {
          make_many(attr)
        } else {
          make_one(attr)
        }
      }
      buffer.println("return doc;")
      buffer.indentDown
      buffer.println("}")
    }

    def make_make_persistent_method {
      buffer.println()
      buffer.println("public void make_persistent(PersistenceManager pm) {")
      buffer.indentUp
      buffer.println("entity_sync();")
      buffer.println("pm.makePersistent(this);")
      buffer.println("MEEntityModelInfo.updateEntityModel(pm, entity_model.entity.name, entity_model.entity.version, entity_model.entity.build, entity_model.name, entity_model.version, entity_model.build);");
      buffer.indentDown
      buffer.println("}")
    }

    def make_delete_persistent_method {
      buffer.println()
      buffer.println("public void delete_persistent(PersistenceManager pm) {")
      buffer.indentUp
      buffer.println("pm.deletePersistent(this);")
      buffer.indentDown
      buffer.println("}")
    }

    def make_get_entity_method {
      buffer.println()
      buffer.print("public static ")
      buffer.print(name)
      buffer.print(" get_entity(")
      buffer.print(idAttr.typeName)
      buffer.println(" key, PersistenceManager pm) {")
      buffer.indentUp
      buffer.print("return pm.getObjectById(")
      buffer.print(name)
      buffer.println(".class, key);")
      buffer.indentDown
      buffer.println("}")
    }

    def make_delete_entity_method {
      buffer.println()
      buffer.print("public static void delete_entity(")
      buffer.print(idAttr.typeName)
      buffer.println(" key, PersistenceManager pm) {")
      buffer.indentUp
      buffer.print(name)
      buffer.println(" entity = get_entity(key, pm);")
      buffer.println("entity.delete_persistent(pm);")
      buffer.indentDown
      buffer.println("}")
    }

    def make_entity_info {
      var code = """
  public Date entity_getUpdated() {
    return _entity_updated;
  }

  public MDEntityInfo entity_getEntityInfo() {
    ensure_entityInfo();
    return entity_info;
  }

  public void entity_sync() {
    if (entity_info == null) {
      if (_entity_info == null) {
        entity_create();
      } else {
        entity_update();
      }
    }
    _entity_updated = entity_info.sync();
  }

  public void entity_create() {
    ensure_entityInfo();
    entity_info.model = entity_model;
    entity_info.create();
    _entity_info = new Text(entity_info.toString());
  }

  public void entity_update() {
    ensure_entityInfo();
    entity_info.model = entity_model;
    entity_info.update();
    _entity_info = new Text(entity_info.toString());
  }

  public void entity_remove() {
    ensure_entityInfo();
    entity_info.model = entity_model;
    entity_info.remove();
    _entity_info = new Text(entity_info.toString());
  }

  private void ensure_entityInfo() {
    if (entity_info != null) {
      return;
    }
    if (_entity_info != null) {
      entity_info = MDEntityInfo.reconstituteMDEntityInfo(_entity_info.getValue());
    } else {
      entity_info = new MDEntityInfo();
    }
  }

  private final static MDEntityInfo.MDModel entity_model = new MDEntityInfo.MDModel();

  static {
    entity_model.name = "";
    entity_model.version = "";
    entity_model.build = "";
    MDEntityInfo.MDModel.MDEntity entity = new MDEntityInfo.MDModel.MDEntity();
    entity.name = "%entityName%";
    entity.version = "";
    entity.build = "";
%addAttributes%
    entity_model.entity = entity;
  }
"""
      def add_attributes(buf: JavaTextMaker) {
        buf.indentUp
        buf.indentUp
        for (attr <- attributes) {
          val attrName = {
            if (attr.modelAttribute != null) {
              attr.modelAttribute.name
            } else if (attr.modelAssociation != null) {
              attr.modelAssociation.name
            } else {
              "?"
            }
          }
          val attrType = {
            if (attr.modelAttribute != null) {
              attr.modelAttribute.attributeType.qualifiedName
            } else if (attr.modelAssociation != null) {
              attr.modelAssociation.associationType.qualifiedName
            } else {
              "?"
            }
          }
          val columnName = attr.name
          val columnType = attr.attributeType.objectTypeName
          buf.println("""entity.addAttribute("%s", "%s", "%s", "%s");""".
                      format(attrName, attrType, columnName, columnType))
        }
        buf.indentDown
        buf.indentDown
      }

      buffer.append(code, Map("%entityName%" -> qualifiedName))
      buffer.replace("%addAttributes%")(add_attributes)
    }

    buffer.print("public class ")
    buffer.print(name)
    buffer.print(" {")
    buffer.println()
    buffer.indentUp
    make_attribute_variables
    make_null_constructor
    make_init_constructor
    make_string_constructor
    make_xml_element_constructor
    make_toString_method
    make_attribute_methods
    make_init_method
//    make_update_method
    make_document_method
//    make_make_persistent_method
//    make_delete_persistent_method
//    make_get_entity_method
//    make_delete_entity_method
    make_entity_asXmlString(buffer)
    make_build_xml_element(buffer)
    make_ns_context
    buffer.indentDown
//    make_entity_info
    buffer.println("}")
  }

/* 2009-07-20
  final def setBaseClass(aBaseClass: GaejEntityType) {
    _baseClass = aBaseClass
  }
*/
}
