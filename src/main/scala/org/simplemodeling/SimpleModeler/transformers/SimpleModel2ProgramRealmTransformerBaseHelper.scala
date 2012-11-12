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
 * @version Nov. 12, 2012
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

  /*
   * find objects in the realm space.
   */
  protected final def make_pathname(obj: PObjectEntity): String = {
    target_context.makePathname(obj)
  }

  protected final def make_pathname(qname: String): String = {
    target_context.makePathname(qname)
  }

  protected final def make_pathname(name: String, pkgname: String): String = {
    target_context.makePathname(name, pkgname)
  }

  protected final def find_object_by_pathname[T <: PObjectEntity](pathname: String): Option[T] = {
    target_realm.getNode(pathname) match {
      case Some(node) => node.entity.asInstanceOf[Some[T]]
      case None => None
    }
  }

  protected final def findObject(name: String, pkgname: String): Option[PObjectEntity] = {
    find_object_by_pathname(make_pathname(name, pkgname))
  }

  protected final def getObject(name: String, pkgname: String): PObjectEntity = {
    try {
      findObject(name, pkgname).get
    } catch {
      case _ => sys.error("No object = " + name + "/" + pkgname)
    }
  }

  protected final def findObject(aQName: String): Option[PObjectEntity] = {
    find_object_by_pathname(make_pathname(aQName))
  }

  protected final def getObject(aQName: String): PObjectEntity = {
    try {
      findObject(aQName).get
    } catch {
      case _ => sys.error("No object = " + aQName)
    }
  }

  protected final def findEntity(aQName: String): Option[PEntityEntity] = {
    target_realm.getNode(make_pathname(aQName)) match {
      case Some(node) => node.entity.asInstanceOf[Some[PEntityEntity]]
      case None => {
        println("SimpleModel2ProgramRealmTransformerBase#findEntity(%s, %s) = None".format(aQName, make_pathname(aQName)))
        target_realm.dump
        None
      }
    }
  }

  protected final def getEntity(aQName: String): PEntityEntity = {
    try {
      findEntity(aQName).get
    } catch _no_entry("entity", aQName)
  }

  protected final def getModelEntity(aQName: String): PEntityEntity = {
    try {
      (findEntity(aQName) orElse findEntity(get_kinded_qname("model", aQName))).get
    } catch _no_entry("entity", aQName)
  }

  private def _no_entry[T](typename: String, qname: String): PartialFunction[Throwable, T] = {
    case e => {
      record_error(e, "No " + typename + " = " + qname)
      throw e
    }
  }

  protected final def findPart(aQName: String): Option[PEntityPartEntity] = {
    target_realm.getNode(make_pathname(aQName)) match {
      case Some(node) => node.entity.asInstanceOf[Some[PEntityPartEntity]]
      case None => None
    }
  }

  protected final def getPart(aQName: String): PEntityPartEntity = {
    try {
      findPart(aQName).get
    } catch _no_entry("part", aQName)
  }

  protected final def findPowertype(aQName: String): Option[PPowertypeEntity] = {
    target_realm.getNode(make_pathname(aQName)) match {
      case Some(node) => node.entity.asInstanceOf[Some[PPowertypeEntity]]
      case None => None
    }
  }

  protected final def getPowertype(aQName: String): PPowertypeEntity = {
    try {
      findPowertype(aQName).get
    } catch _no_entry("powertype", aQName)
  }

  protected final def findDocument(aQName: String): Option[PDocumentEntity] = {
    println("SimpleModel2ProgramRealmTransformerBaseHelper#findDocument = " + aQName)
    target_realm.getNode(make_pathname(aQName)) match {
      case Some(node) => node.entity.get match {
        case d: PDocumentEntity => Some(d)
        case e: PEntityEntity => {
          val docname = make_document_name(e.modelObject) // e.documentName
          println("SimpleModel2ProgramRealmTransformerBaseHelper#findDocument = " + docname + "/" + e.documentName)
          findDocument(_make_qname(docname, e.packageName))
        }
      }
      case None => None
    }
  }

  private def _make_qname(name: String, pkg: String): String = {
    pkg + "." + name
  }

  protected final def getDocument(aQName: String): PDocumentEntity = {
    println("SimpleModel2ProgramRealmTransformerBaseHelper#getDocument = " + aQName)
    if (aQName == "app.") sys.error("????")
    try {
      findDocument(aQName).get
    } catch _no_entry("document", aQName)
  }

  protected final def findValue(aQName: String): Option[PValueEntity] = {
    target_realm.getNode(make_pathname(aQName)) match {
      case Some(node) => node.entity.asInstanceOf[Some[PValueEntity]]
      case None => None
    }
  }

  protected final def getValue(aQName: String): PValueEntity = {
    try {
      findValue(aQName).get
    } catch _no_entry("value", aQName)
  }
}
