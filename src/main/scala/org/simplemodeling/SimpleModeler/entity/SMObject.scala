package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._
import com.asamioffice.goldenport.text.UPathString

/*
 * @since   Sep. 13, 2008
 *  version Jul. 13, 2011
 * @version Feb.  7, 2012
 * @author  ASAMI, Tomoharu
 */
class SMObject(val dslObject: SObject) extends SMElement(dslObject) {
  // resolved in SimpleModelEntity#ParicipationBuilder. Until resolving
  // only baseObjectName, baseObjectPackageName and baseObjectQualifiedName
  // methods are available.
  private var _baseObject: SMObject = _
  // resolved in SimpleModelEntity#ParicipationBuilder.
  val derivedObjects: Buffer[SMObject] = new ArrayBuffer[SMObject]

  val _powertypes = new ArrayBuffer[SMPowertypeRelationship]
  val _attributes = new ArrayBuffer[SMAttribute]
  val _associations = new ArrayBuffer[SMAssociation]
  val _operations = new ArrayBuffer[SMOperation]
  val _ports = new ArrayBuffer[SMPort]
  val _stateMachines = new ArrayBuffer[SMStateMachine]
  val _roles = new ArrayBuffer[SMRoleRelationship]
  val _services = new ArrayBuffer[SMServiceRelationship]
  val _rules = new ArrayBuffer[SMRuleRelationship]
  val _documents = new ArrayBuffer[SMDocumentRelationship]
  val _uses = new ArrayBuffer[SMUse]
  val _participations = new ArrayBuffer[SMParticipation]

  def baseObject: SMObject = {
    if (_baseObject == null) {
      _baseObject = SMNullObject
    }
    _baseObject
  }

  def getBaseObject: Option[SMObject] = {
    baseObject match {
      case SMNullObject => None
      case base => Some(base)
    }
  }

  def baseObject_=(aBase: SMObject) {
    require (aBase != null)
    _baseObject = aBase
  }

  def derivedObjectNames: Seq[String] = {
    if (derivedObjects.isEmpty) Nil
    else derivedObjects.map(_.qualifiedName)
  }

  def packageName = dslObject.packageName

  def version = dslObject.version

  // unify SMPackage
  def xmlNamespace = {
    dslObject.getXmlNamespace match {
      case Some(ns) => ns
      case None => {
        "http://" + packageName.split("\\.").reverse.mkString(".") + "/"
      }
    }
  }

  def baseObjectName = {
    if (dslObject.baseObject == NullObject) {
      var clazz = dslObject.getClass.getSuperclass.asInstanceOf[Class[SObject]]
      if (USimpleModelEntity.isWellknownClass(clazz)) ""
      else clazz.getSimpleName
    } else dslObject.baseObject.name
  }

  def baseObjectPackageName = {
    if (dslObject.baseObject == NullObject) {
      var clazz = dslObject.getClass.getSuperclass.asInstanceOf[Class[SObject]]
      if (USimpleModelEntity.isWellknownClass(clazz)) ""
      else clazz.getPackage.getName
    } else dslObject.baseObject.packageName
  }

  def baseObjectQualifiedName = {
    if (dslObject.baseObject == NullObject) {
      var clazz = dslObject.getClass.getSuperclass.asInstanceOf[Class[SObject]]
      if (USimpleModelEntity.isWellknownClass(clazz)) ""
      else clazz.getName
    } else dslObject.baseObject.qualifiedName
  }

  def typeName: String = null
  def kindName: String = null
  def powertypeName: String = null

  add_feature(FeaturePackage, package_literal) label_is "パッケージ"
  add_feature(FeatureName, SText(name)) label_is "名前"
  add_feature(FeatureBaseClass, base_class_literal) label_is "基底クラス"
  add_feature(FeatureDerivedClasses)(() => derived_classes_literal) label_is "派生クラス"
  add_feature(FeatureType, type_literal) label_is "種類"
  add_feature(FeatureKind, kind_literal) label_is "種別"
  add_feature(FeaturePowertype, powertype_literal) label_is "区分"
  add_feature(FeatureTerm, term_literal) label_is "用語"

  private def package_literal: SDoc = {
    SIAnchor(SText(packageName)) unresolvedRef_is package_element_ref
  }

  private def package_element_ref = {
    new SElementRef(packageName)
  }

  private def base_class_literal: SDoc = {
    if (baseObjectName == "") "-"
    else SIAnchor(SText(baseObjectName)) unresolvedRef_is base_element_ref
  }

  private def base_element_ref = {
    new SElementRef(baseObjectPackageName, baseObjectName)
  }

  private def derived_classes_literal: SDoc = {
    if (derivedObjectNames.isEmpty) return "-"
    val fragment = SFragment()
    for (qname <- derivedObjectNames) {
      val name = UPathString.getLastComponent(qname, ".")
      fragment.addChild(SIAnchor(SText(name)) unresolvedRef_is derived_element_ref(qname))
      if (derivedObjectNames.last != qname) {
	fragment.addChild(SText(", "))
      }
    }
    fragment
  }

  private def derived_element_ref(aQName: String) = {
    new SElementRef(UPathString.getContainerPathname(aQName, "."),
		    UPathString.getLastComponent(aQName, "."))
  }

  private def type_literal: SDoc = {
    if (typeName == null) "-"
    else SIAnchor(SText(typeName)) unresolvedRef_is SHelpRef("object-type", typeName)
  }

  private def kind_literal: SDoc = {
    if (kindName == null) "-"
    else SIAnchor(SText(kindName)) unresolvedRef_is SHelpRef("object-kind", kindName)
  }

  private def powertype_literal: SDoc = {
    if (powertypeName == null) "-"
    else SIAnchor(SText(powertypeName)) unresolvedRef_is SHelpRef("object-powertype", powertypeName)
  }

  private def term_literal: SDoc = {
    if (term == null) "-"
    else term
  }

  dslObject.getPowertypes.foreach(add_powertype)
  dslObject.getAttributes.foreach(add_attribute)
  dslObject.getAssociations.foreach(add_association)
  dslObject.getOperations.foreach(add_operation)
  dslObject.getPorts.foreach(add_port)
  dslObject.getStateMachines.foreach(add_stateMachine)
  dslObject.getRoles.foreach(add_role)
  dslObject.getServices.foreach(add_service)
  dslObject.getRules.foreach(add_rule)
  dslObject.getDocuments.foreach(add_document)

  def powertypes: Seq[SMPowertypeRelationship] = _powertypes
  def attributes: Seq[SMAttribute] = _attributes
  def associations: Seq[SMAssociation] = _associations
  def operations: Seq[SMOperation] = _operations
  def ports: Seq[SMPort] = _ports
  def stateMachines: Seq[SMStateMachine] = _stateMachines
  def roles: Seq[SMRoleRelationship] = _roles
  def services: Seq[SMServiceRelationship] = _services
  def rules: Seq[SMRuleRelationship] = _rules
  def documents: Seq[SMDocumentRelationship] = _documents
  def uses: Seq[SMUse] = _uses
  def participations: Seq[SMParticipation] = _participations

  private def add_powertype(aPowertype: SPowertypeRelationship) {
    _powertypes += new SMPowertypeRelationship(aPowertype)
  }

  private def add_attribute(anAttr: SAttribute) {
    _attributes += new SMAttribute(anAttr)
  }

  private def add_association(anAssoc: SAssociation) {
    _associations += new SMAssociation(anAssoc)
  }

  private def add_operation(anOperation: SOperation) {
    _operations += new SMOperation(anOperation)
  }

  private def add_port(aPort: SPort) {
    _ports += new SMPort(aPort)
  }

  private def add_stateMachine(aStatemachine: (String, SStateMachine)) {
    _stateMachines += new SMStateMachine(aStatemachine._2, this)
  }

  private def add_role(aRole: SRoleRelationship) {
    _roles += new SMRoleRelationship(aRole)
  }

  private def add_service(aService: SServiceRelationship) {
    _services += new SMServiceRelationship(aService)
  }

  private def add_rule(aRule: SRuleRelationship) {
    _rules += new SMRuleRelationship(aRule)
  }

  private def add_document(aDocument: SDocumentRelationship) {
    _documents += new SMDocumentRelationship(aDocument)
  }

  private[entity] def addUse(aUse: SMUse) {
    require (aUse != null)
    _uses += aUse
  }

  private[entity] def addParticipation(aParticipation: SMParticipation) {
    require (aParticipation != null)
    _participations += aParticipation
  }

  // for document
  private[entity] def addAttribute(anAttr: SMAttribute) {
    require (anAttr != null)
    _attributes += anAttr
  }
}

object SMObject {
  def getIdDatatypeName(obj: SMObject): Option[String] = {
    getIdDatatype(obj) map (_.name)
  }

  def getIdDatatype(obj: SMObject): Option[SMDatatype] = {
    obj match {
      case id: domain.SMDomainValueId => {
        id.attributes.headOption flatMap { m2 =>
          m2.attributeType.typeObject match {
            case d: SMDatatype => Some(d)
            case _ => None
          }
        }
      }
      case _ => None
    }
  }
}

object SMNullObject extends SMObject(new NullObject())
