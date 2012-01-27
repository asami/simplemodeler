package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.XML
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.AppendableTextBuilder

/*
 * Apr.  6, 2009
 * Apr.  7, 2009
 * ASAMI, Tomoharu
 */
class GaeAppYamlEntity(val projectName: String, aContext: GEntityContext) extends GEntity(aContext) {

  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  val appYaml = """application: %project%
version: 1
runtime: python
api_version: 1

handlers:
- url: /.*
  script: %project%.py
"""

  override protected def write_Content(out: BufferedWriter) {
    val buffer = new AppendableTextBuilder(out)
    buffer.append(appYaml, Map("%project%" -> projectName))
    buffer.flush
  }
}

class GaeAppYamlEntityClass extends GEntityClass {
  type Instance_TYPE = GaeModelsEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "html"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GaeModelsEntity(aDataSource, aContext))
}
