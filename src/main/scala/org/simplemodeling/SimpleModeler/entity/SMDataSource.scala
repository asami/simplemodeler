package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.flow._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._
import org.goldenport.value.{GTreeNode, GTreeVisitor}
import com.asamioffice.goldenport.text.UPathString

/*
 * @since   Mar. 26, 2011
 * @version Mar. 26, 2011
 * @author  ASAMI, Tomoharu
 */
class SMDataSource(val dslDataSource: DataSource) extends SMObject(dslDataSource) {
  override def typeName: String = "datasource"

  val entities = dslDataSource.entities.map(new SMEntityType(_))
  
}

object SMNullDataSource extends SMDataSource(NullDataSource)
