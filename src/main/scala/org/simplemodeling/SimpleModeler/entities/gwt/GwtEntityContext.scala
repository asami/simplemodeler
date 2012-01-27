package org.simplemodeling.SimpleModeler.entities.gwt

import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej._
import com.asamioffice.goldenport.text.{UString, UJavaString}

/*
 * @since   Apr. 16, 2009
 * @version Nov.  6, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtEntityContext(val gaejContext: GaejEntityContext) extends GSubEntityContext(gaejContext) {

  def entityRepositoryServiceName(aPackageName: String): String = {
    gaejContext.entityRepositoryGwtServiceName(aPackageName)
  }

  def serviceName(service: GaejServiceEntity): String = {
    "Gwt" + service.name.capitalize
  }

  def documentName(anEntity: GaejObjectEntity): String = {
    "Gwt" + anEntity.name.capitalize
  }

  def keyTypeName(anEntity: GaejObjectEntity): String = {
    anEntity.idAttr.typeName
/*
    anEntity.idAttr.idPolicy match {
      case SMAutoIdPolicy => "Long"
      case SMUserIdPolicy => "String"
    }
*/
  }

  def queryName(anEntity: GaejEntityEntity): String = {
    gaejContext.queryName(anEntity)
  }

  def moduleName(aPackageName: String) = {
    gaejContext.gwtModuleName(aPackageName)
  }

  def entryPointId(anEntity: GaejEntityEntity): String = {
    anEntity.term_en + "Editor"
  }

  def editorName(anEntity: GaejEntityEntity): String = {
    "Gwt" + gaejContext.entityBaseName(anEntity) + "Editor"
  }

  def editorClassName(anEntity: GaejEntityEntity): String = {
    anEntity.packageName + ".client." + editorName(anEntity)
  }

  def viewFilePathname(anEntity: GaejEntityEntity): String = {
    gaejContext.entityRepositoryServiceConfig.viewFilePathname(anEntity)
  }

  def operatorName(aService: GaejServiceEntity): String = {
    "Gwt" + aService.term.capitalize + "Operator"
  }

  def operatorClassName(aService: GaejServiceEntity): String = {
    aService.packageName + ".client." + operatorName(aService)
  }
}
