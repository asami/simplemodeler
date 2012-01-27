package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString}
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Apr. 23, 2009
 * @version May. 12, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeDocumentEntity(aContext: GEntityContext) extends GaeObjectEntity(aContext) {
  var modelDocument: SMDocument = null
}

