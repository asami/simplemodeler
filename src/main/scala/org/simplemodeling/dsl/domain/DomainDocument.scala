package org.simplemodeling.dsl.domain

import scala.collection.mutable.ArrayBuffer
import org.goldenport.value._
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.datatype._

/*
 * derived from DomainDocument since Nov. 23, 2007
 *
 * Sep. 11, 2008
 * Mar. 17, 2009
 */
abstract class DomainDocument(aName: String, aPkgName: String) extends SDocument(aName, aPkgName) {
  private val _slots = new PlainTree[SDocumentSlot]
  private val _cursor = _slots.cursor

  def this() = this(null, null)
  def this(aName: String) = this(aName, null)

  private def current_container: ContainerDocumentSlot = {
    _cursor.content.asInstanceOf[ContainerDocumentSlot]
  }

  def All {
    _cursor.add(new LeafDocumentSlot(AllDocumentSlotKind, current_container))
  }

  def Attributes {
    _cursor.add(new LeafDocumentSlot(AttributesDocumentSlotKind, current_container))
  }

  def Id {
    _cursor.add(new LeafDocumentSlot(IdDocumentSlotKind, current_container))
  }

  def DateTime {
    _cursor.add(new LeafDocumentSlot(DateTimeDocumentSlotKind, current_container))
  }

  def Entity(entity: => DomainEntity)(theDefinitions: => Unit) {
    if (!isMaster) return
    _cursor.enter(new ContainerDocumentSlot(entity, current_container))
    theDefinitions
    _cursor.leave()
  }

  def Association(entity: => DomainEntity)(theDefinitions: => Unit) {
    if (!isMaster) return
    _cursor.enter(new ContainerDocumentSlot(entity, current_container))
    theDefinitions
    _cursor.leave()
  }

  def actor(actor: => DomainAgent)(theDefinitions: => Unit) {
    if (!isMaster) return
    _cursor.enter(new ContainerDocumentSlot(actor, current_container))
    theDefinitions
    _cursor.leave()
  }

/*
  def actor(actor: => DomainRole)(theDefinitions: => Unit) {
    if (!isMaster) return
    _cursor.enter(new ContainerDocumentSlot(actor, current_container))
    theDefinitions
    _cursor.leave()
  }
*/

  def resource(resource: => DomainResource)(theDefinitions: => Unit) {
    if (!isMaster) return
    _cursor.enter(new ContainerDocumentSlot(resource, current_container))
    theDefinitions
    _cursor.leave()
  }

  def resource(resource: => DomainResource, multiplicity: SMultiplicity)(theDefinitions: => Unit) {
    if (!isMaster) return
    _cursor.enter(new ContainerDocumentSlot(resource, current_container, multiplicity))
    theDefinitions
    _cursor.leave()
  }

/*
  def resource(resource: => DomainEntityPart, multiplicity: SMultiplicity)(theDefinitions: => Unit) {
    if (!isMaster) return
    _cursor.enter(new ContainerDocumentSlot(resource, current_container, multiplicity))
    theDefinitions
    _cursor.leave()
  }
*/

  def event(event: => DomainEvent)(theDefinitions: => Unit) {
    if (!isMaster) return
    _cursor.enter(new ContainerDocumentSlot(event, current_container))
    theDefinitions
    _cursor.leave()
  }

  //
  def slots: GTree[SDocumentSlot] = _slots
}

abstract class SDocumentSlot(val container: ContainerDocumentSlot, val multiplicity: SMultiplicity) {
  def this(aContainer: ContainerDocumentSlot) = this(aContainer, One)

  final def isEmptyContainer: Boolean = {
    container == null
  }

  final def containerQName: String = {
    container.entity.qualifiedName
  }
}

class LeafDocumentSlot(val kind: DocumentSlotKind, aContainer: ContainerDocumentSlot) extends SDocumentSlot(aContainer) {
}

class ContainerDocumentSlot(val entity: SEntity, aContainer: ContainerDocumentSlot, aMultiplicity: SMultiplicity) extends SDocumentSlot(aContainer, aMultiplicity) {
  def this(anEntity: SEntity, aContainer: ContainerDocumentSlot) = this(anEntity, aContainer, One)

  final def entityQName: String = {
    entity.qualifiedName
  }
}

//
abstract class DocumentSlotKind

class AllDocumentSlotKind extends DocumentSlotKind
class AttributesDocumentSlotKind extends DocumentSlotKind
class DateTimeDocumentSlotKind extends DocumentSlotKind
class IdDocumentSlotKind extends DocumentSlotKind

object AllDocumentSlotKind extends AllDocumentSlotKind
object AttributesDocumentSlotKind extends AttributesDocumentSlotKind
object DateTimeDocumentSlotKind extends DateTimeDocumentSlotKind
object IdDocumentSlotKind extends IdDocumentSlotKind

/* 2009-01-07
class SEntityPath(aPath: SEntityPath) {
  val path = new ArrayBuffer[SEntityPathNode]
  if (aPath != null) {
    path ++= aPath.path
  }

  final def add(entity: SEntity) {
    path += new SEntityPathNode(entity, "")
  }

  final def add(entity: SEntity, name: String) {
    path += new SEntityPathNode(entity, name)
  }
}

class SEntityPathNode(val entity: SEntity, val name: String) {
  require (entity != null && name != null)
}
*/
