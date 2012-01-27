package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._

/*
 * @since   Jul. 15, 2008
 * @version Jul. 16, 2011
 * @author  ASAMI, Tomoharu
 */
case class SimpleModelVisitor {
  def visit(mo: SMElement) {
    mo match {
      case actor: SMDomainActor         => visit_Actor(actor)
      case resource: SMDomainResource   => visit_Resource(resource)
      case event: SMDomainEvent         => visit_Event(event)
      case role: SMDomainRole           => visit_Role(role)
      case summary: SMDomainSummary     => visit_Summary(summary)
      case entity: SMDomainEntity       => visit_Entity(entity)
      case part: SMDomainEntityPart     => visit_Entity_Part(part)
      case id: SMDomainValueId          => visit_Id(id)
      case name: SMDomainValueName      => visit_Name(name)
      case value: SMDomainValue         => visit_Value(value)
      case powertype: SMDomainPowertype => visit_Powertype(powertype)
      case document: SMDomainDocument   => visit_Document(document)
      case rule: SMDomainRule           => visit_Rule(rule)
      case service: SMDomainService     => visit_Service(service)
      //        case port: SMDomainPort => visit_Port(port)
      //        case facade: SMDomainFacade => visit_Facade(facade)
      case datatype: SMDatatype         => {}
      case uc: SMBusinessUsecase        => {}
      case task: SMBusinessTask         => {}
      case pkg: SMPackage               => {}
      case obj: SMObject                => visit_Object(obj)
      case unknown                      => error("Unspported simple model object = " + unknown)
    }
  }

  protected def visit_Actor(actor: SMDomainActor) {
    visit_Entity(actor)
  }

  protected def visit_Resource(resource: SMDomainResource) {
    visit_Entity(resource)
  }

  protected def visit_Event(event: SMDomainEvent) {
    visit_Entity(event)
  }

  protected def visit_Role(role: SMDomainRole) {
    visit_Entity(role)
  }

  protected def visit_Summary(summary: SMDomainSummary) {
    visit_Entity(summary)
  }

  protected def visit_Entity(entity: SMDomainEntity) {
    visit_Object(entity)
  }

  protected def visit_Entity_Part(part: SMDomainEntityPart) {
    visit_Object(part)
  }

  protected def visit_Powertype(powertype: SMDomainPowertype) {
    visit_Object(powertype)
  }

  protected def visit_Id(id: SMDomainValueId) {
    visit_Value(id)
  }

  protected def visit_Name(name: SMDomainValueName) {
    visit_Value(name)
  }

  protected def visit_Value(value: SMDomainValue) {
    visit_Object(value)
  }

  protected def visit_Document(document: SMDomainDocument) {
    visit_Object(document)
  }

  protected def visit_Rule(rule: SMDomainRule) {
    visit_Object(rule)
  }

  protected def visit_Service(service: SMDomainService) {
    visit_Object(service)
  }

  protected def visit_Object(obj: SMObject) {
  }
}