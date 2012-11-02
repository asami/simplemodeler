package org.simplemodeling.SimpleModeler.transformers

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.SimpleModelerConstants
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
import com.asamioffice.goldenport.text.{UJavaString, UString, UPathString}
import com.asamioffice.goldenport.io.UIO
import com.asamioffice.goldenport.util.MultiValueMap
import org.goldenport.recorder.Recordable

/**
 * @since   Nov.  2, 2012
 * @version Nov.  2, 2012
 * @author  ASAMI, Tomoharu
 */
trait SimpleModel2ProgramRealmTransformerBaseHelper {
  self: SimpleModel2ProgramRealmTransformerBase =>

  protected final def build_object_for_package(obj: PObjectEntity, modelPackage: SMPackage, ppkg: PPackageEntity, name: String = null) = {
    obj.name = if (name != null) name else make_object_name(modelPackage.name)
    obj.term = modelPackage.term
    obj.term_en = modelPackage.term_en
    obj.term_ja = modelPackage.term_ja
    obj.asciiName = target_context.asciiName(modelPackage)
    obj.uriName = target_context.uriName(modelPackage)
    obj.classNameBase = target_context.classNameBase(modelPackage)
    obj.modelPackage = Some(modelPackage)
    obj.platformPackage = Some(ppkg)
    obj.setKindedPackageName(modelPackage.qualifiedName)
    obj.xmlNamespace = modelPackage.xmlNamespace
//      obj.modelObject = modelPackage
//      build_properties(obj, modelPackage)
    val pathname = make_pathname(obj)
    target_realm.setEntity(pathname, obj)
    obj
  }

  protected final def build_object_for_package_at_pathname(obj: PObjectEntity, modelPackage: SMPackage, ppkg: PPackageEntity, pathname: String) = {
    obj.name = UPathString.getLastComponent(pathname)
    obj.term = modelPackage.term
    obj.term_en = modelPackage.term_en
    obj.term_ja = modelPackage.term_ja
    obj.asciiName = target_context.asciiName(modelPackage)
    obj.uriName = target_context.uriName(modelPackage)
    obj.classNameBase = target_context.classNameBase(modelPackage)
    obj.modelPackage = Some(modelPackage)
    obj.platformPackage = Some(ppkg)
    obj.packageName = modelPackage.qualifiedName
    obj.xmlNamespace = modelPackage.xmlNamespace
//      obj.modelObject = modelPackage
//      build_properties(obj, modelPackage)
    target_realm.setEntity(pathname, obj)
    obj
  }

  protected final def build_object_for_package_in_script(obj: PObjectEntity, modelPackage: SMPackage, ppkg: PPackageEntity, name: String = null) = {
    obj.name = if (name != null) name else make_object_name(modelPackage.name)
    obj.term = modelPackage.term
    obj.term_en = modelPackage.term_en
    obj.term_ja = modelPackage.term_ja
    obj.asciiName = target_context.asciiName(modelPackage)
    obj.uriName = target_context.uriName(modelPackage)
    obj.classNameBase = target_context.classNameBase(modelPackage)
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

  protected final def build_package(ppkg: PPackageEntity, modelPackage: SMPackage) = {
    ppkg.term = modelPackage.term
    ppkg.term_en = modelPackage.term_en
    ppkg.term_ja = modelPackage.term_ja
    ppkg.asciiName = target_context.asciiName(modelPackage)
    ppkg.uriName = target_context.uriName(modelPackage)
    ppkg.classNameBase = target_context.classNameBase(modelPackage)
//      ppkg.modelPackage = Some(modelPackage)
//      ppkg.platformPackage = Some(ppkg)
    ppkg.packageName = modelPackage.qualifiedName
    ppkg.xmlNamespace = modelPackage.xmlNamespace
//      ppkg.modelObject = modelPackage
//      build_properties(ppkg, modelPackage)
    val pathname = make_pathname(modelPackage.qualifiedName)
    val node = target_realm.setEntity(pathname, ppkg)
//      ppkg.containerNode = Some(node)
    ppkg      
  }

  /*
   * try out for android implementation
   * TODO: refactor for generic feature
   */
  protected final def build_entity_android(obj: PObjectEntity, entity: PEntityEntity, name: String = null) {
    obj.name = if (name != null) name else make_object_name(entity.name)
    obj.term = entity.term
    obj.term_en = entity.term_en
    obj.term_ja = entity.term_ja
    obj.asciiName = entity.asciiName
    obj.uriName = entity.uriName
    obj.classNameBase = entity.classNameBase
    obj.packageName = entity.packageName
    obj.xmlNamespace = entity.xmlNamespace
//      obj.modelObject = modelPackage
//      build_properties(obj, modelPackage)
    val pathname = srcMainDir + UJavaString.packageName2pathname(obj.packageName) + "/" + obj.name + "." + obj.fileSuffix
    target_realm.setEntity(pathname, obj)
    obj
  }

  protected final def build_package_android(obj: PObjectEntity, pkg: PPackageEntity, name: String = null) {
    val modelPackage = pkg.modelPackage.get
    obj.name = if (name != null) name else make_object_name(modelPackage.name)
    obj.term = modelPackage.term
    obj.term_en = modelPackage.term_en
    obj.term_ja = modelPackage.term_ja
    obj.asciiName = target_context.asciiName(modelPackage)
    obj.uriName = target_context.uriName(modelPackage)
    obj.classNameBase = target_context.classNameBase(modelPackage)
    obj.modelPackage = Some(modelPackage)
    obj.platformPackage = Some(pkg)
    obj.packageName = modelPackage.qualifiedName
    obj.xmlNamespace = modelPackage.xmlNamespace
//      obj.modelObject = modelPackage
//      build_properties(obj, modelPackage)
    val pathname = srcMainDir + UJavaString.packageName2pathname(obj.packageName) + "/" + obj.name + "." + obj.fileSuffix
    target_realm.setEntity(pathname, obj)
    obj
  }
}
