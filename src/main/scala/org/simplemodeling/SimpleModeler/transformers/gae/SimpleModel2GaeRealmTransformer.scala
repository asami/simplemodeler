package org.simplemodeling.SimpleModeler.transformers.gae

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities.gae._
import org.goldenport.value._
import org.goldenport.service.GServiceContext
import org.goldenport.entity._
import org.goldenport.entity.content.{GContent, EntityContent}
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.util.UDsl
import com.asamioffice.goldenport.text.{UJavaString, UPathString, UString}

/*
 * @since   Mar.  8, 2009
 *  version Apr. 17, 2011
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2GaeRealmTransformer(val simpleModel: SimpleModelEntity, val serviceContext: GServiceContext) {
  private val _context = new GaeEntityContext(simpleModel.entityContext)
  private val _gaeRealm = new GaeRealmEntity(_context)
  private val _models = new ArrayBuffer[GaeModelsEntity]

  def toGaeRealm: GaeRealmEntity = {
    simpleModel.open()
    _gaeRealm.open()
    simpleModel.traverse(new Transformer())
    _gaeRealm.traverse(new Dump())
    _gaeRealm.traverse(new Resolver())
    _gaeRealm.traverse(new CrudMaker())
    make_project()
    simpleModel.close()
    _gaeRealm ensuring(_.isOpened)
  }

  private def make_object_name(aName: String): String = {
    aName // XXX
  }

  private def make_document_name(anObject: SMObject): String = {
    "DD" + UString.capitalize(anObject.term)
  }

  private def make_term_name(modelObject: SMObject): String = {
    pickup_name(modelObject.term_en, modelObject.term, modelObject.name)
  }

  // TODO: unify SimpleModel2GaeJavaRealmTransformer
  private def pickup_name(names: String*): String = {
    for (name <- names) {
      if (!(name == null || "".equals(name))) {
        return name
      }
    }
    throw new IllegalArgumentException("no name")
  }

  class Transformer extends GTreeVisitor[SMElement] {
    override def enter(aNode: GTreeNode[SMElement]) {
//      record_trace("dump(orig) = " + aNode.pathname)
      val entity = aNode.content
      val obj = entity match {
        case actor: SMDomainActor => transform_actor(actor)
        case resource: SMDomainResource => transform_resource(resource)
        case event: SMDomainEvent => transform_event(event)
        case role: SMDomainRole => transform_role(role)
        case summary: SMDomainSummary => transform_summary(summary)
        case assoc: SMDomainAssociationEntity => transform_associationEntity(assoc)
        case entity: SMDomainEntity => transform_entity(entity)
        case part: SMDomainEntityPart => transform_entity_part(part)
        case id: SMDomainValueId => transform_id(id)
        case name: SMDomainValueName => transform_name(name)
        case value: SMDomainValue => transform_value(value)
        case powertype: SMDomainPowertype => transform_powertype(powertype)
        case document: SMDomainDocument => transform_document(document)
        case rule: SMDomainRule => null
        case service: SMDomainService => transform_service(service)
        case datatype: SMDatatype => null
        case uc: SMBusinessUsecase => null
        case task: SMBusinessTask => null
        case pkg: SMPackage => null
        case _ => error("Unspported simple model object = " + entity)
      }
      obj match {
        case entity: GaeEntityEntity => setup_entity(entity, aNode)
        case part: GaeEntityPartEntity => setup_part(part, aNode)
        case powertype: GaePowertypeEntity => setup_powertype(powertype, aNode)
        case document: GaeDocumentEntity => setup_document(document, aNode)
        case service: GaeServiceEntity => setup_service(service, aNode)
        case _ => //
      }
    }

    private def get_models(aNode: GTreeNode[SMElement]) = {
      val packagePathname = aNode.parent.pathname
      val packageName = UJavaString.pathname2classifierName(packagePathname)
      val modelsPathname = packagePathname + "/models.py"
      _gaeRealm.getEntity[GaeModelsEntity](modelsPathname) match {
        case Some(m) => m
        case None => {
          val m = new GaeModelsEntity(_context)
          m.packageName = packageName
          m.name = "models.py"
          _gaeRealm.setEntity(modelsPathname, m)
          //record_trace("Gae: models = " + modelsPathname) 2009-03-10
          _models += m
          m
        }
      }
    }

    private def setup_entity(anEntity: GaeEntityEntity, aNode: GTreeNode[SMElement]) {
      val packagePathname = aNode.parent.pathname
      val packageName = UJavaString.pathname2classifierName(packagePathname)
      val modelsPathname = packagePathname + "/models.py"
      val models = get_models(aNode)
      models.addEntity(anEntity)
      val servicePathname = packagePathname + "/__init__.py"
      val service = _gaeRealm.getEntity[GaeDomainServiceEntity](servicePathname) match {
        case Some(s) => s
        case None => {
          val s = new GaeDomainServiceEntity(_context)
          s.packageName = packageName
          s.name = "__init__.py"
          _gaeRealm.setEntity(servicePathname, s)
          //record_trace("Gae: models = " + modelsPathname) 2009-03-10
          s
        }
      }
      service.addEntity(anEntity)
    }

    private def setup_part(aPart: GaeEntityPartEntity, aNode: GTreeNode[SMElement]) {
      val models = get_models(aNode)
      models.addEntityPart(aPart)
    }

    private def setup_powertype(aPowertype: GaePowertypeEntity, aNode: GTreeNode[SMElement]) {
      val models = get_models(aNode)
      models.addPowertype(aPowertype)
    }

    private def setup_document(aDocument: GaeDocumentEntity, aNode: GTreeNode[SMElement]) {
      val packagePathname = aNode.parent.pathname
      val pathname = packagePathname + "/" + aDocument.name + ".py"
      _gaeRealm.setEntity(pathname, aDocument)
    }

    private def setup_service(aService: GaeServiceEntity, aNode: GTreeNode[SMElement]) {
      val packagePathname = aNode.parent.pathname
      val pathname = packagePathname + "/" + aService.name + ".py"
      _gaeRealm.setEntity(pathname, aService)
    }

    private def transform_actor(actor: SMDomainActor): GaeEntityEntity = {
      transform_entity(actor)
    }

    private def transform_resource(resource: SMDomainResource): GaeEntityEntity = {
      transform_entity(resource)
    }

    private def transform_event(event: SMDomainEvent): GaeEntityEntity = {
      transform_entity(event)
    }

    private def transform_role(role: SMDomainRole): GaeEntityEntity = {
      transform_entity(role)
    }

    private def transform_summary(summary: SMDomainSummary): GaeEntityEntity = {
      transform_entity(summary)
    }

    private def transform_associationEntity(assoc: SMDomainAssociationEntity): GaeEntityEntity = {
      transform_entity(assoc)
    }

    private def transform_entity(entity: SMDomainEntity): GaeEntityEntity = {
      val obj = new GaeEntityEntity(_context)
      build_object(obj, entity)
      obj.modelEntity = entity
      obj
    }

    private def transform_entity_part(part: SMDomainEntityPart): GaeEntityPartEntity = {
      val obj = new GaeEntityPartEntity(_context)
      build_object(obj, part)
      obj.modelEntityPart = part
      obj
    }

    private def transform_powertype(powertype: SMDomainPowertype): GaePowertypeEntity = {
      val obj = new GaePowertypeEntity(_context)
      build_object(obj, powertype)
      obj.modelPowertype = powertype
      obj
    }

    private def transform_id(id: SMDomainValueId): GaeValueEntity = {
      transform_value(id)
    }

    private def transform_name(name: SMDomainValueName): GaeValueEntity = {
      transform_value(name)
    }

    private def transform_value(value: SMDomainValue): GaeValueEntity = {
      val obj = new GaeValueEntity(_context)
      build_object(obj, value)
      obj
    }

    private def transform_document(document: SMDomainDocument): GaeDocumentEntity = {
      val obj = new GaeDocumentEntity(_context)
      build_object(obj, document)
      obj.modelDocument = document
      obj
    }

    private def transform_service(service: SMDomainService): GaeServiceEntity = {
      val obj = new GaeServiceEntity(_context)
      build_object(obj, service)
      obj
    }

    private def transform_object(anObject: SMObject): GaeObjectEntity = {
      val obj = new GaeObjectEntity(_context)
      build_object(obj, anObject)
      obj
    }

    private def build_object(obj: GaeObjectEntity, anObject: SMObject) {
      obj.name = make_object_name(anObject.name)
      obj.term = anObject.term
      obj.term_en = anObject.term_en
      obj.documentName = make_document_name(anObject)
      obj.packageName = anObject.packageName
      obj.xmlNamespace = anObject.xmlNamespace
      obj.modelObject = anObject
//      object.setDescription(new LJDescription(description_to_string(anObject)))
      if (anObject.baseObject != SMNullObject) {
        build_super(obj, anObject)
      }
      build_properties(obj, anObject)
      obj
    }

    private def build_super(obj: GaeObjectEntity, anObject: SMObject) {
      obj.setBaseClass(new GaeEntityType(obj.name, obj.packageName))
    }

    private def build_properties(obj: GaeObjectEntity, anObject: SMObject) {
      anObject.attributes.foreach(build_attribute(obj, _))
      anObject.associations.foreach(build_association(obj, _))
      anObject.powertypes.foreach(build_powertype(obj, _))
    }

    private def build_attribute(aObj: GaeObjectEntity, anAttr: SMAttribute) {
      val attr = aObj.addAttribute(anAttr.name, object_type(anAttr))
      attr.multiplicity = get_multiplicity(anAttr.multiplicity)
      attr.isId = anAttr.isId
      attr.modelAttribute = anAttr
    }

    private def build_association(aObj: GaeObjectEntity, anAssoc: SMAssociation) {
      val attr = aObj.addAttribute(anAssoc.name, object_type(anAssoc))
      attr.multiplicity = get_multiplicity(anAssoc.multiplicity)
      attr.modelAssociation = anAssoc
    }

    private def build_powertype(aObj: GaeObjectEntity, aPowertype: SMPowertypeRelationship) {
      val attr = aObj.addAttribute(aPowertype.name, object_type(aPowertype))
      attr.multiplicity = get_multiplicity(aPowertype.multiplicity)
      attr.modelPowertype = aPowertype
    }

    private def object_type(anAttr: SMAttribute): GaeObjectType = {
      val attributeType = anAttr.attributeType
      val objectType = anAttr.attributeType.qualifiedName match {
        case "org.simplemodeling.dsl.datatype.XString" => GaeStringType
        case "org.simplemodeling.dsl.datatype.XHexByte" => GaeByteStringType
        case "org.simplemodeling.dsl.datatype.XBase64Byte" => GaeBlobType
        case "org.simplemodeling.dsl.datatype.XBoolean" => GaeBooleanType
        case "org.simplemodeling.dsl.datatype.XByte" => new GaeIntegerType(attributeType)
        case "org.simplemodeling.dsl.datatype.XShort" => new GaeIntegerType(attributeType)
        case "org.simplemodeling.dsl.datatype.XInt" => GaeIntegerType
        case "org.simplemodeling.dsl.datatype.XLong" => new GaeIntegerType(attributeType)
        case "org.simplemodeling.dsl.datatype.XFloat" => GaeFloatType
        case "org.simplemodeling.dsl.datatype.XDouble" => new GaeFloatType(attributeType)
        case "org.simplemodeling.dsl.datatype.XDateTime" => GaeDateTimeType
        case "org.simplemodeling.dsl.datatype.XDate" => GaeDateType
        case "org.simplemodeling.dsl.datatype.XTime" => GaeTimeType
        case "org.simplemodeling.dsl.datatype.XAnyURI" => GaeLinkType
        case "org.simplemodeling.dsl.datatype.ext.XText" => GaeTextType
        case "org.simplemodeling.dsl.datatype.ext.XCategory" => GaeCategoryType
        case "org.simplemodeling.dsl.datatype.ext.XUser" => GaeUserType
        case "org.simplemodeling.dsl.datatype.ext.XEmail" => GaeEmailType
        case "org.simplemodeling.dsl.datatype.ext.XGeoPt" => GaeGeoPtType
        case "org.simplemodeling.dsl.datatype.ext.XIM" => GaeIMType
        case "org.simplemodeling.dsl.datatype.ext.XPhoneNumber" => GaePhoneNumberType
        case "org.simplemodeling.dsl.datatype.ext.XPostalAddress" => GaePostalAddressType
        case "org.simplemodeling.dsl.datatype.ext.Rating" => GaeRatingType
        case _ => new GaeGenericType(attributeType)
      }
      objectType
    }

    private def object_type(anAssoc: SMAssociation): GaeObjectType = {
      val assocType = anAssoc.associationType
      val name = make_object_name(assocType.name)
      val objectType = new GaeEntityType(name, assocType.packageName)
      objectType
    }

    private def object_type(aPowertype: SMPowertypeRelationship): GaeObjectType = {
      val powertype = aPowertype.powertype
//      record_trace("transformer(py): powertype = " + powertype.name)
      val name = make_object_name(powertype.name)
//      record_trace("transformer(py): powertype name = " + name)
      val objectType = new GaePowertypeType(name, powertype.packageName)
      objectType
    }

    private def get_multiplicity(aMultiplicity: SMMultiplicity): GaeMultiplicity = {
      aMultiplicity.kind match {
        case m: SMMultiplicityOne => GaeOne
        case m: SMMultiplicityZeroOne => GaeZeroOne
        case m: SMMultiplicityOneMore => GaeOneMore
        case m: SMMultiplicityZeroMore => GaeZeroMore
        case m: SMMultiplicityRange => new GaeRange // XXX
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
    def findObject(aQName: String): Option[GaeObjectEntity] = {
      val mayObject = _gaeRealm.getNode(UJavaString.className2pathname(aQName) + ".py")
      if (mayObject.isDefined) {
        return mayObject.get.entity.asInstanceOf[Some[GaeObjectEntity]]
      }
      val packageName = UJavaString.qname2packageName(aQName)
      val modelPath = UJavaString.packageName2pathname(packageName) + "/models.py"
//      record_trace("models path = " + modelPath + "," + _gaeRealm.getNode(modelPath))
      _gaeRealm.getNode(modelPath) match {
        case Some(node) => {
//          record_trace("models = " + node.entity)
          val models: GaeModelsEntity = node.entity.get.asInstanceOf[GaeModelsEntity]
          models.findObject(aQName)
        }
        case None => None
      }
    }

    def getObject(aQName: String): GaeObjectEntity = {
      try {
        findObject(aQName).get
      } catch {
        case _ => error("No object = " + aQName)
      }
    }

/*
    def object_type(anAssoc: SMAssociation): GaeObjectType = {
      val assocType = anAssoc.associationType
      val name = make_object_name(assocType.name)
      val objectType = new GaeEntityType(name, assocType.packageName)
      objectType
    }
*/

    def resolve_object(obj: GaeObjectEntity) {
      def resolve_attributes {
        for (attr <- obj.attributes) {
          attr.attributeType match {
            case powertypeType: GaePowertypeType => {
//              record_trace("gae powertype type = " + powertypeType.qualifiedName)
              powertypeType.powertype = getObject(powertypeType.qualifiedName)
            }
            case entityType: GaeEntityType => {
//              record_trace("gae entity type = " + entityType.qualifiedName)
              entityType.entity = getObject(entityType.qualifiedName)
            }
            case _ => //
          }
        }
      }

      resolve_attributes
      ensure_package(obj.packageName)
    }

    def ensure_package(aPackageName: String) {
      require (aPackageName != null)
      if (aPackageName == "") return

      def ensure_init(aPathname: String) {
	if (aPathname == "/") return
	var pathname = aPathname
	while (pathname != "") {
//	  record_trace("ensure_init = " + pathname) 2009-03-25
          val initPath = pathname + "/__init__.py"
	  _gaeRealm.getNode(initPath) match {
	    case Some(node) => // record_trace("already exists = " + initPath)//
	    case None => _gaeRealm.setString(initPath, "")
	  }
	  pathname = UPathString.getContainerPathname(pathname)
	  require (pathname != null)
	}
      }

      var pathname = UJavaString.packageName2pathname(aPackageName)
      ensure_init(pathname)
    }

    override def enter(aNode: GTreeNode[GContent]) {
      aNode.asInstanceOf[GContainerEntityNode].entity match {
        case Some(obj: GaeObjectEntity) => resolve_object(obj)
        case Some(models: GaeModelsEntity) => {
          models.entities.foreach(resolve_object)
          models.parts.foreach(resolve_object)
        }
        case Some(service: GaeDomainServiceEntity) => //
        case None => //
      }
    }
  }

  class CrudMaker extends GTreeVisitor[GContent] {
    override def enter(aNode: GTreeNode[GContent]) {
      val node = aNode.asInstanceOf[GTreeContainerEntityNode]
      node.entity match {
        case Some(models: GaeModelsEntity) => make_controllers_views(models, node)
        case Some(domain: GaeDomainServiceEntity) => //
        case Some(service: GaeServiceEntity) => //
        case Some(document: GaeDocumentEntity) => //
        case None => //
      }
    }

    private def make_controllers_views(models: GaeModelsEntity, node: GTreeContainerEntityNode) {
      val packagePathname = node.parent.pathname
      _gaeRealm.setString(packagePathname + "/controller/__init__.py", "")
      val entityRefs = for (obj <- models.entities) yield {
        (obj.qualifiedName, obj.term)
      }
      for (obj <- models.entities) {
        val controllerPathname = packagePathname + "/controller/" + obj.name + ".py"
        val controller = new GaeControllerEntity(obj, _context)
        controller.name = obj.name + ".py"
        controller.setEntityRefs(entityRefs)
        _gaeRealm.setEntity(controllerPathname, controller)
        //
	val viewPathname = packagePathname + "/view/" + obj.name
        val indexView = new GaeIndexViewEntity(obj, _context)
        indexView.name = "index.html"
        _gaeRealm.setEntity(viewPathname + "/" + indexView.name, indexView)
        val showView = new GaeShowViewEntity(obj, _context)
        showView.name = "show.html"
        _gaeRealm.setEntity(viewPathname + "/" + showView.name, showView)
        val editView = new GaeEditViewEntity(obj, _context)
        editView.name = "edit.html"
        _gaeRealm.setEntity(viewPathname + "/" + editView.name, editView)
        val newView = new GaeNewViewEntity(obj, _context)
        newView.name = "new.html"
        _gaeRealm.setEntity(viewPathname + "/" + newView.name, newView)
      }
    }
  }

  private def make_project() {
    val mayProject = serviceContext.getParameter("gae.project")
    if (mayProject.isEmpty) return
    val projectName = mayProject.get.asInstanceOf[String]
    if (projectName == "") {
      throw new IllegalArgumentException("gae.projectにプロジェクト名が設定されていません。")
    }
    val appYaml = new GaeAppYamlEntity(projectName, _context)
    val projectController = new GaeProjectControllerEntity(_models, _context)
    val indexHtml = new GaeProjectIndexEntity(projectName, _models, _context)
    _gaeRealm.setEntity("/app.yaml", appYaml)
    _gaeRealm.setEntity("/" + projectName + ".py", projectController)
    _gaeRealm.setEntity("/index.html", indexHtml)
  }
}
