package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * @since   Dec.  8, 2008
 * Dec.  9, 2008
 * @version Nov.  4, 2011
 * @author  ASAMI, Tomoharu
 */
class SMTaskStep(val dslTaskStep: STaskStep, val dslTask: STask) extends SMStep(dslTaskStep) {
  var entities: List[SMEntity] = Nil

  def entityNames: List[String] = {
    dslTask.basicFlow.collect(
      _.content.isInstanceOf[SExecutionStep]).map(_.content).collect {
        case es: SExecutionStep => es
      }.map(_.entity.qualifiedName).toList
  }
}
