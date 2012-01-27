package org.simplemodeling.SimpleModeler.entities.gaej

import org.simplemodeling.SimpleModeler.entity._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}

/*
 * @since   Apr. 11, 2009
 * @version Nov.  9, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejEntityContext(aContext: GEntityContext, val serviceContext: GServiceContext) extends GSubEntityContext(aContext) {
  final def simplemodelerVersion = serviceContext.parameter[String](Application_Version)
  final def simplemodelerBuild = serviceContext.parameter[String](Application_Version_Build)

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

  final def entityBaseName(anEntity: GaejEntityEntity) = {
    objectBaseName(anEntity.modelObject)
  }

  final def serviceBaseName(aService: GaejServiceEntity) = {
    val modelObject = aService.modelObject
    if (modelObject == SMNullObject) {
      pascal_case_name(aService.name)
    } else {
      pascal_case_name(enTerm(modelObject))
    }
  }

  final def objectBaseName(anObject: SMObject) = {
    pascal_case_name(enTerm(anObject))
  }

  final def entityDocumentName(anObject: SMObject) = {
    "DD" + objectBaseName(anObject)
  }

  final def className(anObject: GaejObjectEntity): String = {
    className(anObject.modelObject)
  }

  final def className(modelObject: SMObject): String = {
    pascal_case_name(enName(modelObject))
  }

  final def termName(anObject: GaejObjectEntity): String = {
    termName(anObject.modelObject)
  }

  final def termName(modelElement: SMElement): String = {
//    underscore_name(enTerm(modelElement))
    camel_case_name(enTerm(modelElement))
  }

  final def queryName(anEntity: GaejEntityEntity): String = {
    entityBaseName(anEntity) + "Query"
  }

  final def attributeName(attr: SMAttribute): String = {
    make_attr_element_name(attr)
  }

  final def attributeName(attr: GaejAttribute): String = {
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

  final def attributeTypeName4RefId(attr: GaejAttribute): String = {
    attr.attributeType match {
      case e: GaejEntityType => {
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

  final def variableName(attr: GaejAttribute): String = {
    UString.uncapitalize(attributeName(attr))
  }

  final def attributeName4RefId(attr: GaejAttribute): String = {
    attributeName(attr) + "_id"
  }

  final def variableName4RefId(attr: GaejAttribute): String = {
    variableName(attr) + "_id"
  }

  final def documentAttributeName(attr: GaejAttribute): String = {
    attr.attributeType match {
      case e: GaejEntityType => attributeName4RefId(attr)
      case _ => attributeName(attr)
    }
  }

  final def documentVariableName(attr: GaejAttribute): String = {
    attr.attributeType match {
      case e: GaejEntityType => variableName4RefId(attr)
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

  var serviceConfigs = Map("entity" -> new EntityRepositoryServiceConfiguration(this),
                           "event" -> new EventManagementServiceConfiguration(this),
                           "plain" -> new PlainServiceConfiguration(this))

  def entityRepositoryServiceConfig = serviceConfigs("entity")
  def eventManagementServiceConfig = serviceConfigs("event")
  def plainServiceConfig = serviceConfigs("plain")

  final def packagePathname(aPackageName: String): String = {
    "/src" + UJavaString.className2pathname(aPackageName)
  }

  final def factoryName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Factory"
  }

  final def contextName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    UString.capitalize(domainName) + "Context"
  }

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
  final def entityCntrollerPath(anEntity: GaejEntityEntity) = {
    "/" + entityBaseName(anEntity)
  }

  final def serviceName(anEntity: GaejEntityEntity): String = {
    serviceName(anEntity.packageName)
  }

  final def serviceName(aPackageName: String): String = {
    val domainName = {
      if (aPackageName == "") ""
      else UJavaString.qname2simpleName(aPackageName)
    }
    "DS" + UString.capitalize(domainName) + "DomainService"
  }

  final def queryName(anEntity: GaejEntityEntity): String = {
    anEntity.term.capitalize + "Query"
  }

  final def entityControllerServletName(anEntity: GaejEntityEntity): String = {
    anEntity.term_en + "Controller"
  }

  final def entityControllerServletClassName(anEntity: GaejEntityEntity): String = {
    anEntity.packageName + ".controller." + anEntity.name + "Controller"
  }

  final def entityControllerServletUrlPatterns(anEntity: GaejEntityEntity): Seq[String] = {
    val controller = anEntity.term_en
    Array("/" + controller + "*", "/" + controller + "/ *")
  }

  final def viewPathname(anEntity: GaejEntityEntity): String = {
    "/war/view/" + anEntity.term_en
  }

  final def viewPathnameOnWar(anEntity: GaejEntityEntity): String = {
    "/view/" + anEntity.term_en
  }

  // GWT
  final def gwtEntityServiceName(aPackageName: String) = {
    "Gwt" + UString.capitalize(gwtModuleName(aPackageName)) + "DomainEntityService"
  }

  final def gwtEntityServiceServletLabel(aService: GaejDomainServiceEntity): String = {
    gwtModuleName(aService.packageName) + "DomainEntityService"
  }

  final def gwtEntityServiceServletClassName(aService: GaejDomainServiceEntity): String = {
    aService.packageName + ".server." + gwtEntityServiceName(aService.packageName) + "Impl"
  }

  final def gwtEntityServiceServletUrlPatterns(aService: GaejDomainServiceEntity): Seq[String] = {
    Array("/" + gwtModuleName(aService.packageName) + "/entity")
  }

  final def gwtServiceName(aPackageName: String) = {
    "Gwt" + UString.capitalize(gwtModuleName(aPackageName)) + "Service"
  }

  final def gwtServiceServletLabel(aService: GaejServiceEntity): String = {
    gwtModuleName(aService.packageName) + "Service"
  }

  final def gwtServiceServletClassName(aService: GaejServiceEntity): String = {
    aService.packageName + ".server." + gwtServiceName(aService.packageName) + "Impl"
  }

  final def gwtServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    Array("/" + gwtModuleName(aService.packageName) + "/service/" + aService.name)
  }
*/

abstract class GaejServiceConfiguration(val context: GaejEntityContext) {
  def serviceName(anEntity: GaejEntityEntity): String
  // mvc2
  def controllerUrlPathname(anEntity: GaejEntityEntity): String
  def controllerServletName(anEntity: GaejEntityEntity): String
  def controllerServletClassName(anEntity: GaejEntityEntity): String
  def controllerServletUrlPatterns(anEntity: GaejEntityEntity): Seq[String]
  def viewFilePathname(anEntity: GaejEntityEntity): String
  def viewUrlPathname(anEntity: GaejEntityEntity): String
//  def serviceServletName(aService: GaejServiceEntity): String
//  def serviceServletClassName(aService: GaejServiceEntity): String
//  def serviceServletUrlPatterns(aService: GaejServiceEntity): Seq[String]
  // gwt
  def gwtServiceName(aService: GaejServiceEntity): String
  def gwtServiceServletName(aService: GaejServiceEntity): String
  def gwtServiceServletSimpleClassName(aService: GaejServiceEntity): String
  def gwtServiceServletQualifiedClassName(aService: GaejServiceEntity): String
  def gwtServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String]
  // atom
  def atomServiceName(aService: GaejServiceEntity): String
  def atomServiceServletName(aService: GaejServiceEntity): String
  def atomServiceServletSimpleClassName(aService: GaejServiceEntity): String
  def atomServiceServletQualifiedClassName(aService: GaejServiceEntity): String
  def atomServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String]
  // rest
  def restServiceName(aService: GaejServiceEntity): String
  def restServiceServletName(aService: GaejServiceEntity): String
  def restServiceServletSimpleClassName(aService: GaejServiceEntity): String
  def restServiceServletQualifiedClassName(aService: GaejServiceEntity): String
  def restServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String]
}

class EntityRepositoryServiceConfiguration(aContext: GaejEntityContext) extends GaejServiceConfiguration(aContext) {
  override def serviceName(anEntity: GaejEntityEntity): String = {
    aContext.entityRepositoryServiceName(anEntity.packageName)
  }

  override def controllerUrlPathname(anEntity: GaejEntityEntity): String = {
    "/" + context.termName(anEntity)
  }

  override def controllerServletName(anEntity: GaejEntityEntity): String = {
    context.entityBaseName(anEntity) + "Controller"
  }

  override def controllerServletClassName(anEntity: GaejEntityEntity): String = {
    anEntity.packageName + ".controller." + context.className(anEntity) + "Controller"
  }

  override def controllerServletUrlPatterns(anEntity: GaejEntityEntity): Seq[String] = {
    val controller = context.termName(anEntity)
    Array("/" + controller + "*", "/" + controller + "/*")
  }

  override def viewFilePathname(anEntity: GaejEntityEntity): String = {
    "/war/view/" + context.termName(anEntity)
  }

  override def viewUrlPathname(anEntity: GaejEntityEntity): String = {
    "/view/" + context.termName(anEntity)
  }

  // gwt
  override def gwtServiceName(aService: GaejServiceEntity): String = {
    context.entityRepositoryGwtServiceName(aService.packageName)
  }

  override def gwtServiceServletName(aService: GaejServiceEntity): String = {
    gwtServiceName(aService)
  }

  override def gwtServiceServletSimpleClassName(aService: GaejServiceEntity): String = {
    gwtServiceName(aService) + "Impl"
  }

  override def gwtServiceServletQualifiedClassName(aService: GaejServiceEntity): String = {
    aService.packageName + ".server." + gwtServiceServletSimpleClassName(aService)
  }

  override def gwtServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    Array("/" + context.gwtModuleName(aService.packageName) + "/gwt/repository")
  }

  // atom
  override def atomServiceName(aService: GaejServiceEntity): String = {
    context.entityRepositoryAtomServiceName(aService.packageName)
  }

  override def atomServiceServletName(aService: GaejServiceEntity): String = {
    atomServiceName(aService)
  }

  override def atomServiceServletSimpleClassName(aService: GaejServiceEntity): String = {
    atomServiceName(aService) + "Servlet"
  }

  override def atomServiceServletQualifiedClassName(aService: GaejServiceEntity): String = {
    aService.packageName + ".atom." + atomServiceServletSimpleClassName(aService)
  }

  override def atomServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    Array("/" + context.atomModuleName(aService.packageName) + "/atom",
          "/" + context.atomModuleName(aService.packageName) + "/atom/*")
  }

  // rest
  override def restServiceName(aService: GaejServiceEntity): String = {
    "Rest" + UString.capitalize(context.restModuleName(aService.packageName)) + "EntityRepositoryService"
  }

  override def restServiceServletName(aService: GaejServiceEntity): String = {
    restServiceName(aService)
  }

  override def restServiceServletSimpleClassName(aService: GaejServiceEntity): String = {
    restServiceName(aService) + "Servlet"
  }

  override def restServiceServletQualifiedClassName(aService: GaejServiceEntity): String = {
    aService.packageName + ".service." + restServiceServletSimpleClassName(aService)
  }

  override def restServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    Array("/" + context.restModuleName(aService.packageName) + "/service/repository/*")
  }
}

class EventManagementServiceConfiguration(aContext: GaejEntityContext) extends GaejServiceConfiguration(aContext) {
  override def serviceName(anEntity: GaejEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerUrlPathname(anEntity: GaejEntityEntity): String = {
    "/" + context.termName(anEntity)
  }

  override def controllerServletName(anEntity: GaejEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerServletClassName(anEntity: GaejEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerServletUrlPatterns(anEntity: GaejEntityEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }

  override def viewFilePathname(anEntity: GaejEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def viewUrlPathname(anEntity: GaejEntityEntity): String = {
    "/view/" + context.termName(anEntity)
  }

  //
  override def gwtServiceName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def gwtServiceServletName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def gwtServiceServletSimpleClassName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def gwtServiceServletQualifiedClassName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def gwtServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }

  // atom
  override def atomServiceName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def atomServiceServletName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def atomServiceServletSimpleClassName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def atomServiceServletQualifiedClassName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def atomServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }

  // rest
  override def restServiceName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def restServiceServletName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def restServiceServletSimpleClassName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def restServiceServletQualifiedClassName(aService: GaejServiceEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def restServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }
}

class PlainServiceConfiguration(aContext: GaejEntityContext) extends GaejServiceConfiguration(aContext) {
  override def serviceName(anEntity: GaejEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerUrlPathname(anEntity: GaejEntityEntity): String = {
    "/" + context.termName(anEntity)
  }

  override def controllerServletName(anEntity: GaejEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerServletClassName(anEntity: GaejEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def controllerServletUrlPatterns(anEntity: GaejEntityEntity): Seq[String] = {
    throw new UnsupportedOperationException()
  }

  override def viewFilePathname(anEntity: GaejEntityEntity): String = {
    throw new UnsupportedOperationException()
  }

  override def viewUrlPathname(anEntity: GaejEntityEntity): String = {
    "/view/" + context.termName(anEntity)
  }

/*
  override def serviceServletName(aService: GaejServiceEntity): String = {
    aService.name + "Servlet"
  }

  override def serviceServletClassName(aService: GaejServiceEntity): String = {
    aService.packageName + ".controller." + context.className(aService) + "Controller"
  }

  override def serviceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    val controller = context.termName(aService)
    Array("/" + controller + "*", "/" + controller + "/??*")
  }
*/

  //
  override def gwtServiceName(aService: GaejServiceEntity): String = {
    "Gwt" + aService.name
  }

  override def gwtServiceServletName(aService: GaejServiceEntity): String = {
    gwtServiceName(aService)
  }

  override def gwtServiceServletSimpleClassName(aService: GaejServiceEntity): String = {
    gwtServiceName(aService) + "Impl"
  }

  override def gwtServiceServletQualifiedClassName(aService: GaejServiceEntity): String = {
    aService.packageName + ".server." + gwtServiceServletSimpleClassName(aService)
  }

  override def gwtServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    Array("/" + context.gwtModuleName(aService.packageName) + "/gwt/" + context.termName(aService))
  }

  // atom
  override def atomServiceName(aService: GaejServiceEntity): String = {
    "Atom" + aService.name
  }

  override def atomServiceServletName(aService: GaejServiceEntity): String = {
    atomServiceName(aService)
  }

  override def atomServiceServletSimpleClassName(aService: GaejServiceEntity): String = {
    atomServiceName(aService) + "Servlet"
  }

  override def atomServiceServletQualifiedClassName(aService: GaejServiceEntity): String = {
    aService.packageName + ".server." + atomServiceServletSimpleClassName(aService)
  }

  override def atomServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    Array("/" + context.atomModuleName(aService.packageName) + "/atom/" + context.termName(aService))
  }

  // rest
  override def restServiceName(aService: GaejServiceEntity): String = {
    "Rest" + aService.name
  }

  override def restServiceServletName(aService: GaejServiceEntity): String = {
    restServiceName(aService)
  }

  override def restServiceServletSimpleClassName(aService: GaejServiceEntity): String = {
    restServiceName(aService) + "Servlet"
  }

  override def restServiceServletQualifiedClassName(aService: GaejServiceEntity): String = {
    aService.packageName + ".service." + restServiceServletSimpleClassName(aService)
  }

  override def restServiceServletUrlPatterns(aService: GaejServiceEntity): Seq[String] = {
    Array("/" + context.restModuleName(aService.packageName) + "/service/" + context.termName(aService) + "/*")
  }
}
