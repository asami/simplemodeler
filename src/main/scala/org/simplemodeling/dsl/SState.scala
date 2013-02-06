package org.simplemodeling.dsl

import scala.collection.mutable.{ArrayBuffer, LinkedHashMap}

/*
 * @since   Dec. 20, 2008
 *  version Mar. 18, 2009
 *  version Nov. 26, 2012
 * @version Feb.  6, 2013
 * @author  ASAMI, Tomoharu
 */
abstract class SState(
  aName: String,
  val value: Option[String],
  val lifecycle: Option[String]
) extends SElement(aName) {
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

object NullState extends SState(null, None, None)
