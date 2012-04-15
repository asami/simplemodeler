package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * @since   Jun. 18, 2009
 *  version Jun. 20, 2009
 *  version Feb.  9, 2012
 * @version Apr. 11, 2012
 * @author  ASAMI, Tomoharu
 */
class SMConstraint(val dslConstraint: SConstraint) {
  final def name = sys.error("not implemented yet")
  final def value = sys.error("not implemented yet")
}

object SMConstraint {
  def apply(dsl: SConstraint): SMConstraint = {
    new SMConstraint(dsl)
  }
}
