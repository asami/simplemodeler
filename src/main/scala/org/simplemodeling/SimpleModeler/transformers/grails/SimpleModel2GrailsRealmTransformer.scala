package org.simplemodeling.SimpleModeler.transformers.grails

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities.grails._
import org.goldenport.value._
import org.goldenport.entity._
import org.goldenport.entity.content.{GContent, EntityContent}
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.util.UDsl
import com.asamioffice.goldenport.text.UJavaString

/*
 * @since   Jan. 25, 2009
 *  version Apr. 17, 2011
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2GrailsRealmTransformer(val simpleModel: SimpleModelEntity) {
  private val _context = simpleModel.entityContext
  private val _grailsRealm = new GrailsRealmEntity(_context)

  def toGrailsRealm: GrailsRealmEntity = {
    simpleModel.open()
    _grailsRealm.open()
    simpleModel.traverse(new Transformer())
//    _grailsRealm.traverse(new Dump())
    _grailsRealm.traverse(new Resolver())
    simpleModel.close()
    _grailsRealm ensuring(_.isOpened)
  }

  private def make_pogo_name(aName: String): String = {
    UDsl.makeRigidCamelName(aName)
  }

  class Transformer extends GTreeVisitor[SMElement] {
    override def enter(aNode: GTreeNode[SMElement]) {
      val entity = aNode.content
      val pogo = entity match {
	case actor: SMDomainActor => transform_actor(actor)
	case resource: SMDomainResource => transform_resource(resource)
	case event: SMDomainEvent => transform_event(event)
	case role: SMDomainRole => transform_role(role)
	case summary: SMDomainSummary => transform_summary(summary)
	case assoc: SMDomainAssociationEntity => transform_associationEntity(assoc)
	case entity: SMDomainEntity => transform_entity(entity)
	case id: SMDomainValueId => transform_id(id)
	case name: SMDomainValueName => transform_name(name)
	case value: SMDomainValue => transform_value(value)
	case powertype: SMDomainPowertype => transform_powertype(powertype)
	case document: SMDomainDocument => transform_document(document)
	case rule: SMDomainRule => null
	case service: SMDomainService => null
	case datatype: SMDatatype => null
	case uc: SMBusinessUsecase => null
	case task: SMBusinessTask => null
	case pkg: SMPackage => null
	case _ => error("Unspported simple model object = " + entity)
      }
      if (pogo != null) {
	val packagePathname = aNode.parent.pathname
	val className = pogo.name
	val pogoSourcePathname = "/grails-app/domain" + packagePathname + "/" + className + ".groovy"
	_grailsRealm.setEntity(pogoSourcePathname, pogo)
      }
    }

    private def transform_actor(actor: SMDomainActor): GRPogoEntity = {
      transform_entity(actor)
    }

    private def transform_resource(resource: SMDomainResource): GRPogoEntity = {
      transform_entity(resource)
    }

    private def transform_event(event: SMDomainEvent): GRPogoEntity = {
      transform_entity(event)
    }

    private def transform_role(role: SMDomainRole): GRPogoEntity = {
      transform_entity(role)
    }

    private def transform_summary(summary: SMDomainSummary): GRPogoEntity = {
      transform_entity(summary)
    }

    private def transform_associationEntity(assoc: SMDomainAssociationEntity): GRPogoEntity = {
      transform_entity(assoc)
    }

    private def transform_entity(entity: SMDomainEntity): GRPogoEntity = {
      val pogo = transform_object(entity)
      pogo
    }

    private def transform_id(id: SMDomainValueId): GRPogoEntity = {
      null
    }

    private def transform_name(name: SMDomainValueName): GRPogoEntity = {
      null
    }

    private def transform_value(value: SMDomainValue): GRPogoEntity = {
      null
    }

    private def transform_powertype(powertype: SMDomainPowertype): GRPogoEntity = {
      null
    }

    private def transform_document(document: SMDomainDocument): GRPogoEntity = {
      null
    }

    private def transform_object(anObject: SMObject): GRPogoEntity = {
      val pogo = new GRPogoEntity(_context)

      pogo.name = make_pogo_name(anObject.name)
      pogo.packageName = anObject.packageName
//      pogo.setDescription(new LJDescription(description_to_string(anObject)))
      if (anObject.baseObject != SMNullObject) {
	build_super(pogo, anObject)
      }
      build_properties(pogo, anObject)
      pogo
    }

    private def build_super(pogo: GRPogoEntity, anObject: SMObject) {
      pogo.setBaseClass(new GREntityType(pogo.name, pogo.packageName))
    }

    private def build_properties(pogo: GRPogoEntity, anObject: SMObject) {
      anObject.attributes.foreach(build_attribute(pogo, _))
      anObject.associations.foreach(build_association(pogo, _))
    }

    private def build_attribute(aPogo: GRPogoEntity, anAttr: SMAttribute) {
      val attr = aPogo.addAttribute(anAttr.name, pogo_type(anAttr))
      attr.multiplicity = get_multiplicity(anAttr.multiplicity)
    }

    private def build_association(aPogo: GRPogoEntity, anAssoc: SMAssociation) {
      val attr = aPogo.addAttribute(anAssoc.name, pogo_type(anAssoc))
      attr.multiplicity = get_multiplicity(anAssoc.multiplicity)
    }

    private def pogo_type(anAttr: SMAttribute): GRPogoType = {
      val attributeType = anAttr.attributeType
      val pogoType = anAttr.attributeType.qualifiedName match {
	case "org.simplemodeling.dsl.datatype.XDateTime" => new GRDateType
	case "org.simplemodeling.dsl.datatype.XAnyURI" => new GRUrlType
	case _ => new GRStringType
      }
      pogoType
    }

    private def pogo_type(anAssoc: SMAssociation): GRPogoType = {
      val assocType = anAssoc.associationType
      val name = make_pogo_name(assocType.name)
      val pogoType = new GREntityType(name, assocType.packageName)
      pogoType
    }

    private def get_multiplicity(aMultiplicity: SMMultiplicity): GRMultiplicity = {
      aMultiplicity.kind match {
	case m: SMMultiplicityOne => GROne
	case m: SMMultiplicityZeroOne => GRZeroOne
	case m: SMMultiplicityOneMore => GROneMore
	case m: SMMultiplicityZeroMore => GRZeroOne
	case m: SMMultiplicityRange => GRRange
	case _ => error("Unkown multiplicity = " + aMultiplicity.kind)
      }
    }
  }

  class Dump extends GTreeVisitor[GContent] {
    override def enter(aNode: GTreeNode[GContent]) {
      println("dump = " + aNode.pathname)
    }
  }

  class Resolver extends GTreeVisitor[GContent] {
    def findPogo(aQName: String): Option[GRPogoEntity] = {
      val mayNode = _grailsRealm.getNode("/grails-app/domain" + UJavaString.className2pathname(aQName) + ".groovy")
      if (mayNode.isDefined) mayNode.get.entity.asInstanceOf[Some[GRPogoEntity]]
      else None
    }

    def getPogo(aQName: String): GRPogoEntity = {
      try {
	findPogo(aQName).get
      } catch {
	case _ => error("No pogo = " + aQName)
      }
    }

    def pogo_type(anAssoc: SMAssociation): GRPogoType = {
      val assocType = anAssoc.associationType
      val name = make_pogo_name(assocType.name)
      val pogoType = new GREntityType(name, assocType.packageName)
      pogoType
    }

    override def enter(aNode: GTreeNode[GContent]) {
      aNode.asInstanceOf[GContainerEntityNode].entity match {
	case Some(entity: GRPogoEntity) => {
	  for (attr <- entity.attributes if (attr.isHasMany)) {
	    attr.attributeType match {
	      case entityType: GREntityType => {
		val target = getPogo(entityType.qualifiedName)
		val name = entity.name // XXX
		target.addAttribute(name, new GREntityType(entity))
	      }
	      case _ => //
	    }
	  }
	}
	case None => //
      }
    }
  }
}
