package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * @since   Nov.  8, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
class STask(aName: String, aPkgName: String) extends SStoryObject(aName, aPkgName) {
  var taskKind: TaskKind = TaskKind.IndependentTask
}

object NullTask extends STask(null, null)

abstract class TaskKind(val label: String) {
  override def toString = label
}

case class IndependentTask() extends TaskKind("Independent")
case class PartTask() extends TaskKind("Part")
case class WholeTask() extends TaskKind("Whole")

object TaskKind {
  val IndependentTask = new IndependentTask
  val PartTask = new PartTask
  val WholeTask = new WholeTask
}
