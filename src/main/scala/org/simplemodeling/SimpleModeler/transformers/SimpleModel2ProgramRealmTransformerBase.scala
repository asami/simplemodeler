package org.simplemodeling.SimpleModeler.transformers

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
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
import com.asamioffice.goldenport.text.{UJavaString, UString}
import com.asamioffice.goldenport.io.UIO
import com.asamioffice.goldenport.util.MultiValueMap
import org.goldenport.recorder.Recordable

/**
 * Derived from SimpleModel2JavaRealmTransformerBase (Feb. 3, 2011)
 * 
 * @since   Apr.  7, 2012
 * @version Apr. 16, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SimpleModel2ProgramRealmTransformerBase(val simpleModel: SimpleModelEntity, val serviceContext: GServiceContext
    ) extends Recordable {
  type EntityContextTYPE <: PEntityContext
  type TargetRealmTYPE <: PRealmEntity

  val target_context: EntityContextTYPE
  val target_realm: TargetRealmTYPE
  val defaultFileSuffix: String

  var srcMainDir = "/src"
  var scriptSrcDir= ""
  var useEntityDocument = true
  var useValue = true
  var usePowertype = true
  var isMakeProject = true

  setup_FowardingRecorder(serviceContext)

  def transform(): TargetRealmTYPE = {
    simpleModel.open()
    target_realm.open()
    simpleModel.traverse(make_Builder)
    target_realm.print
    for (phase <- make_Phases) {
      target_realm.traverse(phase)
    }
    if (isMakeProject) {
      make_Project()
    }
    simpleModel.close()
    target_realm ensuring(_.isOpened)
  }

  protected def make_Builder(): BuilderBase

  protected def make_Phases(): List[TransformerPhase] = {
    Nil
  }

  protected def make_Project() {
  }

  protected def make_PackageName(obj: SMObject): String = {
    obj.packageName
  }

  protected def make_Pathname(obj: PObjectEntity): String = {
//    val kind = if (UString.isNull(obj.kindName)) "" else "/" + obj.kindName 
//    srcMainDir + UJavaString.packageName2pathname(obj.packageName) + kind + "/" + obj.name + "." + obj.fileSuffix
    srcMainDir + UJavaString.packageName2pathname(obj.packageName) + "/" + obj.name + "." + obj.fileSuffix
  }

  protected def make_Pathname(qname: String): String = {
    srcMainDir + UJavaString.className2pathname(qname) + "." + defaultFileSuffix
  }

  abstract class BuilderBase extends GTreeVisitor[SMElement] { 
    type DomainObjectTYPE = PObjectEntity
    type DomainActorTYPE = PEntityObjectEntity
    type DomainResourceTYPE = PEntityObjectEntity
    type DomainEventTYPE = PEntityObjectEntity
    type DomainRoleTYPE = PEntityObjectEntity
    type DomainSummaryTYPE = PEntityObjectEntity
    type DomainEntityTYPE = PEntityObjectEntity
    type DomainEntityPartTYPE = PEntityPartEntity
    type DomainValueIdTYPE = PValueEntity
    type DomainValueNameTYPE = PValueEntity
    type DomainValueTYPE = PValueEntity
    type DomainPowertypeTYPE = PPowertypeEntity
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
        case actor: SMDomainActor => transform_Actor(actor)
        case resource: SMDomainResource => transform_Resource(resource)
        case event: SMDomainEvent => transform_Event(event)
        case role: SMDomainRole => transform_Role(role)
        case summary: SMDomainSummary => transform_Summary(summary)
        case entity: SMDomainEntity => transform_Entity(entity)
        case part: SMDomainEntityPart => transform_Entity_Part(part)
        case id: SMDomainValueId => transform_Id(id)
        case name: SMDomainValueName => transform_Name(name)
        case value: SMDomainValue => transform_Value(value)
        case powertype: SMDomainPowertype => transform_Powertype(powertype)
        case document: SMDomainDocument => transform_Document(document)
        case rule: SMDomainRule => transform_Rule(rule)
        case service: SMDomainService => transform_Service(service)
//        case port: SMDomainPort => transform_Port(port)
//        case facade: SMDomainFacade => transform_Facade(facade)
        case datatype: SMDatatype => null
        case uc: SMBusinessUsecase => null
        case task: SMBusinessTask => null
        case pkg: SMPackage => transform_Package(pkg)
        case unknown => error("Unspported simple model object = " + unknown)
      }
    }

    protected def transform_Actor(actor: SMDomainActor): DomainActorTYPE = {
      val obj = create_Actor(actor)
      build_entity(obj, actor)
      make_Actors(actor).foreach(store_object)
      obj
    }

    protected def create_Actor(entity: SMDomainActor): DomainActorTYPE = {
      create_Entity(entity)
    }

    protected def make_Actors(entity: SMDomainActor): List[PObjectEntity] = {
      make_Entities(entity)
    }

    protected def transform_Resource(resource: SMDomainResource): DomainResourceTYPE = {
      val obj = create_Resource(resource)
      build_entity(obj, resource)
      make_Resources(resource).foreach(store_object)
      obj
    }

    protected def create_Resource(entity: SMDomainResource): DomainResourceTYPE = {
      create_Entity(entity)
    }

    protected def make_Resources(entity: SMDomainResource): List[PObjectEntity] = {
      make_Entities(entity)
    }

    protected def transform_Event(event: SMDomainEvent): DomainEventTYPE = {
      val obj = create_Event(event)
      build_entity(obj, event)
      make_Events(event).foreach(store_object)
      obj
    }

    protected def create_Event(entity: SMDomainEvent): DomainEventTYPE = {
      create_Entity(entity)
    }

    protected def make_Events(entity: SMDomainEvent): List[PObjectEntity] = {
      make_Entities(entity)
    }

    protected def transform_Role(role: SMDomainRole): DomainRoleTYPE = {
      val obj = create_Role(role)
      build_entity(obj, role)
      make_Roles(role).foreach(store_object)
      obj
    }

    protected def create_Role(role: SMDomainRole): DomainRoleTYPE = {
      create_Entity(role)
    }

    protected def make_Roles(entity: SMDomainRole): List[PObjectEntity] = {
      make_Entities(entity)
    }

    protected def transform_Summary(summary: SMDomainSummary): DomainSummaryTYPE = {
      val obj = create_Summary(summary)
      build_entity(obj, summary)
      make_Summarys(summary).foreach(store_object)
      obj
    }

    protected def create_Summary(entity: SMDomainSummary): DomainSummaryTYPE = {
      create_Entity(entity)
    }

    protected def make_Summarys(entity: SMDomainSummary): List[PObjectEntity] = {
      make_Entities(entity)
    }

    protected def transform_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      val obj = create_Entity(entity)
      build_entity(obj, entity)
      make_Entities(entity).foreach(store_object)
      obj
    }

    protected def create_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      throw new UnsupportedOperationException
    }

    protected def make_Entities(entity: SMDomainEntity): List[PObjectEntity] = Nil

    protected def transform_Entity_Part(part: SMDomainEntityPart): DomainEntityPartTYPE = {
      val obj = create_Entity_Part(part)
      build_entity(obj, part)
      make_Parts(part).foreach(store_object)
      obj
    }

    protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      throw new UnsupportedOperationException
    }

    protected def make_Parts(entity: SMDomainEntityPart): List[PObjectEntity] = Nil

    protected def transform_Powertype(powertype: SMDomainPowertype) {
      if (!usePowertype) return;
      val obj = create_Powertype(powertype)
      build_object(obj, powertype)
      make_Powertypes(powertype).foreach(store_object)
    }

    protected def create_Powertype(entity: SMDomainPowertype): DomainPowertypeTYPE = {
      throw new UnsupportedOperationException
    }

    protected def make_Powertypes(entity: SMDomainPowertype): List[PObjectEntity] = Nil

    protected def transform_Id(id: SMDomainValueId) {
      if (!useValue) return
      val obj = create_ValueId(id)
      build_object(obj, id)
      make_ValueIds(id).foreach(store_object)
    }

    protected def create_ValueId(id: SMDomainValueId): DomainValueTYPE = {
      create_Value(id)
    }

    protected def make_ValueIds(value: SMDomainValueId): List[PObjectEntity] = Nil

    protected def transform_Name(name: SMDomainValueName) {
      if (!useValue) return
      val obj = create_ValueName(name)
      build_object(obj, name)
      make_ValueNames(name).foreach(store_object)
    }

    protected def create_ValueName(name: SMDomainValueName): DomainValueTYPE = {
      create_Value(name)
    }

    protected def make_ValueNames(entity: SMDomainValueName): List[PObjectEntity] = Nil

    private def transform_Value(value: SMDomainValue) {
      if (!useValue) return
      val obj = create_Value(value)
      build_object(obj, value)
      make_Values(value).foreach(store_object)
    }

    protected def create_Value(entity: SMDomainValue): DomainValueTYPE = {
//      new PValueEntity(target_context)
      throw new UnsupportedOperationException
    }

    protected def make_Values(entity: SMDomainValue): List[PObjectEntity] = Nil

    private def transform_Document(document: SMDomainDocument): DomainDocumentTYPE = {
      val obj = create_Document(document)
      build_object(obj, document)
      make_Documents(document).foreach(store_object)
      obj
    }

    protected def create_Document(entity: SMDomainDocument): DomainDocumentTYPE = {
      //new PDocumentEntity(target_context)
      throw new UnsupportedOperationException
    }

    protected def make_Documents(entity: SMDomainDocument): List[PObjectEntity] = Nil

    private def transform_Rule(rule: SMDomainRule) {
      val obj = create_Rule(rule)
      build_object(obj, rule)
      make_Rules(rule).foreach(store_object)
    }

    protected def create_Rule(entity: SMDomainRule): PRuleEntity = {
//      new PRuleEntity(target_context)
      throw new UnsupportedOperationException
    }

    protected def make_Rules(entity: SMDomainRule): List[PObjectEntity] = Nil

    private def transform_Service(service: SMDomainService): DomainServiceTYPE = {
      val obj = create_Service(service)
      build_object(obj, service)
      make_Services(service).foreach(store_object)
      obj
    }

    protected def create_Service(entity: SMDomainService): DomainServiceTYPE = {
//      new PServiceEntity(target_context)
      throw new UnsupportedOperationException
    }

    protected def make_Services(entity: SMDomainService): List[PObjectEntity] = Nil

    protected def transform_Package(pkg: SMPackage) {
      val contextname = target_context.contextName(pkg)
      val modulename = target_context.moduleName(pkg)
      val factoryname = target_context.factoryName(pkg)
      val repositoryname = target_context.repositoryName(pkg)
      val controllername = target_context.controllerName(pkg)
      val viewname = target_context.viewName(pkg)
      val modelname = target_context.modelName(pkg)
      val errormodelname = target_context.errorModelName(pkg)
      val agentname = target_context.agentName(pkg)

      if (pkg.children.exists(_.isInstanceOf[SMEntity])) {
        val ppkg = create_Package(pkg)
        val context = create_Context()
        val repository = create_Repository()
        val controller = create_Controller()
        val view = create_View()
        val model = create_Model()
        val errormodel = create_ErrorModel()
        val agent = create_Agent()
        // build_package(p, pkg, ppkg, "Package"); // XXX 
        for (c <- create_Context) { 
          build_package(c, pkg, ppkg, target_context.contextName(pkg))
        }
        for (r <- repository) {
          build_package(r, pkg, ppkg, repositoryname)
        }
        for (c <- controller) {
          build_package(c, pkg, ppkg, controllername)
        }
        for (v <- view) {
          build_package(v, pkg, ppkg, viewname)
        }
        for (m <- model) {
          build_package(m, pkg, ppkg, modelname)
        }
        for (e <- errormodel) {
          build_package(e, pkg, ppkg, errormodelname)
        }
        for (a <- agent) {
          build_package(a, pkg, ppkg, agentname)
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
          if (agent.isDefined) {
            module.entries += PModuleEntry(agentname, None, true)
          }
          build_package(module, pkg, ppkg, modulename)
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
          if (agent.isDefined) {
            factory.entries += PModuleEntry(agentname, None, true)
          }
          build_package(factory, pkg, ppkg, factoryname)
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

    // XXX
    protected def create_Service(): Option[PServiceEntity] = {
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
    private def build_entity(obj: PEntityObjectEntity, anObject: SMObject) {
      build_object(obj, anObject);
      if (useEntityDocument) {
        obj.documentName = make_entity_document_name(anObject)
        make_entity_document(obj.documentName, anObject)
      }
    }

    private def make_entity_document(docName: String, modelObject: SMObject) = {
      val obj = create_Document(null)
      obj.name = docName
      obj.term = modelObject.term // XXX
      obj.term_en = modelObject.term_en // XXX
      obj.term_ja = modelObject.term_ja // XXX
      obj.termName = target_context.termName(modelObject) // XXX
      obj.termNameBase = target_context.objectNameBase(modelObject) // XXX
      obj.setKindedPackageName(make_PackageName(modelObject))
      obj.xmlNamespace = modelObject.xmlNamespace
      obj.modelObject = modelObject
      modelObject.getBaseObject match { // XXX doc name
        case Some(base) => {
          obj.setBaseObjectType(make_class_name(base), base.packageName)
        }
        case None => {}
      }
      build_properties(obj, modelObject)
      store_object(obj)
    }

    private def build_object(obj: PObjectEntity, modelObject: SMObject) = {
      obj.name = make_object_name(modelObject.name)
      obj.term = modelObject.term
      obj.term_en = modelObject.term_en
      obj.term_ja = modelObject.term_ja
      obj.termName = target_context.termName(modelObject)
      obj.termNameBase = target_context.objectNameBase(modelObject)
      obj.setKindedPackageName(make_PackageName(modelObject))
      obj.xmlNamespace = modelObject.xmlNamespace
      obj.modelObject = modelObject
      modelObject.getBaseObject match {
        case Some(base) => {
          obj.setKindedBaseObjectType(make_class_name(base), base.packageName)
        }
        case None => {}
      }
      build_properties(obj, modelObject)
      store_object(obj)
    }

    private def store_object(obj: PObjectEntity) = {
      require (obj != null, "store_object: object should be not null: " + obj)
      require (obj.name != null && obj.name.nonEmpty, "store_object: object name should not be empty:" + obj)
      val pathname = make_Pathname(obj)
      println("store_object = " + pathname)
      target_realm.setEntity(pathname, obj)
      obj
    }      

    private def build_super(obj: PObjectEntity, anObject: SMObject) {
//      obj.setBaseClass(new PEntityType(obj.name, obj.packageName))
            obj.setBaseObjectType(obj.name, obj.packageName)
    }

    private def build_properties(obj: PObjectEntity, anObject: SMObject) {
      anObject.attributes.foreach(build_attribute(obj, _))
      anObject.associations.foreach(build_association(obj, _))
      anObject.powertypes.foreach(build_powertype(obj, _))
      anObject.operations.foreach(build_operation(obj, _))
    }

    private def build_attribute(aObj: PObjectEntity, anAttr: SMAttribute) {
      val attr = make_attribute(anAttr.name, object_type(anAttr))
      build_attribute(attr, anAttr)
      aObj.attributes += attr
      attr.multiplicity = get_multiplicity(anAttr.multiplicity)
      attr.isId = anAttr.isId
      attr.modelAttribute = anAttr
    }

    private def build_association(aObj: PObjectEntity, anAssoc: SMAssociation) {
      val attr = make_attribute(anAssoc.name, object_type(anAssoc))
      aObj.attributes += attr
      attr.multiplicity = get_multiplicity(anAssoc.multiplicity)
      attr.modelAssociation = anAssoc
    }

    private def build_powertype(aObj: PObjectEntity, aPowertype: SMPowertypeRelationship) {
      val attr = make_attribute(aPowertype.name, object_type(aPowertype))
      aObj.attributes += attr
      attr.multiplicity = get_multiplicity(aPowertype.multiplicity)
      attr.modelPowertype = aPowertype
    }

    private def make_attribute(name: String, otype: PObjectType) = {
      new PAttribute(name, otype);
    }

    private def build_attribute(attr: PAttribute, smattr: SMAttribute) = {
    }

    private def build_operation(aObj: PObjectEntity, aOperation: SMOperation) {
      // XXX
    }

    private def object_type(anAttr: SMAttribute): PObjectType = {
      val attributeType = anAttr.attributeType
      val objectType = anAttr.attributeType.qualifiedName match {
        case "org.simplemodeling.dsl.datatype.XString" => PStringType
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
        case "org.simplemodeling.dsl.datatype.XDateTime" => PDateTimeType
        case "org.simplemodeling.dsl.datatype.XDate" => PDateType
        case "org.simplemodeling.dsl.datatype.XTime" => PTimeType
        case "org.simplemodeling.dsl.datatype.XAnyURI" => PLinkType
        case "org.simplemodeling.dsl.datatype.ext.XText" => PTextType
        case "org.simplemodeling.dsl.datatype.ext.XCategory" => PCategoryType
        case "org.simplemodeling.dsl.datatype.ext.XUser" => PUserType
        case "org.simplemodeling.dsl.datatype.ext.XEmail" => PEmailType
        case "org.simplemodeling.dsl.datatype.ext.XGeoPt" => PGeoPtType
        case "org.simplemodeling.dsl.datatype.ext.XIM" => PIMType
        case "org.simplemodeling.dsl.datatype.ext.XPhoneNumber" => PPhoneNumberType
        case "org.simplemodeling.dsl.datatype.ext.XPostalAddress" => PPostalAddressType
        case "org.simplemodeling.dsl.datatype.ext.Rating" => PRatingType
        case _ => anAttr.attributeType.dslAttributeType match { 
          case v: SValue => new PValueType(v.name, v.packageName);
          case d: SDocument => new PDocumentType(d.name, d.packageName)
          case _ => new PGenericType(attributeType)
        }
      }
      objectType
    }

    private def object_type(anAssoc: SMAssociation): PObjectType = {
      val assocType = anAssoc.associationType
      val name = make_object_name(assocType.name)
      val objectType = new PEntityType(name, assocType.packageName)
      objectType
    }

    private def object_type(aPowertype: SMPowertypeRelationship): PObjectType = {
      val powertype = aPowertype.powertype
      record_trace("transformer(py): powertype = " + powertype.name)
      val name = make_object_name(powertype.name)
      record_trace("transformer(py): powertype name = " + name)
      val objectType = new PPowertypeType(name, powertype.packageName)
      objectType
    }

    private def get_multiplicity(aMultiplicity: SMMultiplicity): PMultiplicity = {
      aMultiplicity.kind match {
        case m: SMMultiplicityOne => POne
        case m: SMMultiplicityZeroOne => PZeroOne
        case m: SMMultiplicityOneMore => POneMore
        case m: SMMultiplicityZeroMore => PZeroMore
        case m: SMMultiplicityRange => new PRange // XXX
        case _ => error("Unkown multiplicity = " + aMultiplicity.kind)
      }
    }

    protected final def build_package(obj: PObjectEntity, modelPackage: SMPackage, ppkg: PPackageEntity, name: String = null) {
      obj.name = if (name != null) name else make_object_name(modelPackage.name)
      obj.term = modelPackage.term
      obj.term_en = modelPackage.term_en
      obj.term_ja = modelPackage.term_ja
      obj.termName = target_context.termName(modelPackage)
      obj.termNameBase = target_context.objectNameBase(modelPackage)
      obj.modelPackage = Some(modelPackage)
      obj.platformPackage = Some(ppkg)
      obj.setKindedPackageName(modelPackage.qualifiedName)
      obj.xmlNamespace = modelPackage.xmlNamespace
//      obj.modelObject = modelPackage
//      build_properties(obj, modelPackage)
      val pathname = make_Pathname(obj)
      target_realm.setEntity(pathname, obj)
      obj
    }

    protected final def build_package_script(obj: PObjectEntity, modelPackage: SMPackage, ppkg: PPackageEntity, name: String = null) {
      obj.name = if (name != null) name else make_object_name(modelPackage.name)
      obj.term = modelPackage.term
      obj.term_en = modelPackage.term_en
      obj.term_ja = modelPackage.term_ja
      obj.termName = target_context.termName(modelPackage)
      obj.termNameBase = target_context.objectNameBase(modelPackage)
      obj.modelPackage = Some(modelPackage)
      obj.platformPackage = Some(ppkg)
      obj.packageName = modelPackage.qualifiedName
      obj.xmlNamespace = modelPackage.xmlNamespace
//      obj.modelObject = modelPackage
//      build_properties(obj, modelPackage)
      val pathname = scriptSrcDir + "/" + obj.name + "." + obj.fileSuffix
      target_realm.setEntity(pathname, obj)
      obj
    }
  }

  class TransformerPhase extends GTreeVisitor[GContent] {
    override def enter(aNode: GTreeNode[GContent]) {
      aNode.asInstanceOf[GContainerEntityNode].entity match {
        case Some(entity: PEntityEntity) => transform_Entity(entity)
        case Some(entity: PEntityPartEntity) => //
        case Some(powertype: PPowertypeEntity) => //
        case Some(value: PValueEntity) => //
        case Some(doc: PDocumentEntity) => //
//        case Some(service: PDomainServiceEntity) => resolve_service(service)
        case Some(service: PServiceEntity) => //
//        case Some(utility: PUtilitiesEntity) => //
//        case Some(utility: PMDEntityInfoEntity) => //
//        case Some(utility: PMEEntityModelInfoEntity) => //
        case Some(pkg: PPackageEntity) => transform_Package(pkg)
        case Some(x) => //
        case None => //
      }
    }

    def transform_Entity(entity: PEntityEntity) {
    }

    def transform_Package(pkg: PPackageEntity) {
    }

/*
    protected final def build_entity(obj: PObjectEntity, entity: PEntityEntity, name: String = null) {
      obj.name = if (name != null) name else make_object_name(entity.name)
      obj.term = entity.term
      obj.term_en = entity.term_en
      obj.term_ja = entity.term_ja
      obj.termName = entity.termName
      obj.termNameBase = entity.termNameBase
      obj.setKindedPackageName(entity.packageName)
      obj.xmlNamespace = entity.xmlNamespace
//      obj.modelObject = modelPackage
//      build_properties(obj, modelPackage)
      val pathname = make_Pathname(obj)
      target_realm.setEntity(pathname, obj)
      obj
    }

    protected final def build_package(obj: PObjectEntity, pkg: PPackageEntity, name: String = null) {
      val modelPackage = pkg.modelPackage.get
      obj.name = if (name != null) name else make_object_name(modelPackage.name)
      obj.term = modelPackage.term
      obj.term_en = modelPackage.term_en
      obj.term_ja = modelPackage.term_ja
      obj.termName = target_context.termName(modelPackage)
      obj.termNameBase = target_context.objectNameBase(modelPackage)
      obj.modelPackage = Some(modelPackage)
      obj.platformPackage = Some(pkg)
      obj.packageName = modelPackage.qualifiedName
      obj.xmlNamespace = modelPackage.xmlNamespace
//      obj.modelObject = modelPackage
//      build_properties(obj, modelPackage)
      val pathname = make_Pathname(obj)
      target_realm.setEntity(pathname, obj)
      obj
    }
*/
  }

  // XXX unify ResolveTransformerPhase methods with TransformerPhase methods
  abstract class ResolvePhase extends TransformerPhase {
    def findObject(aQName: String): Option[PObjectEntity] = {
      target_realm.getNode(make_Pathname(aQName)) match {
        case Some(node) => node.entity.asInstanceOf[Some[PObjectEntity]]
        case None => None
      }
    }

    def getObject(aQName: String): PObjectEntity = {
      try {
        findObject(aQName).get
      } catch {
        case _ => error("No object = " + aQName)
      }
    }

    def findEntity(aQName: String): Option[PEntityEntity] = {
      target_realm.getNode(make_Pathname(aQName)) match {
        case Some(node) => node.entity.asInstanceOf[Some[PEntityEntity]]
        case None => None
      }
    }

    def getEntity(aQName: String): PEntityEntity = {
      try {
        findEntity(aQName).get
      } catch {
        case _ => error("No entity = " + aQName)
      }
    }

    def getModelEntity(aQName: String): PEntityEntity = {
      try {
        (findEntity(aQName) orElse findEntity(get_kinded_qname("model", aQName))).get
      } catch {
        case _ => error("No entity = " + aQName)
      }
    }

    def findPart(aQName: String): Option[PEntityPartEntity] = {
      target_realm.getNode(make_Pathname(aQName)) match {
        case Some(node) => node.entity.asInstanceOf[Some[PEntityPartEntity]]
        case None => None
      }
    }

    def getPart(aQName: String): PEntityPartEntity = {
      try {
        findPart(aQName).get
      } catch {
        case _ => error("No part = " + aQName)
      }
    }

    def findPowertype(aQName: String): Option[PPowertypeEntity] = {
      target_realm.getNode(make_Pathname(aQName)) match {
        case Some(node) => node.entity.asInstanceOf[Some[PPowertypeEntity]]
        case None => None
      }
    }

    def getPowertype(aQName: String): PPowertypeEntity = {
      try {
        findPowertype(aQName).get
      } catch {
        case _ => error("No powertype = " + aQName)
      }
    }

    def findDocument(aQName: String): Option[PDocumentEntity] = {
      target_realm.getNode(make_Pathname(aQName)) match {
        case Some(node) => node.entity.asInstanceOf[Some[PDocumentEntity]]
        case None => None
      }
    }

    def getDocument(aQName: String): PDocumentEntity = {
      try {
        findDocument(aQName).get
      } catch {
        case _ => error("No document = " + aQName)
      }
    }

    def findValue(aQName: String): Option[PValueEntity] = {
      target_realm.getNode(make_Pathname(aQName)) match {
        case Some(node) => node.entity.asInstanceOf[Some[PValueEntity]]
        case None => None
      }
    }

    def getValue(aQName: String): PValueEntity = {
      try {
        findValue(aQName).get
      } catch {
        case _ => error("No value = " + aQName)
      }
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
      resolve_attributes(obj)
      resolve_operations(obj)
    }      

    def resolve_base(obj: PObjectEntity) {
      obj.getBaseObjectType match {
        case Some(base) => {
          base.reference = getObject(base.qualifiedName)
        }
        case None => {}
      }
    }

    def resolve_attributes(obj: PObjectEntity) {
      for (attr <- obj.attributes) {
        attr.attributeType match {
          case entityType: PEntityType => {
            entityType.entity = getModelEntity(entityType.qualifiedName)
          }
          case partType: PEntityPartType => {
            partType.part = getPart(partType.qualifiedName)
          }
          case powertypeType: PPowertypeType => {
            if (usePowertype) {
              powertypeType.powertype = getPowertype(powertypeType.qualifiedName)
            }
          }
          case documentType: PDocumentType => {
            documentType.document = getDocument(documentType.qualifiedName)
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
          case Some(docType) => docType.document = getDocument(docType.qualifiedName)
          case None => {}
        }
        op.out match {
          case Some(docType) => docType.document = getDocument(docType.qualifiedName)
          case None => {}
        }
      }
    }

    def resolve_package(pkg: PPackageEntity, aNode: GTreeNode[GContent]) {
      pkg.containerNode = Some(aNode.parent)
    }

    override def enter(aNode: GTreeNode[GContent]) {
      aNode.asInstanceOf[GContainerEntityNode].entity match {
        case Some(entity: PEntityEntity) => resolve_entity(entity)
        case Some(entity: PEntityPartEntity) => resolve_part(entity)
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

  @deprecated
  protected final def make_document_name(anObject: SMObject): String = {
    "DD" + UString.capitalize(make_term_name(anObject))
  }

  protected final def make_term_name(modelObject: SMObject): String = {
    pickup_name(modelObject.term_en, modelObject.term, modelObject.name)
  }

  protected final def pickup_name(names: String*): String = {
    for (name <- names) {
      if (!(name == null || "".equals(name))) {
        return name
      }
    }
    throw new IllegalArgumentException("no name")
  }

  protected final def get_kinded_qname(kind: String, qname: String): String = {
    require (kind != null && kind.nonEmpty, "get_kinded_qname: kind should not be empty.")
    require (kind != null && kind.nonEmpty, "get_kinded_qname: qname should not be empty.")
    (qname.split("[.]").toList.reverse match {
      case (x :: xs) => x :: kind :: xs
    }).reverse.mkString(".")
  }
}