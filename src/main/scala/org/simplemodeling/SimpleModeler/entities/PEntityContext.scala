package org.simplemodeling.SimpleModeler.entities

import scala.util.control.Exception._
import org.simplemodeling.SimpleModeler.entity._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.entity.content._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}
import scala.MatchError

// derived from GaejEntityContext since Apr. 11, 2009
/**
 * @since   Apr. 18, 2011
 *  version Aug. 26, 2011
 * @version May.  2, 2012
 * @author  ASAMI, Tomoharu
 */
class PEntityContext(aContext: GEntityContext, val serviceContext: GServiceContext) extends GSubEntityContext(aContext) {
  final def simplemodelerVersion = serviceContext.parameter[String](Application_Version)
  final def simplemodelerBuild = serviceContext.parameter[String](Application_Version_Build)
  // XXX abstract val
  private var _model: Option[SimpleModelEntity] = None
  private var _platform: Option[PRealmEntity] = None

  protected def dbc_invariants {
    assume (_model.isDefined, "model should be setted.")
    assume (_platform.isDefined, "platform should be setted.")
  }

  def setModel(m: SimpleModelEntity) {
    require (m != null, "model should not be null.")
    assert (_model.isEmpty, "model should not be setted.")
    _model = Some(m)
  }

  def setPlatform(p: PRealmEntity) {
    require (p != null, "model should not be null.")
    assert (_platform.isEmpty, "model should not be setted.")
    _platform = Some(p)
  }

  def model = {
    dbc_invariants
    _model.get
  }

  def platform = {
    dbc_invariants
    _platform.get
  }

  def applicationName(po: PObjectEntity): String = {
    applicationName(po.packageName)
  }

  def applicationName(pkg: SMPackage): String = {
    applicationName(pkg.name)
  }

  def applicationName(pkgname: String): String = {
    _make_object_name(pkgname)
  }

  def contextName(po: PObjectEntity): String = {
    po.modelPackage match {
      case Some(pkg) => contextName(pkg)
      case None => sys.error("no reach")
    }
  }

  def contextName(pkg: SMPackage) = {
    _make_object_name(pkg.name + "Context")
  }

  def moduleName(po: PObjectEntity): String = {
    po.modelPackage match {
      case Some(pkg) => moduleName(pkg)
      case None => sys.error("no reach")
    }
  }

  def moduleName(pkg: SMPackage) = {
    _make_object_name(pkg.name + "Module")
  }

  def factoryName(po: PObjectEntity): String = {
    po.modelPackage match {
      case Some(pkg) => factoryName(pkg)
      case None => sys.error("no reach")
    }
  }

  def factoryName(pkg: SMPackage) = {
    _make_object_name(pkg.name + "Factory")
  }

  def repositoryName(po: PObjectEntity): String = {
    po.modelPackage match {
      case Some(pkg) => repositoryName(pkg)
      case None => sys.error("no reach")
    }
  }

  def repositoryName(pkg: SMPackage) = {
    _make_object_name(pkg.name + "Repository")
  }

  def modelName(po: PObjectEntity): String = {
    po.modelPackage match {
      case Some(pkg) => modelName(pkg)
      case None => sys.error("no reach")
    }
  }

  def modelName(pkg: SMPackage) = {
    _make_object_name(pkg.name + "Model")
  }

  def errorModelName(pkg: SMPackage) = {
    _make_object_name(pkg.name + "ErrorModel")
  }

  def controllerName(po: PObjectEntity): String = {
    po.modelPackage match {
      case Some(pkg) => controllerName(pkg)
      case None => sys.error("no reach")
    }
  }

  def agentName(pkg: SMPackage) = {
    _make_object_name(pkg.name + "Agent")
  }

  def controllerName(pkg: SMPackage) = {
    _make_object_name(pkg.name + "Controller")
  }

  def viewName(pkg: SMPackage) = {
    _make_object_name(pkg.name + "View")
  }

  def restDriverInterface(pkg: SMPackage) = {
    "I" + _make_object_name(pkg.name + "RestDriver")
  }

  def restDriverDefault(pkg: SMPackage) = {
    _make_object_name(pkg.name + "G3Driver")
  }

  def className(pkg: SMPackage, suffix: String) = {
    _make_object_name(pkg.name + suffix)
  }

  def className(name: String) = {
    _make_object_name(name)
  }

  def packageClassName(pkgName: String, suffix: String) = {
    if (UString.isNull(pkgName)) {
      suffix
    } else {
      val index = pkgName.lastIndexOf(".")
      if (index == -1) {
        _make_object_name(pkgName + suffix)
      } else {
        _make_object_name(pkgName.substring(index + 1) + suffix)
      }
    }
  }

  def constantName(name: String) = {
    name.toUpperCase
  }

  private def _make_object_name(name: String): String = {
//    name.capitalize
    pascal_case_name(name)
  }

  final def sqlName(anObject: PObjectEntity): String = {
    pascal_case_name(term_en(anObject))
  }

  final def name_en(anObject: PObjectEntity): String = {
    name_en(anObject.modelObject)
  }

  final def name_en(modelElement: SMElement) = {
    pickup_name(modelElement.name_en, modelElement.term_en, modelElement.name)
  }

  final def term_en(anObject: PObjectEntity): String = {
    term_en(anObject.modelObject)
  }

  final def term_en(modelElement: SMElement) = {
    pickup_name(modelElement.term_en, modelElement.term, modelElement.name_en, modelElement.name)
  }

  // OLD features
  final def isProject = {
    serviceContext.getParameter("gaej.project").isDefined
  }

  final def applicationName = {
    serviceContext.getParameter("gaej.project").get.toString
  }

  final def enName(modelElement: SMElement) = {
    pickup_name(modelElement.name_en, modelElement.term_en, modelElement.name)
  }

  final def enTerm(modelElement: SMElement) = {
    pickup_name(modelElement.term_en, modelElement.term, modelElement.name_en, modelElement.name)
  }

  final def localeName(modelElement: SMElement) = {
    pickup_name(modelElement.name_ja, modelElement.term_ja, modelElement.name)
  }

  final def localeTerm(modelElement: SMElement) = {
    pickup_name(modelElement.term_ja, modelElement.term, modelElement.name_ja, modelElement.name)
  }

  // not base object
  final def entityNameBase(anEntity: PEntityEntity) = {
    classNameBase(anEntity.modelObject)
  }

  final def serviceNameBase(aService: PServiceEntity) = {
    val modelObject = aService.modelObject
    if (modelObject == SMNullObject) {
      pascal_case_name(aService.name)
    } else {
      pascal_case_name(enTerm(modelObject))
    }
  }

  // not base object
  final def classNameBase(anObject: SMObject) = {
    pascal_case_name(enTerm(anObject))
  }

  // not base object
  final def classNameBase(pkg: SMPackage) = {
    pascal_case_name(enTerm(pkg))
  }

  final def entityDocumentName(anObject: SMObject): String = {
    "DD" + classNameBase(anObject)
  }

  final def entityDocumentName(anObject: PObjectEntity): String = {
    entityDocumentName(anObject.modelObject)
  }

  final def className(anObject: PObjectEntity): String = {
    className(anObject.modelObject)
  }

  final def className(modelObject: SMObject): String = {
    pascal_case_name(enName(modelObject))
  }

/*
  final def termName(anObject: PObjectEntity): String = {
    termName(anObject.modelObject)
  }

  final def termName(modelElement: SMElement): String = {
//    underscore_name(enTerm(modelElement))
    camel_case_name(enTerm(modelElement))
  }
*/

  final def labelName(anObject: PObjectEntity): String = {
    labelName(anObject.modelObject)
  }

  final def labelName(attr: PAttribute): String = {
    labelName(attr.modelElement)
  }

  final def labelName(modelElement: SMElement): String = {
//    underscore_name(enTerm(modelElement))
    localeTerm(modelElement)
  }

  final def dataKey(attr: PAttribute): String = {
    asciiName(attr)
  }

  final def asciiName(anObject: PObjectEntity): String = {
    asciiName(anObject.modelObject)
  }

  final def asciiName(attr: PAttribute): String = {
    asciiName(attr.modelElement)
  }

  final def asciiName(modelElement: SMElement): String = {
//    underscore_name(enTerm(modelElement))
    camel_case_name(enTerm(modelElement))
  }

  final def queryName(anEntity: PEntityEntity): String = {
    entityNameBase(anEntity) + "Query"
  }

  final def attributeName(attr: SMAttribute): String = {
    make_attr_element_name(attr)
  }

  final def attributeName(attr: PAttribute): String = {
    if (attr.modelAttribute != null) {
      make_attr_element_name(attr.modelAttribute)
    } else if (attr.modelAssociation != null) {
      make_attr_element_name(attr.modelAssociation)
    } else if (attr.modelPowertype != null) {
      make_attr_element_name(attr.modelPowertype)
    } else {
      attr.name
    }
  }

  final def attributeTypeName4RefId(attr: PAttribute): String = {
    attr.attributeType match {
      case e: PEntityType => {
        e.entity.idAttr.typeName
/* 2009-11-06
        e.entity.idAttr.idPolicy match {
          case SMAutoIdPolicy => "Long"
          case SMUserIdPolicy => "String"
        }
*/
      }
      case _ => error("Attribute type = " + attr.attributeType)
    }
  }

  final def associationName(assoc: SMAssociation): String = {
    make_attr_element_name(assoc)
  }

  final def powertypeName(powertype: SMPowertypeRelationship): String = {
    make_attr_element_name(powertype.powertype)
  }

  final def variableName(attr: PAttribute): String = {
    UString.uncapitalize(attributeName(attr))
  }

  final def attributeName4RefId(attr: PAttribute): String = {
    attributeName(attr) + "_id"
  }

  final def variableName4RefId(attr: PAttribute): String = {
    variableName(attr) + "_id"
  }

  final def documentAttributeName(attr: PAttribute): String = {
    attr.attributeType match {
      case e: PEntityType => attributeName4RefId(attr)
      case _ => attributeName(attr)
    }
  }

  final def documentVariableName(attr: PAttribute): String = {
    attr.attributeType match {
      case e: PEntityType => variableName4RefId(attr)
      case _ => variableName(attr)
    }
  }

  private def make_attr_element_name(modelElement: SMElement): String = {
    camel_case_name(pickup_name(modelElement.name_en, modelElement.term_en, modelElement.name))
  }

  private def pickup_name(names: String*): String = {
    for (name <- names) {
      if (!(name == null || "".equals(name))) {
        return name
      }
    }
    throw new IllegalArgumentException("no name")
  }

  private def underscore_name(name: String): String = {
    val buf = new StringBuilder
    for (c <- name) {
      c match {
        case '-' => buf.append('_');
        case ':' => buf.append('_');
        case '.' => buf.append('_');
        case ' ' => buf.append('_');
        case _ => buf.append(c);
      }
    }
    buf.toString
  }

  /**
   * camel case
   */
  private def camel_case_name(name: String): String = {
    val buf = new StringBuilder
    var afterSpace = false
    for (c <- name) {
      c match {
        case '-' => buf.append('_');afterSpace = false
        case ':' => buf.append('_');afterSpace = false
        case '.' => buf.append('_');afterSpace = false
        case ' ' => afterSpace = true
        case _ if afterSpace => buf.append(c.toUpperCase);afterSpace = false
        case _ => buf.append(c);afterSpace = false
      }
    }
    buf.toString
  }

  /**
   * capitalize camel case
   */
  private def pascal_case_name(name: String): String = {
    val buf = new StringBuilder
    var firstCharacter = true
    var afterSpace = false
    for (c <- name) {
      c match {
        case '-' => buf.append('_');afterSpace = false
        case ':' => buf.append('_');afterSpace = false
        case '.' => buf.append('_');afterSpace = false
        case ' ' => afterSpace = true
        case _ if firstCharacter => buf.append(c.toUpperCase);firstCharacter = false
        case _ if afterSpace => buf.append(c.toUpperCase);afterSpace = false
        case _ => buf.append(c);afterSpace = false
      }
    }
    buf.toString
  }

  /*
   * Utility methods 
   */
  final def collectModel[T](pf: PartialFunction[SMElement, T]): Seq[T] = {
    model.collectContent(pf)
  }

  final def collectModel[T](pathname: String, pf: PartialFunction[SMElement, T]): Seq[T] = {
    model.collectContent(pathname, pf)
  }

  final def traverseModel[T](pf: PartialFunction[SMElement, T]) {
    model.traverseContent(pf)
  }

  final def traverseModel[T](pathname: String, pf: PartialFunction[SMElement, T]) {
    model.traverseContent(pathname, pf)
  }

  // XXX
  class GEntityContentPartialFunction[A <: GEntity, +B](pf: PartialFunction[A, B]) extends PartialFunction[GContent, B] {
    val pfentity = pf.asInstanceOf[PartialFunction[GEntity, B]] 
    def isDefinedAt(x: GContent): Boolean = {
      failAsValue(classOf[MatchError])(false) {
        x match {
          case c: EntityContent => pfentity.isDefinedAt(c.entity)
          case _ => false
        }
      } 
    }

    def apply(x: GContent): B = {
      x match {
        case c: EntityContent => pfentity.apply(c.entity)
        case _ => sys.error("not reached")
      }
    }
  }

  final def collectPlatform[B](pf: PartialFunction[PObjectEntity, B]): Seq[B] = {
    platform.collectContent(new GEntityContentPartialFunction(pf))
  }

  final def collectPlatform[B](pathname: String, pf: PartialFunction[PObjectEntity, B]): Seq[B] = {
    platform.collectContent(pathname, new GEntityContentPartialFunction(pf))
  }

  final def traversePlatform(pf: PartialFunction[PObjectEntity, _]) {
    platform.traverseContent(new GEntityContentPartialFunction(pf))
  }

  final def traversePlatform(pathname: String, pf: PartialFunction[PObjectEntity, _]) {
    platform.traverseContent(pathname, new GEntityContentPartialFunction(pf))
  }

  /*
   * XXX
   * App Engine service implementation
   */
  var serviceConfigs = Map("entity" -> new EntityRepositoryServiceConfiguration(this),
                           "event" -> new EventManagementServiceConfiguration(this),
                           "plain" -> new PlainServiceConfiguration(this))

  def entityRepositoryServiceConfig = serviceConfigs("entity")
  def eventManagementServiceConfig = serviceConfigs("event")
  def plainServiceConfig = serviceConfigs("plain")

  final def packagePathname(aPackageName: String): String = {
    "/src" + UJavaString.className2pathname(aPackageName)
  }

  final def contextName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Context"
  }

  final def moduleName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Module"
  }

  final def factoryName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Factory"
  }

  // added
  final def repositoryName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Repository"
  }

  final def modelName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Model"
  }

  final def errorModelName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "ErrorModel"
  }

  final def agentName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Agent"
  }

  final def controllerName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Controller"
  }

  // old
  final def eventName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Event"
  }

  final def entityRepositoryServiceName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    "DS" + UString.capitalize(domainName) + "EntityRepository"
  }

  final def entityRepositoryGwtServiceName(aPackageName: String): String = {
    val domainName = gwtModuleName(aPackageName)
    "Gwt" + UString.capitalize(domainName) + "EntityRepositoryService"
  }

  final def entityRepositoryAtomServiceName(aPackageName: String): String = {
    val domainName = atomModuleName(aPackageName)
    "Atom" + UString.capitalize(domainName) + "EntityRepositoryService"
  }

  final def isGwt() = {
    serviceContext.getParameter("gaej.gwt").isDefined
  }

  final def gwtModuleName(aPackageName: String) = {
    if (aPackageName == "") ""
    else UJavaString.qname2simpleName(aPackageName)
  }

  final def isAtom() = {
    serviceContext.getParameter("gaej.atom").isDefined
  }

  final def atomModuleName(aPackageName: String) = {
    if (aPackageName == "") ""
    else UJavaString.qname2simpleName(aPackageName)
  }

  final def isRest() = {
    serviceContext.getParameter("gaej.rest").isDefined
  }

  final def restModuleName(aPackageName: String) = {
    if (aPackageName == "") ""
    else UJavaString.qname2simpleName(aPackageName)
  }
}

/*
  final def entityCntrollerPath(anEntity: PEntityEntity) = {
    "/" + entityBaseName(anEntity)
  }

  final def serviceName(anEntity: PEntityEntity): String = {
    serviceName(anEntity.packageName)
  }

  final def serviceName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    "DS" + UString.capitalize(domainName) + "DomainService"
  }

  final def queryName(anEntity: PEntityEntity): String = {
    anEntity.term.capitalize + "Query"
  }

  final def entityControllerServletName(anEntity: PEntityEntity): String = {
    anEntity.term_en + "Controller"
  }

  final def entityControllerServletClassName(anEntity: PEntityEntity): String = {
    anEntity.packageName + ".controller." + anEntity.name + "Controller"
  }

  final def entityControllerServletUrlPatterns(anEntity: PEntityEntity): Seq[String] = {
    val controller = anEntity.term_en
    Array("/" + controller + "*", "/" + controller + "/ *")
  }

  final def viewPathname(anEntity: PEntityEntity): String = {
    "/war/view/" + anEntity.term_en
  }

  final def viewPathnameOnWar(anEntity: PEntityEntity): String = {
    "/view/" + anEntity.term_en
  }

  // GWT
  final def gwtEntityServiceName(aPackageName: String) = {
    "Gwt" + UString.capitalize(gwtModuleName(aPackageName)) + "DomainEntityService"
  }

  final def gwtEntityServiceServletLabel(aService: PDomainServiceEntity): String = {
    gwtModuleName(aService.packageName) + "DomainEntityService"
  }

  final def gwtEntityServiceServletClassName(aService: PDomainServiceEntity): String = {
    aService.packageName + ".server." + gwtEntityServiceName(aService.packageName) + "Impl"
  }

  final def gwtEntityServiceServletUrlPatterns(aService: PDomainServiceEntity): Seq[String] = {
    Array("/" + gwtModuleName(aService.packageName) + "/entity")
  }

  final def gwtServiceName(aPackageName: String) = {
    "Gwt" + UString.capitalize(gwtModuleName(aPackageName)) + "Service"
  }

  final def gwtServiceServletLabel(aService: PServiceEntity): String = {
    gwtModuleName(aService.packageName) + "Service"
  }

  final def gwtServiceServletClassName(aService: PServiceEntity): String = {
    aService.packageName + ".server." + gwtServiceName(aService.packageName) + "Impl"
  }

  final def gwtServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    Array("/" + gwtModuleName(aService.packageName) + "/service/" + aService.name)
  }
*/

abstract class PServiceConfiguration(val context: PEntityContext) {
  def serviceName(anEntity: PEntityEntity): String
  // mvc2
  def controllerUrlPathname(anEntity: PEntityEntity): String
  def controllerServletName(anEntity: PEntityEntity): String
  def controllerServletClassName(anEntity: PEntityEntity): String
  def controllerServletUrlPatterns(anEntity: PEntityEntity): Seq[String]
  def viewFilePathname(anEntity: PEntityEntity): String
  def viewUrlPathname(anEntity: PEntityEntity): String
//  def serviceServletName(aService: PServiceEntity): String
//  def serviceServletClassName(aService: PServiceEntity): String
//  def serviceServletUrlPatterns(aService: PServiceEntity): Seq[String]
  // gwt
  def gwtServiceName(aService: PServiceEntity): String
  def gwtServiceServletName(aService: PServiceEntity): String
  def gwtServiceServletSimpleClassName(aService: PServiceEntity): String
  def gwtServiceServletQualifiedClassName(aService: PServiceEntity): String
  def gwtServiceServletUrlPatterns(aService: PServiceEntity): Seq[String]
  // atom
  def atomServiceName(aService: PServiceEntity): String
  def atomServiceServletName(aService: PServiceEntity): String
  def atomServiceServletSimpleClassName(aService: PServiceEntity): String
  def atomServiceServletQualifiedClassName(aService: PServiceEntity): String
  def atomServiceServletUrlPatterns(aService: PServiceEntity): Seq[String]
  // rest
  def restServiceName(aService: PServiceEntity): String
  def restServiceServletName(aService: PServiceEntity): String
  def restServiceServletSimpleClassName(aService: PServiceEntity): String
  def restServiceServletQualifiedClassName(aService: PServiceEntity): String
  def restServiceServletUrlPatterns(aService: PServiceEntity): Seq[String]
}

class EntityRepositoryServiceConfiguration(aContext: PEntityContext) extends PServiceConfiguration(aContext) {
  override def serviceName(anEntity: PEntityEntity): String = {
    aContext.entityRepositoryServiceName(anEntity.packageName)
  }

  override def controllerUrlPathname(anEntity: PEntityEntity): String = {
    "/" + context.asciiName(anEntity)
  }

  override def controllerServletName(anEntity: PEntityEntity): String = {
    context.entityNameBase(anEntity) + "Controller"
  }

  override def controllerServletClassName(anEntity: PEntityEntity): String = {
    anEntity.packageName + ".controller." + context.className(anEntity) + "Controller"
  }

  override def controllerServletUrlPatterns(anEntity: PEntityEntity): Seq[String] = {
    val controller = context.asciiName(anEntity)
    Array("/" + controller + "*", "/" + controller + "/*")
  }

  override def viewFilePathname(anEntity: PEntityEntity): String = {
    "/war/view/" + context.asciiName(anEntity)
  }

  override def viewUrlPathname(anEntity: PEntityEntity): String = {
    "/view/" + context.asciiName(anEntity)
  }

  // gwt
  override def gwtServiceName(aService: PServiceEntity): String = {
    context.entityRepositoryGwtServiceName(aService.packageName)
  }

  override def gwtServiceServletName(aService: PServiceEntity): String = {
    gwtServiceName(aService)
  }

  override def gwtServiceServletSimpleClassName(aService: PServiceEntity): String = {
    gwtServiceName(aService) + "Impl"
  }

  override def gwtServiceServletQualifiedClassName(aService: PServiceEntity): String = {
    aService.packageName + ".server." + gwtServiceServletSimpleClassName(aService)
  }

  override def gwtServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    Array("/" + context.gwtModuleName(aService.packageName) + "/gwt/repository")
  }

  // atom
  override def atomServiceName(aService: PServiceEntity): String = {
    context.entityRepositoryAtomServiceName(aService.packageName)
  }

  override def atomServiceServletName(aService: PServiceEntity): String = {
    atomServiceName(aService)
  }

  override def atomServiceServletSimpleClassName(aService: PServiceEntity): String = {
    atomServiceName(aService) + "Servlet"
  }

  override def atomServiceServletQualifiedClassName(aService: PServiceEntity): String = {
    aService.packageName + ".atom." + atomServiceServletSimpleClassName(aService)
  }

  override def atomServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    Array("/" + context.atomModuleName(aService.packageName) + "/atom",
          "/" + context.atomModuleName(aService.packageName) + "/atom/*")
  }

  // rest
  override def restServiceName(aService: PServiceEntity): String = {
    "Rest" + UString.capitalize(context.restModuleName(aService.packageName)) + "EntityRepositoryService"
  }

  override def restServiceServletName(aService: PServiceEntity): String = {
    restServiceName(aService)
  }

  override def restServiceServletSimpleClassName(aService: PServiceEntity): String = {
    restServiceName(aService) + "Servlet"
  }

  override def restServiceServletQualifiedClassName(aService: PServiceEntity): String = {
    aService.packageName + ".service." + restServiceServletSimpleClassName(aService)
  }

  override def restServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    Array("/" + context.restModuleName(aService.packageName) + "/service/repository/*")
  }
}

class EventManagementServiceConfiguration(aContext: PEntityContext) extends PServiceConfiguration(aContext) {
  override def serviceName(anEntity: PEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerUrlPathname(anEntity: PEntityEntity): String = {
    "/" + context.asciiName(anEntity)
  }

  override def controllerServletName(anEntity: PEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerServletClassName(anEntity: PEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerServletUrlPatterns(anEntity: PEntityEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }

  override def viewFilePathname(anEntity: PEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def viewUrlPathname(anEntity: PEntityEntity): String = {
    "/view/" + context.asciiName(anEntity)
  }

  //
  override def gwtServiceName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def gwtServiceServletName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def gwtServiceServletSimpleClassName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def gwtServiceServletQualifiedClassName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def gwtServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }

  // atom
  override def atomServiceName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def atomServiceServletName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def atomServiceServletSimpleClassName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def atomServiceServletQualifiedClassName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def atomServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }

  // rest
  override def restServiceName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def restServiceServletName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def restServiceServletSimpleClassName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def restServiceServletQualifiedClassName(aService: PServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def restServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }
}

class PlainServiceConfiguration(aContext: PEntityContext) extends PServiceConfiguration(aContext) {
  override def serviceName(anEntity: PEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerUrlPathname(anEntity: PEntityEntity): String = {
    "/" + context.asciiName(anEntity)
  }

  override def controllerServletName(anEntity: PEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerServletClassName(anEntity: PEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerServletUrlPatterns(anEntity: PEntityEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }

  override def viewFilePathname(anEntity: PEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def viewUrlPathname(anEntity: PEntityEntity): String = {
    "/view/" + context.asciiName(anEntity)
  }

/*
  override def serviceServletName(aService: PServiceEntity): String = {
    aService.name + "Servlet"
  }

  override def serviceServletClassName(aService: PServiceEntity): String = {
    aService.packageName + ".controller." + context.className(aService) + "Controller"
  }

  override def serviceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    val controller = context.asciiName(aService)
    Array("/" + controller + "*", "/" + controller + "/??*")
  }
*/

  //
  override def gwtServiceName(aService: PServiceEntity): String = {
    "Gwt" + aService.name
  }

  override def gwtServiceServletName(aService: PServiceEntity): String = {
    gwtServiceName(aService)
  }

  override def gwtServiceServletSimpleClassName(aService: PServiceEntity): String = {
    gwtServiceName(aService) + "Impl"
  }

  override def gwtServiceServletQualifiedClassName(aService: PServiceEntity): String = {
    aService.packageName + ".server." + gwtServiceServletSimpleClassName(aService)
  }

  override def gwtServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    Array("/" + context.gwtModuleName(aService.packageName) + "/gwt/" + context.asciiName(aService))
  }

  // atom
  override def atomServiceName(aService: PServiceEntity): String = {
    "Atom" + aService.name
  }

  override def atomServiceServletName(aService: PServiceEntity): String = {
    atomServiceName(aService)
  }

  override def atomServiceServletSimpleClassName(aService: PServiceEntity): String = {
    atomServiceName(aService) + "Servlet"
  }

  override def atomServiceServletQualifiedClassName(aService: PServiceEntity): String = {
    aService.packageName + ".server." + atomServiceServletSimpleClassName(aService)
  }

  override def atomServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    Array("/" + context.atomModuleName(aService.packageName) + "/atom/" + context.asciiName(aService))
  }

  // rest
  override def restServiceName(aService: PServiceEntity): String = {
    "Rest" + aService.name
  }

  override def restServiceServletName(aService: PServiceEntity): String = {
    restServiceName(aService)
  }

  override def restServiceServletSimpleClassName(aService: PServiceEntity): String = {
    restServiceName(aService) + "Servlet"
  }

  override def restServiceServletQualifiedClassName(aService: PServiceEntity): String = {
    aService.packageName + ".service." + restServiceServletSimpleClassName(aService)
  }

  override def restServiceServletUrlPatterns(aService: PServiceEntity): Seq[String] = {
    Array("/" + context.restModuleName(aService.packageName) + "/service/" + context.asciiName(aService) + "/*")
  }
}
