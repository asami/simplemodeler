package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * Dec.  5, 2008
 * Dec.  8, 2008
 */
class SMExecutionStep(val dslExecutionStep: SExecutionStep) extends SMStep(dslExecutionStep) {
  override protected def copy_Node(): SMExecutionStep = {
    new SMExecutionStep(dslExecutionStep)
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
