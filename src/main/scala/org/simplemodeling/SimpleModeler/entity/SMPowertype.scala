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
 * @since   Dec. 22, 2008
 *  version Jan. 20, 2009
 * @version Nov. 23, 2012
 * @author  ASAMI, Tomoharu
 */
class SMPowertype(val dslPowertype: SPowertype) extends SMObject(dslPowertype) {
  override def typeName: String = "powertype"
  override def kindName: String = {
    if (isKnowledge) "knowledge"
    else null
  }

  final def kinds = dslPowertype.kinds
  /**
   * isKnowledge means that kinds of this powertype are managed a database
   * and updates as master data.
   */
  final def isKnowledge = {
    hasBaseObject || hasTraits || hasFeatures
  }
}

object SMNullPowertype extends SMPowertype(NullPowertype)
