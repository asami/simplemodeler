package org.simplemodeling.SimpleModeler.entity

import org.goldenport.sdoc.SText
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entity.system._
import org.simplemodeling.SimpleModeler.entity.flow._

/*
 * @since   Sep. 15, 2008
 *  version Jul. 13, 2011
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class SMPackage(val dslPackage: SPackage) extends SMElement(dslPackage) {
  add_feature(FeaturePackage)(() => SText(qualifiedName)) label_is "名前"

  final def version = dslPackage.version

  def xmlNamespace = {
    dslPackage.getXmlNamespace match {
      case Some(ns) => ns
      case None => {
        "http://" + name.split("\\.").reverse.mkString(".") + "/"
      }
    }
  }

  final def domainActors: Seq[SMDomainActor] = {
    children.filter(_.isInstanceOf[SMDomainActor]).map(_.asInstanceOf[SMDomainActor])
  }

  final def domainRoles: Seq[SMDomainRole] = {
    children.filter(_.isInstanceOf[SMDomainRole]).map(_.asInstanceOf[SMDomainRole])
  }

  final def domainResources: Seq[SMDomainResource] = {
    children.filter(_.isInstanceOf[SMDomainResource]).map(_.asInstanceOf[SMDomainResource])
  }

  final def domainEvents: Seq[SMDomainEvent] = {
    children.filter(_.isInstanceOf[SMDomainEvent]).map(_.asInstanceOf[SMDomainEvent])
  }

  final def domainSummaries: Seq[SMDomainSummary] = {
    children.filter(_.isInstanceOf[SMDomainSummary]).map(_.asInstanceOf[SMDomainSummary])
  }

  final def domainAssociationEntities: Seq[SMDomainAssociationEntity] = {
    children.filter(_.isInstanceOf[SMDomainAssociationEntity]).map(_.asInstanceOf[SMDomainAssociationEntity])
  }

  final def domainEntities: Seq[SMDomainEntity] = {
    children.filter(child => child.isInstanceOf[SMDomainEntity] &&
		    !(child.isInstanceOf[SMDomainActor] ||
		      child.isInstanceOf[SMDomainRole] ||
		      child.isInstanceOf[SMDomainResource] ||
		      child.isInstanceOf[SMDomainEvent] ||
		      child.isInstanceOf[SMDomainSummary] ||
		      child.isInstanceOf[SMDomainAssociationEntity])).map(_.asInstanceOf[SMDomainEntity])
  }

  final def domainIds: Seq[SMDomainValueId] = {
    children.filter(_.isInstanceOf[SMDomainValueId]).map(_.asInstanceOf[SMDomainValueId])
  }

  final def domainNames: Seq[SMDomainValueName] = {
    children.filter(_.isInstanceOf[SMDomainValueName]).map(_.asInstanceOf[SMDomainValueName])
  }

  final def domainValues: Seq[SMDomainValue] = {
    children.filter(child => child.isInstanceOf[SMDomainValue] &&
		    !(child.isInstanceOf[SMDomainValueId] ||
		      child.isInstanceOf[SMDomainValueName])).map(_.asInstanceOf[SMDomainValue])
  }

  final def domainDocuments: Seq[SMDomainDocument] = {
    children.filter(_.isInstanceOf[SMDomainDocument]).map(_.asInstanceOf[SMDomainDocument])
  }

  final def domainRules: Seq[SMDomainRule] = {
    children.filter(_.isInstanceOf[SMDomainRule]).map(_.asInstanceOf[SMDomainRule])
  }

  final def domainServices: Seq[SMDomainService] = {
    children.filter(_.isInstanceOf[SMDomainService]).map(_.asInstanceOf[SMDomainService])
  }

  final def domainPowertypes: Seq[SMDomainPowertype] = {
    children.filter(_.isInstanceOf[SMDomainPowertype]).map(_.asInstanceOf[SMDomainPowertype])
  }

  final def datatypes: Seq[SMDatatype] = {
    children.filter(_.isInstanceOf[SMDatatype]).map(_.asInstanceOf[SMDatatype])
  }

  final def businessUsecases: Seq[SMBusinessUsecase] = {
    children.filter(_.isInstanceOf[SMBusinessUsecase]).map(_.asInstanceOf[SMBusinessUsecase])
  }

  final def businessTasks: Seq[SMBusinessTask] = {
    children.filter(_.isInstanceOf[SMBusinessTask]).map(_.asInstanceOf[SMBusinessTask])
  }

  final def requirementUsecases: Seq[SMRequirementUsecase] = {
    children.filter(_.isInstanceOf[SMRequirementUsecase]).map(_.asInstanceOf[SMRequirementUsecase])
  }

  final def requirementTasks: Seq[SMRequirementTask] = {
    children.filter(_.isInstanceOf[SMRequirementTask]).map(_.asInstanceOf[SMRequirementTask])
  }

  final def systemComponents: Seq[SMSystemComponent] = {
    children.filter(_.isInstanceOf[SMSystemComponent]).map(_.asInstanceOf[SMSystemComponent])
  }

  final def flows: Seq[SMFlow] = {
    children.filter(_.isInstanceOf[SMFlow]).map(_.asInstanceOf[SMFlow])
  }
}
