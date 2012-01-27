package org.simplemodeling.SimpleModeler.entity

import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.simplemodeling.dsl._

/*
 * @since   Oct. 20, 2008
 * @version Aug.  6, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class SMRelationship(val dslRelationship: SRelationship) extends SMElement(dslRelationship) {
  val relationshipType = new SMRelationshipType(dslRelationship.target)
  def targetName = relationshipType.name
  def targetPackageName = relationshipType.packageName

  final def targetTypeLiteral: SDoc = {
    SIAnchor(SText(targetName)) unresolvedRef_is target_ref summary_is get_summary
  }

  private def target_ref = {
    new SElementRef(targetPackageName, targetName)
  }

  private def get_summary: SDoc = {
    dslRelationship.target.summary
  }
}
