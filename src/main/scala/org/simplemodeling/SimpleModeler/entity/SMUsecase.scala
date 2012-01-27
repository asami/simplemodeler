package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business.BusinessUsecase
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._
import org.goldenport.value.{GTreeNode, GTreeVisitor}
import com.asamioffice.goldenport.text.UPathString

/*
 * Nov.  9, 2008
 * Jan. 18, 2009
 */
abstract class SMUsecase(val dslUsecase: SUsecase) extends SMStoryObject(dslUsecase) {
  override def typeName: String = "usecase"
}
