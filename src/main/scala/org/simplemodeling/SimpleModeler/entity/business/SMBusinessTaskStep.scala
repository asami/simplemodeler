package org.simplemodeling.SimpleModeler.entity.business

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.util._

/*
 * @since   Dec.  7, 2008
 *  version Dec. 18, 2010
 * @version Nov. 18, 2012
 * @author  ASAMI, Tomoharu
 */
class SMBusinessTaskStep(val dslBusinessTaskStep: BusinessTaskStep, val dslBusinessTask: BusinessTask) extends SMTaskStep(dslBusinessTaskStep, dslBusinessTask) {
  import scala.collection.mutable.ArrayBuffer

  val businessTasks = new ArrayBuffer[SMBusinessTask]

  override def usedEntities() = Nil

  override def includedStories() = businessTasks

  override def resolve(f: String => Option[SMObject]): ResolveResult = {
    val qname = dslBusinessTaskStep.businessTask.qualifiedName
    f(qname) match {
      case Some(t: SMBusinessTask) => businessTasks += t; ResolveSuccess
      case Some(x) => ResolveResult.notMatch(qname, x.toString)
      case None => ResolveResult.notFound(qname)
    }
  }
}
