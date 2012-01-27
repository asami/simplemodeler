package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._

/*
 * @since   Sep. 12, 2008
 * @version Oct. 22, 2009
 * @author  ASAMI, Tomoharu
 */
class SAssociationSet(val isMaster: Boolean) {
  private val _associations = new ArrayBuffer[SAssociation]
  private val _candidates = new ArrayBuffer[SAssociationCandidate]

  final def associations: Seq[SAssociation] = _associations

  final def getAssociation(aName: String): Option[SAssociation] = {
    _associations.find(_.name == aName)
  }

  final def findAssociation(theNames: String*): Option[SAssociation] = {
    for (name <- theNames) {
      val mayAssoc = getAssociation(name)
      if (mayAssoc.isDefined) return mayAssoc
    }
    None
  }

  //
  def apply(anEntity: => SEntity): SAssociation = {
    create(anEntity)
  }

  def apply(anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    create(anEntity, aMultiplicity)
  }

  def apply(aName: String, anEntity: => SEntity): SAssociation = {
    create(aName, anEntity)
  }

  def apply(aName: String, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    create(aName, anEntity, aMultiplicity)
  }

  def apply(aName: Symbol, anEntity: => SEntity): SAssociation = {
    create(aName.name, anEntity)
  }

  def apply(aName: Symbol, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    create(aName.name, anEntity, aMultiplicity)
  }

  //
  def create(anEntity: => SEntity): SAssociation = {
    create(anEntity, One)
  }

  def create(anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    create(reference_name(anEntity), anEntity, aMultiplicity)
  }

  //
  def create(aName: String, anEntity: => SEntity): SAssociation = {
    create(aName, anEntity, One)
  }

  def create(aName: String, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    if (isMaster) {
      val assoc = new SAssociation(aName)
      assoc.entity = anEntity
      assoc.multiplicity = aMultiplicity
      _associations += assoc
      assoc
    } else {
      NullAssociation
    }
  }

  def create(aName: Symbol, anEntity: => SEntity): SAssociation = {
    create(aName.name, anEntity)
  }

  def create(aName: Symbol, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    create(aName.name, aMultiplicity)
  }

  //
  def create(aName: String): SAssociation = {
    create(aName, One)
  }

  def create(aName: String, aMultiplicity: SMultiplicity): SAssociation = {
    val assoc = new SAssociation(aName)
    assoc.multiplicity = aMultiplicity
    _associations += assoc
    assoc
  }

  // from SPropertyProxy via SAssociationCandidate 
  final def candidate(aName: String): SAssociationCandidate = {
    val candidate = new SAssociationCandidate(aName, this)
    _candidates += candidate
    candidate
  }

  final def create(aCandidate: SAssociationCandidate, aName: String): SAssociation = {
    create(aCandidate, aName, One)
  }

  final def create(aCandidate: SAssociationCandidate, aName: String, aMultiplicity: SMultiplicity): SAssociation = {
    aCandidate.done = true
    create(aName, aMultiplicity)
  }

  // from SObject via SAssociationCandidate 
  final def create(aCandidate: SAssociationCandidate, anEntity: SEntity): SAssociation = {
    create(aCandidate, anEntity, One)
  }

  final def create(aCandidate: SAssociationCandidate, anEntity: SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    aCandidate.done = true
    create(aCandidate.name, anEntity, aMultiplicity)
  }

  // from ??? via SAssociationCandidate
  final def create(aCandidate: SAssociationCandidate, aName: String, anEntity: SEntity): SAssociation = {
    create(aCandidate, aName, anEntity, One)
  }

  final def create(aCandidate: SAssociationCandidate, aName: String, anEntity: SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    aCandidate.done = true
    create(aName, anEntity, aMultiplicity)
  }

  // from BusinessUsecase
  final def create(anAssoc: SAssociation): SAssociation = {
    create(anAssoc.name, anAssoc.entity, anAssoc.multiplicity)
  }

  //
  private def reference_name(anObject: SObject): String = {
    util.UDsl.makeReferenceName(anObject)
  }
}
