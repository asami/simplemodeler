package org.simplemodeling.SimpleModeler.transformers

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.SimpleModelerConstants
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.goldenport.value._
import org.goldenport.service.GServiceContext
import org.goldenport.entity._
import org.goldenport.entity.content.{GContent, EntityContent}
import org.goldenport.entities.fs.FileStoreEntity
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.util.UDsl
import com.asamioffice.goldenport.text.{UJavaString, UString, UPathString}
import com.asamioffice.goldenport.io.UIO
import com.asamioffice.goldenport.util.MultiValueMap
import org.goldenport.recorder.Recordable

/**
 * Derived from SimpleModel2JavaRealmTransformerBase (Feb. 3, 2011)
 * 
 * @since   Apr.  7, 2012
 *  version May.  6, 2012
 *  version Jun. 17, 2012
 * @version Nov. 29, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SimpleModel2ProgramRealmTransformerBase(val simpleModel: SimpleModelEntity, val serviceContext: GServiceContext
    ) extends SimpleModel2ProgramRealmTransformerBaseHelper with Recordable with SimpleModelerConstants {
  type EntityContextTYPE <: PEntityContext
  type TargetRealmTYPE <: PRealmEntity

  val target_context: EntityContextTYPE
  val target_realm: TargetRealmTYPE
  val defaultFileSuffix: String

  var srcMainDir: String = ""
  var scriptSrcDir= ""
  var useEntityDocument = true
  var useValue = true
  var usePowertype = true
  var useStateMachine = true
  var isMakeProject = true
  var useKindPackage = false 
  var usePackageObject = true // TODO turn off

  var entityKindName = DEFAULT_MODEL_KIND
  var viewKindName = DEFAULT_VIEW_KIND
  var controllerKindName = DEFAULT_CONTROLLER_KIND
  var docKindName = DEFAULT_DOC_KIND
  var storeKindName = DEFAULT_STORE_KIND

  setup_FowardingRecorder(serviceContext)

  def transform(): TargetRealmTYPE = {
    target_context.defaultFileSuffix = defaultFileSuffix
    target_context.srcMainDir = srcMainDir
    simpleModel.open()
    target_realm.open()
    record_debug("SimpleModel2ProgramRealTransform.transform: SimpleModel tree start")
    simpleModel.dumpDebug()
    record_debug("SimpleModel2ProgramRealTransform.transform: SimpleModel tree end")
    simpleModel.traverse(make_Builder)
    record_debug("SimpleModel2ProgramRealTransform.transform: Target tree start")
    target_realm.dumpDebug()
    record_debug("SimpleModel2ProgramRealTransform.transform: Traget tree end")
    make_Pre_Resolver_Phases.foreach(target_realm.traverse)
    make_Resolver.foreach(target_realm.traverse)
    make_Phases.foreach(target_realm.traverse)
    if (isMakeProject) {
      make_Project()
    }
    _remove_unused_entities
    simpleModel.close()
    target_realm ensuring(_.isOpened)
  }

  protected def make_Builder(): BuilderBase

  protected def make_Pre_Resolver_Phases(): List[TransformerPhase] = Nil

  protected def make_Resolver(): Option[ResolvePhase] = None

  protected def make_Phases(): List[TransformerPhase] = {
    Nil
  }

  protected def make_Project() {
  }

  protected def make_Package_Name(obj: SMObject): String = {
    make_Package_Name(obj.packageName)
  }

  protected def make_Package_Name(pkg: SMPackage): String = {
    make_Package_Name(pkg.qualifiedName)
  }

  protected def make_Package_Name(qname: String): String = {
    qname
  }

  protected def make_Qualified_Name(qname: String): String = {
    qname
  }

  abstract class BuilderBase extends GTreeVisitor[SMElement] { 
    type DomainObjectTYPE = PObjectEntity
    type DomainTraitTYPE = PTraitEntity
    type DomainActorTYPE = PEntityObjectEntity
    type DomainResourceTYPE = PEntityObjectEntity
    type DomainEventTYPE = PEntityObjectEntity
    type DomainRoleTYPE = PEntityObjectEntity
    type DomainSummaryTYPE = PEntityObjectEntity
    type DomainAssociationEntityTYPE = PEntityObjectEntity
    type DomainEntityTYPE = PEntityObjectEntity
    type DomainEntityPartTYPE = PEntityPartEntity
    type DomainValueIdTYPE = PValueEntity
    type DomainValueNameTYPE = PValueEntity
    type DomainValueTYPE = PValueEntity
    type DomainPowertypeTYPE = PPowertypeEntity
    type DomainStateMachineTYPE = PStateMachineEntity
    type DomainDocumentTYPE = PDocumentEntity
    type DomainRuleTYPE = PRuleEntity
    type DomainServiceTYPE = PServiceEntity
//    type DatatypeTYPE = PDatatypeEntity
//    type BusinessUsecaseTYPE = PBusinessUsecaseEntity
//    type BusinessTaskTYPE = PBusinessTaskEntity
//    type PackageTYPE = <: DomainObjectTYPE

    override def enter(aNode: GTreeNode[SMElement]) {
      record_trace("dump(orig) = " + aNode.pathname)
      aNode.content match {
        case tr: SMDomainTrait => transform_Trait(tr)
        case actor: SMDomainActor => transform_Actor(actor)
        case resource: SMDomainResource => transform_Resource(resource)
        case event: SMDomainEvent => transform_Event(event)
        case role: SMDomainRole => transform_Role(role)
        case summary: SMDomainSummary => transform_Summary(summary)
        case assoc: SMDomainAssociationEntity => transform_AssociationEntity(assoc)
        case entity: SMDomainEntity => transform_Entity(entity)
        case part: SMDomainEntityPart => transform_Entity_Part(part)
        case id: SMDomainValueId => transform_Id(id)
        case name: SMDomainValueName => transform_Name(name)
        case value: SMDomainValue => transform_Value(value)
        case powertype: SMDomainPowertype => transform_Powertype(powertype)
        case sm: SMDomainStateMachine => transform_StateMachine(sm)
        case document: SMDomainDocument => transform_Document(document)
        case rule: SMDomainRule => transform_Rule(rule)
        case service: SMDomainService => transform_Service(service)
//        case port: SMDomainPort => transform_Port(port)
//        case facade: SMDomainFacade => transform_Facade(facade)
        case datatype: SMDatatype => // ignore in code
        case uc: SMStoryObject => // ignore in code
        case b: SMBusinessEntity => // ignore in code
        case pkg: SMPackage => transform_Package(pkg)
        case unknown => sys.error("Unspported simple model object = " + unknown)
      }
    }

    protected def transform_Trait(tr: SMDomainTrait): DomainTraitTYPE = {
      val obj = create_Trait(tr)
      build_object(obj, tr)
      make_Traits(tr, obj).foreach(build_derived_object(tr, obj))
      obj
    }

    protected def create_Trait(tr: SMDomainTrait): DomainTraitTYPE = {
      throw new UnsupportedOperationException
    }

    protected def make_Traits(tr: SMDomainTrait, po: DomainTraitTYPE): List[PObjectEntity] = Nil

    protected def transform_Actor(actor: SMDomainActor): DomainActorTYPE = {
      val obj = create_Actor(actor)
      build_entity(obj, actor)
      make_Actors(actor, obj).foreach(build_derived_object(actor, obj))
      obj
    }

    protected def create_Actor(entity: SMDomainActor): DomainActorTYPE = {
      create_Entity(entity)
    }

    protected def make_Actors(entity: SMDomainActor, po: DomainActorTYPE): List[PObjectEntity] = Nil

    protected def transform_Resource(resource: SMDomainResource): DomainResourceTYPE = {
      val obj = create_Resource(resource)
      build_entity(obj, resource)
      make_Resources(resource, obj).foreach(build_derived_object(resource, obj))
      obj
    }

    protected def create_Resource(entity: SMDomainResource): DomainResourceTYPE = {
      create_Entity(entity)
    }

    protected def make_Resources(entity: SMDomainResource, po: DomainResourceTYPE): List[PObjectEntity] = Nil

    protected def transform_Event(event: SMDomainEvent): DomainEventTYPE = {
      val obj = create_Event(event)
      build_entity(obj, event)
      make_Events(event, obj).foreach(build_derived_object(event, obj))
      obj
    }

    protected def create_Event(entity: SMDomainEvent): DomainEventTYPE = {
      create_Entity(entity)
    }

    protected def make_Events(entity: SMDomainEvent, po: DomainEventTYPE): List[PObjectEntity] = Nil

    protected def transform_Role(role: SMDomainRole): DomainRoleTYPE = {
      val obj = create_Role(role)
      build_entity(obj, role)
      make_Roles(role, obj).foreach(build_derived_object(role, obj))
      obj
    }

    protected def create_Role(role: SMDomainRole): DomainRoleTYPE = {
      create_Entity(role)
    }

    protected def make_Roles(entity: SMDomainRole, po: DomainRoleTYPE): List[PObjectEntity] = Nil

    protected def transform_Summary(summary: SMDomainSummary): DomainSummaryTYPE = {
      val obj = create_Summary(summary)
      build_entity(obj, summary)
      make_Summarys(summary, obj).foreach(build_derived_object(summary, obj))
      obj
    }

    protected def create_Summary(entity: SMDomainSummary): DomainSummaryTYPE = {
      create_Entity(entity)
    }

    protected def make_Summarys(entity: SMDomainSummary, po: DomainSummaryTYPE): List[PObjectEntity] = Nil

    protected def transform_AssociationEntity(assoc: SMDomainAssociationEntity): DomainAssociationEntityTYPE = {
      val obj = create_AssociationEntity(assoc)
      build_entity(obj, assoc)
      make_AssociationEntities(assoc, obj).foreach(build_derived_object(assoc, obj))
      obj
    }

    protected def create_AssociationEntity(entity: SMDomainAssociationEntity): DomainAssociationEntityTYPE = {
      create_Entity(entity)
    }

    protected def make_AssociationEntities(entity: SMDomainAssociationEntity, po: DomainAssociationEntityTYPE): List[PObjectEntity] = Nil

    protected def transform_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      val obj = create_Entity(entity)
      build_entity(obj, entity)
      make_Entities(entity, obj).foreach(build_derived_object(entity, obj))
      obj
    }

    protected def create_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      throw new UnsupportedOperationException
    }

    protected def make_Entities(entity: SMDomainEntity, obj: DomainEntityTYPE): List[PObjectEntity] = Nil

    protected def transform_Entity_Part(part: SMDomainEntityPart): DomainEntityPartTYPE = {
      val obj = create_Entity_Part(part)
      build_entity(obj, part)
      make_Parts(part, obj).foreach(build_derived_object(part, obj))
      obj
    }

    protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      throw new UnsupportedOperationException
    }

    protected def make_Parts(entity: SMDomainEntityPart, po: DomainEntityPartTYPE): List[PObjectEntity] = Nil

    protected def transform_Powertype(powertype: SMDomainPowertype) {
      if (!usePowertype) return;
      for (obj <- create_Powertype(powertype)) {
        build_object(obj, powertype)
        make_Powertypes(powertype, obj).foreach(build_derived_object(powertype, obj))
      }
    }

    protected def create_Powertype(entity: SMDomainPowertype): Option[DomainPowertypeTYPE] = None

    protected def make_Powertypes(entity: SMDomainPowertype, po: DomainPowertypeTYPE): List[PObjectEntity] = Nil

    protected def transform_StateMachine(powertype: SMDomainStateMachine) {
      if (!useStateMachine) return;
      for (obj <- create_StateMachine(powertype)) {
        build_object(obj, powertype)
        make_StateMachines(powertype, obj).foreach(build_derived_object(powertype, obj))
      }
    }

    protected def create_StateMachine(entity: SMDomainStateMachine): Option[DomainStateMachineTYPE] = None

    protected def make_StateMachines(entity: SMDomainStateMachine, po: DomainStateMachineTYPE): List[PObjectEntity] = Nil

    protected def transform_Id(id: SMDomainValueId) {
      if (!useValue) return
      for (obj <- create_ValueId(id)) {
        build_object(obj, id)
        make_ValueIds(id, obj).foreach(build_derived_object(id, obj))
      }
    }

    protected def create_ValueId(id: SMDomainValueId): Option[DomainValueTYPE] = {
      create_Value(id)
    }

    protected def make_ValueIds(value: SMDomainValueId, po: DomainValueTYPE): List[PObjectEntity] = Nil

    protected def transform_Name(name: SMDomainValueName) {
      if (!useValue) return
      for (obj <- create_ValueName(name)) {
        build_object(obj, name)
        make_ValueNames(name, obj).foreach(build_derived_object(name, obj))
      }
    }

    protected def create_ValueName(name: SMDomainValueName): Option[DomainValueTYPE] = {
      create_Value(name)
    }

    protected def make_ValueNames(entity: SMDomainValueName, po: DomainValueTYPE): List[PObjectEntity] = Nil

    private def transform_Value(value: SMDomainValue) {
      if (!useValue) return
      for (obj <- create_Value(value)) {
        build_object(obj, value)
        make_Values(value, obj).foreach(build_derived_object(value, obj))
      }
    }

    protected def create_Value(entity: SMDomainValue): Option[DomainValueTYPE] = None

    protected def make_Values(entity: SMDomainValue, po: DomainValueTYPE): List[PObjectEntity] = Nil

    private def transform_Document(document: SMDomainDocument) {
      for (obj <- create_Document(document)) {
        build_object(obj, document)
        if (useKindPackage) {
          obj.kindName = docKindName
        }
        make_Documents(document, obj).foreach(build_derived_object(document, obj))
        obj
      }
    }

    protected def create_Document(entity: SMDomainDocument): Option[DomainDocumentTYPE] = None

    protected def make_Documents(entity: SMDomainDocument, po: DomainDocumentTYPE): List[PObjectEntity] = Nil

    private def transform_Rule(rule: SMDomainRule) {
      for (obj <- create_Rule(rule)) {
        build_object(obj, rule)
        make_Rules(rule, obj).foreach(build_derived_object(rule, obj))
      }
    }

    protected def create_Rule(entity: SMDomainRule): Option[PRuleEntity] = None

    protected def make_Rules(entity: SMDomainRule, po: PRuleEntity): List[PObjectEntity] = Nil

    private def transform_Service(service: SMDomainService) {
      for (obj <- create_Service(service)) {
        build_object(obj, service)
        make_Services(service, obj).foreach(build_derived_object(service, obj))
        obj
      }
    }

    protected def create_Service(entity: SMDomainService): Option[DomainServiceTYPE] = None

    protected def make_Services(entity: SMDomainService, po: DomainServiceTYPE): List[PObjectEntity] = Nil

    protected def transform_Package(pkg: SMPackage) {
      val contextname = target_context.contextName(pkg)
      val modulename = target_context.moduleName(pkg)
      val factoryname = target_context.factoryName(pkg)
      val repositoryname = target_context.repositoryName(pkg)
      val controllername = target_context.controllerName(pkg)
      val viewname = target_context.viewName(pkg)
      val modelname = target_context.modelName(pkg)
      val errormodelname = target_context.errorModelName(pkg)
      val entityservicename = target_context.entityServiceName(pkg)
      val eventservicename = target_context.eventServiceName(pkg)
      val agentname = target_context.agentName(pkg)

      if (pkg.children.exists(_.isInstanceOf[SMEntity])) {
        val ppkg = create_Package(pkg)
        val context = create_Context()
        val repository = create_Repository()
        val controller = create_Controller()
        val view = create_View()
        val model = create_Model()
        val errormodel = create_ErrorModel()
        val entityservice = create_EntityService()
        val eventservice = create_EventService()
        val agent = create_Agent()
        if (usePackageObject) {
          build_package(ppkg, pkg)
        }
        // build_package(p, pkg, ppkg, "Package"); // XXX 
        for (c <- create_Context) { 
          build_object_for_package(c, pkg, ppkg, target_context.contextName(pkg))
        }
        for (r <- repository) {
          build_object_for_package(r, pkg, ppkg, repositoryname)
        }
        for (c <- controller) {
          if (useKindPackage) {
            c.kindName = controllerKindName
          }
          build_object_for_package(c, pkg, ppkg, controllername)
        }
        for (v <- view) {
          if (useKindPackage) {
            v.kindName = viewKindName
          }
          build_object_for_package(v, pkg, ppkg, viewname)
        }
        for (m <- model) {
          if (useKindPackage) {
            m.kindName = entityKindName
          }
          build_object_for_package(m, pkg, ppkg, modelname)
        }
        for (e <- errormodel) {
          build_object_for_package(e, pkg, ppkg, errormodelname)
        }
        for (s <- entityservice) {
          build_object_for_package(s, pkg, ppkg, entityservicename)
        }
        for (s <- eventservice) {
          build_object_for_package(s, pkg, ppkg, eventservicename)
        }
        for (a <- agent) {
          build_object_for_package(a, pkg, ppkg, agentname)
        }
        val moduleOption = for (module <- create_Module) yield {
          if (repository.isDefined) {
            module.entries += PModuleEntry(repositoryname, None, true)
          }
          if (controller.isDefined) {
            module.entries += PModuleEntry(controllername, None, true)
          }
          if (model.isDefined) {
            module.entries += PModuleEntry(modelname, None, true)
          }
          if (errormodel.isDefined) {
            module.entries += PModuleEntry(errormodelname, None, true)
          }
          if (entityservice.isDefined) {
            module.entries += PModuleEntry(entityservicename, None, true)
          }
          if (eventservice.isDefined) {
            module.entries += PModuleEntry(eventservicename, None, true)
          }
          if (agent.isDefined) {
            module.entries += PModuleEntry(agentname, None, true)
          }
          build_object_for_package(module, pkg, ppkg, modulename)
          module
        }
        val factoryOption = for (factory <- create_Factory) yield {
          if (repository.isDefined) {
            factory.entries += PModuleEntry(repositoryname, None, true)
          }
          if (controller.isDefined) {
            factory.entries += PModuleEntry(controllername, None, true)
          }
          if (model.isDefined) {
            factory.entries += PModuleEntry(modelname, None, true)
          }
          if (errormodel.isDefined) {
            factory.entries += PModuleEntry(errormodelname, None, true)
          }
          if (entityservice.isDefined) {
            factory.entries += PModuleEntry(entityservicename, None, true)
          }
          if (eventservice.isDefined) {
            factory.entries += PModuleEntry(eventservicename, None, true)
          }
          if (agent.isDefined) {
            factory.entries += PModuleEntry(agentname, None, true)
          }
          build_object_for_package(factory, pkg, ppkg, factoryname)
          factory
        }
        transform_Package_Extension(pkg, ppkg, moduleOption, factoryOption)
      }
    }

    protected def transform_Package_Extension(pkg: SMPackage, ppkg: PPackageEntity, module: Option[PModuleEntity], factory: Option[PFactoryEntity]) {
    }

    protected def create_Package(entity: SMPackage): PPackageEntity = {
      new DefaultPackageEntity(target_context)
    }

    // platform specific models
    protected def create_Agent(): Option[PAgentEntity] = {
      None
    }

    // XXX
    protected def create_Batch(): Option[PBatchEntity] = {
      None
    }

    // XXX
    protected def create_Command(): Option[PCommanderEntity] = {
      None
    }

    protected def create_Component(): Option[PComponentEntity] = {
      None
    }

    protected def create_Context(): Option[PContextEntity] = {
      None
    }

    protected def create_Controller(): Option[PControllerEntity] = {
      None
    }

    // XXX
    protected def create_Daemon(): Option[PDaemonEntity] = {
      None
    }

    protected def create_ErrorModel(): Option[PErrorModelEntity] = {
      None
    }

    // XXX
    protected def create_Facade(): Option[PFacadeEntity] = {
      None
    }

    protected def create_Factory(): Option[PFactoryEntity] = {
      None
    }

    protected def create_Model(): Option[PModelEntity] = {
      None
    }

    protected def create_Module(): Option[PModuleEntity] = {
      None
    }

    // XXX
    protected def create_Port(): Option[PPortEntity] = {
      None
    }

    protected def create_Repository(): Option[PRepositoryEntity] = {
      None
    }

    protected def create_EntityService(): Option[PServiceEntity] = {
      None
    }

    protected def create_EventService(): Option[PServiceEntity] = {
      None
    }

    // XXX
    protected def create_View(): Option[PViewEntity] = {
      None
    }

/*
    private def transform_Object(anObject: SMObject): DomainObjectTYPE = {
      val obj = create_Object(anObject)
      build_object(obj, anObject)
      obj
    }

    protected def create_Object[T <: DomainObjectTYPE](obj: SMDomainObject): T
*/
    protected def build_entity(obj: PEntityObjectEntity, anObject: SMObject) {
      build_object(obj, anObject);
      if (useKindPackage) {
        obj.kindName = entityKindName
      }
      if (useEntityDocument) {
        obj.documentName = make_entity_document_name(anObject)
        make_entity_document(obj.documentName, anObject)
      }
    }

    protected def make_entity_document(docName: String, modelObject: SMObject) = {
      for (obj <- create_Document(null)) {
        val basename = modelObject.getBaseObject.map(make_document_name)
        val qname = basename.map((_, modelObject.packageName))
        val mixins = modelObject.traits.map(x => make_trait_document(x.mixinTrait))
        build_entity_document(obj, docName, modelObject, qname, mixins)
      }
    }

    protected def make_trait_document(modelObject: SMObject): SMDomainTrait = {
//      println("SimpleModel2ProgramRealmTransformerBase#make_trait_document: " + modelObject.attributes)
      val docname = make_document_name(modelObject)
      val pkgname = make_Package_Name(modelObject.packageName)
      val mo = SMDomainTrait.createDocument(docname, pkgname, modelObject)
      record_trace("SimpleModel2ProgramRealmTransformerBase#make_trait_document: " + mo.attributes)
      findObject(docname, pkgname) match {
        case Some(s) => s match {
          case tr: PTraitEntity => ;
          case _ => record_warning("%sとしてトレイト以外のもの(%s)が存在しています。", docname, s)
        }
        case None => {
          val basename = modelObject.getBaseObject.map(make_class_name)
          val qname = basename.map((_, modelObject.packageName))
          val obj = create_Trait(mo)
          obj.isImmutable = true
          obj.isDocument = mo.isDocument
          build_object_with_name(obj, docname, mo, qname, Nil)
          record_trace("SimpleModel2ProgramRealmTransformerBase#make_trait_document: " + obj.attributes)
        }
      }
      mo
    }

    private def make_entity_service(serviceName: String, modelPkg: SMPackage) = {
      for (obj <- create_Service(null)) {
        obj.name = serviceName
        obj.asciiName = target_context.asciiName(modelPkg)
        obj.uriName = target_context.uriName(modelPkg)
        obj.classNameBase = target_context.classNameBase(modelPkg)
        obj.setKindedPackageName(make_Package_Name(modelPkg))
        obj.xmlNamespace = modelPkg.xmlNamespace
        obj.modelPackage = Some(modelPkg)
        // XXX
        store_object(obj)
      }
    }

    protected def build_object(obj: PObjectEntity, modelObject: SMObject) = {
      val basename = modelObject.getBaseObject.map(make_class_name)
      val qname = basename.map((_, make_Package_Name(modelObject.packageName)))
      val mixins = modelObject.traits.map(_.mixinTrait)
      build_object_with_name(obj, make_object_name(modelObject.name), modelObject, qname, mixins)
    }

    protected def build_object_with_name(obj: PObjectEntity, name: String, modelObject: SMObject, basename: Option[(String, String)], mixins: Seq[SMTrait]) = {
      obj.name = name
      obj.asciiName = target_context.asciiName(modelObject)
      obj.uriName = target_context.uriName(modelObject)
      obj.classNameBase = target_context.classNameBase(modelObject)
      obj.setKindedPackageName(make_Package_Name(modelObject))
      obj.xmlNamespace = modelObject.xmlNamespace
      obj.modelObject = modelObject
      for ((n, p) <- basename) {
        obj.setKindedBaseObjectType(n, p)
      }
      for (tr <- mixins) {
        obj.addKindedTraitObjectType(make_class_name(tr), make_Package_Name(tr.packageName))
      }
      build_properties(obj, modelObject)
      store_object(obj)
    }

    /**
     * Build for document drived from entity.
     */
    protected def build_entity_document(obj: PObjectEntity, name: String, modelObject: SMObject, basename: Option[(String, String)], mixins: Seq[SMTrait]) = {
      obj.name = name
      obj.asciiName = target_context.asciiName(modelObject)
      obj.uriName = target_context.uriName(modelObject)
      obj.classNameBase = target_context.classNameBase(modelObject)
      obj.setKindedPackageName(make_Package_Name(modelObject))
      obj.xmlNamespace = modelObject.xmlNamespace
      obj.modelObject = modelObject
      for ((n, p) <- basename) {
        obj.setKindedBaseObjectType(n, p)
      }
      for (tr <- mixins) {
        obj.addKindedTraitObjectType(make_class_name(tr), make_Package_Name(tr.packageName))
      }
      build_properties_entity_document(obj, modelObject)
      store_object(obj)
    }

    /**
     * Used for service and rule.
     */
    private def build_derived_object(modelObject: SMObject, source: PObjectEntity)(obj: PObjectEntity) = {
      obj.modelObject = modelObject
      obj.sourcePlatformObject = Some(source)
      obj.asciiName = target_context.asciiName(modelObject)
      obj.uriName = target_context.uriName(modelObject)
      obj.classNameBase = target_context.classNameBase(modelObject)
      obj.setKindedPackageName(make_Package_Name(modelObject))
      obj.xmlNamespace = modelObject.xmlNamespace
      obj.modelObject = modelObject
/* XXX
      modelObject.getBaseObject match {
        case Some(base) => {
          obj.setKindedBaseObjectType(make_class_name(base), base.packageName)
        }
        case None => {}
      }
*/
      build_properties(obj, modelObject)
      store_object(obj)
    }

    private def build_super(obj: PObjectEntity, anObject: SMObject) {
//      obj.setBaseClass(new PEntityType(obj.name, obj.packageName))
      obj.setBaseObjectType(obj.name, obj.packageName)
    }

    private def build_properties(obj: PObjectEntity, anObject: SMObject) {
      anObject.powertypes.foreach(build_powertype(obj, _))
      anObject.stateMachines.foreach(build_statemachine(obj, _))
      anObject.attributes.foreach(build_attribute(obj, _))
      anObject.associations.foreach(build_association(obj, _))
      anObject.operations.foreach(build_operation(obj, _))
    }

    private def build_properties_entity_document(obj: PObjectEntity, anObject: SMObject) {
      anObject.powertypes.foreach(build_powertype(obj, _))
      anObject.stateMachines.foreach(build_statemachine(obj, _))
      anObject.attributes.foreach(build_attribute(obj, _))
      anObject.associations.foreach(build_association_entity_document(obj, _))
      anObject.operations.foreach(build_operation(obj, _))
    }

    private def build_attribute(aObj: PObjectEntity, anAttr: SMAttribute) {
      val attr = make_attribute(anAttr.name, object_type(anAttr))
      build_attribute(attr, anAttr)
      aObj.addAttribute(attr)
      attr.multiplicity = get_multiplicity(anAttr.multiplicity)
      attr.isId = anAttr.isId
      attr.modelAttribute = anAttr
    }

    private def build_association(aObj: PObjectEntity, anAssoc: SMAssociation) {
      val attr = make_attribute(anAssoc.name, object_type(anAssoc))
      aObj.addAttribute(attr)
      attr.multiplicity = get_multiplicity(anAssoc.multiplicity)
      attr.modelAssociation = anAssoc
    }

    private def build_association_entity_document(aObj: PObjectEntity, anAssoc: SMAssociation) {
      val attr = make_attribute(anAssoc.name, object_type_entity_document(anAssoc))
      aObj.addAttribute(attr)
      attr.multiplicity = get_multiplicity(anAssoc.multiplicity)
      attr.modelAssociation = anAssoc
    }

    private def build_powertype(aObj: PObjectEntity, aPowertype: SMPowertypeRelationship) {
      val attr = make_attribute(aPowertype.name, object_type(aPowertype))
      aObj.addAttribute(attr)
      attr.multiplicity = get_multiplicity(aPowertype.multiplicity)
      attr.modelPowertype = aPowertype
    }

    private def build_statemachine(aObj: PObjectEntity, aStateMachine: SMStateMachineRelationship) {
      val attr = make_attribute(aStateMachine.name, object_type(aStateMachine))
      aObj.addAttribute(attr)
      attr.modelStateMachine = aStateMachine
    }

    private def make_attribute(name: String, otype: PObjectType) = {
      new PAttribute(name, otype);
    }

    private def build_attribute(attr: PAttribute, smattr: SMAttribute) = {
    }

    private def build_operation(aObj: PObjectEntity, aOperation: SMOperation) {
      val in: Option[PDocumentType] = _document_type(aOperation.in)
      val out: Option[PDocumentType] = _document_type(aOperation.out)
      val op = _make_operation(aOperation.name, in, out)
      aObj.addOperation(op)
      op.model = aOperation
    }

    private def _document_type(in: Option[SMDocument]): Option[PDocumentType] = {
      in.map(x => {
        new PDocumentType(x.name, x.packageName)
      })
    }

    private def _make_operation(name: String, in: Option[PDocumentType], out: Option[PDocumentType]) = {
      new POperation(name, in, out)
    }

    private def object_type(anAttr: SMAttribute): PObjectType = {
      val attributeType = anAttr.attributeType
      val objectType = anAttr.attributeType.qualifiedName match {
        case "org.simplemodeling.dsl.datatype.XString" => PStringType
        case "org.simplemodeling.dsl.datatype.XToken" => PTokenType
        case "org.simplemodeling.dsl.datatype.XHexByte" => PByteStringType
        case "org.simplemodeling.dsl.datatype.XBase64Byte" => PBlobType
        case "org.simplemodeling.dsl.datatype.XBoolean" => PBooleanType
        case "org.simplemodeling.dsl.datatype.XByte" => new PByteType(attributeType)
        case "org.simplemodeling.dsl.datatype.XShort" => new PShortType(attributeType)
        case "org.simplemodeling.dsl.datatype.XInt" => new PIntType(attributeType)
        case "org.simplemodeling.dsl.datatype.XLong" => new PLongType(attributeType)
        case "org.simplemodeling.dsl.datatype.XFloat" => new PFloatType(attributeType)
        case "org.simplemodeling.dsl.datatype.XDouble" => new PDoubleType(attributeType)
        case "org.simplemodeling.dsl.datatype.XInteger" => new PIntegerType(attributeType)
        case "org.simplemodeling.dsl.datatype.XDecimal" => new PDecimalType(attributeType)
        case "org.simplemodeling.dsl.datatype.XDateTime" => PDateTimeType
        case "org.simplemodeling.dsl.datatype.XDate" => PDateType
        case "org.simplemodeling.dsl.datatype.XTime" => PTimeType
        case "org.simplemodeling.dsl.datatype.XAnyURI" => PAnyURIType
        case "org.simplemodeling.dsl.datatype.ext.XText" => PTextType
        case "org.simplemodeling.dsl.datatype.ext.XLink" => PLinkType
        case "org.simplemodeling.dsl.datatype.ext.XCategory" => PCategoryType
        case "org.simplemodeling.dsl.datatype.ext.XUser" => PUserType
        case "org.simplemodeling.dsl.datatype.ext.XEmail" => PEmailType
        case "org.simplemodeling.dsl.datatype.ext.XGeoPt" => PGeoPtType
        case "org.simplemodeling.dsl.datatype.ext.XIM" => PIMType
        case "org.simplemodeling.dsl.datatype.ext.XPhoneNumber" => PPhoneNumberType
        case "org.simplemodeling.dsl.datatype.ext.XPostalAddress" => PPostalAddressType
        case "org.simplemodeling.dsl.datatype.ext.XRating" => PRatingType
        case "org.simplemodeling.dsl.datatype.business.XMoney" => PMoneyType
        case "org.simplemodeling.dsl.datatype.business.XPercent" => PPercentType
        case "org.simplemodeling.dsl.datatype.business.XUnit" => PUnitType
        case _ => anAttr.attributeType.dslAttributeType match { 
          case v: SValue => new PValueType(v.name, v.packageName);
          case d: SDocument => new PDocumentType(d.name, d.packageName)
          case _ => {
            record_warning("「%s」は未定義のデータ型です。", anAttr.attributeType.qualifiedName)
            new PGenericType(attributeType)
          }
        }
      }
      objectType
    }

    private def object_type(anAssoc: SMAssociation): PObjectType = {
      val assocType = anAssoc.associationType
      val name = make_object_name(assocType.name)
      // PEntityType serves PEntityPartType
      new PEntityType(name, make_Package_Name(assocType.packageName))
    }

    /**
     * In old implementation, DocumentJavaClassAttributeDefinition handles
     * PEntityType specially to convert document reference instead of entity reference.
     * This method override this special handling to convert document reference
     * in platform object conversion.
     */
    private def object_type_entity_document(anAssoc: SMAssociation): PObjectType = {
      val assocType = anAssoc.associationType
      val name = make_entity_document_name(assocType.typeObject)
      new PDocumentType(name, make_Package_Name(assocType.packageName))
    }

    private def object_type(aPowertype: SMPowertypeRelationship): PObjectType = {
      val powertype = aPowertype.powertype
      record_trace("SimpleModel2ProgramRealmTransformerBase#object_type: powertype = " + powertype.name)
      val name = make_object_name(powertype.name)
      record_trace("SimpleModel2ProgramRealmTransformerBase#object_type: powertype name = " + name)
      val objectType = new PPowertypeType(name, make_Package_Name(powertype.packageName))
      objectType
    }

    private def object_type(aStateMachine: SMStateMachineRelationship): PObjectType = {
      val statemachine = aStateMachine.statemachine
      record_trace("SimpleModel2ProgramRealmTransformerBase#object_type: statemachine = " + statemachine.name)
      val name = make_object_name(statemachine.name)
      record_trace("SimpleModel2ProgramRealmTransformerBase#object_type: statemachine name = " + name)
      val objectType = new PStateMachineType(name, make_Package_Name(statemachine.packageName))
      objectType
    }

    private def get_multiplicity(aMultiplicity: SMMultiplicity): PMultiplicity = {
      aMultiplicity.kind match {
        case m: SMMultiplicityOne => POne
        case m: SMMultiplicityZeroOne => PZeroOne
        case m: SMMultiplicityOneMore => POneMore
        case m: SMMultiplicityZeroMore => PZeroMore
        case m: SMMultiplicityRange => new PRange // XXX
        case _ => sys.error("Unkown multiplicity = " + aMultiplicity.kind)
      }
    }
  }

  class TransformerPhase extends GTreeVisitor[GContent] {
    override def enter(aNode: GTreeNode[GContent]) {
      aNode.asInstanceOf[GContainerEntityNode].entity match {
        case Some(entity: PEntityPartEntity) => ;
        case Some(entity: PEntityEntity) => transform_Entity(entity)
        case Some(powertype: PPowertypeEntity) => ;
        case Some(value: PValueEntity) => ;
        case Some(doc: PDocumentEntity) => ;
//        case Some(service: PDomainServiceEntity) => resolve_service(service)
        case Some(service: PServiceEntity) => ;
//        case Some(utility: PUtilitiesEntity) => //
//        case Some(utility: PMDEntityInfoEntity) => //
//        case Some(utility: PMEEntityModelInfoEntity) => //
        case Some(pkg: PPackageEntity) => transform_Package(pkg)
        case Some(x) => ;
        case None => ;
      }
    }

    def transform_Entity(entity: PEntityEntity) {
    }

    def transform_Package(pkg: PPackageEntity) {
    }
  }  

  // XXX unify ResolveTransformerPhase methods with TransformerPhase methods
  abstract class ResolvePhase extends TransformerPhase {
    def resolve_trait(obj: PTraitEntity) {
      resolve_object(obj)
    }

    def resolve_entity(obj: PEntityEntity) {
      resolve_object(obj)
    }

    def resolve_part(obj: PEntityPartEntity) {
      resolve_object(obj)
    }

    def resolve_powertype(obj: PPowertypeEntity) {
      resolve_object(obj)
    }

    def resolve_document(doc: PDocumentEntity) {
      resolve_object(doc)
    }

    def resolve_value(value: PValueEntity) {
      resolve_object(value)
    }

    def resolve_service(service: PServiceEntity) {
      resolve_object(service)
    }

    def resolve_object(obj: PObjectEntity) {
      resolve_base(obj)
      resolve_traits(obj)
      resolve_attributes(obj)
      resolve_operations(obj)
    }      

    def resolve_base(obj: PObjectEntity) {
      obj.getBaseObjectType match {
        case Some(base) => {
//          val qname = make_Package_Name(base.qualifiedName)
          val qname = base.qualifiedName
          base.reference = getObject(qname)
          base.reference.participations += BaseParticipation(obj)
        }
        case None => {}
      }
    }

    def resolve_traits(obj: PObjectEntity) {
      for (tt <- obj.getTraitObjectTypes) {
        val qname = make_Qualified_Name(tt.qualifiedName)
        findObject(qname) match {
          case Some(s) => {
            tt.reference = s
            tt.reference.participations += TraitParticipation(obj)
          }
          // some generator does not use platform trait.
          case None => record_trace("SimpleModel2ProgramRealmTransformerBase#resolve_traits(%s): platform trait not found = %s".format(obj.name, tt.qualifiedName))
        }
      }
    }

    def resolve_attributes(obj: PObjectEntity) {
      for (attr <- obj.attributes) {
        /*
         * attributeType is setted in SimpleModelProgramRealmTransformerBase.
         */
        record_trace("SimpleModel2ProgramRealmTransformerBase#resolve_attributes(%s) = %s / %s".format(obj.name, attr.name, attr.getModelElement))
        attr.attributeType match {
          case entityType: PEntityType => {
            val qname = make_Qualified_Name(entityType.qualifiedName)
            entityType.entity = getModelEntity(qname)
            entityType.entity.participations += AttributeParticipation(obj, attr)
          }
//          case partType: PEntityPartType => {
//            partType.part = getPart(partType.qualifiedName)
//          }
          case powertypeType: PPowertypeType => {
            if (usePowertype) {
              val qname = make_Qualified_Name(powertypeType.qualifiedName)
              findPowertype(qname) match {
                case Some(s) => powertypeType.powertype = s
                // some generator does not use platform trait.
                case None => record_trace("SimpleModel2ProgramRealmTransformerBase#resolve_attributes(%s): platform powertype '%s' not found = %s".format(obj.name, attr.name, qname))
              }
            }
          }
          case smType: PStateMachineType => {
            if (usePowertype) {
              record_trace("SimpleModel2ProgramRealmTransformerBase#resolve_attributes statemachine: " + smType.qualifiedName)
              val qname = make_Qualified_Name(smType.qualifiedName)
              findStateMachine(qname) match {
                case Some(s) => smType.statemachine = s
                // some generator does not use platform trait.
                case None => record_trace("SimpleModel2ProgramRealmTransformerBase#resolve_attributes(%s): platform state machine '%s' not found = %s".format(obj.name, attr.name, qname))
              }
            }
          }
          case documentType: PDocumentType => {
            val qname = make_Qualified_Name(documentType.qualifiedName)
            documentType.document = findDocument(qname)
            if (attr.modelAssociation != null) { // entity document
              for (d <- documentType.document) {
                d.participations += AttributeParticipation(obj, attr)
              }
            }
          }
          case valueType: PValueType => {
            if (useValue) {
              valueType.value = getValue(valueType.qualifiedName)
            }
          }
          case _ => //
        }
      }
    }

    def resolve_operations(obj: PObjectEntity) {
      for (op <- obj.operations) {
        op.in match {
          case Some(docType) => {
            val qname = make_Qualified_Name(docType.qualifiedName)
            docType.document = findDocument(qname)
          }
          case None => {}
        }
        op.out match {
          case Some(docType) => {
            val qname = make_Qualified_Name(docType.qualifiedName)
            docType.document = findDocument(qname)
          }
          case None => {}
        }
      }
    }

    def resolve_package(pkg: PPackageEntity, aNode: GTreeNode[GContent]) {
//      pkg.containerNode = Some(aNode.parent)
    }

    override def enter(aNode: GTreeNode[GContent]) {
      aNode.asInstanceOf[GContainerEntityNode].entity match {
        case Some(tr: PTraitEntity) => resolve_trait(tr)
        case Some(entity: PEntityPartEntity) => resolve_part(entity)
        case Some(entity: PEntityEntity) => resolve_entity(entity)
        case Some(powertype: PPowertypeEntity) => resolve_powertype(powertype)
        case Some(value: PValueEntity) => resolve_value(value)
        case Some(doc: PDocumentEntity) => resolve_document(doc)
//        case Some(service: PDomainServiceEntity) => resolve_service(service)
        case Some(service: PServiceEntity) => resolve_service(service)
//        case Some(utility: PUtilitiesEntity) => //
//        case Some(utility: PMDEntityInfoEntity) => //
//        case Some(utility: PMEEntityModelInfoEntity) => //
        case Some(pkg: PPackageEntity) => resolve_package(pkg, aNode)
        case Some(x) => //
        case None => //
      }
    }
  }

  abstract class CrudMakePhase extends TransformerPhase {
  }

  //
  // object utilities
  //
  protected final def make_class_name(pObj: PObjectEntity): String = {
    target_context.className(pObj.modelObject)
  }

  protected final def make_class_name(anObject: SMObject): String = {
    target_context.className(anObject)
  }

  protected final def make_term(pObj: PObjectEntity): String = {
    make_term(pObj.modelObject)
  }

  protected final def make_term(anObject: SMObject): String = {
    target_context.enTerm(anObject)
  }

  protected final def make_entity_document_name(anObject: SMObject): String = {
    target_context.entityDocumentName(anObject)
  }

  protected final def make_entity_service_name(pkg: SMPackage): String = {
    target_context.entityServiceName(pkg)
  }

  protected final def make_multiplicity(aMultiplicity: SMMultiplicity): PMultiplicity = {
    aMultiplicity.kind match {
      case m: SMMultiplicityOne => POne
      case m: SMMultiplicityZeroOne => PZeroOne
      case m: SMMultiplicityOneMore => POneMore
      case m: SMMultiplicityZeroMore => PZeroMore
      case m: SMMultiplicityRange => new PRange // XXX
    }
  }

  protected final def make_object_name(aName: String): String = {
    aName.capitalize // XXX
  }

  protected final def make_document_name(anObject: SMObject): String = {
    simpleModel.entityDocumentName(anObject)
//    "DD" + UString.capitalize(make_term_name(anObject))
  }

  protected final def make_term_name(modelObject: SMObject): String = {
    simpleModel.term_en(modelObject)
//    pickup_name(modelObject.term_en, modelObject.term, modelObject.name)
  }

/*
  protected final def pickup_name(names: String*): String = {
    for (name <- names) {
      if (!(name == null || "".equals(name))) {
        return name
      }
    }
    throw new IllegalArgumentException("no name")
  }
*/

  protected final def get_kinded_qname(kind: String, qname: String): String = {
    require (kind != null && kind.nonEmpty, "get_kinded_qname: kind should not be empty.")
    require (kind != null && kind.nonEmpty, "get_kinded_qname: qname should not be empty.")
    (qname.split("[.]").toList.reverse match {
      case (x :: xs) => x :: kind :: xs
    }).reverse.mkString(".")
  }

  private def _remove_unused_entities {
    _remove_unused_entities(target_realm.root)
  }

  private def _remove_unused_entities(node: GTreeContainerEntityNode) {
    val cs = node.children.toList
    for (c <- cs) {
      if (c.content == null) _remove_unused_entities(c)
      else if (c.content.isCommitable) _remove_unused_entities(c)
      else node.removeChild(c)
    }
  }
}
