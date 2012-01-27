package org.simplemodeling.SimpleModeler.entities

/*
 * If target is only specific PObjectEntity class,
 * use GenericClassDefinition#package_children_entity.
 * If target are few specific PObjectEntity class,
 * use GenericClassDefinition#package_children_collect.
 * 
 * @since   Jul. 29, 2011
 * @version Aug. 21, 2011
 * @author  ASAMI, Tomoharu
 */
// XXX define all pobject classes
trait PObjectEntityFunction[T] extends PartialFunction[PObjectEntity, T] {
  override def isDefinedAt(x: PObjectEntity) = true

  override def apply(objectentity: PObjectEntity): T = {
    objectentity match {
      case oe: PEntityEntity => apply_EntityEntity(oe)
      case oe: PPackageEntity => apply_PackageEntity(oe)
      case _ => apply_ObjectEntity(objectentity)
    }
  }

  protected def apply_EntityEntity(entity: PEntityEntity): T
  protected def apply_PackageEntity(entity: PPackageEntity): T
  protected def apply_ObjectEntity(entity: PObjectEntity): T
}
