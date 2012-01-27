package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.ArrayBuffer

/*
 * TODO: 事前条件、事後条件の抽出
 *
 * Dec. 19, 2008
 * Dec. 20, 2008
 */
class SMEntityUsage {
  val usages = new ArrayBuffer[SMUsage]
}

class SMUsage(val kind: SMUsageKind, val entity: SMEntity) {
}

class SMUpdateUsage(entity: SMEntity) extends SMUsage(UpdateUsageKind, entity) {
  
}

abstract class SMUsageKind

class NullUsageKind extends SMUsageKind
class IssueUsageKind extends SMUsageKind
class OpenUsageKind extends SMUsageKind
class CloseUsageKind extends SMUsageKind
class CreateUsageKind extends SMUsageKind
class ReadUsageKind extends SMUsageKind
class UpdateUsageKind extends SMUsageKind
class DeleteUsageKind extends SMUsageKind

object NullUsageKind extends NullUsageKind
object IssueUsageKind extends IssueUsageKind
object OpenUsageKind extends OpenUsageKind
object CloseUsageKind extends CloseUsageKind
object CreateUsageKind extends CreateUsageKind
object ReadUsageKind extends ReadUsageKind
object UpdateUsageKind extends UpdateUsageKind
object DeleteUsageKind extends DeleteUsageKind
