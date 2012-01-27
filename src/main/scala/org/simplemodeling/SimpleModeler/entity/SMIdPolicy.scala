package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * @since   May. 31, 2009
 *  version Nov. 12, 2010
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
sealed abstract class SMIdPolicy {
}

case class SMAutoIdPolicy() extends SMIdPolicy
case class SMApplicationIdPolicy() extends SMIdPolicy
object SMAutoIdPolicy extends SMAutoIdPolicy
object SMApplicationIdPolicy extends SMApplicationIdPolicy
