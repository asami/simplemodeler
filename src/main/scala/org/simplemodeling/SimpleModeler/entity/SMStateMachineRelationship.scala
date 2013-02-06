package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * @since   Nov. 14, 2012
 *  version Nov. 24, 2012
 * @version Feb.  6, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * Created in SMObject#add_stateMachine.
 */
class SMStateMachineRelationship(val dslStateMachineRelationship: SStateMachineRelationship) extends SMRelationship(dslStateMachineRelationship) {
  /**
   * Resolved in SimpleModelEntity.PackageCollector.
   * Used by SimpleModelEntity#resolve_stateMachines.
   */
  def statemachine: SMStateMachine = relationshipType.typeObject.asInstanceOf[SMStateMachine]

  def isEntityReference = false // XXX future enhancement

  def isLogicalDelete: Boolean = statemachine.isLogicalDelete
}
