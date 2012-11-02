package org.simplemodeling.SimpleModeler.entities

import scala.util.control.Exception._
import org.simplemodeling.SimpleModeler.entity._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.entity.content._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}
import org.simplemodeling.SimpleModeler.entities.sql._

/*
 * derived from GaejEntityContext since Apr. 11, 2009
 * 
 * @since   Nov.  2, 2012
 * @version Nov.  2, 2012
 * @author  ASAMI, Tomoharu
 */
/*
 * App Engine service implementation
 */
trait PEntityContextAppEngineService {
  self: PEntityContext =>

  var serviceConfigs = Map("entity" -> new EntityRepositoryServiceConfiguration(this),
                           "event" -> new EventManagementServiceConfiguration(this),
                           "plain" -> new PlainServiceConfiguration(this))

  def entityRepositoryServiceConfig = serviceConfigs("entity")
  def eventManagementServiceConfig = serviceConfigs("event")
  def plainServiceConfig = serviceConfigs("plain")

  final def packagePathname0(aPackageName: String): String = {
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
