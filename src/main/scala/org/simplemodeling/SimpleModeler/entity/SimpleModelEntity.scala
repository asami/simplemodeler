package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable
import java.util.UUID
import org.goldenport.value._
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.content.GContent
import org.goldenport.sdoc._
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.business._
import org.simplemodeling.dsl.requirement._
import org.simplemodeling.dsl.system._
import org.simplemodeling.dsl.flow._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entity.system._
import org.simplemodeling.SimpleModeler.entity.flow._
import com.asamioffice.goldenport.text.UJavaString

/*
 * @since   Sep. 13, 2008
 *  version Sep. 19, 2011
 * @version Jan. 30, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModelEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GTreeEntityBase[SMElement](aIn, aOut, aContext) {
  override type TreeNode_TYPE = SMElement
  override type DataSource_TYPE = SObjectDataSource
  private val _done_classes = new mutable.HashSet[Class[SObject]]
  private val _done_objects = new mutable.HashSet[String]

  var title: SDoc = SEmpty
  private var _new_logic = true

  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)

  override protected def open_Entity_Update(aDataSource: SObjectDataSource) {
//println("open start simplemodel") 2009-03-01
    set_root(new SMRoot)
    build_datatypes()
    _build_objects(aDataSource)
    resolve_document()
    resolve_value()
    resolve_stateMachine()
    resolve_flowMachine()
    resolve_powertype()
    resolve_role()
    resolve_document_relationship()
    resolve_usecase()
    resolve_use()
    resolve_participation()
//    resolve_stateTransition()
//println("open end simplemodel")
  }

  def findObject(aQName: String): Option[SMObject] = {
    val mayNode = getNode(UJavaString.className2pathname(aQName))
    if (mayNode.isDefined) Some(mayNode.get.asInstanceOf[SMObject])
    else None
  }

  def getObject(aQName: String): SMObject = {
    try {
      findObject(aQName).get
    } catch {
      case _ => error("No object = " + aQName)
    }
  }

  private def build_datatypes() {
    def build_datatype(aDatatype: SDatatype) {
      val pkg = build_package(aDatatype)
      val datatype = new SMDatatype(aDatatype)
      pkg.addChild(datatype)
    }

    build_datatype(XAnyURI)
    build_datatype(XBase64Binary)
    build_datatype(XBoolean)
    build_datatype(XByte)
    build_datatype(XDate)
    build_datatype(XDateTime)
    build_datatype(XDecimal)
    build_datatype(XDouble)
    build_datatype(XDuration)
    build_datatype(XFloat)
    build_datatype(XGDay)
    build_datatype(XGMonth)
    build_datatype(XGMonthDay)
    build_datatype(XGYear)
    build_datatype(XGYearMonth)
    build_datatype(XHexBinary)
    build_datatype(XInt)
    build_datatype(XInteger)
    build_datatype(XLanguage)
    build_datatype(XLong)
    build_datatype(XNegativeInteger)
    build_datatype(XNonNegativeInteger)
    build_datatype(XNonPositiveInteger)
    build_datatype(XPositiveInteger)
    build_datatype(XShort)
    build_datatype(XString)
    build_datatype(XTime)
    build_datatype(XUnsignedByte)
    build_datatype(XUnsignedInt)
    build_datatype(XUnsignedLong)
    build_datatype(XUnsignedShort)
    build_datatype(XText)
    build_datatype(XLink)
    build_datatype(XCategory)
    build_datatype(XUser)
    build_datatype(XEmail)
    build_datatype(XGeoPt)
    build_datatype(XIM)
    build_datatype(XPhoneNumber)
    build_datatype(XPostalAddress)
    build_datatype(XRating)
  }

  private def _build_objects(ds: SObjectDataSource) {
    ds.objects.foreach(build_object)
  }

  def build_object(anObject: SObject) {
    record_trace("build_object = " + anObject.name)
    if (_new_logic) {
      if (!is_exists_or_register(anObject)) {
        build_object_body(anObject)
      }
    } else {
      if (anObject.isObjectScope) {
        build_object_body(anObject)
      } else if (is_exists_or_register(anObject)) {
        return
      } else if (anObject.isMaster) {
        build_object_body(anObject)
      } else {
        build_object_body(SObjectRepository.getObject(anObject.qualifiedName))
      }
    }
  }

  private def build_object_body(anObject: SObject) {
    record_trace("build_object_body = " + anObject.name + "," + anObject.isInstanceOf[DataSource])
    anObject match {
      case actor: DomainActor => build_domain_actor(actor)
      case event: DomainEvent => build_domain_event(event)
      case resource: DomainResource => build_domain_resource(resource)
      case summary: DomainSummary => build_domain_summary(summary)
      case role: DomainRole => build_domain_role(role)
      case part: DomainEntityPart => build_domain_entity_part(part)
      case generic: GenericDomainEntity => build_generic_domain_entity(generic)
      case entity: DomainEntity => build_domain_entity(entity)
      case id: DomainValueId => build_domain_id(id)
      case name: DomainValueName => build_domain_name(name)
      case value: DomainValue => build_domain_value(value)
      case document: DomainDocument => build_domain_document(document)
      case powertype: DomainPowertype => build_domain_powertype(powertype)
      case service: DomainService => build_domain_service(service)
      case rule: DomainRule => build_domain_rule(rule)
      case value: SValue => build_value(value)
      case document: SDocument => build_document(document)
      case datatype: SDatatype => build_datatype(datatype)
      case buc: ExtensionBusinessUsecase => build_extensionBusinessUsecase(buc)
      case buc: BusinessUsecase => build_businessUsecase(buc)
      case task: BusinessTask => build_businessTask(task)
      case ruc: ExtensionRequirementUsecase => build_extensionRequirementUsecase(ruc)
      case ruc: RequirementUsecase => build_requirementUsecase(ruc)
      case task: RequirementTask => build_requirementTask(task)
      case comp: SystemComponent => build_system_component(comp)
      case ds: DataSet => _build_dataset(ds)
      case ds: DataSource => _build_datasource(ds)
      case flow: Flow => _build_flow(flow)
      case any => error("unimplemnted object = " + any)
    }
  }

  private def is_exists_or_register(anObject: SObject): Boolean = {
    if (_done_objects.contains(anObject.qualifiedName)) {
      true
    } else {
      _done_objects += anObject.qualifiedName
      _done_classes += anObject.getClass.asInstanceOf[Class[SObject]]
      false
    }
  }

/*
  private def is_exists_or_register(anObject: SObject): Boolean = {
    val clazz = anObject.getClass.asInstanceOf[Class[SObject]]
    if (is_exists(clazz)) {
      true
    } else {
      done_classes += clazz
      false
    }
  }
*/

  private def is_exists(aClass: Class[SObject]) = {
    _done_classes.contains(aClass)
  }

  private def build_domain_actor(anActor: DomainActor) {
    val pkg = build_package(anActor)
    val actor = new SMDomainActor(anActor)
//    println("SMEntity actor = " + actor.name) 2008-10-19
    build_object(actor, anActor)
    pkg.addChild(actor)
    build_relation_objects(anActor)
  }

  private def build_domain_event(anEvent: DomainEvent) {
    val pkg = build_package(anEvent)
    val event = new SMDomainEvent(anEvent)
//    println("SMEntity event = " + event.name) 2008-10-19
    build_object(event, anEvent)
    pkg.addChild(event)
    build_relation_objects(anEvent)
  }

  private def build_domain_resource(aResource: DomainResource) {
    val pkg = build_package(aResource)
    val resource = new SMDomainResource(aResource)
//    println("SMEntity resource = " + resource.name) 2008-10-19
    build_object(resource, aResource)
    pkg.addChild(resource)
    build_relation_objects(aResource)
  }

  private def build_domain_summary(aSummary: DomainSummary) {
    val pkg = build_package(aSummary)
    val summary = new SMDomainSummary(aSummary)
//    println("SMEntity summary = " + summary.name) 2008-10-19
    build_object(summary, aSummary)
    pkg.addChild(summary)
    build_relation_objects(aSummary)
  }

  private def build_domain_role(aRole: DomainRole) {
    val pkg = build_package(aRole)
    val role = new SMDomainRole(aRole)
//    println("SMEntity role = " + role.name) 2008-10-19
    build_object(role, aRole)
    pkg.addChild(role)
    build_relation_objects(aRole)
  }

  private def build_domain_entity_part(aPart: DomainEntityPart) {
    val pkg = build_package(aPart)
    val part = new SMDomainEntityPart(aPart)
    build_object(part, aPart)
    pkg.addChild(part)
    build_relation_objects(aPart)
  }

  private def build_generic_domain_entity(aGen: GenericDomainEntity) {
    val pkg = build_package(aGen)
    val gen = new SMGenericDomainEntity(aGen)
    build_object(gen, aGen)
    pkg.addChild(gen)
    build_relation_objects(aGen)
  }

  private def build_domain_entity(anEntity: DomainEntity) {
    val pkg = build_package(anEntity)
    val entity = new SMDomainEntity(anEntity)
//    println("SMEntity entity = " + entity.name) 2008-10-19
    build_object(entity, anEntity)
    pkg.addChild(entity)
    build_relation_objects(anEntity)
  }

  private def build_domain_id(anId: DomainValueId) {
    val pkg = build_package(anId)
    val id = new SMDomainValueId(anId)
    build_object(id, anId)
    pkg.addChild(id)
    build_relation_objects(anId)
  }

  private def build_domain_name(aName: DomainValueName) {
    val pkg = build_package(aName)
    val name = new SMDomainValueName(aName)
    build_object(name, aName)
    pkg.addChild(name)
    build_relation_objects(aName)
  }

  private def build_domain_value(aValue: DomainValue) {
    val pkg = build_package(aValue)
    val value = new SMDomainValue(aValue)
//    println("SMEntity value = " + value.name) 2008-10-19
    build_object(value, aValue)
    pkg.addChild(value)
    build_relation_objects(aValue)
  }

  private def build_domain_document(aDocument: DomainDocument) {
    val pkg = build_package(aDocument)
    val document = new SMDomainDocument(aDocument)
//    println("SMEntity document = " + document.name) 2008-10-19
    build_object(document, aDocument)
    pkg.addChild(document)
    build_relation_objects(aDocument)
  }

  private def build_domain_powertype(aPowertype: DomainPowertype) {
    val pkg = build_package(aPowertype)
    val powertype = new SMDomainPowertype(aPowertype)
    build_object(powertype, aPowertype)
    pkg.addChild(powertype)
    build_relation_objects(aPowertype)
  }

  private def build_domain_service(aService: DomainService) {
    val pkg = build_package(aService)
    val service = new SMDomainService(aService)
//    println("SMEntity service = " + service.name) 2008-10-19
    build_object(service, aService)
    pkg.addChild(service)
    build_relation_objects(aService)
  }

  private def build_domain_rule(aRule: DomainRule) {
    val pkg = build_package(aRule)
    val rule = new SMDomainRule(aRule)
//    println("SMEntity rule = " + rule.name) 2008-10-19
    build_object(rule, aRule)
    pkg.addChild(rule)
    build_relation_objects(aRule)
  }

  private def build_value(aValue: SValue) {
    // do nothing
  }

  private def build_document(aDocument: SDocument) {
    // do nothing
  }

  private def build_datatype(aDatatype: SDatatype) {
    // do nothing
  }

  private def build_extensionBusinessUsecase(aUsecase: ExtensionBusinessUsecase) {
    val pkg = build_package(aUsecase)
    val buc = new SMExtensionBusinessUsecase(aUsecase)
    record_trace("SMExtensionBusinessUsecase = " + buc.name)
    build_object(buc, aUsecase)
    pkg.addChild(buc)
    build_relation_objects_in_flows(aUsecase)
//    build_task_in_usecase(buc, pkg)
    build_relation_objects(aUsecase)
  }

  private def build_businessUsecase(aUsecase: BusinessUsecase) {
    val pkg = build_package(aUsecase)
    val buc = new SMBusinessUsecase(aUsecase)
    record_trace("SMBusinessUsecase = " + buc.name)
    build_object(buc, aUsecase)
    pkg.addChild(buc)
    build_relation_objects_in_flows(aUsecase)
//    build_task_in_usecase(buc, pkg)
    build_relation_objects(aUsecase)
  }

/* 2008-12-09
  private def build_task_in_usecase(aUsecase: SMBusinessUsecase, aPkg: SMPackage) {
    for (step <- aUsecase.basicFlow) {
      val task = step.task
      aPkg.addChild(task)
      record_trace("build_task_in_usecase = " + task.basicFlow(0).toXml)
//      build_relation_objects_in_flows(aUsecase)
//      build_relation_objects(aUsecase)
    }
  }
*/

  private def build_businessTask(aTask: BusinessTask) {
    val pkg = build_package(aTask)
    val task = new SMBusinessTask(aTask)
    record_trace("SMBusinessTask = " + task.name)
    build_object(task, aTask)
    pkg.addChild(task)
    build_relation_objects_in_flows(aTask)
    build_relation_objects(aTask)
  }

  private def build_extensionRequirementUsecase(aUsecase: ExtensionRequirementUsecase) {
    val pkg = build_package(aUsecase)
    val buc = new SMExtensionRequirementUsecase(aUsecase)
    record_trace("SMExtensionRequirementUsecase = " + buc.name)
    build_object(buc, aUsecase)
    pkg.addChild(buc)
    build_relation_objects_in_flows(aUsecase)
    build_relation_objects(aUsecase)
  }

  private def build_requirementUsecase(aUsecase: RequirementUsecase) {
    val pkg = build_package(aUsecase)
    val buc = new SMRequirementUsecase(aUsecase)
    record_trace("SMRequirementUsecase = " + buc.name)
    build_object(buc, aUsecase)
    pkg.addChild(buc)
    aUsecase.businessTasks.foreach(build_object)
    build_relation_objects_in_flows(aUsecase)
    build_relation_objects(aUsecase)
  }

  private def build_requirementTask(aTask: RequirementTask) {
    val pkg = build_package(aTask)
    val task = new SMRequirementTask(aTask)
    record_trace("SMRequirementTask = " + task.name)
    build_object(task, aTask)
    pkg.addChild(task)
    build_relation_objects_in_flows(aTask)
    build_relation_objects(aTask)
  }

  private def build_system_component(aComp: SystemComponent) {
    val pkg = build_package(aComp)
    val comp = new SMSystemComponent(aComp)
    build_object(comp, aComp)
    pkg.addChild(comp)
    build_relation_objects(aComp)
  }

  private def _build_dataset(ds: DataSet) {
    val pkg = build_package(ds)
    val dataset = new SMDataSet(ds)
    build_object(dataset, ds)
    pkg.addChild(dataset)
    build_relation_objects(ds)
  }

  private def _build_datasource(ds: DataSource) {
    val pkg = build_package(ds)
    val datasource = new SMDataSource(ds)
    build_object(datasource, ds)
    pkg.addChild(datasource)
    build_relation_objects(ds)
    for (entity <- ds.entities) {
      build_object(entity)
    }
  }

  private def _build_flow(flow: Flow) {
    val pkg = build_package(flow)
    val smflow = new SMFlow(flow)
    
    build_object(smflow, flow)
    pkg.addChild(smflow)
    build_relation_objects(flow)
    for (s <- flow.mainLine; ds <- s.operator.get.entities) {
      build_object(ds)
    }
  }

  private def build_object(aModel: SMObject, aDsl: SObject) {
    // for future usage
  }

  private def build_relation_objects_in_flows(aStory: SStoryObject) {
    aStory.basicFlow.traverse(new GTreeVisitor[SStep] {
      override def enter(aNode: GTreeNode[SStep]) {
        aNode.content match {
          case step: BusinessTaskStep => {
            if (step.businessTask != NullBusinessTask) {
              build_object(step.businessTask)
            }
          }
          case step: ExtendBusinessUsecaseStep => {
            for ((value: String, usecase: ExtensionBusinessUsecase) <- step.businessUsecases) {
              build_object(usecase)
            }
          }
          case step: BusinessUsecaseStep => build_object(step.usecase)
          case step: SActionStep => //
          case step: SExecutionStep => {
            build_object(step.entity)
          }
          case step: SInvocationStep => {
            step.requestDocumentOption match {
              case Some(doc: SDocument) => build_object(doc)
              case None => //
            }
            step.responseDocumentOption match {
              case Some(doc: SDocument) => build_object(doc)
              case None => //
            }
          }
          case step => error("Unkonwn step = " + step)
        }
      }
    })
  }

  private def build_relation_objects(anObject: SObject) {
    build_super_objects(anObject)
    anObject.getPowertypes.foreach(build_powertype_object)
    anObject.getAttributes.foreach(build_attribute_object)
    anObject.getAssociations.foreach(build_association_object)
    anObject.getOperations.foreach(build_operation_object)
    anObject.getStateMachines.foreach(build_stateMachine_object)
    anObject.getRoles.foreach(build_role_object)
    anObject.getServices.foreach(build_service_object)
    anObject.getRules.foreach(build_rule_object)
    anObject.getDocuments.foreach(build_document_object)
    anObject.getPorts.foreach(build_port_object)
  }

  private def build_super_objects(anObject: SObject) {
    build_super_objects_isKindOf(anObject)
    build_super_objects_extends(anObject)
  }

  private def build_super_objects_isKindOf(anObject: SObject) {
    var obj = anObject.baseObject
    while (!obj.isNull) {
      val clazz = obj.getClass.asInstanceOf[Class[SObject]]
      if (is_exists(clazz) || is_wellknown_class(clazz)) {
        obj = NullObject
      } else {
        build_object(obj)
        obj = obj.baseObject
      }
    }
  }

  private def build_super_objects_extends(anObject: SObject) {
    var clazz = anObject.getClass.getSuperclass.asInstanceOf[Class[SObject]]
    while (clazz != null) {
      if (is_exists(clazz) || is_wellknown_class(clazz)) clazz = null
      else {
        build_object(clazz.newInstance())
        clazz = clazz.getSuperclass.asInstanceOf[Class[SObject]]
      }
    }
  }

  private def is_wellknown_class(anClass: Class[SObject]): Boolean = {
    USimpleModelEntity.isWellknownClass(anClass)
  }

  private def build_powertype_object(aPowertype: SPowertypeRelationship) {
    require (aPowertype.powertype != null, "build_attribute_object = " + aPowertype.name)
record_trace("build_powertype_object = " + aPowertype)
    build_object(aPowertype.powertype)
  }

  private def build_attribute_object(anAttr: SAttribute) {
    if (anAttr.attributeType == null) {
      error("build_attribute_object = " + anAttr.name)
//      record_trace("build_attribute_object = " + anAttr.name)
    }
    build_object(anAttr.attributeType)
  }

  private def build_association_object(anAssoc: SAssociation) {
    if (anAssoc.entity == null) {
      error("build_association_object = " + anAssoc.name)
//      record_trace("build_association_object = " + anAssoc.name)
    }
    build_object(anAssoc.entity)
  }

  private def build_operation_object(anOper: SOperation) {
    if (anOper.in != NullDocument)
      build_object(anOper.in)
    if (anOper.out != NullDocument)
      build_object(anOper.out)
  }

  private def build_stateMachine_object(aStateMachine: (String, SStateMachine)) {
    def build_state(aState: SState) {
      val state = SStateRepository.getState(aState.qualifiedName)
      for (transition <- state.transitions) {
        build_object(transition.event)
      }
      state.subStates.foreach(build_state)
    }

    for (state <- aStateMachine._2.stateMap.values.toList) {
      build_state(state)
    }
  }

  private def build_role_object(aRole: SRoleRelationship) {
    build_object(aRole.role)
  }

  private def build_service_object(aService: SServiceRelationship) {
    build_object(aService.service)
  }

  private def build_rule_object(aRule: SRuleRelationship) {
    build_object(aRule.rule)
  }

  private def build_document_object(aDocument: SDocumentRelationship) {
    build_object(aDocument.document)
  }

  private def build_port_object(aPort: SPort) {
    build_object(aPort.entity)
  }
  
  private def build_package(anObject: SObject): SMPackage = {
//    val scalaPkg = anObject.getClass.getPackage
//    val pkgQName = scalaPkg.getName
    val pkgQName = anObject.packageName
    val pathName = UJavaString.className2pathname(pkgQName)
    val pkg = getNode(pathName) match {
      case Some(node) => node.asInstanceOf[SMPackage]
      case None => setNode(pathName).asInstanceOf[SMPackage]
    }
    val dslPkg = pkg.dslPackage
    if (dslPkg.manifestOption.isEmpty) {
      try {
        val manifestClass = anObject.getClass.getClassLoader.loadClass(pkgQName + ".MANIFEST")
        val manifest = manifestClass.newInstance.asInstanceOf[SManifest]
        if (manifest.term != null) {
         dslPkg.term = manifest.term
        }
        dslPkg.caption = manifest.caption
        dslPkg.brief = manifest.brief
        dslPkg.summary = manifest.summary
        dslPkg.description = manifest.description
        dslPkg.note = manifest.note
        dslPkg.history.copyIn(manifest.history)
        dslPkg.manifestOption = Some(manifest)
        record_trace("manifest = " + manifest)
      } catch {
        case e: ClassNotFoundException => record_trace("bad manifest = " + pkgQName) //
        dslPkg.manifestOption = None
      }
    }
    pkg
  }

  final def activePackages: Seq[SMPackage] = {
    val collector = new PackageCollector
    traverse(collector)
    collector.result
  }

  private class PackageCollector extends GTreeVisitor[SMElement] {
    val pkgs = new mutable.ArrayBuffer[SMPackage]
    override def startEnter(aNode: GTreeNode[SMElement]) {
      if (!aNode.isInstanceOf[SMPackage]) return
      if (aNode.children.exists(_.isInstanceOf[SMObject])) {
        pkgs += aNode.asInstanceOf[SMPackage]
      }
    }
    def result: Seq[SMPackage] = pkgs
  }

  private def resolve_document() { // XXX
    def import_attributes(anEntity: SMEntity, aDoc: SMDocument) {
      for (attr <- anEntity.attributes) {
        val attrName = makeImportAttributeName(anEntity, attr)
        val attrType = attr.dslAttribute.attributeType
        val multiplicity = attr.dslAttribute.multiplicity
        // XXX derivation
        val dslAttr = new SAttribute(attrName, multiplicity)
        dslAttr.attributeType = attrType
        aDoc.addAttribute(new SMAttribute(dslAttr))
      }
    }

    def import_associations(anEntity: SMEntity, aDoc: SMDocument) {
      for (assoc <- anEntity.associations) {
        val mayId = assoc.dslAssociation.entity.getEffectiveIdAttribute
        if (mayId.isDefined) {
          val attrName = makeImportAssociationName(anEntity, assoc)
          val id = mayId.get
          val attrType = id.attributeType
          val multiplicity = assoc.dslAssociation.multiplicity
          // XXX derivation
          val dslAttr = new SAttribute(attrName, multiplicity)
          dslAttr.attributeType = attrType
          aDoc.addAttribute(new SMAttribute(dslAttr))
        }
      }
    }

    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case doc: SMDomainDocument => resolve(doc)
          case _ => //
        }
      }

      private def resolve(aDoc: SMDomainDocument) {
        def resolve_entity(aSlot: SMDocumentSlot) {
          val qName = aSlot.containerEntityQName
          if (qName != "") {
            aSlot.containerEntity = getObject(qName).asInstanceOf[SMEntity]
//            record_trace("resolve_entity = " + aSlot.entity.name + "," + aSlot)
          }
          aSlot match {
            case leaf: SMLeafDocumentSlot => //
            case container: SMContainerDocumentSlot => {
              val partQName = container.partEntityQName
              if (partQName != "") {
                container.partEntity = getObject(partQName).asInstanceOf[SMEntity]
              }
            }
          }
        }

        def setup_container(aParent: GTreeNode[SMDocumentSlot], aContainerDoc: SMDocument) {
          for (child <- aParent.children) {
            child.content match {
              case null => error("illegal slot")
              case leaf: SMLeafDocumentSlot => {
                leaf.containerDocument = aContainerDoc
              }
              case container: SMContainerDocumentSlot => {
//                record_trace("enter container = " + container.containerEntity.name + ", " + container.containerDocument.name + ", " + container.partEntity.name + ", " + container.partDocument.name) 2009-02-27
                container.containerDocument = aContainerDoc
                container.multiplicity match {
                  case _: One => setup_container(child, aContainerDoc)
                  case _: ZeroOne => setup_container(child, make_part(container))
                  case _: OneMore => setup_container(child, make_part(container))
                  case _: ZeroMore => setup_container(child, make_part(container))
                }
//                record_trace("leave container = " + container.containerEntity.name + ", " + container.containerDocument.name + ", " + container.partEntity.name + ", " + container.partDocument.name)
              }
            }
          }
        }

        def make_part(aContainer: SMContainerDocumentSlot): SMDocument = {
          val dslDoc = new DomainDocumentPart(
            makeDomainDocumentPartName(aContainer.partEntity),
            aContainer.partEntity.packageName)
          val pkg = build_package(dslDoc)
          val doc = new SMDomainDocumentPart(dslDoc)
          build_object(doc, dslDoc)
          aContainer.partDocument = doc
          pkg.addChild(doc)
          build_relation_objects(dslDoc)
          doc
        }

        def resolve_leaf(aSlot: SMLeafDocumentSlot) {
          aSlot.kind match {
            case _: AllDocumentSlotKind => {
              import_attributes(aSlot.containerEntity, aSlot.containerDocument)
              import_associations(aSlot.containerEntity, aSlot.containerDocument)
            }
            case _: AttributesDocumentSlotKind => {
              import_attributes(aSlot.containerEntity, aSlot.containerDocument)
            }
            case _: DateTimeDocumentSlotKind => {
//              error("???")
            }
            case _: IdDocumentSlotKind => {
//              error("???")
            }
          }
        }

        def resolve_container(aSlot: SMContainerDocumentSlot) {
          if (aSlot.partDocument == SMNullDocument || aSlot.partDocument == SMNullDocumentPart) return
          val attrName = makeDocumentPartAttributeName(aSlot.partDocument)
          val attrType = aSlot.partDocument.dslDocument
          val multiplicity = aSlot.multiplicity
          // XXX derivation
          val dslAttr = new SAttribute(attrName, multiplicity)
          dslAttr.attributeType = attrType
          aSlot.containerDocument.addAttribute(new SMAttribute(dslAttr))
        }

        aDoc.slots.traverse(new GTreeVisitor[SMDocumentSlot] {
          override def enter(aNode: GTreeNode[SMDocumentSlot]) {
            resolve_entity(aNode.content)
          }
        })
        setup_container(aDoc.slots.root, aDoc)
        aDoc.slots.traverse(new GTreeVisitor[SMDocumentSlot] {
          override def enter(aNode: GTreeNode[SMDocumentSlot]) {
            aNode.content match {
              case null => error("???")
              case leaf: SMLeafDocumentSlot => resolve_leaf(leaf)
              case container: SMContainerDocumentSlot => resolve_container(container)
            }
          }
        })
      }
    })
  }

  private def resolve_value() {
    def resolve(value: SMDomainValue) {
      for (a <- value.attributes) {
        a.attributeType.typeObject = getObject(a.attributeType.qualifiedName) 
      }
    }

    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case doc: SMDomainValue => resolve(doc)
          case _ => //
        }
      }
    })
  }

  private def resolve_stateMachine() { // XXX
    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case obj: SMObject => resolve_stateMachines(obj)
          case _ => //
        }
      }

      private def resolve_stateMachines(anObject: SMObject) {
        def resolve_stateMachine(aMachine: SMStateMachine) {
          def resolve_state(aState: SMState) {
            for (transition <- aState.transitions) {
              transition.resource = anObject
              transition.event = getObject(transition.dslTransition.event.qualifiedName)
              transition.preState = aMachine.getState(transition.dslTransition.preState)
              transition.postState = aMachine.getState(transition.dslTransition.postState)
              transition.event match {
                case event: SMDomainEvent => event.resourceTransitions += transition
                case _ => error("not event = " + transition.event)
              }
            }
            for (subState <- aState.subStates) {
              resolve_state(subState)
            }
          }

          for (state <- aMachine.states) {
            resolve_state(state)
          }
/*
          for (transition <- aMachine.transitions) {
            transition.resource = anObject
            transition.event = getObject(transition.dslTransition.event.qualifiedName)
            transition.preState = aMachine.getState(transition.dslTransition.preState) // 2008-12-25 new SMState(transition.dslTransition.preState)
            transition.postState = aMachine.getState(transition.dslTransition.postState) // 2008-12-25 new SMState(transition.dslTransition.postState)
            transition.event match {
              case event: SMDomainEvent => event.resourceTransitions += transition
              case _ => error("not event = " + transition.event)
            }
          }
*/
        }

        anObject.stateMachines.foreach(resolve_stateMachine)
      }
    })
  }

  private def resolve_flowMachine() {
    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case obj: SMFlow => resolve_flowMachines(obj)
          case _ => //
        }
      }

      private def resolve_flowMachines(anObject: SMFlow) {
        def resolve_flowMachine(aMachine: SMFlowMachine) {
          def resolve_step(step: SMFlowStep) {
            for (et <- step.entities) {
              et.typeObject = getObject(et.qualifiedName)
              et.typeObject match {
                case ds: SMDataSource => {
                  for (et2 <- ds.entities) {
                    et2.typeObject = getObject(et2.qualifiedName)
                  }
                }
                case _ => {}
              }
            }
          }
          aMachine.steps.foreach(resolve_step)
        }

        resolve_flowMachine(anObject.flowMachine)
      }
    })
  }

  private def resolve_powertype() {
    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case obj: SMObject => resolve_powertypes(obj)
          case _ => //
        }
      }

      private def resolve_powertypes(anObject: SMObject) {
        def resolve_powertype(aRel: SMPowertypeRelationship) {
          aRel.powertype = getObject(aRel.dslPowertypeRelationship.powertype.qualifiedName).asInstanceOf[SMPowertype]
        }

        anObject.powertypes.foreach(resolve_powertype)
      }
    })
  }

  private def resolve_role() {
    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case obj: SMObject => resolve_roles(obj)
          case _ => //
        }
      }

      private def resolve_roles(anObject: SMObject) {
        def resolve_role(aRel: SMRoleRelationship) {
          aRel.role = getObject(aRel.dslRoleRelationship.role.qualifiedName).asInstanceOf[SMRole]
        }

        anObject.roles.foreach(resolve_role)
      }
    })
  }

  private def resolve_document_relationship() {
    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case obj: SMObject => resolve_documents(obj)
          case _ => //
        }
      }

      private def resolve_documents(anObject: SMObject) {
        def resolve_document(aRel: SMDocumentRelationship) {
          aRel.document = getObject(aRel.dslDocumentRelationship.document.qualifiedName).asInstanceOf[SMDocument]
        }

        anObject.documents.foreach(resolve_document)
      }
    })
  }

  private def resolve_usecase() {
    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case uc: SMBusinessUsecase => resolve_usecase(uc)
          case uc: SMRequirementUsecase => resolve_usecase(uc)
          case _ => //
        }
      }

      private def resolve_usecase(aUc: SMBusinessUsecase) {
        record_trace("resolve business usecase = " + aUc)
        val x = aUc.getBusinessTaskSteps;
        for (step <- aUc.getBusinessTaskSteps) {
          val task = getObject(step.dslBusinessTask.qualifiedName).asInstanceOf[SMBusinessTask]
          step.businessTask = task
          aUc.includeBusinessTasks += task
          task.userBusinessUsecases += aUc
          step.entities = step.entityNames.map(getObject) collect {
            case e: SMEntity => e
          }
          record_trace(step.entities + "," + this)
        }
        for (step <- aUc.getBusinessUsecaseSteps) {
          val usecase = getObject(step.dslBusinessUsecase.qualifiedName).asInstanceOf[SMBusinessUsecase]
          step.businessUsecase = usecase
          aUc.includeBusinessUsecases += usecase
          usecase.userBusinessUsecases += aUc
        }
      }

      private def resolve_usecase(aUc: SMRequirementUsecase) {
        record_trace("resolve requirement usecase = " + aUc)
        for (task <- aUc.dslRequirementUsecase.businessTasks) {
          aUc.userBusinessTasks += getObject(task.qualifiedName).asInstanceOf[SMBusinessTask]
        }
        for (step <- aUc.getRequirementTaskSteps) {
          val task = getObject(step.dslRequirementTask.qualifiedName).asInstanceOf[SMRequirementTask]
          step.requirementTask = task
          aUc.includeRequirementTasks += task
          task.userRequirementUsecases += aUc
        }
        for (step <- aUc.getRequirementUsecaseSteps) {
          val usecase = getObject(step.dslRequirementUsecase.qualifiedName).asInstanceOf[SMRequirementUsecase]
          step.requirementUsecase = usecase
          aUc.includeRequirementUsecases += usecase
          usecase.userRequirementUsecases += aUc
        }
      }
    })
  }

  private def resolve_use() {
    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case obj: SMObject => resolve_object(obj)
          case _ => //
        }
      }

      private def resolve_object(anObject: SMObject) {
        def resolve_use(aUse: SMUse) {
          if (aUse.element == SMNullObject && aUse.elementQName != "") {
            aUse.element = getObject(aUse.elementQName)
          }
          if (aUse.user == SMNullObject && aUse.userQName != "") {
            aUse.user = getObject(aUse.userQName)
          }
          if (aUse.receiver == SMNullObject && aUse.receiverQName != "") {
            aUse.receiver = getObject(aUse.receiverQName)
          }
        }

        anObject.uses.foreach(resolve_use)
      }
    })
  }

  //
  private def resolve_participation() {
    traverse(new ParticipationBuilder)
  }

  private class ParticipationBuilder extends GTreeVisitor[SMElement] {
    override def enter(aNode: GTreeNode[SMElement]) {
      aNode.content match {
        case dt: SMDatatype => resolve_datatype(dt)
        case obj: SMObject => resolve_object(obj)
        case pkg: SMPackage => // do nothing
      }
    }

    private def resolve_object(anObject: SMObject) {
      def resolve_base_object() {
        val objectName = anObject.baseObjectQualifiedName
        require (objectName != null)
        if (objectName != "") {
          val base = getObject(objectName)
          anObject.baseObject = base
          base.derivedObjects += anObject
        }
      }

      def resolve_relationship_with_participation(aRel: SMRelationship, aParticipationRole: SMParticipationRole) {
        val relType = aRel.relationshipType
        val target = getObject(relType.qualifiedName)
        relType.typeObject = target
        val participation = make_participation
        participation.roleType = aParticipationRole
        participation.roleName = aRel.name
        target.addParticipation(participation)
      }

      def resolve_role(aRoleRel: SMRoleRelationship) {
        resolve_relationship_with_participation(aRoleRel, RoleParticipationRole)
      }

      def resolve_service(aServiceRel: SMServiceRelationship) {
        resolve_relationship_with_participation(aServiceRel, ServiceParticipationRole)
      }

      def resolve_rule(aRuleRel: SMRuleRelationship) {
        resolve_relationship_with_participation(aRuleRel, RuleParticipationRole)
      }

      def resolve_attribute(anAttr: SMAttribute) {
        val attrType = anAttr.attributeType
        val target = getObject(attrType.qualifiedName)
        attrType.typeObject = target
        val participation = make_participation
        participation.roleType = AttributeParticipationRole
        participation.roleName = anAttr.name
        target.addParticipation(participation)
      }

      def resolve_association(anAssoc: SMAssociation) {
        val assocType = anAssoc.associationType
        val target = getObject(assocType.qualifiedName)
        assocType.typeObject = target
        val participation = make_participation
        participation.roleType = if (anAssoc.isComposition) {
          CompositionParticipationRole
        } else if (anAssoc.isAggregation) {
          AggregationParticipationRole
        } else {
          AssociationParticipationRole
        }
        participation.roleName = anAssoc.name
        participation.associationOption = Some(anAssoc)
        target.addParticipation(participation)
      }

      def make_participation = {
        val dslParticipation = new SParticipation(UUID.randomUUID.toString)
        val participation = new SMParticipation(dslParticipation)
        participation.element = anObject
        participation
      }

      def resolve_operation(anOper: SMOperation) {
        // XXX
      }

      def resolve_port(port: SMPort) {
        val portType = port.entityType
        val target = getObject(portType.qualifiedName)
        portType.typeObject = target
        val participation = make_participation
        participation.roleType = PortParticipationRole
        participation.roleName = port.name
        target.addParticipation(participation)
      }

      resolve_base_object()
      anObject.roles.foreach(resolve_role)
      anObject.services.foreach(resolve_service)
      anObject.rules.foreach(resolve_rule)
      anObject.attributes.foreach(resolve_attribute)
      anObject.associations.foreach(resolve_association)
      anObject.operations.foreach(resolve_operation)
      anObject.ports.foreach(resolve_port)
    }

    private def resolve_datatype(aDatatype: SMDatatype) {
      resolve_object(aDatatype)
    }
  }

/*
  private def resolve_stateTransition() {
    traverse(new GTreeVisitor[SMElement] {
      override def enter(aNode: GTreeNode[SMElement]) {
        aNode.content match {
          case event: SMDomainEvent => resolve_stateTransition(event)
          case _ => //
        }
      }

      def resolve_stateTransition(anEvent: SMDomainEvent) {
        def resolve_association(anAssoc: SMAssociation) {
          val resource: SMObject = anAssoc.associationType.typeObject
          for (sm <- resource.stateMachines; transition <- sm.transitions) {
            transition.resource = resource
            transition.stateMachine = sm
            transition.event = getObject(transition.dslTransition.event.qualifiedName)
            transition.preState = new SMState(transition.dslTransition.preState)
            transition.postState = new SMState(transition.dslTransition.postState)
            anEvent.resourceTransitions += transition
          }
        }

        anEvent.associations.foreach(resolve_association)
      }
    })
  }
*/

  // context
  protected final def syntax_error(aMessage: String) {
    error(aMessage)
  }

  // configuration : XXX separate
  final def makeDomainDocumentPartName(anEntity: SMEntity): String = {
    require (anEntity != SMNullEntity)
    "DDI" + anEntity.term
  }

  final def makeImportAttributeName(anEntity: SMEntity, anAttr: SMAttribute): String = {
    anEntity.term + "_" + anAttr.dslAttribute.name
  }

  final def makeImportAssociationName(anEntity: SMEntity, anAssoc: SMAssociation): String = {
    anEntity.term + "_" + anAssoc.dslAssociation.name
  }

  final def makeDocumentPartAttributeName(anPart: SMDocumentPart): String = {
    require (anPart != SMNullDocumentPart)
    anPart.term
  }
}

class SimpleModelEntityClass extends GEntityClass {
  type Instance_TYPE = SimpleModelEntity

  override def accept_DataSource(aDataSource: GDataSource): Boolean = {
    aDataSource.isInstanceOf[SObjectDataSource]
  }

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new SimpleModelEntity(aDataSource, aContext))
}
