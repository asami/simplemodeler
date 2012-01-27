package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * Nov. 18, 2008
 * Nov. 19, 2008
 */
class SRuleSet {
  private val _rules = new ArrayBuffer[SRuleRelationship]

  def rules: Seq[SRuleRelationship] = _rules

  // from model itself
  def apply(aRule: SRule): SRuleRelationship = {
    create(aRule)
  }

  //
  def create(aRule: SRule): SRuleRelationship = {
    val rule = new SRuleRelationship(aRule)
    _rules += rule
    rule
  }
}
