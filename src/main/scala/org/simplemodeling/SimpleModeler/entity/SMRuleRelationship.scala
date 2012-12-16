package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * @since   Nov. 20, 2008
 * @version Dec. 17, 2012
 * @author  ASAMI, Tomoharu
 */
class SMRuleRelationship(val dslRuleRelationship: SRuleRelationship) extends SMRelationship(dslRuleRelationship) {
  def rule = relationshipType.typeObject.asInstanceOf[SMRule]
}
