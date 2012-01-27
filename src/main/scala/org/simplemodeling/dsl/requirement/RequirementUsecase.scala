package org.simplemodeling.dsl.requirement

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.util.UDsl
import org.goldenport.value._

/*
 * Dec. 19, 2008
 * 
 * @since   Dec. 10, 2008
 * @version Sep. 18, 2011
 * @author  ASAMI, Tomoharu
 */
class RequirementUsecase(name: String, pkgname: String) extends SUsecase(name, pkgname) with RequirementStory {
  def this() = this(null, null)

  val businessTasks = new ArrayBuffer[BusinessTask]

  def realize(businessTask: BusinessTask): BusinessTask = {
    businessTasks += businessTask
    businessTask
  }
}

object NullRequirementUsecase extends RequirementUsecase
