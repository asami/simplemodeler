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
 * @version Jun. 10, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsEntityContext(ectx: GEntityContext, sctx: GServiceContext) extends JavaScriptEntityContextBase(ectx, sctx) {
  def entityModelQualifiedName(obj: PObjectEntity) = {
    _entity_qname(obj)
  }

  private def _entity_qname(obj: PObjectEntity): String = {
    obj.sourcePlatformObject.map(_.qualifiedName) getOrElse obj.qualifiedName
  }

  def entityGridViewName(obj: SMDomainEntity) = {
    className(obj) + "Grid"
  }

  def entityGridViewQualifiedName(obj: PObjectEntity) = {
    (_entity_qname(obj) + "Grid").replace(".model.", ".view.") // XXX
  }

  def entityGridViewWidgetName(obj: PObjectEntity) = {
    asciiName(obj) + "grid"
  }

  def entityFormViewName(obj: SMDomainEntity) = {
    className(obj) + "ViewForm"
  }

  def entityFormViewQualifiedName(obj: PObjectEntity) = {
    (_entity_qname(obj) + "ViewForm").replace(".model.", ".view.") // XXX
  }

  def entityEditFormViewName(obj: SMDomainEntity) = {
    className(obj) + "EditForm"
  }

  def entityEditFormViewQualifiedName(obj: PObjectEntity) = {
    (_entity_qname(obj) + "EditForm").replace(".model.", ".view.") // XXX
  }

  def entityStoreName(obj: SMDomainEntity) = {
    className(obj) + "Store"
  }

  def entityStoreQualifiedName(obj: PObjectEntity) = {
    (_entity_qname(obj) + "Store").replace(".model.", ".store.") // XXX
  }

  def entityControllerName(obj: SMDomainEntity) = {
    className(obj) + "Controller"
  }
}
