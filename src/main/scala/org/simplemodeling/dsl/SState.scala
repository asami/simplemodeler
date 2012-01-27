package org.simplemodeling.dsl

import scala.collection.mutable.{ArrayBuffer, LinkedHashMap}

/*
 * Dec. 20, 2008
 * Mar. 18, 2009
 * ASAMI, Tomoharu
 */
abstract class SState(aName: String) extends SElement(aName) {
  type Descriptable_TYPE = SState
  type Historiable_TYPE = SState
  val transitions = new ArrayBuffer[STransition]
  val subStateMap = new LinkedHashMap[String, SState]
  def subStates = subStateMap.values.toList

  if (aName == null) {
    set_name_by_className
  }

  val packageName: String = {
    val pkg = getClass.getPackage
    if (pkg == null) ""
    else pkg.getName
  }

  val qualifiedName: String = {
    packageName match {
      case "" => name
      case pkgName => pkgName + "." + name
    }
  }

  val isMaster: Boolean = SStateRepository.register(this)

  protected final def transit_event(event: => SEvent, state: => SState): STransition = {
    if (isMaster) {
      val transition = new STransition(event, this, state)
      transitions += transition
      transition
    } else {
      NullTransition
    }
  }
}

object NullState extends SState(null)
