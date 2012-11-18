package org.simplemodeling.SimpleModeler.entity

import scalaz._, Scalaz._
import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._
import org.simplemodeling.SimpleModeler.util._

/*
 * @since   Dec.  5, 2008
 *  version Dec.  8, 2008
 * @version Nov. 18, 2012
 * @author  ASAMI, Tomoharu
 */
class SMExecutionStep(val dslExecutionStep: SExecutionStep) extends SMStep(dslExecutionStep) {
  var entity: Option[SMEntity] = None

  override protected def copy_Node(): SMExecutionStep = {
    new SMExecutionStep(dslExecutionStep)
  }

  override def resolve(f: String => Option[SMObject]): ResolveResult = {
    var qname = dslExecutionStep.entity.qualifiedName
    f(qname) match {
      case Some(s: SMEntity) => {
        entity = Some(s)
        ResolveSuccess
      }
      case Some(s) => ResolveResult.notMatch(qname, s.qualifiedName)
      case None => ResolveResult.notFound(qname)
    }
  }

  override def usedEntities = {
    entity.orEmpty[List]
  }

  final def isTargetEntity: Boolean = {
    require (dslExecutionStep.entity != null)
    dslExecutionStep.entity != NullEntity
  }

  final def getTargetEntityTerm: SDoc = {
    new SMTermRef(dslExecutionStep.entity)
  }

  final def getVerbTerm: SDoc = {
    dslExecutionStep.kind match {
      case kind: Issue => new SMKeywordRef("発行", "help:issue")
      case kind: Open => new SMKeywordRef("オープン", "help:open")
      case kind: Close => new SMKeywordRef("クローズ", "help:close")
      case kind: Create => new SMKeywordRef("作成", "help:create")
      case kind: Read => new SMKeywordRef("参照", "help:read")
      case kind: Update => new SMKeywordRef("更新", "help:update")
      case kind: Delete => new SMKeywordRef("削除", "help:delete")
    }
  }
}
