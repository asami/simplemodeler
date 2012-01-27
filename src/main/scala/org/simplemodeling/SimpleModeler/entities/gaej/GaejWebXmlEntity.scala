package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.XML
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Apr. 18, 2009
 * @version Sep. 28, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejWebXmlEntity(val services: Seq[GaejServiceEntity], val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  val entityConfig = gaejContext.entityRepositoryServiceConfig
  val eventConfig = gaejContext.eventManagementServiceConfig
  val plainConfig = gaejContext.plainServiceConfig
  def isGwt = gaejContext.isGwt
  def isAtom = gaejContext.isAtom
  def isRest = gaejContext.isRest
  var service_list = services.toList

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val web =
<web-app>
  <welcome-file-list>
    <welcome-file>{welcome_file}</welcome-file>
  </welcome-file-list>
  { setup_servlets }
  { setup_servlet_mappings }
</web-app>

  override protected def write_Content(out: BufferedWriter) {
    XML.write(out, web, "UTF-8", true, null)
    out.flush()
  }

  private def welcome_file = {
    "index.html" // XXX
  }

  private def setup_servlets = {
    def controller_servlets = {
      for (service <- service_list) yield service match {
        case domainService: GaejDomainServiceEntity => {
          for(entity <- domainService.entities.toList) yield {
            servlet(entityConfig.controllerServletName(entity),
                    entityConfig.controllerServletClassName(entity))
          }
        }
        case _ => //
      }
    }

    def gwtrpc_servlets = {
      if (!isGwt) Nil
      else {
        for (service <- service_list) yield service match {
          case domainService: GaejDomainServiceEntity => {
            servlet(entityConfig.gwtServiceServletName(domainService),
                    entityConfig.gwtServiceServletQualifiedClassName(domainService))
          }
          case _ => {
            servlet(plainConfig.gwtServiceServletName(service),
                    plainConfig.gwtServiceServletQualifiedClassName(service))
          }
        }
      }
    }

    def atom_servlets = {
      if (!isAtom) Nil
      else {
        for (service <- service_list) yield service match {
          case domainService: GaejDomainServiceEntity => {
            servlet(entityConfig.atomServiceServletName(domainService),
                    entityConfig.atomServiceServletQualifiedClassName(domainService))
          }
          case _ => {
//            servlet(plainConfig.atomServiceServletName(service),
//                    plainConfig.atomServiceServletQualifiedClassName(service))
          }
        }
      }
    }

    def rest_servlets = {
      if (!isRest) Nil
      else {
        for (service <- service_list) yield service match {
          case domainService: GaejDomainServiceEntity => {
            servlet(entityConfig.restServiceServletName(domainService),
                    entityConfig.restServiceServletQualifiedClassName(domainService))
          }
          case _ => {
            servlet(plainConfig.restServiceServletName(service),
                    plainConfig.restServiceServletQualifiedClassName(service))
          }
        }
      }
    }

    def servlet(name: String, className: String) = 
<servlet>
  <servlet-name>{name}</servlet-name>
  <servlet-class>{className}</servlet-class>
</servlet>

    controller_servlets ::: gwtrpc_servlets ::: atom_servlets ::: rest_servlets
  }

  private def setup_servlet_mappings = {
    def controller_mappings = {
      for (service <- service_list) yield service match {
        case domainService: GaejDomainServiceEntity => {
          for (entity <- domainService.entities.toList) yield {
            servlet_mappings(entityConfig.controllerServletName(entity),
                             entityConfig.controllerServletUrlPatterns(entity))
          }
        }
        case _ => //
      }
    }

    def gwtrpc_mappings = {
      if (!isGwt) Nil
      else {
        for (service <- service_list) yield service match {
          case domainService: GaejDomainServiceEntity => {
            servlet_mappings(entityConfig.gwtServiceServletName(domainService),
                             entityConfig.gwtServiceServletUrlPatterns(domainService))
          }
          case _ => {
            servlet_mappings(plainConfig.gwtServiceServletName(service),
                             plainConfig.gwtServiceServletUrlPatterns(service))
          }
        }
      }
    }

    def atom_mappings = {
      if (!isAtom) Nil
      else {
        for (service <- service_list) yield service match {
          case domainService: GaejDomainServiceEntity => {
            servlet_mappings(entityConfig.atomServiceServletName(domainService),
                             entityConfig.atomServiceServletUrlPatterns(domainService))
          }
          case _ => {
//            servlet_mappings(plainConfig.atomServiceServletName(service),
//                             plainConfig.atomServiceServletUrlPatterns(service))
          }
        }
      }
    }

    def rest_mappings = {
      if (!isRest) Nil
      else {
        for (service <- service_list) yield service match {
          case domainService: GaejDomainServiceEntity => {
            servlet_mappings(entityConfig.restServiceServletName(domainService),
                             entityConfig.restServiceServletUrlPatterns(domainService))
          }
          case _ => {
            servlet_mappings(plainConfig.restServiceServletName(service),
                             plainConfig.restServiceServletUrlPatterns(service))
          }
        }
      }
    }

    def servlet_mappings(name: String, urlPatterns: Seq[String]) =
      for (pattern <- urlPatterns.toList) yield
        servlet_mapping(name, pattern)

    def servlet_mapping(name: String, urlPattern: String) = 
<servlet-mapping>
  <servlet-name>{name}</servlet-name>
  <url-pattern>{urlPattern}</url-pattern>
</servlet-mapping>

    controller_mappings ::: gwtrpc_mappings ::: atom_mappings ::: rest_mappings
  }
}
