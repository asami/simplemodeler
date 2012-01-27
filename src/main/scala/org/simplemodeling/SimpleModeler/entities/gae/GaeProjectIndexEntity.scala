package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.{XML, Elem}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.AppendableTextBuilder

/*
 * @since   Apr.  6, 2009
 * @version Apr.  7, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeProjectIndexEntity(val projectName: String, val models: Seq[GaeModelsEntity], aContext: GEntityContext) extends GEntity(aContext) {

  override def is_Text_Output = true

  val entities = for (model <- models; obj <- model.entities) yield obj

  override protected def open_Entity_Create() {
  }

  override protected def write_Content(out: BufferedWriter) {
    val html = 
<html>
<head>
<title>{projectName}</title>
</head>
<body>
<h1>{projectName}</h1>

<ul>
    { list_entities }
</ul>

</body>
</html>
    XML.write(out, html, entityContext.textEncoding, false, null)
  }

  private def list_entities: Seq[Elem] = {
    for (obj <- entities) yield {
      <li><a href={"/" + obj.term_en}>{obj.term.capitalize}</a></li>
    }
  }
}

class GaeProjectIndexEntityClass extends GEntityClass {
  type Instance_TYPE = GaeModelsEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "html"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GaeModelsEntity(aDataSource, aContext))
}
