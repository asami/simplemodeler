package org.simplemodeling.SimpleModeler.entities.simplemodel

import java.io.BufferedWriter
import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.LinkedHashSet
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntity
import org.goldenport.entity.GEntityContext
import org.goldenport.entity.NullEntityContext
import org.simplemodeling.dsl.datatype.XBoolean
import org.simplemodeling.dsl.datatype.XByte
import org.simplemodeling.dsl.datatype.XDecimal
import org.simplemodeling.dsl.datatype.XDouble
import org.simplemodeling.dsl.datatype.XFloat
import org.simplemodeling.dsl.datatype.XInt
import org.simplemodeling.dsl.datatype.XInteger
import org.simplemodeling.dsl.datatype.XLong
import org.simplemodeling.dsl.datatype.XShort
import org.simplemodeling.dsl.datatype.XString
import org.simplemodeling.dsl.domain.DomainActor
import org.simplemodeling.dsl.domain.DomainEntity
import org.simplemodeling.dsl.domain.DomainEvent
import org.simplemodeling.dsl.domain.DomainPowertype
import org.simplemodeling.dsl.domain.DomainResource
import org.simplemodeling.dsl.domain.DomainRole
import org.simplemodeling.dsl.domain.DomainRule
import org.simplemodeling.dsl.domain.DomainState
import org.simplemodeling.dsl.domain.DomainStateMachine
import org.simplemodeling.dsl.domain.DomainSummary
import org.simplemodeling.dsl.domain.DomainValue
import org.simplemodeling.dsl.domain.DomainValueId
import org.simplemodeling.dsl.domain.DomainValueName
import org.simplemodeling.dsl.One
import org.simplemodeling.dsl.OneMore
import org.simplemodeling.dsl.SAttribute
import org.simplemodeling.dsl.SAttributeType
import org.simplemodeling.dsl.SEntity
import org.simplemodeling.dsl.SMultiplicity
import org.simplemodeling.dsl.SObject
import org.simplemodeling.dsl.SValue
import org.simplemodeling.dsl.ZeroMore
import org.simplemodeling.dsl.ZeroOne
import com.asamioffice.goldenport.text.AppendableTextBuilder
import com.asamioffice.goldenport.text.TextBuilder
import com.asamioffice.goldenport.text.UJavaString
import com.asamioffice.goldenport.text.UString
import org.simplemodeling.dsl.business.BusinessUsecase
import org.simplemodeling.dsl.SAssociation
import org.simplemodeling.dsl.domain.GenericDomainEntity

/*
 * @since   Jan. 30, 2009
 *  version Dec.  8, 2011
 *  version Jan. 30, 2012
 * @version Mar. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMEntityEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GEntity(aIn, aOut, aContext) {
  type DataSource_TYPE = GDataSource

  var packageName: String = ""
  var kind: ElementKind = null
  var name_en: String = ""
  var name_ja: String = ""
  var term: String = ""
  var term_en: String = ""
  var term_ja: String = ""
  var caption: String = ""
  var brief: String = ""
  var summary: String = ""
  var description: String = ""
  var tableName: String = ""
  var base: SMMEntityEntity = NullEntityEntity
  val powertypes = new ArrayBuffer[SMMAttribute]
  val roles = new ArrayBuffer[SMMAssociation]
  val attributes = new ArrayBuffer[SMMAttribute]
  val associations = new ArrayBuffer[SMMAssociation]
  val aggregations = new ArrayBuffer[SMMAssociation]
  val compositions = new ArrayBuffer[SMMAssociation]
  val statemachines = new ArrayBuffer[SMMAttribute]
  val powertypeKinds = new ArrayBuffer[String]
  val primaryActors = new ArrayBuffer[SMMAssociation]
  val secondaryActors = new ArrayBuffer[SMMAssociation]
  val supportingActors = new ArrayBuffer[SMMAssociation]
  val statemachineStates = new ArrayBuffer[(String, String)]
  val scenarioSteps = new ArrayBuffer[SMMAssociation]
  var narrativeBase: String = ""
  val narrativePowertypes = new ArrayBuffer[String]
  val narrativeRoles = new ArrayBuffer[String]
  val narrativeAttributes = new ArrayBuffer[String]
  val narrativeParts = new ArrayBuffer[String]
  val narrativeStateMachines = new ArrayBuffer[String]
  val narrativeAnnotations = new ArrayBuffer[String]
  val narrativePrimaryActors = new ArrayBuffer[String]
  val narrativeSecondaryActors = new ArrayBuffer[String]
  val narrativeSupportingActors = new ArrayBuffer[String]
  val narrativeGoals = new ArrayBuffer[String]
  val narrativeStateTransitions = new ArrayBuffer[String]
  val narrativeScenarioSteps = new ArrayBuffer[String]
  val narrativeCompositions = new ArrayBuffer[(String, SMMEntityEntity)]
  private val private_objects = new ArrayBuffer[SMMEntityEntity]

  private var _sobject: Option[SObject] = None

  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: GEntityContext) = this(null, aContext)

  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  final def isDerived = base != NullEntityEntity || narrativeBase != ""

  def getBase(): Option[SMMEntityEntity] = {
    if (base == NullEntityEntity) None
    else Some(base)
  }
    
  final def baseClassQName: String = {
    kind match {
      case ActorKind        => "org.simplemodeling.dsl.domain.DomainActor"
      case ResourceKind     => "org.simplemodeling.dsl.domain.DomainResource"
      case EventKind        => "org.simplemodeling.dsl.domain.DomainEvent"
      case RoleKind         => "org.simplemodeling.dsl.domain.DomainRole"
      case EntityKind       => "org.simplemodeling.dsl.domain.DomainEntity"
      case RuleKind         => "org.simplemodeling.dsl.domain.DomainRule"
      case IdKind           => "org.simplemodeling.dsl.domain.DomainValueId"
      case NameKind         => "org.simplemodeling.dsl.domain.DomainValueName"
      case PowertypeKind    => "org.simplemodeling.dsl.domain.DomainPowertype"
      case StateMachineKind => "org.simplemodeling.dsl.domain.DomainStateMachine"
      case StateMachineStateKind        => "org.simplemodeling.dsl.domain.DomainState"
      case UsecaseKind      => "org.simplemodeling.dsl.business.BusinessUsecase"
      case _              => error("not implemented yet = " + kind)
    }
  }

  final def baseClassName: String = {
    kind match {
      case ActorKind        => "DomainActor"
      case ResourceKind     => "DomainResource"
      case EventKind        => "DomainEvent"
      case RoleKind         => "DomainRole"
      case EntityKind       => "DomainEntity"
      case RuleKind         => "DomainRule"
      case IdKind           => "DomainValueId"
      case NameKind         => "DomainValueName"
      case PowertypeKind    => "DomainPowertype"
      case StateMachineKind => "DomainStateMachine"
      case StateMachineStateKind        => "DomainState"
      case UsecaseKind      => "BusinessUsecase"
      case _              => error("not implemented yet = " + kind)
    }
  }

  final def addPrivateObject(anObject: SMMEntityEntity) {
    private_objects += anObject
  }

  final def powertype(aName: String, aPowertypeType: SMMPowertypeType): SMMAttribute = {
    val powertype = new SMMEntityEntity(entityContext)
    powertype.name = aPowertypeType.name
    powertype.kind = PowertypeKind
    powertype.term = aPowertypeType.term
    powertype.packageName = aPowertypeType.packageName
    powertype.powertypeKinds ++= aPowertypeType.instances
    addPrivateObject(powertype)
    val pt = new SMMAttribute(aName, aPowertypeType)
    powertypes += pt
    pt
  }

  final def role(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
    val roleType = new SMMEntityType(anObject.name, anObject.packageName)
    roleType.term = anObject.term
    val role = new SMMAssociation(aName, roleType)
    roles += role
    role
  }

  final def attribute(aName: String, anObject: SMMEntityEntity): SMMAttribute = {
    val attrType = anObject.kind match {
      case IdKind => new SMMValueIdType(anObject.name, anObject.packageName)
      case _    => new SMMValueType(anObject.name, anObject.packageName)
    }
    attrType.term = anObject.term
    val attr = new SMMAttribute(aName, attrType)
    attributes += attr
    attr
  }

  final def attribute(aName: String, anObjectType: SMMObjectType = SMMStringType): SMMAttribute = {
    val attr = new SMMAttribute(aName, new SMMValueType(anObjectType.name, anObjectType.packageName))
    attributes += attr
    attr
  }

  final def association(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
    val assocType = new SMMEntityType(anObject.name, anObject.packageName)
    assocType.term = anObject.term
    val assoc = new SMMAssociation(aName, assocType)
    associations += assoc
    assoc
  }

  final def aggregation(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
    val assocType = new SMMEntityType(anObject.name, anObject.packageName)
    assocType.term = anObject.term
    val assoc = new SMMAssociation(aName, assocType)
    aggregations += assoc
    assoc
  }

  final def composition(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
    val assocType = new SMMEntityType(anObject.name, anObject.packageName)
    assocType.term = anObject.term
    val assoc = new SMMAssociation(aName, assocType)
    compositions += assoc
    assoc
  }

  final def statemachine(aName: String, aStateMachineType: SMMStateMachineType): SMMAttribute = {
    val states = aStateMachineType.states
    val statemachine = new SMMEntityEntity(entityContext)
    statemachine.name = aStateMachineType.name
    statemachine.kind = StateMachineKind
    statemachine.term = aStateMachineType.term
    statemachine.packageName = aStateMachineType.packageName
    statemachine.statemachineStates ++= states
    addPrivateObject(statemachine)
    for (state <- states) {
      val s = new SMMEntityEntity(entityContext)
      s.name = state._2
      s.kind = StateMachineKind
      s.term = state._1
      s.packageName = aStateMachineType.packageName
      addPrivateObject(s)
    }
    val sm = new SMMAttribute(aName, aStateMachineType)
    statemachines += sm
    sm
  }

  final def annotation(aKey: String, aValue: String) {
    aKey match {
      case "name_en"     => name_en = aValue
      case "name_ja"     => name_ja = aValue
      case "term"        => term = aValue
      case "caption"     => caption = aValue
      case "brief"       => brief = aValue
      case "summary"     => summary = aValue
      case "description" => description = aValue
      case "tableName"   => tableName = aValue
      case _             => description = "bad key [" + aKey + "] = " + aValue
    }
  }

  def primaryActor(name: String, entity: SMMEntityEntity): SMMAssociation = {
    _add_special_association(primaryActors, _association(name, entity)) 
  }

  def secondaryActor(name: String, entity: SMMEntityEntity): SMMAssociation = {
    _add_special_association(secondaryActors, _association(name, entity)) 
  }

  def supportingActor(name: String, entity: SMMEntityEntity): SMMAssociation = {
    _add_special_association(supportingActors, _association(name, entity)) 
  }

  def scenarioStep(name: String, entity: SMMEntityEntity): SMMAssociation = {
    _add_special_association(scenarioSteps, _association(name, entity)) 
  }

  private def _add_special_association(assocs: ArrayBuffer[SMMAssociation], assoc: SMMAssociation) = {
    assocs += assoc
    assoc
  }

  private def _association(name: String, entity: SMMEntityEntity): SMMAssociation = {
    val assocType = new SMMEntityType(entity.name, entity.packageName)
    assocType.term = entity.term
    new SMMAssociation(name, assocType)
  }

  final def setNarrativeBase(aName: String) {
    require(aName != null)
    narrativeBase = aName
  }

  final def addNarrativePowertype(aName: String) {
    narrativePowertypes += aName
  }

  final def addNarrativeRole(aName: String) {
    narrativeRoles += aName
  }

  final def addNarrativeAttribute(aName: String) {
    narrativeAttributes += aName
  }

  final def addNarrativePart(aName: String) {
    narrativeParts += aName
  }

  final def addNarrativeStateMachine(aName: String) {
    narrativeStateMachines += aName
  }

  final def addNarrativeAnnotation(aName: String) {
    narrativeAnnotations += aName
  }

  def addNarrativePrimaryActor(name: String) {
    narrativePrimaryActors += name
  }

  def addNarrativeSecondaryActor(name: String) {
    narrativeSecondaryActors += name
  }

  def addNarrativeSupportingActor(name: String) {
    narrativeSupportingActors += name
  }

  def addNarrativeGoal(name: String) {
    narrativeGoals += name
  }

  def addNarrativeStateTransition(name: String) {
    narrativeStateTransitions += name
  }

  def addNarrativeScenarioStep(step: String) {
    narrativeScenarioSteps += step;
  }

  override protected def write_Content(out: BufferedWriter) {
    val buffer = new AppendableTextBuilder(out)

    def make_package {
      require(packageName != null)
      if (packageName != "") {
        buffer.print("package ")
        buffer.println(packageName)
        buffer.println()
      }
    }

    def make_imports {
      val classes = new LinkedHashSet[String]
      collectImports(classes)
      private_objects.foreach(_.collectImports(classes))
      buffer.println("import org.simplemodeling.dsl._")
      buffer.println("import org.simplemodeling.dsl.datatype._")
      buffer.println("import org.simplemodeling.dsl.domain._")
      buffer.println("import org.simplemodeling.dsl.domain.values._")
      for (klass <- classes) {
        buffer.print("import ")
        buffer.println(klass)
      }
      buffer.println()
    }

    def make_private_object_body(anObject: SMMEntityEntity) {
      buffer.println()
      anObject.writeClassBody(buffer)
    }

    make_package
    make_imports
    writeClassBody(buffer)
    private_objects.foreach(make_private_object_body)
  }

  final def collectImports(classes: Set[String]) {
    def is_predefined_package(aPkgName: String) = {
      packageName == aPkgName ||
        "org.simplemodeling.dsl" == aPkgName ||
        "org.simplemodeling.dsl.datatype" == aPkgName ||
        "org.simplemodeling.dsl.domain" == aPkgName ||
        "org.simplemodeling.dsl.domain.values" == aPkgName
    }

    def add_import_type(aObjectType: SMMObjectType) {
      if (!is_predefined_package(aObjectType.packageName)) {
        classes += aObjectType.qualifiedName
      }
    }

    def add_import_string(qName: String) {
      if (!is_predefined_package(UJavaString.qname2packageName(qName))) {
        classes += qName
      }
    }

    if (baseClassQName != "") {
      add_import_string(baseClassQName)
    }
    //    val attrs = attributes.filter(_.attributeType.isEntity)
    for (attr <- attributes) {
      val attrType = attr.attributeType.asInstanceOf[SMMObjectType]
      add_import_type(attrType)
    }
  }

  final def writeClassBody(buffer: TextBuilder) {
    def make_specifications {
      if (name_en != "") {
        buffer.print("name_en = \"")
        buffer.print(name_en)
        buffer.println("\"")
      }
      if (name_ja != "") {
        buffer.print("name_ja = \"")
        buffer.print(name_ja)
        buffer.println("\"")
      }
      buffer.print("term = \"")
      buffer.print(term)
      buffer.println("\"")
      if (term_en != "") {
        buffer.print("term_en = \"")
        buffer.print(term_en)
        buffer.println("\"")
      }
      if (term_ja != "") {
        buffer.print("term_ja = \"")
        buffer.print(term_ja)
        buffer.println("\"")
      }
      buffer.print("caption = \"")
      if (caption != "") {
        buffer.print(caption)
      } else {
        buffer.print(term)
      }
      buffer.println("\"")
      buffer.print("brief = <t>")
      buffer.print(brief)
      buffer.println("</t>")
      if (summary != "") {
        buffer.print("summary = <t>")
        buffer.print(summary)
        buffer.println("</t>")
      }
      buffer.print("description = <text>")
      buffer.print(description)
      buffer.println("</text>")
      if (tableName != "") {
        buffer.print("tableName = \"")
        buffer.print(tableName)
        buffer.println("\"")
      }
      buffer.println()
    }

    def make_base {
      if (base != NullEntityEntity) {
        buffer.print("base") // this is_kind_of
        buffer.print("(")
        buffer.print(base.name)
        buffer.print("()")
        buffer.print(")")
        buffer.println()
      }
    }

    def make_powertypes {
      for (power <- powertypes) {
        buffer.print("powertype")
        buffer.print("(")
        buffer.print("\"")
        buffer.print(power.name)
        buffer.print("\", ")
        buffer.print(power.attributeType.name)
        buffer.print("()")
        make_multiplicity_parameter(power.multiplicity)
        buffer.print(")")
        buffer.println()
      }
    }

    def make_roles {
      for (role <- roles) {
        buffer.print("role")
        buffer.print("(")
        buffer.print("\"")
        buffer.print(role.name)
        buffer.print("\", ")
        buffer.print(role.associationType.name)
        buffer.print("()")
        make_multiplicity_parameter(role.multiplicity)
        buffer.print(")")
        buffer.println()
      }
    }

    def make_primary_actors {
      primaryActors.foreach(make_association_kind("primary_actor", _))
    }
    
    def make_secondary_actors {
      secondaryActors.foreach(make_association_kind("secondary_actor", _))
    }

    def make_supporting_actors {
      supportingActors.foreach(make_association_kind("supporting_actor", _))
    }

    def make_association_kind(kind: String, assoc: SMMAssociation) {
      buffer.print(kind)
      buffer.print("(")
      buffer.print("\"")
      buffer.print(assoc.name)
      buffer.print("\", ")
      buffer.print(assoc.associationType.name)
      buffer.print("()")
      make_multiplicity_parameter(assoc.multiplicity)
      buffer.print(")")
      buffer.println()
    }

    def make_multiplicity_parameter(aMultiplicity: GRMultiplicity) {
      aMultiplicity match {
        case m: GROne      => //
        case m: GRZeroOne  => buffer.print(", ZeroOne")
        case m: GROneMore  => buffer.print(", OneMore")
        case m: GRZeroMore => buffer.print(", ZeroMore")
      }
    }

    def make_attributes {
      for (attr <- attributes) {
        attr.attributeType match {
          case t: SMMValueIdType => {
            buffer.print("id")
          }
          case _ => {
            buffer.print("attribute")
          }
        }
        buffer.print("(")
        buffer.print("\"")
        buffer.print(attr.name)
        buffer.print("\"")
        buffer.print(", ")
        buffer.print(attr.attributeType.name)
        if (attr.attributeType.packageName != "org.simplemodeling.dsl.datatype") {
          buffer.print("()")
        }
        make_multiplicity_parameter(attr.multiplicity)
        buffer.print(")")
        buffer.println()
      }
    }

    def make_associations {
      associations.foreach(make_association_kind("association", _))
    }

    def make_aggregations {
      aggregations.foreach(make_association_kind("aggregation", _))
    }

    def make_compositions {
      compositions.foreach(make_association_kind("composition", _))
    }

    def make_statemachines {
      for (machine <- statemachines) {
        buffer.print("statemachine")
        buffer.print("(")
        buffer.print(machine.attributeType.name)
        buffer.print("()")
        buffer.print(")")
        buffer.println()
      }
    }

    def make_powertypeKinds {
      for (kind <- powertypeKinds) {
        buffer.print("kind")
        buffer.print("(")
        buffer.print("\"")
        buffer.print(kind)
        buffer.print("\"")
        buffer.print(")")
        buffer.println()
      }
    }

    def make_statemachineStates {
      for (state <- statemachineStates) {
        buffer.print("state")
        buffer.print("(")
        buffer.print(state._2)
        buffer.print("()")
        buffer.print(")")
        buffer.println()
      }
    }

    def make_scenario_steps {
      for (step <- scenarioSteps) {
        buffer.println("basic_flow {")
        buffer.indentUp()
        buffer.print("event_issue(")
        buffer.print(step.associationType.name)
        buffer.println("())()")
        buffer.indentDown()
        buffer.println("}")
      }
    }

    buffer.print("case class ")
    buffer.print(name)
    if (baseClassName != "") {
      buffer.print(" extends ")
      buffer.print(baseClassName)
    }
    buffer.println(" {")
    buffer.indentUp
    make_specifications
    make_base
    make_powertypes
    make_roles
    make_attributes
    make_associations
    make_aggregations
    make_compositions
    make_primary_actors
    make_secondary_actors
    make_supporting_actors
    make_statemachines
    make_powertypeKinds
    make_statemachineStates
    make_scenario_steps
    buffer.indentDown
    buffer.println("}")
    buffer.flush
  }

  def createSObjects(): List[SObject] = {
    _sobject = _sobject match {
      case Some(s) => Some(s)
      case None => Some(_create_object())
    }
    _sobject.get :: private_objects.toList.flatMap(_.createSObjects()) 
  }

  private def _create_object(): SObject = {
    kind match {
      case ActorKind    => new DomainActor(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case ResourceKind => new DomainResource(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case EventKind    => new DomainEvent(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case RoleKind     => new DomainRole(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case SummaryKind  => new DomainSummary(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case EntityKind   => new DomainEntity(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case RuleKind         => new DomainRule(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case IdKind           => new DomainValueId(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case NameKind         => new DomainValueName(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case PowertypeKind    => new DomainPowertype(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
/*
      case StateMachineKind => new DomainStateMachine(name) {
        override def isObjectScope = true        
      }
      case StateMachineStateKind => new DomainState(name) {
        override def isObjectScope = true        
      }
*/
      case UsecaseKind      => new BusinessUsecase(name, packageName) {
//        override def isObjectScope = true
      }
      case NoneKind => new GenericDomainEntity(name, packageName, Nil) {
//        override def isObjectScope = true
      }
      case gk: GenericKind => new GenericDomainEntity(name, packageName, List(gk.name)) {
//        override def isObjectScope = true
      }
      case _          => error("not implemented yet = " + kind)
    }
  }

  // produce SObject
  def buildSObjects(entities: Map[String, SObject]) {
    _sobject match {
      case Some(event: DomainEvent) => _build_event(event, entities)
      case Some(entity: DomainEntity) => _build_entity(entity, entities)
      case Some(value: DomainValue) => _build_value(value)
      case Some(power: DomainPowertype) => _build_powertype(power)
      case Some(uc: BusinessUsecase) => _build_usecase(entities, uc)
      case Some(dr: DomainRule) => _build_rule(entities, dr)
      case Some(x) => sys.error("buildSObject:" + x)
      case None => sys.error("buildSObject")
    }
    private_objects.foreach(_.buildSObjects(entities))
  }

  private def _build_event(event: DomainEvent, entities: Map[String, SObject]): DomainEntity = {
    _build_entity(event, entities)
    primaryActors.foreach(_describe_association(entities, event.primary_actor.apply, _))
    secondaryActors.foreach(_describe_association(entities, event.secondary_actor.apply, _))
    event
  }

  private def _build_entity(entity: DomainEntity, entities: Map[String, SObject]): DomainEntity = {
    entity.term = if (UString.isNull(term)) name else term
    _build_specifications(entity)
    _build_base(entities, entity)
    _build_powertypes(entities, entity)
    _build_roles(entities, entity)
    _build_attributes(entity)
    _build_associations(entity, entities)
    _build_aggregations(entity, entities)
    _build_compositions(entity, entities)
    _build_statemachines(entity)
    _build_statemachineStates(entity)
    entity
  }

  private def _build_specifications(entity: DomainEntity) {

  }

  private def _build_base(entities: Map[String, SObject], entity: DomainEntity) {
    if (base != NullEntityEntity) {
      entity.base(_entity_ref(base.name, entities))
    }
  }

  private def _build_powertypes(entities: Map[String, SObject], entity: DomainEntity) {
    for (power <- powertypes) {
      _powertype_ref(power.attributeType.name, entities) match {
        case sp: DomainPowertype => entity.powertype(power.name, sp, _dsl_multiplicity(power.multiplicity))
        case entity => record_debug("SMMEntityEntity: " + entity)
      }
    }
  }

  private def _build_roles(entities: Map[String, SObject], entity: DomainEntity) {
    for (role <- roles) {
      _role_ref(role.associationType.name, entities) match {
        case r: DomainRole => entity.role(role.name, r, _dsl_multiplicity(role.multiplicity))
        case entity => record_debug("SMMEntityEntity: " + entity)
      }
    }
  }

  private def _build_attributes(entity: DomainEntity) {
    for (attr <- attributes) {
      attr.attributeType match {
        case t: SMMValueIdType => {
//          entity.attribute_id.attributeType = _dsl_type(t)
          _build_attribute(entity.attribute_id, attr)
        }
        case _ => {
          val a = entity.attribute(attr.name, _dsl_type(attr.attributeType), _dsl_multiplicity(attr.multiplicity))
          _build_attribute(a, attr)
        }
      }
    }
  }

  private def _build_attributes(value: SObject) {
    for (attr <- attributes) {
      attr.attributeType match {
        case _ => {
          val a = value.attribute(attr.name, _dsl_type(attr.attributeType), _dsl_multiplicity(attr.multiplicity))
          _build_attribute(a, attr)
        }
      }
    }
  }

  private def _build_attribute(attr: SAttribute, src: SMMAttribute) {
    for (a <- _dsl_text(src.name_ja)) {
      attr.name_ja = a
    }
    for (a <- _dsl_text(src.name_en)) {
      attr.name_en = a
    }
    for (a <- _dsl_text(src.term)) {
      attr.term = a
    }
    for (a <- _dsl_text(src.term_ja)) {
      attr.term_ja = a
    }
    for (a <- _dsl_text(src.term_en)) {
      attr.term_en = a
    }
    for (a <- _dsl_text(src.title)) {
//      attr.title = a XXX
      attr.caption = a
    }
    for (a <- _dsl_text(src.subtitle)) {
//      attr.subtitle = a XXX
      attr.brief = a
    }
    for (a <- _dsl_text(src.caption)) {
      attr.caption = a
    }
    for (a <- _dsl_text(src.brief)) {
      attr.brief = a
    }
    for (a <- _dsl_text(src.summary)) {
      attr.summary = a
    }
    for (a <- _dsl_text(src.columnName)) {
      attr.columnName = a
    }
  }

  private def _dsl_text(s: String): Option[String] = {
    if (UString.isNull(s)) None
    else Some(s)
  }

  private def _dsl_type(otype: SMMObjectType): SAttributeType = {
    def stubvalue(name: String, pkgname: String) = {
      val v = new SValue(name, pkgname)
      v.isMasterSingleton = false
      v
    }
    otype match {
      case v: SMMValueIdType => stubvalue(v.name, v.packageName)
      case v: SMMValueType => stubvalue(v.name, v.packageName)
      case _: SMMStringType => XString
      case _: SMMBooleanType => XBoolean
      case _: SMMByteType => XByte
      case _: SMMShortType => XShort
      case _: SMMIntType => XInt
      case _: SMMLongType => XLong
      case _: SMMFloatType => XFloat
      case _: SMMDoubleType => XDouble
      case _: SMMIntegerType => XInteger
      case _: SMMDecimalType => XDecimal
    }
  }

  private def _dsl_multiplicity(mul: GRMultiplicity): SMultiplicity = {
    mul match {
      case _: GROne => One
      case _: GRZeroOne => ZeroOne
      case _: GROneMore => OneMore
      case _: GRZeroMore => ZeroMore
    }
  }

  private def _build_associations(entity: DomainEntity, entities: Map[String, SObject]) {
      for (assoc <- associations) {
        entity.association(assoc.name, _entity_ref(assoc.associationType.name, entities), _dsl_multiplicity(assoc.multiplicity))
      }
  }

  private def _build_aggregations(entity: DomainEntity, entities: Map[String, SObject]) {
      for (assoc <- aggregations) {
        entity.aggregation(assoc.name, _entity_ref(assoc.associationType.name, entities), _dsl_multiplicity(assoc.multiplicity))
      }
  }

  private def _build_compositions(entity: DomainEntity, entities: Map[String, SObject]) {
      for (assoc <- compositions) {
        entity.composition(assoc.name, _entity_ref(assoc.associationType.name, entities), _dsl_multiplicity(assoc.multiplicity))
      }
  }

  private def _entity_ref(name: String, entities: Map[String, SObject]): SEntity = {
    entities(name) match {
      case entity: SEntity => entity
      case x => error("not entity: " + x)
    }
  }

  private def _powertype_ref(name: String, entities: Map[String, SObject]): DomainPowertype = {
    entities(name) match {
      case p: DomainPowertype => p
      case x => error("not powertype: " + x)
    }
  }

  private def _role_ref(name: String, entities: Map[String, SObject]): DomainRole = {
    entities(name) match {
      case r: DomainRole => r
      case x => error("not role: " + x)
    }
  }

  private def _build_statemachines(entity: DomainEntity) {
    // TODO
  }

  private def _build_statemachineStates(entity: DomainEntity) {
    // TODO
  }

  private def _build_value(value: DomainValue): DomainValue = {
    value.term = name
//    _build_specifications(value)
//    _build_base(value)
//    _build_powertypes(value)
//    _build_roles(value)
    _build_attributes(value)
    value
  }

  private def _build_powertype(power: DomainPowertype): DomainPowertype = {
    _build_powertypeKinds(power)
    power
  }

  private def _build_powertypeKinds(entity: DomainPowertype) {
    for (kind <- powertypeKinds) {
      entity.kind(kind)
    }
  }


  private def _build_usecase(entities: Map[String, SObject], uc: BusinessUsecase): BusinessUsecase = {
    uc.term = name
//    _build_specifications(value)
//    _build_base(value)
//    _build_powertypes(value)
//    _build_roles(value)
    _build_attributes(uc)
    primaryActors.foreach(_describe_association(entities, uc.primary_actor.apply, _))
    secondaryActors.foreach(_describe_association(entities, uc.secondary_actor.apply, _))
    supportingActors.foreach(_describe_association(entities, uc.supporting_actor.apply, _))
    if (!scenarioSteps.isEmpty) {
      uc.basic_flow {
        scenarioSteps.foreach(_describe_event_issue(entities, uc, _))
      }
    }
    uc
  }

  private def _build_rule(entities: Map[String, SObject], dr: DomainRule): DomainRule = {
    dr.term = name
    _build_attributes(dr)
//    _build_associations(dr, entities)
//    _build_aggregations(dr, entities)
//    _build_compositions(dr, entities)
    dr
  }

  private def _describe_association(entities: Map[String, SObject], set: (String, SEntity, SMultiplicity) => SAssociation, assoc: SMMAssociation) {
    set(assoc.name, _entity_ref(assoc.associationType.name, entities), _dsl_multiplicity(assoc.multiplicity))
  }

  private def _describe_event_issue(entities: Map[String, SObject], uc: BusinessUsecase, step: SMMAssociation) {
    _entity_ref(step.associationType.name, entities) match {
      case event: DomainEvent => uc.event_issue(event)()
      case _ => {}
    }
  }
}

object NullEntityEntity extends SMMEntityEntity(NullEntityContext)
