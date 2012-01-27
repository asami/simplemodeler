package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString}
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Mar.  8, 2009
 * @version Jun. 25, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeObjectEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GEntity(aIn, aOut, aContext) {
  type DataSource_TYPE = GDataSource

  var packageName = ""
  var xmlNamespace = ""
  def qualifiedName = if (packageName != "") packageName + "." + name else name
  val attributes = new ArrayBuffer[GaeAttribute]
  var term = ""
  var term_en = ""
  var documentName = ""
  var modelObject: SMObject = null

  lazy val idName = attributes.find(_.isId) match {
      case Some(attr) => attr.name
      case None => throw new UnsupportedOperationException("no id")
  }

  private var _baseClass: GaeEntityType = null // XXX
  private var _belongsTo: GaeEntityType = null // XXX

  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: GEntityContext) = this(null, aContext)

  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  override protected def write_Content(out: BufferedWriter) {
    val buffer = new AppendableTextBuilder(out)

    def make_package {
      require (packageName != null)
      if (packageName != "") {
        buffer.print("package ")
        buffer.println(packageName)
        buffer.println()
      }
    }

    def make_imports {
      var isPrint = false

      def make_import(anEntity: GaeEntityType) {
        if (packageName != anEntity.packageName) {
          buffer.print("import ")
          buffer.println(anEntity.qualifiedName)
          isPrint = true
        }
      }

      if (_baseClass != null) {
        make_import(_baseClass)
      }
      val attrs = attributes.filter(_.attributeType.isEntity)
      for (attr <- attrs) {
        val entity = attr.attributeType.asInstanceOf[GaeEntityType]
        make_import(entity)
      }
      if (isPrint)
        buffer.println()
    }

    make_package
    make_imports
    writeModel(buffer)
    buffer.flush
  }

  final def writeModel(buffer: AppendableTextBuilder) {
    def get_type(anAttr: GaeAttribute) = {
      anAttr.typeLiteral
/*
      } else {
        if (anAttr.isOptional) "StringProperty(required=False,mutiline=False)"
        else if (anAttr.isHasMany) "StringListProperty(required=True,mutiline=False)"
        else "StringProperty(required=True,mutiline=False)"
      }
*/
    }

    def make_attributes {
      for (attr <- attributes) {
        buffer.print(attr.name)
        buffer.print(" = ")
        buffer.print("db.")
        buffer.print(get_type(attr))
        buffer.println()
      }
    }

    def make_init {
      buffer.println()
      buffer.println("def __init__(self, key_name=None, doc=None):")
      buffer.indentUp
      buffer.println("if not key_name:")
      buffer.indentUp
      buffer.println("if doc:")
      buffer.indentUp
      buffer.print("if doc.")
      buffer.print(idName)
      buffer.println(":")
      buffer.indentUp
      buffer.print("key_name = doc.")
      buffer.print(idName)
      buffer.println()
      buffer.indentDown
      buffer.indentDown
      buffer.indentDown
      buffer.println("if key_name:")
      buffer.indentUp
      buffer.print("super(")
      buffer.print(name)
      buffer.println(", self).__init__(key_name=key_name)")
      buffer.print("self.")
      buffer.print(idName)
      buffer.println(" = self.key_name")
      buffer.indentDown
      buffer.println("self._update(doc)")
      buffer.indentDown
    }

    def make_update {
      buffer.println()
      buffer.println("def _update(self, doc):")
      buffer.indentUp
      buffer.print("if doc.")
      buffer.print(idName)
      buffer.println(":")
      buffer.indentUp
      buffer.print("if '%s' % self.key().id_or_name() != doc.")
      buffer.print(idName)
      buffer.println(":")
      buffer.indentUp
      buffer.println("raise #")
      buffer.indentDown
      buffer.indentDown
      for (attr <- attributes if !attr.isId) {
        attr.attributeType match { // sync GaeDomainServiceEntity
          case t: GaeDateTimeType => {
            buffer.print("if doc.")
            buffer.print(attr.name)
            buffer.print(" or doc.")
            buffer.print(attr.name)
            buffer.print("_date or doc.")
            buffer.print(attr.name)
            buffer.print("_time or doc.")
            buffer.print(attr.name)
            buffer.print("_now")
            buffer.println(":")
            buffer.indentUp
            buffer.print("self.")
            buffer.print(attr.name)
            buffer.print(" = ")
            buffer.print("make_dateTime(")
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
            buffer.print(")")
            buffer.println()
            buffer.indentDown
          }
          case e: GaeEntityType => {
            buffer.print("if doc.")
            buffer.print(attr.name)
            buffer.println(":")
            buffer.indentUp
            buffer.print("self.")
            buffer.print(attr.name)
            buffer.print(" = ")
            buffer.print("make_reference_")
            buffer.print(UString.capitalize(e.entity.term))
            buffer.print("(doc.")
            buffer.print(attr.name)
            buffer.print(")")
            buffer.println()
            buffer.indentDown
          }
          case _ => {
            buffer.print("if doc.")
            buffer.print(attr.name)
            buffer.println(":")
            buffer.indentUp
            buffer.print("self.")
            buffer.print(attr.name)
            buffer.print(" = ")
            buffer.print("make_text(doc.")
            buffer.print(attr.name)
            buffer.print(")") // XXX or "?temporary?"
            buffer.println()
            buffer.indentDown
          }
/*
            buffer.print("if not self.")
            buffer.print(attr.name)
            buffer.println(":")
            buffer.indentUp
            buffer.print("if doc.")
            buffer.print(attr.name)
            buffer.println("_now:")
            buffer.indentUp
            buffer.print("self.")
            buffer.print(attr.name)
            buffer.println(" = datetime.now")
            buffer.indentDown
            buffer.println("else:")
            buffer.indentUp
            buffer.print("self.")
            buffer.print(attr.name)
            buffer.print(" = self.make_datetime(")
            buffer.print(attr.name)
            buffer.print("_date, ")
            buffer.print(attr.name)
            buffer.println("_time)")
            buffer.indentDown
            buffer.indentDown
          }
          case _ => //
*/
        }
      }
      buffer.indentDown
    }

    def make_document {
      buffer.println()
      buffer.println("def _document(self):")
      buffer.indentUp
      buffer.print("doc = ")
      buffer.print(documentName)
      buffer.println("()")
      buffer.print("doc.")
      buffer.print(idName)
      buffer.print(" = self.")
      buffer.println(idName)
      for (attr <- attributes if !attr.isId) {
        buffer.print("doc.")
        buffer.print(attr.name)
        buffer.print(" = ")
        attr.attributeType match {
          case t: GaeDateTimeType => {
            buffer.print("self.")
            buffer.print(attr.name)
            buffer.println()
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_date = self.")
            buffer.print(attr.name)
            buffer.print(".date()")
            buffer.println()
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_time = self.")
            buffer.print(attr.name)
            buffer.print(".time()")
            buffer.println()
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_now = False")
            buffer.println()
          }
          case e: GaeEntityType => {
            buffer.print("self.")
            buffer.print(attr.name)
            buffer.print(".")
            buffer.print(e.entity.idName)
            buffer.println()
          }
          case _ => {
            buffer.print("self.")
            buffer.println(attr.name)
          }
        }
      }
      buffer.println("return doc")
      buffer.indentDown
    }

    buffer.print("class ")
    buffer.print(name)
    buffer.print("(db.Model):")
    buffer.println()
    buffer.indentUp
    make_attributes
//    make_init
    make_update
    make_document
    buffer.indentDown
    buffer.println()
    buffer.flush
  }

  final def writeDocument(buffer: AppendableTextBuilder) {
    def make_attributes {
      for (attr <- attributes) {
        buffer.print(attr.name)
        buffer.println(" = None")
        attr.attributeType match {
          case t: GaeDateTimeType => {
            buffer.print(attr.name + "_date")
            buffer.println(" = None")
            buffer.print(attr.name + "_time")
            buffer.println(" = None")
            buffer.print(attr.name + "_now")
            buffer.println(" = None")
          }
          case _ => //
        }
      }
    }

    buffer.print("class ")
    buffer.print(documentName) // XXX
    buffer.print(":")
    buffer.println()
    buffer.indentUp
    make_attributes
    buffer.indentDown
    buffer.flush
  }

  final def setBaseClass(aBaseClass: GaeEntityType) {
    _baseClass = aBaseClass
  }

  final def addAttribute(anAttrName: String, aType: GaeObjectType): GaeAttribute = {
    val attr = new GaeAttribute(anAttrName, aType)
    attributes += attr
    attr
  }
}

class GaeObjectEntityClass extends GEntityClass {
  type Instance_TYPE = GaeObjectEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "py"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GaeObjectEntity(aDataSource, aContext))
}
