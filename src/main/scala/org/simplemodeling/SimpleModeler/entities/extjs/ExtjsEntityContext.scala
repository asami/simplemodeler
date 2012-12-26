package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._, Scalaz._
import org.apache.commons.lang3.StringUtils
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}

/*
 * @since   Mar. 31, 2012
 *  version Jun. 16, 2012
 * @version Dec. 26, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsEntityContext(ectx: GEntityContext, sctx: GServiceContext) extends JavaScriptEntityContextBase(ectx, sctx) {
  var packageNickname: Option[String] = None

  def qualifiedNickname(obj: PObjectEntity) = {
    for (n <- packageNickname) yield {
      obj.getKindName match {
        case Some(s) => n + "." + s + "." + obj.name
        case None => n + "." + obj.name
      }
    }
  }

  def entityModelQualifiedName(obj: PObjectEntity) = {
    _entity_qname(obj)
  }

  private def _entity_qname(obj: PObjectEntity): String = {
    _source_object(obj).qualifiedName
  }

  def entityModelQualifiedNickname(obj: PObjectEntity) = {
    _get_model_nickname(obj) | entityModelQualifiedName(obj)
  }

  private def _get_model_nickname(obj: PObjectEntity): Option[String] = {
    for (n <- packageNickname) yield {
      val o = _source_object(obj)
      o.getKindName match {
        case Some(s) => n + "." + s + "." + o.name
        case None => n + "." + o.name
      }
    }
  }

  private def _source_object(obj: PObjectEntity): PObjectEntity = {
    obj.sourcePlatformObject getOrElse obj
  }

  def entityGridViewName(obj: SMDomainEntity) = {
    className(obj) + "Grid"
  }

  def entityGridViewQualifiedName(obj: PObjectEntity) = {
    (_entity_qname(obj) + "Grid").replace(".model.", ".view.") // XXX
  }

  def entityGridViewQualifiedNickname(obj: PObjectEntity) = {
    (entityModelQualifiedName(obj) + "Grid").replace(".model.", ".view.") // XXX
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

  def entityFormViewQualifiedNickname(obj: PObjectEntity) = {
    (entityModelQualifiedName(obj) + "ViewForm").replace(".model.", ".view.") // XXX
  }

  def entityEditFormViewName(obj: SMDomainEntity) = {
    className(obj) + "EditForm"
  }

  def entityEditFormViewQualifiedName(obj: PObjectEntity) = {
    (_entity_qname(obj) + "EditForm").replace(".model.", ".view.") // XXX
  }

  def entityEditFormViewQualifiedNickname(obj: PObjectEntity) = {
    (entityModelQualifiedName(obj) + "EditForm").replace(".model.", ".view.") // XXX
  }

  def entityStoreName(obj: SMDomainEntity) = {
    className(obj) + "Store"
  }

  def entityStoreQualifiedName(obj: PObjectEntity) = {
    (_entity_qname(obj) + "Store").replace(".model.", ".store.") // XXX
  }

  def entityStoreQualifiedNickname(obj: PObjectEntity) = {
    (entityModelQualifiedName(obj) + "Store").replace(".model.", ".store.") // XXX
  }

  def entityControllerName(obj: SMDomainEntity) = {
    className(obj) + "Controller"
  }

  def restUri(obj: ExtjsObjectEntity) = {
    "app/rest/" + obj.uriName
  }
}
