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
class SMDataSet(val dslDataSet: DataSet) extends SMObject(dslDataSet) {
  override def typeName: String = "dataset"
}

object SMNullDataSet extends SMDataSet(NullDataSet)
