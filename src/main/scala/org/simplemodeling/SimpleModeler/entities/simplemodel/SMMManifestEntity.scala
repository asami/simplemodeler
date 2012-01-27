package org.simplemodeling.SimpleModeler.entities.simplemodel

import java.io._
import scala.collection.mutable.{ArrayBuffer, Set, LinkedHashSet}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, TextBuilder, UJavaString}

/*
 * Feb.  6, 2009
 * Feb.  6, 2009
 */
class SMMManifestEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GEntity(aIn, aOut, aContext) {
  type DataSource_TYPE = GDataSource

  name = "MANIFEST"
  var packageName: String = ""
  val objects = new ArrayBuffer[SMMEntityEntity]

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
      buffer.println("import org.simplemodeling.dsl._")
      buffer.println()
    }

    def make_body {
      buffer.print("case class ")
      buffer.print("MANIFEST")
      buffer.println(" extends SManifest {")
      buffer.indentUp
      buffer.print("objects(")
      if (!objects.isEmpty) {
	var obj = objects.first
	buffer.print(obj.name)
	buffer.print("()")
	for (obj <- objects.drop(1)) {
	  buffer.print(", ")
	  buffer.print(obj.name)
	  buffer.print("()")
	}
      }
      buffer.println(")")
      buffer.indentDown
      buffer.println("}")
      buffer.flush
    }

    make_package
    make_imports
    make_body
  }
}
