package org.simplemodeling.SimpleModeler.entities.grails

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.AppendableTextBuilder

/*
 * Jan. 24, 2009
 * Feb.  5, 2009
 */
class GRPogoEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GEntity(aIn, aOut, aContext) {
  type DataSource_TYPE = GDataSource

  var packageName = ""
  private var _baseClass: GREntityType = null
  val attributes = new ArrayBuffer[GRAttribute]
  private var _belongsTo: GREntityType = null

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

      def make_import(anEntity: GREntityType) {
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
	val entity = attr.attributeType.asInstanceOf[GREntityType]
	make_import(entity)
      }
      if (isPrint)
	buffer.println()
    }

    def make_constraints {
      val attrs = attributes.filter(_.isSingle).filter(!_.isEntity)
      if (attrs.isEmpty) return
      buffer.println("static constraints = {")
      buffer.indentUp
      for (attr <- attrs) {
	buffer.print(attr.name)
	buffer.print("(")
	// XXX
	buffer.println(")")
      }
      buffer.indentDown
      buffer.println("}")
      buffer.println()
    }

    def make_hasMany {
      def make_attribute(anAttr: GRAttribute) {
	buffer.print(anAttr.name)
	buffer.print(":")
	buffer.print(anAttr.attributeType.name)
      }

      val attrs = attributes.filter(_.isHasMany)
      if (attrs.isEmpty) return
      buffer.print("static hasMany = [")
      make_attribute(attrs(0))
      for (attr <- attrs.drop(1)) {
	buffer.print(", ")
	make_attribute(attr)
      }
      buffer.println("]")
      buffer.println()
    }

    def make_belongsTo {
      if (_belongsTo == null) return
      buffer.print("static belongsTo = ")
      buffer.println(_belongsTo.name)
    }

    def make_optionals {
      def make_attribute(anAttr: GRAttribute) {
	buffer.print("'")
	buffer.print(anAttr.name)
	buffer.print("'")
      }

      val attrs = attributes.filter(_.isOptional)
      if (attrs.isEmpty) return
      buffer.print("static optionals = [")
      make_attribute(attrs(0))
      for (attr <- attrs.drop(1)) {
	buffer.print(", ")
	make_attribute(attr)
      }
      buffer.println("]")
      buffer.println()
    }

    def make_single_attributes {
      val attrs = attributes.filter(_.isSingle)
      for (attr <- attrs) {
	buffer.print(attr.attributeType.name)
	buffer.print(" ")
	buffer.println(attr.name)
      }
    }

    make_package
    make_imports
    buffer.print("class ")
    buffer.print(name)
    if (_baseClass != null) {
      buffer.print(" extends ")
      buffer.print(_baseClass.name)
    }
    buffer.println(" {")
    buffer.indentUp
    make_constraints
    make_hasMany
    make_belongsTo
    make_optionals
    make_single_attributes
    buffer.indentDown
    buffer.println("}")
    buffer.flush
  }

  final def setBaseClass(aBaseClass: GREntityType) {
    _baseClass = aBaseClass
  }

  final def addAttribute(anAttrName: String, aType: GRPogoType): GRAttribute = {
    val attr = new GRAttribute(anAttrName, aType)
    attributes += attr
    attr
  }
}

class GRPogoEntityClass extends GEntityClass {
  type Instance_TYPE = GRPogoEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "groovy"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GRPogoEntity(aDataSource, aContext))
}
