package org.simplemodeling.SimpleModeler.entities

/**
 * @since   Aug.  6, 2011
 * @version Aug.  6, 2011
 * @author  ASAMI, Tomoharu
 */
sealed abstract class EntityAttributeKind {
  def apply[T](f: EntityAttributeKind => T): T = {
    f(this);
  }
}

/**
 * No persistency control.
 */
object SimpleEntityAttributeKind extends EntityAttributeKind {
}

/**
 * Used for composition
 */
object CompositionReferenceEntityAttributeKind extends EntityAttributeKind {
}

/**
 * Used for aggregation
 */
object AggregationReferenceEntityAttributeKind extends EntityAttributeKind {
}

/**
 * Used for association
 */
object AssociationReferenceEntityAttributeKind extends EntityAttributeKind {
}

object CompositionIdEntityAttributeKind extends EntityAttributeKind {
}

object AggregationIdEntityAttributeKind extends EntityAttributeKind {
}

object AssociationIdEntityAttributeKind extends EntityAttributeKind {
}

object CompositionIdReferenceEntityAttributeKind extends EntityAttributeKind {
}

object AggregationIdReferenceEntityAttributeKind extends EntityAttributeKind {
}

object AssociationIdReferenceEntityAttributeKind extends EntityAttributeKind {
}

/**
 * Used for association
 */
object QueryEntityAttributeKind extends EntityAttributeKind {
}

abstract class EntityAttributeKindFunction[T] extends PartialFunction[EntityAttributeKind, T] {
  override def isDefinedAt(k: EntityAttributeKind) = true

  override def apply(k: EntityAttributeKind): T = {
    k match {
      case SimpleEntityAttributeKind => apply_Simple
      case CompositionReferenceEntityAttributeKind => apply_Composition_Reference
      case AggregationReferenceEntityAttributeKind => apply_Aggregation_Reference
      case AssociationReferenceEntityAttributeKind => apply_Association_Reference
      case CompositionIdEntityAttributeKind => apply_Composition_Id
      case AggregationIdEntityAttributeKind => apply_Aggregation_Id
      case AssociationIdEntityAttributeKind => apply_Association_Id
      case CompositionIdReferenceEntityAttributeKind => apply_Composition_Id_Reference
      case AggregationIdReferenceEntityAttributeKind => apply_Aggregation_Id_Reference
      case AssociationIdReferenceEntityAttributeKind => apply_Association_Id_Reference
      case QueryEntityAttributeKind => apply_Query
    }
  }

  def apply_Simple(): T
  def apply_Composition_Reference(): T
  def apply_Aggregation_Reference(): T
  def apply_Association_Reference(): T
  def apply_Composition_Id(): T
  def apply_Aggregation_Id(): T
  def apply_Association_Id(): T
  def apply_Composition_Id_Reference(): T
  def apply_Aggregation_Id_Reference(): T
  def apply_Association_Id_Reference(): T
  def apply_Query(): T
}