package org.simplemodeling.SimpleModeler.entities.extjs

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}

/*
 * @since   Mar. 31, 2012
 * @version Apr. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsEntityContext(ectx: GEntityContext, sctx: GServiceContext) extends JavaScriptEntityContextBase(ectx, sctx) {
  def entityGridName(obj: SMDomainEntity) = {
    className(obj) + "Grid"
  }

  def entityViewFormName(obj: SMDomainEntity) = {
    className(obj) + "ViewForm"
  }

  def entityEditFormName(obj: SMDomainEntity) = {
    className(obj) + "EditForm"
  }

  def entityStoreName(obj: SMDomainEntity) = {
    className(obj) + "Store"
  }

  def entityControllerName(obj: SMDomainEntity) = {
    className(obj) + "Controller"
  }
}
