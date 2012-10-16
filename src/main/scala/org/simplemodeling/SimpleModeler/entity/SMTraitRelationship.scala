package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * @since   Oct. 16, 2012
 * @version Oct. 16, 2012
 * @author  ASAMI, Tomoharu
 */
class SMTraitRelationship(val dslTraitRelationship: STraitRelationship) extends SMRelationship(dslTraitRelationship) {
  var mixinTrait: SMTrait = SMNullTrait
}
