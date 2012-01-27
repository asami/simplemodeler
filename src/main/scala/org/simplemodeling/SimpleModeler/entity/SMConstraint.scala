package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * @since   Jun. 18, 2009
 * @version Jun. 20, 2009
 * @author  ASAMI, Tomoharu
 */
class SMConstraint(val dslConstraint: SConstraint) {
  final def name = dslConstraint.name
  final def value = dslConstraint.value
}
