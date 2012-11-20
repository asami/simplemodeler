package org.simplemodeling.dsl.business

import scala.collection.mutable.HashMap
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.util.UDsl
import org.goldenport.value._

/*
 * derived from BusinessUsecase.java since Nov. 22, 2007
 *
 * @since   Nov.  6, 2008
 *  version Nov.  6, 2011
 *  version Sep. 18, 2012
 *  version Oct. 21, 2012
 * @version Nov. 21, 2012
 * @author  ASAMI, Tomoharu
 */
class BusinessUsecase(name:String, packageName: String) extends SUsecase(name, packageName) with BusinessStory {
  def this() = this(null, null)

  def task(aName: String)(theSteps: => Unit): BusinessTaskStep = {
    val task = new ObjectScopeBusinessTask(aName, packageName)
    task.taskKind = TaskKind.PartTask
    val step = new BusinessTaskStep(task)
    execute_step(step, theSteps)
    // XXX more precise actor's role
    for (assoc <- getAssociations) {
      task.association.create(assoc)
    }
//    record_trace("***BusinessUsecase.task*** before")
//    util.UDsl.printFlow(basicFlow.root)
    task.copyInBasicFlow(basicFlow, step)
//    record_trace("***BusinessUsecase.task*** after")
//    util.UDsl.printFlow(task.basicFlow.root)
    step
  }

  def include_task(aTask: BusinessTask): BusinessTaskStep = {
    val step = new BusinessTaskStep(aTask)
    execute_step(step)
    val node = get_step_node(step, basicFlow)
//    record_trace("BusinessUsecase task = " + node)
    aTask.basicFlow.traverse(new GTreeVisitor[SStep] {
      val _cursor = node.cursor
      
      override def enter(aNode: GTreeNode[SStep]) {
	_cursor.enter(aNode.content)
      }

      override def leave(aNode: GTreeNode[SStep]) {
	_cursor.leave()
      }
    })
    step
  }

  def include_usecase(aUsecase: BusinessUsecase): BusinessUsecaseStep = {
    val step = new BusinessUsecaseStep(aUsecase)
    execute_step(step)
    step
  }

  def extend_usecase(aExtensionPointName: String, theUsecases: (String, ExtensionBusinessUsecase)*): ExtendBusinessUsecaseStep = {
    val step = new ExtendBusinessUsecaseStep(aExtensionPointName, theUsecases)
    execute_step(step)
    step
  }

/* 2012-11-21 deleted 
 * To remove redundant task creattion
  override protected def adjust_Embedded_Task(aRoot: GTreeNode[SStep]): Unit = {
    val children = aRoot.children
    for (child <- children) {
      if (child.content.isInstanceOf[STaskStep] ||
	  child.content.isInstanceOf[SUsecaseStep]) {
	return
      }
    }
    aRoot.clear()
    for (child <- children) {
//      record_trace("adjust_Embedded_Task xxx = " + child.content)
//      node.addChild(child.asInstanceOf[node.TreeNode_TYPE])
    }
//    record_trace("adjust_Embedded_Task - clean")
//    UDsl.printFlow(basicFlow.root)
    val task = create_embedded_task("BT" + name, children)
    task.taskKind = TaskKind.WholeTask
    val node: GTreeNode[SStep] = aRoot.addContent(new BusinessTaskStep(task))
    for (child <- children) {
//      record_trace("adjust_Embedded_Task = " + child.content)
      node.addChild(child.asInstanceOf[node.TreeNode_TYPE])
    }
//    record_trace("adjust_Embedded_Task")
//    UDsl.printFlow(basicFlow.root)
  }

  private def create_embedded_task(aName: String, children: Seq[GTreeNode[SStep]]) = {
    val task = new ObjectScopeBusinessTask(aName, packageName)
    val step = new BusinessTaskStep(task)
    // XXX more precise actor's role
    for (assoc <- getAssociations) {
      task.association.create(assoc)
    }
//    record_trace("***BusinessUsecase.task*** before")
//    util.UDsl.printFlow(basicFlow.root)
    task.copyInBasicFlow(basicFlow, children)
//    record_trace("***BusinessUsecase.task*** after")
//    util.UDsl.printFlow(task.basicFlow.root)
    task
  }
*/

  override protected def merge_Alternate_Path(thePaths: GTreeNode[SPath], theSteps: GTreeNode[SStep]) {
    val pathsByMark = make_pathsByMark(thePaths)
    for (step <- theSteps.children) {
      step.content match {
	case taskStep: BusinessTaskStep => {
	  val task = taskStep.task
	  task.taskKind match {
	    case _: IndependentTask => //
	    case _: PartTask => {
	      thePaths.traverse((aPath: SPath) => {
		aPath match {
		  case path: SJumpPath => {
		    if (is_range(task, path.markRange) &&
			is_range(task, path.destination)) {
		      task.addAlternatePath(aPath.asInstanceOf[SAlternatePath])
		    }
		  }
		  case path => {
		    if (is_range(task, path.markRange)) {
		      task.addAlternatePath(aPath.asInstanceOf[SAlternatePath])
		    }
		  }
		}
	      })
	      //	      task.addAlternatePath
	    }
	    case _: WholeTask => //
	  }
	}
	case _ => error("not task step: " + step)
      }
    }
  }

  override protected def merge_Exception_Path(thePaths: GTreeNode[SPath], theSteps: GTreeNode[SStep]) {
    val pathsByMark = make_pathsByMark(thePaths)
    for (step <- theSteps.children) {
      step.content match {
        case taskStep: BusinessTaskStep => // record_trace("taskstep = " + taskStep)
        case _ => error("not task step: " + step)
      }
    }
  }

  private def make_pathsByMark(thePaths: GTreeNode[SPath]) = {
    val pathsByMark = new HashMap[String, SPath] // XXX
    thePaths.traverse(new GTreeVisitor[SPath] {
      override def enter(aNode: GTreeNode[SPath]) {
	val markRange = aNode.content.markRange
	pathsByMark.put(markRange._1, aNode.content) // XXX
      }
    })
    pathsByMark
  }

  private def is_range(aTask: STask, aMarkRange: (String, String)): Boolean = {
    var isRange = false
    aTask.basicFlow.traverse((aStep: SStep) => {
      if (aStep.mark == aMarkRange._1) { // XXX
	isRange = true
	// XXX normal terminate
      }
    })
    isRange
  }

  private def is_range(aTask: STask, aMark: String): Boolean = {
    var isRange = false
    aTask.basicFlow.traverse((aStep: SStep) => {
      if (aStep.mark == aMark) {
	isRange = true
	// XXX normal terminate
      }
    })
    isRange
  }

  override def class_Name = "BusinessUsecase"
}

object NullBusinessUsecase extends BusinessUsecase
