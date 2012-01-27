package org.simplemodeling.SimpleModeler.entity.domain

import scala.collection.mutable.ArrayBuffer
import org.goldenport.value._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._

/*
 * @since   Sep. 15, 2008
 * @version Apr. 17, 2011
 * @author  ASAMI, Tomoharu
 */
class SMDomainDocument(val dslDomainDocument: DomainDocument) extends SMDocument(dslDomainDocument) {
  private val _slots = {
    val slots = new PlainTree[SMDocumentSlot]
    val cursor = slots.cursor
    dslDomainDocument.slots.traverse(new GTreeVisitor[SDocumentSlot] {
      override def enter(node: GTreeNode[SDocumentSlot]) {
	val slot: SMDocumentSlot = node.content match {
	  case null => error("null slot")
	  case leaf: LeafDocumentSlot => new SMLeafDocumentSlot(leaf)
	  case container: ContainerDocumentSlot => new SMContainerDocumentSlot(container)
	}
	cursor.enter(slot)
      }

      override def leave(node: GTreeNode[SDocumentSlot]) {
	cursor.leave()
      }
    })
    slots
  }

  final def slots: GTree[SMDocumentSlot] = _slots
}

abstract class SMDocumentSlot(val dslSlot: SDocumentSlot) {
  final def multiplicity = dslSlot.multiplicity
  final def containerEntityQName = {
    if (dslSlot.isEmptyContainer) ""
    else dslSlot.containerQName
  }

  var containerEntity: SMEntity = SMNullEntity
  var containerDocument: SMDocument = SMNullDocument
}

class SMLeafDocumentSlot(val dslLeafSlot: LeafDocumentSlot) extends SMDocumentSlot(dslLeafSlot) {
  require (dslLeafSlot != null)

  final def kind = dslLeafSlot.kind
}

class SMContainerDocumentSlot(val dslContainerSlot: ContainerDocumentSlot) extends SMDocumentSlot(dslContainerSlot) {
  require (dslContainerSlot != null)
  var partEntity: SMEntity = SMNullEntity
  var partDocument: SMDocumentPart = SMNullDocumentPart

  final def partEntityQName = {
    dslContainerSlot.entityQName
  }
}

/* 2009-01-07
class SMEntityPath(val dslPath: SEntityPath) {
  val path = dslPath.map(new SMEntityPathNode(_))
}

class SMEntityPathNode(val dslPathNode: SEntityPathNode) {
}
*/
