package org.simplemodeling.SimpleModeler.entities.android

import java.io._
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._
import content.EntityContent

/*
 * @since   Jul. 19, 2011
 *  version Sep.  1, 2011
 * @version May.  1, 2012
 * @author  ASAMI, Tomoharu
 */
class AndroidG3RestDriverEntity(val androidContext: AndroidEntityContext) extends JavaObjectEntityBase(androidContext) with PServiceEntity {
/*
  def entities = containerNode.get.children.map(_.content) collect {
    case entity: EntityContent => entity.entity
  } collect {
    case entity: PEntityEntity => entity
  }
*/
  var interfaceName: Option[String] = None 

  override protected def write_Content(out: BufferedWriter) {
    val klass = new AndroidG3RestDriverJavaClassDefinition(androidContext, Nil, AndroidG3RestDriverEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
