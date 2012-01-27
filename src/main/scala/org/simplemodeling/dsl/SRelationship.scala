package org.simplemodeling.dsl

/*
 * Sep. 10, 2008
 * Nov. 21, 2008
 */
abstract class SRelationship(aName: String) extends SElement(aName) {
  def this() = this(null)

  var target: SObject = NullObject
}
