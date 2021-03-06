package org.simplemodeling.SimpleModeler.entities.simplemodel

import org.apache.commons.lang3.StringUtils
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.util.PropertyRecord
import scalaz._, Scalaz._
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
import org.simplemodeling.dsl.datatype.XDate
import org.simplemodeling.dsl.datatype.XTime
import org.simplemodeling.dsl.datatype.XDateTime
import org.simplemodeling.dsl.datatype.XToken
import org.simplemodeling.dsl.datatype.XAnyURI
import org.simplemodeling.dsl.datatype.ext.XText
import org.simplemodeling.dsl.datatype.ext.XLink
import org.simplemodeling.dsl.datatype.business.XMoney
import org.simplemodeling.dsl.datatype.business.XPercent
import org.simplemodeling.dsl.datatype.business.XUnit
import org.simplemodeling.dsl.datatype.platform._
import org.simplemodeling.dsl.{IdAttributeKind, NullAttributeKind}
import org.simplemodeling.dsl.SUsecase
import org.simplemodeling.dsl.STask
import org.simplemodeling.dsl.SPowertypeKind
import org.simplemodeling.dsl.business.BusinessEntity
import org.simplemodeling.dsl.business.BusinessActor
import org.simplemodeling.dsl.business.BusinessResource
import org.simplemodeling.dsl.business.BusinessEvent
import org.simplemodeling.dsl.domain.DomainActor
import org.simplemodeling.dsl.domain.DomainDocument
import org.simplemodeling.dsl.domain.DomainEntity
import org.simplemodeling.dsl.domain.DomainEntityPart
import org.simplemodeling.dsl.domain.DomainTrait
import org.simplemodeling.dsl.domain.DomainEvent
import org.simplemodeling.dsl.domain.DomainResource
import org.simplemodeling.dsl.domain.DomainPowertype
import org.simplemodeling.dsl.domain.DomainRole
import org.simplemodeling.dsl.domain.DomainRule
import org.simplemodeling.dsl.domain.DomainService
import org.simplemodeling.dsl.domain.DomainState
import org.simplemodeling.dsl.domain.DomainStateMachine
import org.simplemodeling.dsl.domain.DomainSummary
import org.simplemodeling.dsl.domain.DomainAssociationEntity
import org.simplemodeling.dsl.domain.DomainValue
import org.simplemodeling.dsl.domain.DomainValueId
import org.simplemodeling.dsl.domain.DomainValueName
import org.simplemodeling.dsl.One
import org.simplemodeling.dsl.OneMore
import org.simplemodeling.dsl.SElement
import org.simplemodeling.dsl.SAttribute
import org.simplemodeling.dsl.SAttributeType
import org.simplemodeling.dsl.SOperation
import org.simplemodeling.dsl.SEntity
import org.simplemodeling.dsl.STrait
import org.simplemodeling.dsl.SMultiplicity
import org.simplemodeling.dsl.SObject
import org.simplemodeling.dsl.SDocument
import org.simplemodeling.dsl.SEntityDocument
import org.simplemodeling.dsl.SValue
import org.simplemodeling.dsl.SExpression
import org.simplemodeling.dsl.ZeroMore
import org.simplemodeling.dsl.ZeroOne
import com.asamioffice.goldenport.text.AppendableTextBuilder
import com.asamioffice.goldenport.text.TextBuilder
import com.asamioffice.goldenport.text.UJavaString
import com.asamioffice.goldenport.text.UString
import org.simplemodeling.dsl.business.BusinessUsecase
import org.simplemodeling.dsl.business.BusinessTask
import org.simplemodeling.dsl.requirement.RequirementUsecase
import org.simplemodeling.dsl.requirement.RequirementTask
import org.simplemodeling.dsl.SAssociation
import org.simplemodeling.dsl.domain.GenericDomainEntity
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Jan. 30, 2009
 *  version Dec.  8, 2011
 *  version Jan. 30, 2012
 *  version Mar. 25, 2012
 *  version Jun. 17, 2012
 *  version Sep. 30, 2012
 *  version Oct. 30, 2012
 *  version Nov. 30, 2012
 *  version Dec. 26, 2012
 *  version Jan. 29, 2013
 * @version Feb. 23, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * OutlineBuilderBase updates this object.
 * SmpleModelDslBuilder setups a resolved model from the narrative model.
 */
class SMMEntityEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GEntity(aIn, aOut, aContext) with SMMElement {
  type DataSource_TYPE = GDataSource

//  implicit def show_sobject(a: List[SObject]) = a.toString

  /**
   * SimpleModelDslBuilder turn on the variable after resolving.
   */
  var isResolved: Boolean = false

  var packageName: String = ""
  var kind: ElementKind = NoneKind

  /*
   * GUI
   */
  var guiNaviLabel: String = ""
  var guiTabLabel: String = ""
  var guiView: String = ""
  var guiTemplate: String = ""
  var guiWidget: String = ""

  /*
   * SQL
   */
  var tableName: String = ""
  var joinName: String = ""
  // to aviod cyclic recursive initialization for base in the NullEntityEntity signleton.
  private var _base: SMMEntityEntity = null
  def base: SMMEntityEntity = {
    require (isResolved, "SMMEntityEntity#base requires that entity has been resolved: " + name)
    if (_base == null) NullEntityEntity else _base
  }
  def base_=(b: SMMEntityEntity): Unit = {
    require (b != null)
    _base = b
  }
  /*
   * Uses in buildSObject.
   * Internal use only except SimpleModelOutlineBuilder(deprecated) and
   * CsvXMindConverter. Virtually obsolated.
   */
  val traits = new ArrayBuffer[SMMEntityEntity]
  val powertypes = new ArrayBuffer[SMMPowertype]
  val roles = new ArrayBuffer[SMMAssociation]
  val attributes = new ArrayBuffer[SMMAttribute]
  val associations = new ArrayBuffer[SMMAssociation]
  val aggregations = new ArrayBuffer[SMMAssociation]
  val compositions = new ArrayBuffer[SMMAssociation]
  val rules = new ArrayBuffer[SMMRuleRelationship]
  val services = new ArrayBuffer[SMMServiceRelationship]
  val statemachines = new ArrayBuffer[SMMStateMachine]
  val statemachineRelationships = new ArrayBuffer[SMMAssociation]
//  val statemachineStates = new ArrayBuffer[(String, String)]
  val operations = new ArrayBuffer[SMMOperation]
//  val powertypeKinds = new ArrayBuffer[String]
  val primaryActors = new ArrayBuffer[SMMAssociation]
  val secondaryActors = new ArrayBuffer[SMMAssociation]
  val supportingActors = new ArrayBuffer[SMMAssociation]
  val scenarioSteps = new ArrayBuffer[SMMAssociation]
  val displays = new ArrayBuffer[SMMDisplay]
  val actions = new ArrayBuffer[SMMAction]

  def isAttribute(name: String) = attributes.exists(_.name == name)
  def isAssociation(name: String) = associations.exists(_.name == name)
  def isAggregation(name: String) = aggregations.exists(_.name == name)
  def isComposition(name: String) = compositions.exists(_.name == name)
  def isStateMachine(name: String) = {
    statemachineRelationships.exists(_.name == name) ||
    statemachines.exists(_.name == name)
  }
  def isPowertype(name: String) = powertypes.exists(_.name == name)
  def isOperation(name: String) = operations.exists(_.name == name)

  /*
   * TableSimpleModelMakerBuilder registers kinds and states.
   * Used by SimpleModelDslBuilder. XXX
   */
  /**
   * Be active in case of Powertype.
   */
  val powertypeKinds = new ArrayBuffer[SMMPowertypeKind]
  /**
   * Be active in case of StateMachine.
   */
  val statemachineStates = new ArrayBuffer[SMMStateMachineState]

  /*
   * Used by SimpleModelDslBuilder.
   */
  var narrativeBase: String = ""
  val narrativeTraits = new ArrayBuffer[String]
  val narrativePowertypes = new ArrayBuffer[String]
  val narrativeKinds = new ArrayBuffer[String]
  val narrativeRoles = new ArrayBuffer[String]
  val narrativeAttributes = new ArrayBuffer[String]
  val narrativeParts = new ArrayBuffer[String]
  val narrativeCompositions = new ArrayBuffer[String]
  val narrativeAggregations = new ArrayBuffer[String]
  val narrativeAssociations = new ArrayBuffer[String]
  val narrativeRules = new ArrayBuffer[String]
  val narrativeServices = new ArrayBuffer[String]
  val narrativeStateMachines = new ArrayBuffer[String]
  val narrativeAnnotations = new ArrayBuffer[String]
//  val narrativeBusinessUsecases = new ArrayBuffer[String]
//  val narrativeBusinessTasks = new ArrayBuffer[String]
//  val narrativeUsecases = new ArrayBuffer[String]
//  val narrativeTasks = new ArrayBuffer[String]
  val narrativePrimaryActors = new ArrayBuffer[String]
  val narrativeSecondaryActors = new ArrayBuffer[String]
  val narrativeSupportingActors = new ArrayBuffer[String]
  val narrativeGoals = new ArrayBuffer[String]
  val narrativeStateTransitions = new ArrayBuffer[String]
  val narrativeOperations = new ArrayBuffer[String]
  val narrativeOperationIns = new ArrayBuffer[String]
  val narrativeOperationOuts = new ArrayBuffer[String]
  val narrativeOperationCreates = new ArrayBuffer[String]
  val narrativeOperationReads = new ArrayBuffer[String]
  val narrativeOperationUpdates = new ArrayBuffer[String]
  val narrativeOperationDeletes = new ArrayBuffer[String]
  val narrativeScenarioSteps = new ArrayBuffer[String]
  /**
   * SimpleModelDslBuilder uses to collect composition classes in narrative.
   */
  val narrativeOwnCompositions = new ArrayBuffer[(String, SMMEntityEntity)]
  val narrativeOwnBusinessUsecases = new ArrayBuffer[(String, SMMEntityEntity)]
  val narrativeOwnBusinessTasks = new ArrayBuffer[(String, SMMEntityEntity)]
  val narrativeOwnUsecases = new ArrayBuffer[(String, SMMEntityEntity)]
  val narrativeOwnTasks = new ArrayBuffer[(String, SMMEntityEntity)]
  //
  val privateObjects = new ArrayBuffer[SMMEntityEntity]

  private var _sobject: Option[SObject] = None

  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: GEntityContext) = this(null, aContext)

  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    sys.error("not implemented yet")
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
      case StateMachineStateKind => "org.simplemodeling.dsl.domain.DomainState"
      case BusinessUsecaseKind => "org.simplemodeling.dsl.business.BusinessUsecase"
      case BusinessTaskKind => "org.simplemodeling.dsl.business.BusinessTask"
      case UsecaseKind      => "org.simplemodeling.dsl.business.Usecase"
      case TaskKind         => "org.simplemodeling.dsl.business.Task"
      case _                => sys.error("not implemented yet = " + kind)
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
      case StateMachineStateKind => "DomainState"
      case BusinessUsecaseKind   => "BusinessUsecase"
      case BusinessTaskKind => "BusinessUsecase"
      case UsecaseKind      => "BusinessUsecase"
      case TaskKind         => "Task"
      case _              => sys.error("not implemented yet = " + kind)
    }
  }

  /**
   * not used
   */
  def effectiveAttributes(): Seq[SMMAttribute] = {
    require (isResolved, "SMMEntityEntity#effectiveAttributes requires that entity has been resolved: " + name)
    base.effectiveAttributes ++ traits.flatMap(_.effectiveAttributes) ++ attributes
  }

  /*
   * Mutate methods
   */ 
  final def addPrivateObject(anObject: SMMEntityEntity) {
//    println("SMMEntityEntity#addPrivateObject: %s %s".format(anObject.name, name))
    privateObjects += anObject
  }

  final def mixinTrait(tr: SMMEntityEntity): SMMEntityEntity = {
    traits += tr
    tr
  }

  final def powertype(aName: String, aPowertypeType: SMMPowertypeType): SMMPowertype = {
    _add_slot(aName, powertypes, "「%s」の区分「%s」は追加定義を持っています。型以外の情報が追加されます。") {
      val powertype = new SMMEntityEntity(entityContext)
      powertype.name = aPowertypeType.name
      powertype.kind = PowertypeKind
      powertype.term = aPowertypeType.term
      powertype.packageName = aPowertypeType.packageName
      powertype.powertypeKinds ++= aPowertypeType.instances.map(SMMPowertypeKind.create)
      addPrivateObject(powertype)
      new SMMPowertype(aName, aPowertypeType)
    }
  }

  final def powertype(aName: String, entitytype: SMMEntityTypeSet): SMMPowertype = {
    _add_slot(aName, powertypes, "「%s」の区分「%s」は追加定義を持っています。型以外の情報が追加されます。") {
      val ptpkg = entitytype.entityType.get.packageName
      val ptname = entitytype.entityType.get.name
      val ptt = new SMMPowertypeType(ptname, ptpkg)
      new SMMPowertype(aName, ptt)
    }
  }


  final def role(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
    _add_slot(aName, roles, "「%s」の役割「%s」は追加定義を持っています。型以外の情報が追加されます。") {
      val roleType = new SMMEntityType(anObject.name, anObject.packageName)
      roleType.term = anObject.term
      new SMMAssociation(aName, new SMMEntityTypeSet(roleType.some), RoleAssociationKind)
    }
  }

  final def rule(aName: String, ruleType: SMMRuleType): SMMRuleRelationship = {
    _add_slot(aName, rules, "「%s」の規則「%s」は追加定義を持っています。型以外の情報が追加されます。") {
//      val ruleType = new SMMEntityType(anObject.name, anObject.packageName)
//      ruleType.term = anObject.term
      new SMMRuleRelationship(aName, ruleType)
    }
  }

  final def service(aName: String, serviceType: SMMServiceType): SMMServiceRelationship = {
    _add_slot(aName, services, "「%s」のサービス「%s」は追加定義を持っています。型以外の情報が追加されます。") {
//      val serviceType = new SMMEntityType(anObject.name, anObject.packageName)
//      serviceType.term = anObject.term
      new SMMServiceRelationship(aName, serviceType)
    }
  }

  /*
   * Attribute
   */
  val attribute_append_message =
    "「%s」の属性「%s」は追加定義を持っています。型以外の情報が追加されます。"

  /**
   * Creates an attribute for Value object.
   */
  final def attribute(aName: String, anObject: SMMEntityEntity): SMMAttribute = {
    _add_slot(aName, attributes, attribute_append_message) {
      val attrType = anObject.kind match {
        case IdKind => new SMMValueIdType(anObject.name, anObject.packageName)
        case _    => new SMMValueType(anObject.name, anObject.packageName)
      }
      val kind = if (anObject.kind == IdKind) IdAttributeKind else NullAttributeKind
      attrType.term = anObject.term
      new SMMAttribute(aName, new SMMAttributeTypeSet(attrType.some), kind)
    }
  }

  final def attribute0(aName: String, anObject: SMMEntityEntity): SMMAttribute = {
    attributes.find(_.name == aName) match {
      case Some(s) => {
        record_report("「%s」の属性「%s」は追加定義を持っています。", this.name, aName)
        s // XXX updates attrtype and isId
      }
      case None => {
        val attrType = anObject.kind match {
          case IdKind => new SMMValueIdType(anObject.name, anObject.packageName)
          case _    => new SMMValueType(anObject.name, anObject.packageName)
        }
        val kind = if (anObject.kind == IdKind) IdAttributeKind else NullAttributeKind
        attrType.term = anObject.term
        val attr = new SMMAttribute(aName, new SMMAttributeTypeSet(attrType.some), kind)
        attributes += attr
        attr
      }
    }
  }

  private def _add_slot[T <: SMMSlot](name: String, elements: ArrayBuffer[T], message: String)(body: => T): T = {
    elements.find(_.name == name) match {
      case Some(s) => {
        record_report(message, this.name, name)
        s
      }
      case None => {
        val elem = body
        elements += elem
        elem
      }
    }
  }

  final def attribute(aName: String): SMMAttribute = {
    attribute(aName, SMMStringType)
  }

  final def attribute(aName: String, atype: SMMValueDataType): SMMAttribute = {
    val attrtype = new SMMAttributeTypeSet(atype.some)
    attribute(aName, attrtype)
  }

  final def attribute(aName: String, attrtype: SMMAttributeTypeSet, kind: SAttributeKind = NullAttributeKind): SMMAttribute = {
    _add_slot(aName, attributes, attribute_append_message) {
      new SMMAttribute(aName, attrtype, kind)
    }
  }

  /**
   * Lift pure attribute to id if needed.
   */
  final def adjustAttributes() {
    if (!attributes.exists(_.id)) {
      attributes.headOption.collect {
        case a if a.name.toLowerCase.endsWith("id") => a.kind = IdAttributeKind
      }
    }
  }

  final def association(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
    val assocType = new SMMEntityType(anObject.name, anObject.packageName)
    assocType.term = anObject.term
    association(aName, assocType)
  }

  final def association(aName: String, entityType: SMMEntityType): SMMAssociation = {
    association(aName, new SMMEntityTypeSet(entityType.some))
  }

  /**
   * @see TableSimpleModelMakerBuilder
   */
  final def association(aName: String, entityType: SMMEntityTypeSet): SMMAssociation = {
    _add_slot(aName, associations, "「%s」の関連「%s」は追加定義を持っています。") {
      new SMMAssociation(aName, entityType, PlainAssociationKind)
    }
  }

/*
  final def association0(aName: String, entityType: SMMEntityTypeSet): SMMAssociation = {
    associations.find(_.name == aName) match {
      case Some(s) => {
        record_report("「%s」の関連「%s」は追加定義を持っています。", this.name, aName)
        s // XXX updates attrtype and isId
      }
      case None => {
        val assoc = new SMMAssociation(aName, entityType)
        associations += assoc
        assoc
      }
    }
  }
*/

  /**
   * 
   */
  final def aggregation(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
    val entityType = new SMMEntityType(anObject.name, anObject.packageName)
    entityType.term = anObject.term
    aggregation(aName, entityType)
  }

  /**
   * @see TableSimpleModelMakerBuilder
   */
  final def aggregation(aName: String, entityType: SMMEntityType): SMMAssociation = {
    aggregation(aName, new SMMEntityTypeSet(entityType.some))
  }

  /**
   * @see TableSimpleModelMakerBuilder
   */
  final def aggregation(aName: String, entityType: SMMEntityTypeSet): SMMAssociation = {
    _add_slot(aName, aggregations, "「%s」の属性「%s」は追加定義を持っています。型以外の情報は更新されます。") {
      new SMMAssociation(aName, entityType, AggregationAssociationKind)
    }
  }

  final def compositionOwn(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
//    println("SMMEntityEntity#compositionOwn: %s %s %s".format(aName, anObject.name, name))
//    println(anObject.attributes)
    privateObjects += anObject
    composition(aName, anObject)
  }

  final def composition(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
    val entityType = new SMMEntityType(anObject.name, anObject.packageName)
    entityType.term = anObject.term
    composition(aName, entityType)
  }

  final def composition(aName: String, entityType: SMMEntityType): SMMAssociation = {
    composition(aName, new SMMEntityTypeSet(entityType.some))
  }

  /**
   * @see TableSimpleModelMakerBuilder
   */
  final def composition(aName: String, entityType: SMMEntityTypeSet): SMMAssociation = {
    _add_slot(aName, compositions, "「%s」の合成「%s」は追加定義を持っています。型以外の情報が追加されます。") {
      new SMMAssociation(aName, entityType, CompositionAssociationKind)
    }
  }

  /**
   * @see TableSimpleModelMakerBuilder
   */
  final def superAssociation(aName: String, entityType: SMMEntityTypeSet): SMMAssociation = {
    _add_slot(aName, associations, "「%s」の上位関連「%s」は追加定義を持っています。型以外の情報が追加されます。") {
//      println("SMMEntityEntity#superAssociation(%s)".format(aName))
      new SMMAssociation(aName, entityType, SuperAssociationKind)
    }
  }

  /**
   * Used by TableSimpleModelMakerBuilder.
   */
  final def statemachine(aName: String, entityType: SMMEntityTypeSet): SMMAssociation = {
    _add_slot(aName, statemachineRelationships, "「%s」の状態機械「%s」は追加定義を持っています。型以外の情報が追加されます。") {
      new SMMAssociation(aName, entityType, StateMachineAssociationKind)
    }
  }

  /**
   * Used by SimpleModelDslBuilder.
   */
  final def statemachine(aName: String, anObject: SMMEntityEntity): SMMAssociation = {
    val assocType = new SMMEntityType(anObject.name, anObject.packageName)
    assocType.term = anObject.term
    statemachine(aName, new SMMEntityTypeSet(assocType.some))
  }

  /**
   * Used by SimpleModelMakerEntity.
   * Notice that SimpleModelMakerEntity is virtually obsolated.
   */
  final def statemachine(aName: String, aStateMachineType: SMMStateMachineType): SMMStateMachine = {
    record_warning("SMMEntityEntity#statemachine(%s) = %s", this.name, aName)
    _add_slot(aName, statemachines, "「%s」の状態機械「%s」は追加定義を持っています。型以外の情報が追加されます。") {
      val states = aStateMachineType.states
      val statemachine = new SMMEntityEntity(entityContext)
      statemachine.name = aStateMachineType.name
      statemachine.kind = StateMachineKind
      statemachine.term = aStateMachineType.term
      statemachine.packageName = aStateMachineType.packageName
      statemachine.statemachineStates ++= states.map(SMMStateMachineState.create)
      addPrivateObject(statemachine)
      for (state <- states) {
        val s = new SMMEntityEntity(entityContext)
        s.name = state._2
        s.kind = StateMachineKind
        s.term = state._1
        s.packageName = aStateMachineType.packageName
        addPrivateObject(s)
      }
      new SMMStateMachine(aName, aStateMachineType)
    }
  }

  /**
   * SimpleModelDslBuilder uses this method to add a state.
   * TableSimpleModelMakerBuilder add state using own add_state method.
   */
  def state(name: String): SMMStateMachineState = {
    val s = new SMMStateMachineState(name, None, None)
    statemachineStates += s
    s
  }

  /**
   * SimpleModelDslBuilder and TableSimpleModelMakerBuilder
   * uses this method to add a operation.
   */
  final def operation(aName: String, inType: SMMAttributeTypeSet, outType: SMMAttributeTypeSet): SMMOperation = {
    _add_slot(aName, operations, "「%s」の操作「%s」は追加定義を持っています。型以外の情報が追加されます。") {
      new SMMOperation(aName, inType, outType)
    }
  }

  override protected def update_Field(label: NaturalLabel, value: String) {
    label match {
      case GuiNaviLabelLabel => guiNaviLabel = value
      case GuiTabLabelLabel => guiTabLabel = value
      case GuiViewLabel => guiView = value
      case GuiTemplateLabel => guiTemplate = value
      case GuiWidgetLabel => guiWidget = value
      case TableNameLabel => tableName = value
      case JoinLabel => joinName = value
      case _ => {}
    }
  }

  def display(name: String, seq: Int, entry: Seq[PropertyRecord]) {
    displays += new SMMDisplay(name, seq, entry)
  }

  def action(name: String, entry: Seq[PropertyRecord]) {
    actions += new SMMAction(name, entry)
  }

/*
  def auxAssociation(name: String, entity: SMMEntityEntity): SMMAssociation = {
    _add_special_association(associations, name, entity)
  }

  def auxAggregation(name: String, entity: SMMEntityEntity): SMMAssociation = {
    _add_special_association(aggregations, name, entity)
  }

  def auxComposition(name: String, entity: SMMEntityEntity): SMMAssociation = {
    _add_special_association(compositions, name, entity)
  }
*/

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

  private def _add_special_association(assocs: ArrayBuffer[SMMAssociation], name: String, entity: SMMEntityEntity): SMMAssociation = {
    _add_special_association(assocs, _association(name, entity))
  }

  private def _add_special_association(assocs: ArrayBuffer[SMMAssociation], assoc: SMMAssociation): SMMAssociation = {
    assocs += assoc
    assoc
  }

  private def _association(name: String, entity: SMMEntityEntity): SMMAssociation = {
    val assocType = new SMMEntityType(entity.name, entity.packageName)
    assocType.term = entity.term
    new SMMAssociation(name, new SMMEntityTypeSet(assocType.some), PlainAssociationKind) // XXX
  }

  final def setNarrativeBase(aName: String) {
    require(aName != null)
    narrativeBase = aName
  }

  final def addNarrativeTrait(aName: String) {
    narrativeTraits += aName
  }

  final def addNarrativePowertype(aName: String) {
    narrativePowertypes += aName
  }

  final def addNarrativeKind(aName: String) {
    narrativeKinds += aName
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
/*
  final def addNarrativeBusinessUsecase(aName: String) {
    narrativeBusinessUsecases += aName
  }

  final def addNarrativeBusinessTask(aName: String) {
    narrativeBusinessTasks += aName
  }

  final def addNarrativeUsecase(aName: String) {
    narrativeUsecases += aName
  }

  final def addNarrativeTask(aName: String) {
    narrativeTasks += aName
  }
*/
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

  def addNarrativeOperation(name: String) {
    narrativeOperations += name
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
      privateObjects.foreach(_.collectImports(classes))
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
    privateObjects.foreach(make_private_object_body)
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
        buffer.print(power.powertypeType.name)
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
        buffer.print(machine.statemachineType.name)
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
        buffer.print(kind.toString) // XXX
        buffer.print("\"")
        buffer.print(")")
        buffer.println()
      }
    }

    def make_statemachineStates {
      for (state <- statemachineStates) {
        buffer.print("state")
        buffer.print("(")
        buffer.print(state.toString) // XXX
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

  var count = 1

  /**
   * Used by SimpleModelDslBuilder#dslObjects.
   * In debug, compare SimpleModelDslBuilder#createObject.
   */
  def createSObjects(): List[SObject] = {
//    println("SMMEntityEntity#createSObjects: " + this.name)
    _sobject = _sobject match {
      case Some(s) => Some(s)
      case None => Some(_create_object())
    }
    dor_d("SMMEntityEntity#createSObjects(%s: %s)", name, kind) {
      _sobject.get :: privateObjects.toList.flatMap(_.createSObjects())
    }
  }

  private def _create_object(): SObject = {
    kind match {
      case BusinessActorKind    => new BusinessActor(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case BusinessResourceKind    => new BusinessResource(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case TraitKind => new DomainTrait(name, packageName) {
        isMasterSingleton = true
      }
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
      case AssociationEntityKind => new DomainAssociationEntity(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case EntityKind   => new DomainEntity(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case EntityPartKind => new DomainEntityPart(name, packageName) {
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
      case StateMachineKind    => new DomainStateMachine(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case DocumentKind    => new DomainDocument(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case ValueKind    => new DomainValue(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
      case ServiceKind    => new DomainService(name, packageName) {
//        override def isObjectScope = true
        isMasterSingleton = true
      }
/*
      case StateMachineKind => new DomainStateMachine(name, packageName) {
//        override def isObjectScope = true        
        isMasterSingleton = true
      }
      case StateMachineStateKind => new DomainState(name) {
//        override def isObjectScope = true        
        isMasterSingleton = true
      }
*/
      case BusinessUsecaseKind      => new BusinessUsecase(name, packageName) {
//        override def isObjectScope = true
      }
      case BusinessTaskKind      => new BusinessTask(name, packageName) {
//        override def isObjectScope = true
      }
      case UsecaseKind      => new RequirementUsecase(name, packageName) {
//        override def isObjectScope = true
      }
      case TaskKind      => new RequirementTask(name, packageName) {
//        override def isObjectScope = true
      }
      case NoneKind => new GenericDomainEntity(name, packageName, Nil) {
//        override def isObjectScope = true
      }
      case gk: GenericKind => new GenericDomainEntity(name, packageName, List(gk.name)) {
//        override def isObjectScope = true
      }
      case _          => sys.error("not implemented yet = " + kind)
    }
  }

  // produce SObject
  def buildSObjects(entities: Map[String, SObject]) {
    record_debug("SMMEntityEntity#buildSObjects = " + entities)
    _sobject match {
      case Some(tr: DomainTrait) => _build_trait(tr, entities)
      case Some(entity: BusinessEntity) => _build_entity(entity, entities)
      case Some(event: DomainEvent) => _build_event(event, entities)
      case Some(assoc: DomainAssociationEntity) => _build_associationentity(assoc, entities)
      case Some(entity: DomainEntity) => _build_entity(entity, entities)
      case Some(value: DomainValue) => _build_value(value)
      case Some(power: DomainPowertype) => _build_powertype(power, entities)
      case Some(sm: DomainStateMachine) => _build_statemachine(sm, entities)
      case Some(doc: DomainDocument) => _build_document(doc, entities)
      case Some(srv: DomainService) => _build_service(srv, entities)
      case Some(dr: DomainRule) => _build_rule(entities, dr)
      case Some(uc: BusinessUsecase) => _build_businessusecase(entities, uc)
      case Some(t: BusinessTask) => _build_businesstask(entities, t)
      case Some(uc: RequirementUsecase) => _build_usecase(entities, uc)
      case Some(t: RequirementTask) => _build_task(entities, t)
      case Some(x) => sys.error("buildSObject: " + x)
      case None => sys.error("buildSObject")
    }
    privateObjects.foreach(_.buildSObjects(entities))
//    println("SMMEntityEntity#buildSObjects(%s) = %s / %s".format(name, _sobject.map(_.label), label))
  }

  private def _build_trait(tr: DomainTrait, entities: Map[String, SObject]): DomainTrait = {
    _build_entity(tr, entities)
    tr
  }

  private def _build_event(event: DomainEvent, entities: Map[String, SObject]): DomainEntity = {
    _build_entity(event, entities)
    primaryActors.foreach(_describe_association(entities, event.primary_actor.apply, _))
    secondaryActors.foreach(_describe_association(entities, event.secondary_actor.apply, _))
    event
  }

  private def _build_associationentity(entity: SObject, entities: Map[String, SObject]): SObject = {
    entity.term = if (UString.isNull(term)) name else term
    _build_specifications(entity)
    _build_properties(entity)
    _build_base(entities, entity)
    _build_traits(entities, entity)
    _build_powertypes(entities, entity)
    _build_statemachines(entities, entity)
    _build_roles(entities, entity)
    _build_attributes(entity, entities)
    _build_associations_in_associationentity(entity, entities)
    if (aggregations.nonEmpty) {
      record_warning("関連クラス「%s」は集約を持つことができません。集約「%s」は無視します。", this.name, aggregations.map(_.name).mkString(" ,"))
    }
    if (compositions.nonEmpty) {
      record_warning("関連クラス「%s」は合成を持つことができません。合成「%s」は無視します。", this.name, aggregations.map(_.name).mkString(" ,"))
    }
    _build_statemachineStates(entity) // XXX
    _build_operations(entity, entities)
    entity
  }

  private def _build_associations_in_associationentity(entity: SObject, entities: Map[String, SObject]) {
    for (assoc <- associations) {
      doe_w(_entity_ref(assoc.associationType.getName, entities)) {
        record_trace("SMMEntityEntity#_build_associations_in_associationentity: " + assoc.name)
        entity.association(assoc.name, _, _dsl_multiplicity(assoc.multiplicity)).withAssociationClass(true)
      }
    }
  }

  private def _build_entity(entity: SObject, entities: Map[String, SObject]): SObject = {
    entity.term = if (UString.isNull(term)) name else term
    _build_specifications(entity)
    _build_properties(entity)
    _build_base(entities, entity)
    _build_traits(entities, entity)
    _build_powertypes(entities, entity)
    _build_statemachines(entities, entity)
    _build_roles(entities, entity)
    _build_attributes(entity, entities)
    _build_associations(entity, entities)
    _build_aggregations(entity, entities)
    _build_compositions(entity, entities)
    _build_statemachineStates(entity) // XXX
    _build_operations(entity, entities)
    _build_rules(entity, entities)
    _build_services(entity, entities)
    _build_displays(entity)
    _build_actions(entity)
    entity
  }

  // narrative specification
  private def _build_specifications(entity: SObject) {
    _build_specifications(entity, this)
    // TODO
  }

  // narrative specification
  private def _build_specifications(target: SElement, source: SMMElement) {
    // TODO
  }

  private def _build_properties(entity: SObject) {
    _build_properties(entity, this)
    entity.guiNaviLabel = guiNaviLabel
    entity.guiTabLabel = guiTabLabel
    entity.guiView = guiView
    entity.guiTemplate = guiTemplate
    entity.guiWidget = guiWidget
    entity.sqlTableName = tableName
    entity.sqlJoinName = joinName
  }

  // _build_element
  private def _build_properties(target: SElement, src: SMMElement) {
    target.properties = src.properties
//    println("SMMEntityEntity#_build_properties = " + target.properties)
    for (a <- _dsl_text(src.name_ja)) {
      target.name_ja = a
    }
    for (a <- _dsl_text(src.name_en)) {
      target.name_en = a
    }
    for (a <- _dsl_text(src.term)) {
      target.term = a
    }
    for (a <- _dsl_text(src.term_ja)) {
      target.term_ja = a
    }
    for (a <- _dsl_text(src.term_en)) {
      target.term_en = a
    }
    for (a <- _dsl_text(src.xmlName)) {
      target.xmlName = a
    }
    for (a <- _dsl_text(src.title)) {
      target.title = a
    }
    for (a <- _dsl_text(src.subtitle)) {
      target.subtitle = a
    }
    for (a <- _dsl_text(src.label)) {
      target.label = a
    }
    for (a <- _dsl_text(src.caption)) {
      target.caption = a
    }
    for (a <- _dsl_text(src.brief)) {
      target.brief = a
    }
    for (a <- _dsl_text(src.summary)) {
      target.summary = a
    }
    for (a <- _dsl_text(src.description)) {
      target.description = a
    }
    for (a <- _dsl_boolean(src.sqlAutoId)) {
      target.sqlAutoId = a
    }
    for (a <- _dsl_boolean(src.sqlReadOnly)) {
      target.sqlReadOnly = a
    }
    for (a <- _dsl_boolean(src.sqlAutoCreate)) {
      target.sqlAutoCreate = a
    }
    for (a <- _dsl_boolean(src.sqlAutoUpdate)) {
      target.sqlAutoUpdate = a
    }
  }

/*
  private def _build_properties0(target: SElement, source: SMMElement) {
    target.name_ja = source.name_ja
    target.name_en = source.name_en
    target.term = source.term
    target.term_ja = source.term_ja
    target.term_en = source.term_en
    target.xmlName = source.xmlName
    target.label = source.label
    target.title = source.title
    target.subtitle = source.subtitle
    target.caption = source.caption
    target.brief = source.brief
    target.summary = source.summary
    target.description = source.description
    target.properties = source.properties
    //
    target.sqlColumnName = source.sqlColumnName
    target.sqlAutoId = source.sqlAutoId
    target.sqlReadOnly = source.sqlReadOnly
    target.sqlCreate = source.sqlCreate
    target.sqlUpdate = source.sqlUpdate
  }
*/

  private def _build_base(entities: Map[String, SObject], entity: SObject) {
    if (base != NullEntityEntity) {
      doe_w(_entity_ref(base.name, entities))(entity.base)
    }
  }

  private def _build_traits(entities: Map[String, SObject], entity: SObject) {
    for (tr <- traits) {
      doe_w(_entity_ref(tr.name, entities))(_ match {
        case dtrait: DomainTrait => entity.mixinTrait(dtrait)
        case entity => record_warning("%sはトレイト(特色)ではありません。", entity.name)
      })
    }
  }

  private def _build_powertypes(entities: Map[String, SObject], entity: SObject) {
    for (power <- powertypes) {
      record_trace("SMMEntityEntity#_build_powertypes: " + power.name)
      doe_w(_powertype_ref(power.powertypeType.name, entities))(p => {
        val r = entity.powertype(power.name, p, _dsl_multiplicity(power.multiplicity))
        r.isInheritancePowertype = power.isInheritancePowertype
      })
    }
  }

  private def _build_statemachines(entities: Map[String, SObject], entity: SObject) {
    for (sm <- statemachines) {
      doe_w(_statemachine_ref(sm.statemachineType.name, entities))(p => {
        entity.statemachine(sm.name, p)
      })
    }
    for (sm <- statemachineRelationships) {
      doe_w(_statemachine_ref(sm.associationType.name, entities))(p => {
        entity.statemachine(sm.name, p)
      })
    }
  }

  private def _build_roles(entities: Map[String, SObject], entity: SObject) {
    for (role <- roles) {
      doe_w(_role_ref(role.associationType.getName, entities))(r => {
        entity.role.create(role.name, r, _dsl_multiplicity(role.multiplicity), role.displaySequence)
      })
    }
  }

  private def _build_attributes(entity: SObject, entities: Map[String, SObject]) {
    for (attr <- attributes) {
      record_trace("SMMEntityEntity#_build_attributes(%s) = %s: %s".format(name, attr.name, attr.attributeType.getName))
//      println("SMMEntityEntity#_build_attributes(%s) = %s".format(name, attr.displaySequence))
      attr.attributeType.idType match {
        case Some(t) => {
//          entity.attribute_id.attributeType = _dsl_type(t)
          _build_attribute(entity.attribute_id, attr)
        }
        case _ => {
          _dsl_type(attr.attributeType, entities) match {
            case Some(t) => {
              val a = entity.attribute.create(attr.name, t, _dsl_multiplicity(attr.multiplicity), attr.displaySequence)
              _build_attribute(a, attr)
            }
            case None => {
              record_warning("「%s」の属性「%s」は型「%s」が解決できません。", entity.name, attr.name, attr.attributeType.name)
            }
          }
          /*
           * SimpleModel2JavaRealmTransformerBase converts SMAttributeType to PObjectType.
           */
//          println("SMMEntityEntity#_build_attributes(%s, %s)".format(a.name, a.attributeType.name))
        }
      }
    }
  }

/*
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
*/
  private def _build_attribute(attr: SAttribute, src: SMMAttribute) {
    _build_properties(attr, src)
    if (src.id) {
      attr kind_is IdAttributeKind
    }
    for (a <- _dsl_expression(src.deriveExpression)) {
      attr.deriveExpression = a
    }
    for (a <- _dsl_text(src.sqlColumnName)) {
      attr.sqlColumnName = a
    }
    for (a <- src.attributeType.getSqlDataTypeName) {
//      println("SMMEntityEntity#_build_attribute sqlDatatypeName = " + a)
      attr.sqlDatatypeName = a
    }
//    println("SMMEntityEntity#_build_attribute(%s/%s) = %s".format(name, attr.name, src.displaySequence))
    attr.displaySequence = src.displaySequence
  }

  private def _dsl_text(s: String): Option[String] = {
    if (UString.isNull(s)) None
    else Some(s)
  }

  private def _dsl_boolean(s: String): Option[Boolean] = {
    s.toLowerCase match {
      case "true" => Some(true)
      case "false" => Some(false)
      case "1" => Some(true)
      case "0" => Some(false)
      case _ => None
    }
  }

  private def _dsl_type(atype: SMMAttributeTypeSet, entities: Map[String, SObject]): Option[SAttributeType] = {
    atype.getAttributeType.flatMap(_dsl_type(_, entities))
  }

  private def _dsl_type(otype: SMMObjectType, entities: Map[String, SObject]): Option[SAttributeType] = {
    import StringUtils.isNotBlank
    def stubvalue(name: String, pkgname: String) = {
      val v = new SValue(name, pkgname)
      v.isMasterSingleton = false
      v
    }
    def doc(name: String, pkgname: String) = {
      require (isNotBlank(name), "document name must not be blank.")
      new SDocument(name, pkgname)
    }
    def entitydoc(e: SMMEntityType): Option[SDocument] = {
      require (isNotBlank(e.name), "entity name must not be blank.")
      _entity_ref(e.name, entities) match {
        case Right(r) => Some(new SEntityDocument(e.name, e.packageName))
        case Left(_) => None
      }
    }
    otype match {
      case v: SMMValueIdType => stubvalue(v.name, v.packageName).some
      case v: SMMValueType => stubvalue(v.name, v.packageName).some
      case _: SMMStringType => XString.some
      case _: SMMTokenType => XToken.some
      case _: SMMTextType => XText.some
      case _: SMMBooleanType => XBoolean.some
      case _: SMMByteType => XByte.some
      case _: SMMShortType => XShort.some
      case _: SMMIntType => XInt.some
      case _: SMMLongType => XLong.some
      case _: SMMFloatType => XFloat.some
      case _: SMMDoubleType => XDouble.some
      case _: SMMIntegerType => XInteger.some
      case _: SMMDecimalType => XDecimal.some
      case _: SMMDateType => XDate.some
      case _: SMMTimeType => XTime.some
      case _: SMMDateTimeType => XDateTime.some
      case _: SMMUriType => XAnyURI.some
      case _: SMMLinkType => XLink.some
      case _: SMMMoneyType => XMoney.some
      case _: SMMPercentType => XPercent.some
      case _: SMMUnitType => XUnit.some
      case _: SMMUuidType => XUuid.some
      case _: SMMEverforthidType => XEverforthid.some
      case _: SMMXmlType => XXml.some
      case _: SMMHtmlType => XHtml.some
      case v: SMMDocumentType => doc(v.name, v.packageName).some
      case e: SMMEntityType => entitydoc(e)
      case _ => None
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

  private def _dsl_expression(s: String): Option[SExpression] = {
    StringUtils.isNotBlank(s) option SExpression(s)
  }

  private def _build_associations(entity: SObject, entities: Map[String, SObject]) {
    for (assoc <- associations) {
      doe_w(_entity_ref(assoc.associationType.getName, entities)) { x =>
        record_trace("SMMEntityEntity#_build_associations: " + assoc.name)
        val a = entity.association.create(assoc.name, x, _dsl_multiplicity(assoc.multiplicity), assoc.kind, assoc.displaySequence)
        _build_specifications(a, assoc)
        _build_properties(a, assoc)
        a
      }
    }
  }

  private def _build_aggregations(entity: SObject, entities: Map[String, SObject]) {
    for (assoc <- aggregations) {
      doe_w(_entity_ref(assoc.associationType.getName, entities)) { x =>
        record_trace("SMMEntityEntity#_build_aggregations: " + assoc.name)
        val a = entity.aggregation(assoc.name, x, _dsl_multiplicity(assoc.multiplicity), assoc.displaySequence)
        _build_specifications(a, assoc)
        _build_properties(a, assoc)
        a
      }
    }
  }

  private def _build_compositions(entity: SObject, entities: Map[String, SObject]) {
    for (assoc <- compositions) {
      doe_w(_entity_ref(assoc.associationType.getName, entities)) { x =>
        record_trace("SMMEntityEntity#_build_compositions: " + assoc.name)
        val a = entity.composition(assoc.name, x, _dsl_multiplicity(assoc.multiplicity), assoc.displaySequence)
        _build_specifications(a, assoc)
        _build_properties(a, assoc)
        a
      }
    }
  }

  private def _entity_ref(name: Option[String], entities: Map[String, SObject]): Either[String, SEntity] = {
    name match {
      case Some(s) => _entity_ref(s, entities)
      case None => "エンティティ名がありません。(参照元: %s)".format(this.name).left
    }
  }

  private def _entity_ref(name: String, entities: Map[String, SObject]): Either[String, SEntity] = {
    entities.get(name) match {
      case Some(entity: SEntity) => entity.right
      case Some(x) => "エンティティに対してのみ関連・集約・合成を持つことができます。(参照元: %s, 参照先: %s)".format(this.name, name).left
      case None => Left("エンティティ「%s」はみつかりません。(参照元: %s)".format(name, this.name))
    }
  }

/*
  private def _entity_ref0(name: String, entities: Map[String, SObject]): Either[String, SEntity] = {
    entities.get(name) match {
      case Some(entity: SEntity) => entity.right
      case Some(obj: SObject) => "エンティティに対してのみ関連・集約・合成を持つことができます。(参照元: %s, 参照先: %s)".format(name, obj.name).left
      case Some(x) => sys.error("not sobject: " + x)
      case None => sys.error("unknown: " + name + " / " + entities)
    }
  }
*/
/*
  private def _trait_ref(name: String, entities: Map[String, SObject]): STrait = {
    println("_trait_ref(%s) = %s".format(name, entities))
    entities(name) match {
      case tr: STrait => tr
      case x => error("not trait: " + x)
    }
  }
*/

  private def _powertype_ref(name: Option[String], entities: Map[String, SObject]): Either[String, DomainPowertype] = {
    name match {
      case Some(s) => _powertype_ref(s, entities)
      case None => "パワータイプ(区分)名がありません。(参照元: %s)".format(this.name).left
    }
  }

  private def _powertype_ref(name: String, entities: Map[String, SObject]): Either[String, DomainPowertype] = {
    entities.get(name) match {
      case Some(p: DomainPowertype) => p.right
      case Some(x) => "「%s」はパワータイプ(区分)ではありません。(参照元: %s)".format(x.name, this.name).left
      case None => Left("パワータイプ(区分)「%s」はみつかりません。(参照元: %s)".format(name, this.name))
    }
  }


  private def _statemachine_ref(name: Option[String], entities: Map[String, SObject]): Either[String, DomainStateMachine] = {
    name match {
      case Some(s) => _statemachine_ref(s, entities)
      case None => "状態機械名がありません。(参照元: %s)".format(this.name).left
    }
  }

  private def _statemachine_ref(name: String, entities: Map[String, SObject]): Either[String, DomainStateMachine] = {
    entities.get(name) match {
      case Some(p: DomainStateMachine) => p.right
      case Some(x) => "「%s」は状態機械ではありません。(参照元: %s)".format(x.name, this.name).left
      case None => Left("状態機械「%s」はみつかりません。(参照元: %s)".format(name, this.name))

    }
  }

  private def _role_ref(name: Option[String], entities: Map[String, SObject]): Either[String, DomainRole] = {
    name match {
      case Some(s) => _role_ref(s, entities)
      case None => "ロール(役割)名がありません。(参照元: %s)".format(this.name).left
    }
  }

  private def _role_ref(name: String, entities: Map[String, SObject]): Either[String, DomainRole] = {
    entities.get(name) match {
      case Some(r: DomainRole) => r.right
      case Some(x) => "「%s」はロール(役割)ではありません。(参照元: %s)".format(x.name, this.name).left
      case None => Left("ロール(役割)「%s」はみつかりません。(参照元: %s)".format(name, this.name))
    }
  }

  private def _rule_ref(name: Option[String], entities: Map[String, SObject]): Either[String, DomainRule] = {
    name match {
      case Some(s) => _rule_ref(s, entities)
      case None => "規則名がありません。(参照元: %s)".format(this.name).left
    }
  }

  private def _rule_ref(name: String, entities: Map[String, SObject]): Either[String, DomainRule] = {
    entities.get(name) match {
      case Some(r: DomainRule) => r.right
      case Some(x) => "「%s」は規則ではありません。(参照元: %s)".format(x.name, this.name).left
      case None => Left("規則「%s」はみつかりません。(参照元: %s)".format(name, this.name))
    }
  }

  private def _service_ref(name: Option[String], entities: Map[String, SObject]): Either[String, DomainService] = {
    name match {
      case Some(s) => _service_ref(s, entities)
      case None => "サービス名がありません。(参照元: %s)".format(this.name).left
    }
  }

  private def _service_ref(name: String, entities: Map[String, SObject]): Either[String, DomainService] = {
//    println("SMMEntityEntity#_service_ref(%s) = %s / %s".format(this.name, name, entities))
    entities.get(name) match {
      case Some(r: DomainService) => r.right
      case Some(x) => "「%s」はサービスではありません。(参照元: %s)".format(x.name, this.name).left
      case None => Left("サービス「%s」はみつかりません。(参照元: %s)".format(name, this.name))
    }
  }

  private def _build_statemachineStates(entity: SObject) {
    // TODO
  }

  private def _build_operations(entity: SObject, entities: Map[String, SObject]) {
    for (op <- operations) {
      val in = _dsl_type(op.inType, entities)
      val out = _dsl_type(op.outType, entities)
      val a = entity.operation(op.name, in, out)
      record_trace("SMMEntityEntity#_build_operations(%s, %s, %s)".format(op.name, in, out))
      _build_operation(a, op)
    }
  }

  private def _build_operation(attr: SOperation, src: SMMOperation) {
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
  }

  private def _build_value(value: DomainValue): DomainValue = {
    value.term = name
    _build_specifications(value)
    _build_properties(value)
//    _build_base(value)
//    _build_powertypes(value)
//    _build_roles(value)
    _build_attributes(value, Map.empty)
    value
  }

  private def _build_document(doc: DomainDocument, entities: Map[String, SObject]): DomainDocument = {
    _build_specifications(doc)
    _build_properties(doc)
    _build_base(entities, doc)
    _build_traits(entities, doc)
    _build_attributes(doc, entities)
    _build_associations(doc, entities)
    _build_aggregations(doc, entities)
    _build_compositions(doc, entities)
    doc
  }

  private def _build_powertype(power: DomainPowertype, entities: Map[String, SObject]): DomainPowertype = {
    _build_specifications(power)
    _build_properties(power)
    _build_base(entities, power)
    _build_traits(entities, power)
    _build_powertypeKinds(power)
    _build_attributes(power, entities)
    _build_associations(power, entities)
    _build_aggregations(power, entities)
    _build_compositions(power, entities)
    power
  }

  private def _build_powertypeKinds(entity: DomainPowertype) {
    for (kind <- powertypeKinds) {
      val k = new SPowertypeKind(kind.name, kind.value)
      _build_specifications(k, kind)
      _build_properties(k, kind)
      entity.kind(k)
    }
  }

  private def _build_statemachine(sm: DomainStateMachine, entities: Map[String, SObject]): DomainStateMachine = {
    _build_specifications(sm)
    _build_properties(sm)
    _build_base(entities, sm)
    _build_traits(entities, sm)
    _build_statemachineStates(sm)
    _build_attributes(sm, entities)
    _build_associations(sm, entities)
    _build_aggregations(sm, entities)
    _build_compositions(sm, entities)
    sm
  }

  private def _build_statemachineStates(entity: DomainStateMachine) {
    for (state <- statemachineStates) {
      val s = new DomainState(state.name, state.value, state.lifecycle)
      _build_specifications(s, state)
      _build_properties(s, state)
//      println("SMMEntityEntity#_build_statemachineStates: %s, %s, %s".format(s.name, s.value, s.label))
      entity.state(s)
    }
  }

  private def _build_rules(entity: SObject, entities: Map[String, SObject]) {
    for (rel <- rules) {
      doe_w(_rule_ref(rel.name, entities)) { x =>
        record_trace("SMMEntityEntity#_build_rules: " + rel.name)
        entity.rule(rel.name, x)
      }
    }
  }

  private def _build_rule(srv: DomainRule, entities: Map[String, SObject]): DomainRule = {
    _build_specifications(srv)
    _build_base(entities, srv)
    _build_traits(entities, srv)
    _build_attributes(srv, entities)
    _build_associations(srv, entities)
    _build_aggregations(srv, entities)
    _build_compositions(srv, entities)
    _build_operations(srv, entities)
    srv
  }

  private def _build_services(entity: SObject, entities: Map[String, SObject]) {
    for (rel <- services) {
      doe_w(_service_ref(rel.name, entities)) { x =>
        record_trace("SMMEntityEntity#_build_services: " + rel.name)
        entity.service(rel.name, x)
      }
    }
  }

  private def _build_service(srv: DomainService, entities: Map[String, SObject]): DomainService = {
    _build_specifications(srv)
    _build_base(entities, srv)
    _build_traits(entities, srv)
    _build_attributes(srv, entities)
    _build_associations(srv, entities)
    _build_aggregations(srv, entities)
    _build_compositions(srv, entities)
    _build_operations(srv, entities)
    srv
  }

  private def _build_businessusecase(entities: Map[String, SObject], uc: BusinessUsecase) {
    _build_story_common(entities, uc)
  }

  private def _build_businesstask(entities: Map[String, SObject], uc: BusinessTask) {
    _build_story_common(entities, uc)
  }

  private def _build_usecase(entities: Map[String, SObject], uc: RequirementUsecase) {
    _build_story_common(entities, uc)
  }

  private def _build_task(entities: Map[String, SObject], uc: RequirementTask) {
    _build_story_common(entities, uc)
  }

  private def _build_story_common(entities: Map[String, SObject], uc: SStoryObject) {
    uc.term = name
//    _build_specifications(value)
//    _build_base(value)
//    _build_powertypes(value)
//    _build_roles(value)
    _build_attributes(uc, entities)
    _build_associations(uc, entities)
    _build_aggregations(uc, entities)
    _build_compositions(uc, entities)
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
    _build_attributes(dr, entities)
//    _build_associations(dr, entities)
//    _build_aggregations(dr, entities)
//    _build_compositions(dr, entities)
    dr
  }

  private def _describe_association(entities: Map[String, SObject], set: (String, SEntity, SMultiplicity, SAssociationKind, Int) => SAssociation, assoc: SMMAssociation) {
    doe_w(_entity_ref(assoc.associationType.getName, entities)) { 
      set(assoc.name, _, _dsl_multiplicity(assoc.multiplicity), assoc.kind, assoc.displaySequence)
    }
  }

  private def _describe_event_issue(entities: Map[String, SObject], uc: SStoryObject, step: SMMAssociation) {
    doe_w(_entity_ref(step.associationType.getName, entities)) {
      case event: DomainEvent => {
        record_trace("SMMEntityEntity#_describe_event_issue(%s) = %s / %s = %s".format(name, step.name, step.associationType.getName, event))
        uc.event_issue(event)()
      }
      case story: BusinessUsecase => {
        record_trace("SMMEntityEntity#_describe_event_issue(%s) = %s / %s = %s".format(name, step.name, step.associationType.getName, story))
        uc.include_business_usecase(story)
      }
      case story: RequirementUsecase => {
        record_trace("SMMEntityEntity#_describe_event_issue(%s) = %s / %s = %s".format(name, step.name, step.associationType.getName, story))
        uc.include_requirement_usecase(story)
      }
      case story: BusinessTask => {
        record_trace("SMMEntityEntity#_describe_event_issue(%s) = %s / %s = %s".format(name, step.name, step.associationType.getName, story))
        uc.include_business_task(story)
      }
      case story: RequirementTask => {
        record_trace("SMMEntityEntity#_describe_event_issue(%s) = %s / %s = %s".format(name, step.name, step.associationType.getName, story))
        uc.include_requirement_task(story)
      }
      case x => {
        record_warning("SMMEntityEntity#_describe_event_issue: " + x)
      }
    }
  }

  private def _build_displays(entity: SObject) {
    entity.displays ++= displays
  }

  private def _build_actions(entity: SObject) {
    entity.actions ++= actions
  }
}

object NullEntityEntity extends SMMEntityEntity(NullEntityContext) {
  override def effectiveAttributes() = Nil
}
