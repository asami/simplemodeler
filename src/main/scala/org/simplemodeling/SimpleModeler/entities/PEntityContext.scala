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
 * @since   Apr. 18, 2011
 *  version Aug. 26, 2011
 *  version Jun. 16, 2012
 * @version Nov.  7, 2012
 * @author  ASAMI, Tomoharu
 */
class PEntityContext(aContext: GEntityContext, val serviceContext: GServiceContext) extends GSubEntityContext(aContext) with PEntityContextAppEngineService {
  final def simplemodelerVersion = serviceContext.parameter[String](Application_Version)
  final def simplemodelerBuild = serviceContext.parameter[String](Application_Version_Build)

  var srcMainDir = "/src"
  var defaultFileSuffix = "bak"

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
    println("PEntityContext: start")
    m.open()
    m.dump()
    println("PEntityContext: end")
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
    // XXX SQL specific
    pascal_case_name(asciiName(anObject))
  }

  final def sqlName(attr: PAttribute): String = {
    // XXX SQL specific
    pascal_case_name(dataKey(attr))
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

  @deprecated("candidate old feature", "before 0.3.3")
  final def enName(modelElement: SMElement) = {
    pickup_name(modelElement.name_en, modelElement.term_en, modelElement.name)
  }

  @deprecated("candidate old feature", "before 0.3.3")
  final def enTerm(modelElement: SMElement) = {
    pickup_name(modelElement.term_en, modelElement.term, modelElement.name_en, modelElement.name)
  }

  @deprecated("candidate old feature", "before 0.3.3")
  final def localeName(modelElement: SMElement) = {
    pickup_name(modelElement.name_ja, modelElement.term_ja, modelElement.name)
  }

  @deprecated("candidate old feature", "before 0.3.3")
  final def localeTerm(modelElement: SMElement) = {
    pickup_name(modelElement.term_ja, modelElement.term, modelElement.name_ja, modelElement.name)
  }

  @deprecated("candidate old feature", "before 0.3.3")
  // not base object
  final def entityNameBase(anEntity: PEntityEntity) = {
    classNameBase(anEntity.modelObject)
  }

  @deprecated("candidate old feature", "before 0.3.3")
  final def serviceNameBase(aService: PServiceEntity) = {
    val modelObject = aService.modelObject
    if (modelObject == SMNullObject) {
      pascal_case_name(aService.name)
    } else {
      pascal_case_name(enTerm(modelObject))
    }
  }

  @deprecated("candidate old feature", "before 0.3.3")
  // not base object
  final def classNameBase(anObject: SMObject) = {
    pascal_case_name(enTerm(anObject))
  }

  @deprecated("candidate old feature", "before 0.3.3")
  // not base object
  final def classNameBase(pkg: SMPackage) = {
    pascal_case_name(enTerm(pkg))
  }

  /**
   * SimpleModel2ProgramRealmTransformerBase uses this method.
   */
  final def entityDocumentName(anObject: SMObject): String = {
    "Doc" + classNameBase(anObject)
  }

  @deprecated("candidate old feature", "before 0.3.3")
  final def entityDocumentName(anObject: PObjectEntity): String = {
    entityDocumentName(anObject.modelObject)
  }

  /**
   * SimpleModel2ProgramRealmTransformerBase uses this method.
   */
  final def entityServiceName(pkg: SMPackage): String = {
    classNameBase(pkg) + "RepositoryService"
  }

  @deprecated("candidate old feature", "before 0.3.3")
  final def className(anObject: PObjectEntity): String = {
    className(anObject.modelObject)
  }

  @deprecated("candidate old feature", "before 0.3.3")
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
    UString.capitalize(localeTerm(modelElement))
  }

  final def dataKey(attr: PAttribute): String = {
    asciiName(attr)
  }

  /**
   * Program oriented name. Capitalize nuetral.
   */
  final def asciiName(anObject: PObjectEntity): String = {
    asciiName(anObject.modelObject)
  }

  /**
   * Program oriented name. Capitalized nuetral.
   */
  final def asciiName(attr: PAttribute): String = {
    asciiName(attr.modelElement)
  }

  /**
   * Program oriented name. Capitalized nuetral.
   */
  final def asciiName(modelElement: SMElement): String = {
//    underscore_name(enTerm(modelElement))
    camel_case_name(enTerm(modelElement))
  }

  /**
   * Program oriented name. Lower case term.
   * 
   * Use cases: URI
   */
  final def uriName(anObject: PObjectEntity): String = {
    uriName(anObject.modelObject)
  }

  /**
   * Program oriented name. Lower case term.
   * 
   * Use cases: URI
   */
  final def uriName(attr: PAttribute): String = {
    uriName(attr.modelElement)
  }

  /**
   * Program oriented name. Lower case term.
   * 
   * Use cases: URI
   */
  final def uriName(modelElement: SMElement): String = {
//    underscore_name(enTerm(modelElement))
    camel_case_name(enTerm(modelElement)).toLowerCase
  }

  @deprecated("candidate old feature", "before 0.3.3")
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
   * camel case (capitalize nuetral)
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

  def makePathname(obj: PObjectEntity): String = {
//    val kind = if (UString.isNull(obj.kindName)) "" else "/" + obj.kindName 
//    srcMainDir + UJavaString.packageName2pathname(obj.packageName) + kind + "/" + obj.name + "." + obj.fileSuffix
    srcMainDir + UJavaString.packageName2pathname(obj.packageName) + "/" + obj.name + "." + obj.fileSuffix
  }

  def makePathname(qname: String): String = {
    require (qname != "app")
    srcMainDir + UJavaString.className2pathname(qname) + "." + defaultFileSuffix
  }

  def makePathname(name: String, packageName: String): String = {
    require (name != null && packageName != null)
    makePathname(packageName + "." + name)
  }

  /*
   * SQL
   */
  /**
   * SqlPlatform automatically generates SQL entities.
   */
  lazy val sqlRealm = SqlPlatform.create(this)

  final def getSqlEntity(entity: PEntityEntity): SqlEntityEntity = {
    sqlRealm.getEntityEntity(entity)
  }

  final def getSqlEntity(doc: PDocumentEntity): SqlDocumentEntity = {
    sqlRealm.getDocumentEntity(doc) getOrElse {
      sys.error("not implemented yet")
    }
  }

  def sqlMaker(entity: PEntityEntity): SqlMaker = {
    new EntitySqlMaker(entity)
  }

  def sqlMaker(doc: PDocumentEntity): SqlMaker = {
    new DocumentSqlMaker(doc)
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
   * Old features
   */
  @deprecated("old feature", "before 0.3.3")
  final def isProject = {
    serviceContext.getParameter("gaej.project").isDefined
  }

  @deprecated("old feature", "before 0.3.3")
  final def applicationName = {
    serviceContext.getParameter("gaej.project").get.toString
  }
}
