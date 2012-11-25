package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc
import org.simplemodeling.dsl.SAssociationStereotype._

/*
 * @since   Sep. 11, 2008
 *  version Nov. 11, 2009
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class SAssociation(aName: String) extends SRelationship(aName) {
  type Descriptable_TYPE = SAssociation
  type Historiable_TYPE = SAssociation

  var kind: SAssociationKind = PlainAssociationKind
  var entity: SEntity = UnknownEntity // XXX target: SObject
  var multiplicity: SMultiplicity = One
  var primaryActor: Boolean = false
  var secondaryActor: Boolean = false
  var service: SService = NullService
  var isAssociationClass: Boolean = false
  var isPersistent: Boolean = true
  var isBinary: Boolean = false
  var isQueryReference: Boolean = false
  var isCache: Boolean = true
  var atomLinkRelName: String = name
  var backReferenceNameOption: Option[String] = None
  var backReferenceMultiplicityOption: Option[SMultiplicity] = None

/*
  // from SPropertyProxy
  final def changeName(aName: String) = {
    change_name(aName)
  }

  def apply(aName: String, anEntity: SEntity): SAssociation = {
    changeName(aName)
    entity = anEntity
    this
  }

  def apply(aName: String, anEntity: SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    changeName(aName)
    entity = anEntity
    multiplicity = aMultiplicity
    this
  }
*/

  def is(aTarget: SEntity): SAssociation = {
    multiplicity = One
    this
  }

  def is_a(aTarget: SEntity): SAssociation = {
    multiplicity = One
    this
  }

  def is_one(aTarget: SEntity): SAssociation = {
    multiplicity = One
    this
  }

  def is_zero_one(aTarget: SEntity): SAssociation = {
    multiplicity = ZeroOne
    this
  }

  def is_one_more(aTarget: SEntity): SAssociation = {
    multiplicity = OneMore
    this
  }

  def is_zero_more(aTarget: SEntity): SAssociation = {
    multiplicity =ZeroMore
    this
  }

  // XXX : usage??

  def by(aTarget: SEntity): SAssociation = {
    entity = aTarget
    this
  }

  // from SUsecase

  final def as(aStereotype: SAssociationStereotype): SAssociation = {
    aStereotype match {
      case Primary_Actor => primaryActor = true
      case Secondary_Actor => secondaryActor = true
      case _ => error("not_implemented_yet")
    }
    this
  }

  final def service_is(aService: SService): SAssociation = {
    require (aService != null)
    require (service != null)
    service = aService
    this
  }

  final def aggregation_is(aFlag: Boolean): SAssociation = {
    if (aFlag) kind = AggregationAssociationKind
    this
  }

  final def composition_is(aFlag: Boolean): SAssociation = {
    if (aFlag) kind = CompositionAssociationKind
    this
  }

  //
  final def persistent_is(aFlag: Boolean): SAssociation = {
    isPersistent = aFlag
    this
  }

  final def binary_is(aFlag: Boolean): SAssociation = {
    isBinary = aFlag
    this
  }

  final def queryReference_is(aFlag: Boolean): SAssociation = {
    isQueryReference = aFlag
    this
  }

  final def cache_is(aFlag: Boolean): SAssociation = {
    isCache = aFlag
    this
  }

  final def atomLinkRel_is(aName: String): SAssociation = {
    atomLinkRelName = aName
    this
  }

  final def backReferenceName_is(aName: String): SAssociation = {
    backReferenceNameOption = Some(aName)
    this
  }

  final def backReferenceMultiplicity_is(aMultiplicity: SMultiplicity): SAssociation = {
    backReferenceMultiplicityOption = Some(aMultiplicity)
    this
  }

  //
  final def setPersistent(aFlag: Boolean): SAssociation = {
    isPersistent = aFlag
    this
  }

  final def setBinary(aFlag: Boolean): SAssociation = {
    isBinary = aFlag
    this
  }

  final def setQueryReference(aFlag: Boolean): SAssociation = {
    isQueryReference = aFlag
    this
  }

  final def setCache(aFlag: Boolean): SAssociation = {
    isCache = aFlag
    this
  }

  final def setAtomLinkRel(aName: String): SAssociation = {
    atomLinkRelName = aName
    this
  }

  final def setBackReferenceName(aName: String): SAssociation = {
    backReferenceNameOption = Some(aName)
    this
  }

  final def setBackReferenceMultiplicity(aMultiplicity: SMultiplicity): SAssociation = {
    backReferenceMultiplicityOption = Some(aMultiplicity)
    this
  }

  //
  def withAssociationClass(b: Boolean): SAssociation = {
    isAssociationClass = b
    this
  }

  final def withPersistent(aFlag: Boolean): SAssociation = {
    isPersistent = aFlag
    this
  }

  final def withBinary(aFlag: Boolean): SAssociation = {
    isBinary = aFlag
    this
  }

  final def withQueryReference(aFlag: Boolean): SAssociation = {
    isQueryReference = aFlag
    this
  }

  final def withCache(aFlag: Boolean): SAssociation = {
    isCache = aFlag
    this
  }

  final def withAtomLinkRel(aName: String): SAssociation = {
    atomLinkRelName = aName
    this
  }


  final def withBackReferenceName(aName: String): SAssociation = {
    backReferenceNameOption = Some(aName)
    this
  }

  final def withBackReferenceMultiplicity(aMultiplicity: SMultiplicity): SAssociation = {
    backReferenceMultiplicityOption = Some(aMultiplicity)
    this
  }

/*
  def abount(aTarget: SEntity): SAssociation = {
    this
  }

  def aboard(aTarget: SEntity): SAssociation = {
    this
  }

  def above(aTarget: SEntity): SAssociation = {
    this
  }

  def across(aTarget: SEntity): SAssociation = {
    this
  }

  def after(aTarget: SEntity): SAssociation = {
    this
  }

  def against(aTarget: SEntity): SAssociation = {
    this
  }

  def along(aTarget: SEntity): SAssociation = {
    this
  }

  def alongside(aTarget: SEntity): SAssociation = {
    this
  }

  def among(aTarget: SEntity): SAssociation = {
    this
  }

  def around(aTarget: SEntity): SAssociation = {
    this
  }

  def at(aTarget: SEntity): SAssociation = {
    this
  }

  def before(aTarget: SEntity): SAssociation = {
    this
  }

  def behind(aTarget: SEntity): SAssociation = {
    this
  }

  def below(aTarget: SEntity): SAssociation = {
    this
  }

  def beneath(aTarget: SEntity): SAssociation = {
    this
  }

  def beside(aTarget: SEntity): SAssociation = {
    this
  }

  def besides(aTarget: SEntity): SAssociation = {
    this
  }

  def between(aTarget: SEntity): SAssociation = {
    this
  }

  def beyond(aTarget: SEntity): SAssociation = {
    this
  }

  def but(aTarget: SEntity): SAssociation = {
    this
  }

  def concerning(aTarget: SEntity): SAssociation = {
    this
  }

  def despite(aTarget: SEntity): SAssociation = {
    this
  }

  def down(aTarget: SEntity): SAssociation = {
    this
  }

  def during(aTarget: SEntity): SAssociation = {
    this
  }

  def except(aTarget: SEntity): SAssociation = {
    this
  }

  def for4(aTarget: SEntity): SAssociation = {
    this
  }

  def `for`(aTarget: SEntity): SAssociation = {
    this
  }

  def from(aTarget: SEntity): SAssociation = {
    this
  }

  def in(aTarget: SEntity): SAssociation = {
    this
  }

  def inside(aTarget: SEntity): SAssociation = {
    this
  }

  def into(aTarget: SEntity): SAssociation = {
    this
  }

  def less(aTarget: SEntity): SAssociation = {
    this
  }

  def like(aTarget: SEntity): SAssociation = {
    this
  }

  def minus(aTarget: SEntity): SAssociation = {
    this
  }

  def near(aTarget: SEntity): SAssociation = {
    this
  }

  def of(aTarget: SEntity): SAssociation = {
    this
  }

  def off(aTarget: SEntity): SAssociation = {
    this
  }

  def on(aTarget: SEntity): SAssociation = {
    this
  }

  def onto(aTarget: SEntity): SAssociation = {
    this
  }

  def opposite(aTarget: SEntity): SAssociation = {
    this
  }

  def out(aTarget: SEntity): SAssociation = {
    this
  }

  def outside(aTarget: SEntity): SAssociation = {
    this
  }

  def over(aTarget: SEntity): SAssociation = {
    this
  }

  def past(aTarget: SEntity): SAssociation = {
    this
  }

  def plus(aTarget: SEntity): SAssociation = {
    this
  }

  def round(aTarget: SEntity): SAssociation = {
    this
  }

  def save(aTarget: SEntity): SAssociation = {
    this
  }

  def since(aTarget: SEntity): SAssociation = {
    this
  }

  def than(aTarget: SEntity): SAssociation = {
    this
  }

  def through(aTarget: SEntity): SAssociation = {
    this
  }

  def throughout(aTarget: SEntity): SAssociation = {
    this
  }

  def till(aTarget: SEntity): SAssociation = {
    this
  }

  def to(aTarget: SEntity): SAssociation = {
    this
  }

  def toward(aTarget: SEntity): SAssociation = {
    this
  }

  def under(aTarget: SEntity): SAssociation = {
    this
  }

  def underneath(aTarget: SEntity): SAssociation = {
    this
  }

  def until(aTarget: SEntity): SAssociation = {
    this
  }

  def up(aTarget: SEntity): SAssociation = {
    this
  }

  def upon(aTarget: SEntity): SAssociation = {
    this
  }

  def via(aTarget: SEntity): SAssociation = {
    this
  }

  def `with`(aTarget: SEntity): SAssociation = {
    this
  }

  def within(aTarget: SEntity): SAssociation = {
    this
  }

  def without(aTarget: SEntity): SAssociation = {
    this
  }
*/
}

object NullAssociation extends SAssociation(null)

abstract class SAssociationKind

class PlainAssociationKind extends SAssociationKind
object PlainAssociationKind extends PlainAssociationKind

class AggregationAssociationKind extends SAssociationKind
object AggregationAssociationKind extends AggregationAssociationKind

class CompositionAssociationKind extends SAssociationKind
object CompositionAssociationKind extends CompositionAssociationKind
