package org.simplemodeling.dsl

/*
 * @since   May. 31, 2009
 *  version Nov. 12, 2010
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class SIdPolicy {
}

case class AutoIdPolicy() extends SIdPolicy 
case class ApplicationIdPolicy() extends SIdPolicy
object AutoIdPolicy extends AutoIdPolicy
object ApplicationIdPolicy extends ApplicationIdPolicy
