package org.simplemodeling.SimpleModeler.builder

import com.asamioffice.goldenport.text.UString
import scalaz._
import Scalaz._
import org.goldenport.entities.csv._
import org.goldenport.value._
import org.goldenport.sdoc.SDoc
import org.simplemodeling.dsl.util.PropertyRecord
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.apache.commons.lang3.StringUtils.isNotBlank

/*
 * TODO refactors a relation with TabularBuilderBase.
 * 
 * @since   Mar.  6, 2012
 *  version Mar. 25, 2012
 *  version Sep. 30, 2012
 *  version Oct. 30, 2012
 *  version Nov. 26, 2012
 * @version Dec. 13, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * OutlineBuilderBase uses this class to build SimpleModel from tables.
 */
class TableSimpleModelMakerBuilder(
  builder: SimpleModelMakerBuilder,
  policy: Policy, packageName: String
) extends TabularBuilderBase(policy, packageName) {
  val model_Builder = builder

  override protected def build_Model {}

  /**
   * OutlineBuilderBase uses the methed.
   */
  def createObjects(kind: ElementKind, table: GTable[String]): List[SMMEntityEntity] = {
    (for (h <- table.headAsStringList) yield {
      val rows: Seq[List[(String, String)]] = for (row <- table.rows) yield {
        h.zip(row)
      }
      rows.map(entry => createObject(kind, PropertyRecord.create(entry))).toList
    }) getOrElse Nil
  }

  private def _object_kind(kind: ElementKind, entry: Seq[PropertyRecord]): ElementKind = {
    NaturalLabel.getObjectKind(entry) | kind
  }

  private def _object_name(entry: Seq[PropertyRecord]): String = {
    NaturalLabel.getObjectName(entry) match {
      case Some(s) => s
      case None => "Unknown"
    }
  }
/*
  private def _kind(kind: ElementKind, entry: Seq[(String, String)]): ElementKind = {
    entry.find(NaturalLabel.isKindKey).flatMap(t => NaturalLabel(t._2) match {
      case ActorLabel => ActorKind.some
      case ResourceLabel => ResourceKind.some
      case EventLabel => EventKind.some
      case RoleLabel => RoleKind.some
      case RuleLabel => RuleKind.some
      case UsecaseLabel => UsecaseKind.some
      case _ => none
    }) | kind
  }

  private def _name(entry: Seq[(String, String)]): String = {
    entry.find(NaturalLabel.isNameKey).map(_._2).get
  }
*/

  def createObject(kind: ElementKind, entry: Seq[PropertyRecord]): SMMEntityEntity = {
    val obj = model_Builder.createObject(_object_kind(kind, entry), _object_name(entry))
    build_object(obj, entry)
    obj
  }

  protected final def build_object(obj: SMMEntityEntity, entry: Seq[PropertyRecord]) {
    var is_derived = false
    for (r <- entry) {
      val (key, value) = r.toTuple
      NaturalLabel(key) match {
        case NameLabel => {}
        case NameJaLabel => obj.name_ja = value
        case NameEnLabel => obj.name_en = value
        case TermLabel => obj.term = value
        case TermJaLabel => obj.term_ja = value
        case TermEnLabel => obj.term_en = value
        case CaptionLabel => obj.caption = value
        case BriefLabel => obj.brief = value
        case SummaryLabel => obj.summary = value
        case DescriptionLabel => obj.description = value
        case TableNameLabel => obj.tableName = value
        case AttributeLabel => value.split("[;, ]+").foreach(obj.addNarrativeAttribute)
        case AggregationLabel => value.split("[;, ]+").foreach(obj.addNarrativePart)
        case BaseLabel => {
          obj.setNarrativeBase(value)
          is_derived = true
        }
        case PowertypeLabel => {
          make_items(value).foreach(obj.addNarrativePowertype)
        }
        case StateLabel => {
          make_items(value).foreach(obj.addNarrativeStateMachine)
        }
        case RoleLabel => value.split("[;, ]+").foreach(obj.addNarrativeRole)
        case _ => {}
      }
    }
  }
  
//  private def _default_columns = List("name")

//  private def _columns(head: Option[List[String]]): List[String] = {
//    head | _default_columns 
//  }

  /*
   * build
   */
  /**
   * OutlineBuilderBase uses the method.
   */
  def buildAttribute(entity: SMMEntityEntity, table: GTable[String]) {
//    println("buildAttributeTable:" + table)
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_attribute(entity, PropertyRecord.create(entry)))
      entity.adjustAttributes // XXX more upper position.
    }
  }

  protected final def add_attribute(obj: SMMEntityEntity, entry: Seq[PropertyRecord]) = {
    _slot_kind(entry) match {
      case IdLabel => add_id(obj, entry)
      case _ => {
        val atype = SMMAttributeTypeSet(entry, obj.packageName)
        val attr = obj.attribute(_slot_name(entry), atype)
        _build_attribute(attr, entry)
      }
    }
  }

  protected final def add_id(obj: SMMEntityEntity, entry: Seq[PropertyRecord]) = {
//    println("TableSimpleModelMakerBuilder#add_id:" + entry)
    val atype = SMMAttributeTypeSet(entry, obj.packageName)
    val attr = obj.attribute(_slot_name(entry), atype, true)
    _build_attribute(attr, entry)
  }

  private def _build_attribute(attr: SMMAttribute, entry: Seq[PropertyRecord]) = {
    _build_slot(attr, entry)
    for (r <- entry) {
      val (key, value) = r.toTuple
      NaturalLabel(key) match {
        case DeriveLabel => {
          attr.deriveExpression = value
//          println("TableSimpleModelMakerBuilder: derive = " + attr.deriveExpression)
        }
        case x => ;
      }
    }
    attr
  }

  private def _build_slot(slot: SMMSlot, entry: Seq[PropertyRecord]) = {
    for (r <- entry) {
//      println("TableSimpleModelMakerBuilder: %s => %s".format(key, NaturalLabel(key)))
      val (key, value) = r.toTuple
      println("TableSimpleModelMakerBuilder#_build_slot = " + key)
      NaturalLabel(key) match {
        case NameLabel => {}
        case TypeLabel => {}
        case DatatypeLabel => {}
        case ObjecttypeLabel => {}
        case MultiplicityLabel => slot.multiplicity = GRMultiplicity(value)
        case NameJaLabel => slot.name_ja = value
        case NameEnLabel => slot.name_en = value
        case TermLabel => slot.term = value
        case TermJaLabel => slot.term_ja = value
        case TermEnLabel => slot.term_en = value
        case TitleLabel => slot.title = value
        case SubtitleLabel => slot.subtitle = value
        case LabelLabel => slot.label = value
        case CaptionLabel => slot.caption = value
        case BriefLabel => slot.brief = value
        case SummaryLabel => slot.summary = value
        case DescriptionLabel => slot.description = value
        case ColumnNameLabel => slot.sqlColumnName = value
        case SqlDatatypeLabel => slot.sqlDatatype = SMMObjectType.getSqlDataType(value)
        case SqlAutoIdLabel => slot.sqlAutoId = value
        case SqlReadOnlyLabel => slot.sqlReadOnly = value
        case SqlAutoCreateLabel => slot.sqlAutoCreate = value
        case SqlAutoUpdateLabel => slot.sqlAutoUpdate = value
        case SqlPropertyLabel => _build_slot_property(slot, value)
        case x => {println("TableSimpleModelMakerBuilder: " + x)}
      }
    }
    slot
  }

  private def _build_slot_property(slot: SMMSlot, value: String) {
    for (v <- UString.getTokens(value, ",")) {
      println("TableSimpleModelMakerBuilder#_build_slot_property(%s) = %s".format(value, v))
      NaturalLabel(v) match {
        case SqlAutoIdLabel => slot.sqlAutoId = "true"
        case SqlReadOnlyLabel => slot.sqlReadOnly = "true"
        case SqlAutoCreateLabel => slot.sqlAutoCreate = "true"
        case SqlAutoUpdateLabel => slot.sqlAutoUpdate = "true"
      }
    }
  }

/*
  private def _value_data_type(entry: Seq[PropertyRecord]): SMMValueDataType = {
    NaturalLabel.getObjectTypeName(entry).map(SMMObjectType.getOrUnkonwn) | SMMStringType
  }

  private def _object_type(entry: Seq[PropertyRecord]): SMMObjectType = {
    val a = entry.find(f => is_type_field(f._1)).flatMap(t => SMMObjectType.get(t._2))
    a | SMMStringType
  }

  protected def is_type_field(string: String): Boolean = {
    NaturalLabel.isDatatype(string)
  }
*/

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildProperty(entity: SMMEntityEntity, table: GTable[String]) {
    val rows = for (row <- table.rows) yield {
      if (row.isEmpty) ("", "", Nil)
      else if (row.length == 1) (row.head, "", Nil)
      else (row(0), row(1), row.drop(2))
    }
    rows.map(entry => add_property(entity, entry))
  }

  protected final def add_property(entity: SMMEntityEntity, entry: (String, String, Seq[String])) {
    _property_kind(entry).collect {
      case BaseLabel => add_base(entity, entry)
      case TraitLabel => add_traits(entity, entry)
      case TableNameLabel => add_table_name(entity, entry)
      case PrimaryActorLabel => add_primary_actor(entity, entry)
      case SecondaryActorLabel => add_secondary_actor(entity, entry)
      case SupportingActorLabel => add_supporting_actor(entity, entry)
      case ActorLabel => add_primary_actor(entity, entry)
      case GoalLabel => add_goal(entity, entry)
    }
  }

  private def _property_kind(entry: (String, String, Seq[String])): Option[NaturalLabel] = {
    NaturalLabel.getLabel(entry._1)
  }

  protected final def add_base(entity: SMMEntityEntity, entry: (String, String, Seq[String])) {
    _set_property_one(entry, x => {
      entity.narrativeBase = x
    })
  }

  protected final def add_traits(entity: SMMEntityEntity, entry: (String, String, Seq[String])) {
    _set_property_seq(entry, xs => {
      xs.foreach(entity.addNarrativeTrait)
    })
  }

  protected final def add_table_name(entity: SMMEntityEntity, entry: (String, String, Seq[String])) {
    _set_property_one(entry, x => {
      entity.tableName = x
    })
  }

  protected final def add_goal(entity: SMMEntityEntity, entry: (String, String, Seq[String])) {
    _set_property_seq(entry, x => {
      entity.narrativeGoals ++= x
    })
  }

  protected final def add_primary_actor(entity: SMMEntityEntity, entry: (String, String, Seq[String])) {
    _set_property_seq(entry, x => {
      entity.narrativePrimaryActors ++= x
    })
  }


  protected final def add_secondary_actor(entity: SMMEntityEntity, entry: (String, String, Seq[String])) {
    _set_property_seq(entry, x => {
      entity.narrativeSecondaryActors ++= x
    })
  }

  protected final def add_supporting_actor(entity: SMMEntityEntity, entry: (String, String, Seq[String])) {
    _set_property_seq(entry, x => {
      entity.narrativeSupportingActors ++= x
    })
  }

  private def _set_property_one(entry: (String, String, Seq[String]),
                                f: String => Unit) {
    if (isNotBlank(entry._2)) f(entry._2.trim)
  }

  private def _set_property_seq(entry: (String, String, Seq[String]),
                                f: Seq[String] => Unit) {
    if (isNotBlank(entry._2)) {
      f(entry._2.split("[ ,;]+").map(_.trim).filter(isNotBlank))
    }
  }

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildFeature(entity: SMMEntityEntity, table: GTable[String]) {
//    record_trace("TableSimpleModelMakerBuilder#buildFeature(%s) = %s/%s/%s".format(entity.name, table.width, table.height, table.head))
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_feature(entity, _record(entry)))
    }
  }

  private def _record(t: Seq[(String, String)]): Seq[PropertyRecord] = {
    t.map(PropertyRecord.create)
  }

  protected final def add_feature(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) {
    _slot_kind(entry) match {
      case IdLabel => add_id(entity, entry)
      case AttributeLabel => add_attribute(entity, entry)
      case CompositionLabel => add_composition(entity, entry)
      case AggregationLabel => add_aggregation(entity, entry)
      case AssociationLabel => add_association(entity, entry)
      case PowertypeLabel => add_powertype(entity, entry)
      case StateMachineLabel => add_statemachine(entity, entry)
      case UnknownNaturalLabel => {
        val name = _slot_name(entry)
        println("TableSimpleModelMakerBuilder#add_feature(%s) = %s".format(entity.name, entry))
        if (entity.isAttribute(name)) {
          add_attribute(entity, entry)          
        } else if (entity.isComposition(name)) {
          add_composition(entity, entry)
        } else if (entity.isAggregation(name)) {
          add_aggregation(entity, entry)
        } else if (entity.isAssociation(name)) {
          add_association(entity, entry)
        } else if (entity.isPowertype(name)) {
          add_powertype(entity, entry)
        } else if (entity.isStateMachine(name)) {
          add_statemachine(entity, entry)
        } else {
          add_attribute(entity, entry)
        }
      }
      case _ => sys.error("???")
    }
  }

  private def _slot_name(entry: Seq[PropertyRecord]): String = {
    NaturalLabel.getSlotName(entry) match {
      case Some(s) => s
      case None => "Unknown"
    }
  }

  private def _slot_kind(entry: Seq[PropertyRecord]): NaturalLabel = {
    val feature = FeatureLabel.findData(entry)
    feature.collect {
      case NaturalLabel(label) => label
    } orElse {
      IdLabel.findData(entry).flatMap(x => {
        isNotBlank(x).option(IdLabel)
      })
    } getOrElse UnknownNaturalLabel
  }

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildComposition(entity: SMMEntityEntity, table: GTable[String]) {
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_composition(entity, _record(entry)))
    }
  }

  protected final def add_composition(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) = {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    val assoc = entity.composition(_slot_name(entry), entitytype)
    _build_association(assoc, entry)
  }

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildAggregation(entity: SMMEntityEntity, table: GTable[String]) {
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_aggregation(entity, _record(entry)))
    }
  }

  protected final def add_aggregation(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) = {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    val assoc = entity.aggregation(_slot_name(entry), entitytype)
    _build_association(assoc, entry)
  }

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildAssociation(entity: SMMEntityEntity, table: GTable[String]) {
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_association(entity, _record(entry)))
    }
  }

  protected final def add_association(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) = {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    val assoc = entity.association(_slot_name(entry), entitytype)
    _build_association(assoc, entry)
  }

  private def _build_association(assoc: SMMAssociation, entry: Seq[PropertyRecord]) = {
    _build_slot(assoc, entry)
  }

/*
  private def _build_association0(assoc: SMMAssociation, entry: Seq[PropertyRecord]) {
    for (r <- entry) {
      val (key, value) = r.toTuple
      NaturalLabel(key) match {
        case NameLabel => {}
        case TypeLabel => {}
        case DatatypeLabel => {}
        case ObjecttypeLabel => {}
        case MultiplicityLabel => assoc.multiplicity = GRMultiplicity(value)
        case NameJaLabel => assoc.name_ja = value
        case NameEnLabel => assoc.name_en = value
        case TermLabel => assoc.term = value
        case TermJaLabel => assoc.term_ja = value
        case TermEnLabel => assoc.term_en = value
        case TitleLabel => assoc.title = value
        case SubtitleLabel => assoc.subtitle = value
        case LabelLabel => assoc.label = value
        case CaptionLabel => assoc.caption = value
        case BriefLabel => assoc.brief = value
        case SummaryLabel => assoc.summary = value
        case DescriptionLabel => assoc.description = value
        case ColumnNameLabel => assoc.sqlColumnName = value
        
        case SqlDatatypeLabel => {}
        case _ => {}
      }
    }
  }
*/

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildDisplay(entity: SMMEntityEntity, table: GTable[String]) {
//    record_trace("TableSimpleModelMakerBuilder#buildDisplay(%s) = %s/%s/%s".format(entity.name, table.width, table.height, table.head))
    for (h <- table.headAsStringList) {
      val counter = Stream.range(100, 100000, 100)
      var c = 100
      val rows = for (row <- table.rows) yield h.zip(row)
      for ((entry, n) <- rows zip counter) {
        add_display(entity, _record(entry), c)
        c += 100
      }
    }
  }

  protected final def add_display(entity: SMMEntityEntity, entry: Seq[PropertyRecord], seq: Int) {
    entity.display(_slot_name(entry), seq, entry)
  }

  protected final def add_display0(entity: SMMEntityEntity, entry: Seq[PropertyRecord], seq: Int) {
    val slot: SMMSlot = _slot_kind(entry) match {
      case IdLabel => add_id(entity, entry)
      case AttributeLabel => add_attribute(entity, entry)
      case CompositionLabel => add_composition(entity, entry)
      case AggregationLabel => add_aggregation(entity, entry)
      case AssociationLabel => add_association(entity, entry)
      case PowertypeLabel => add_powertype(entity, entry)
      case StateMachineLabel => add_statemachine(entity, entry)
      case UnknownNaturalLabel => {
        val name = _slot_name(entry)
        println("TableSimpleModelMakerBuilder#add_display(%s) = %s".format(entity.name, entry))
        if (entity.isAttribute(name)) {
          add_attribute(entity, entry)          
        } else if (entity.isComposition(name)) {
          add_composition(entity, entry)
        } else if (entity.isAggregation(name)) {
          add_aggregation(entity, entry)
        } else if (entity.isAssociation(name)) {
          add_association(entity, entry)
        } else if (entity.isPowertype(name)) {
          add_powertype(entity, entry)
        } else if (entity.isStateMachine(name)) {
          add_statemachine(entity, entry)
        } else {
          add_attribute(entity, entry)
        }
      }
      case _ => sys.error("???")
    }
    slot.displaySequence = seq
    println("TableSimpleModelMakerBuilder#add_display(%s) = %s / %s".format(entity.name, slot.name, slot.displaySequence))
  }

  /*
   * Builds Slots
   */
  /**
   * OutlineBuilderBase uses the method.
   */
  def buildOperation(entity: SMMEntityEntity, table: GTable[String]) {
    println("buildOperationTable:" + table)
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_operation(entity, _record(entry)))
    }
  }

  protected final def add_operation(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) {
    val in = SMMAttributeTypeSet.in(entry, entity.packageName)
    val out = SMMAttributeTypeSet.out(entry, entity.packageName)
    val op = entity.operation(_slot_name(entry), in, out)
  }

  /*
   * Special objects
   */
  /**
   * OutlineBuilderBase uses the method.
   */
  def buildPowertype(entity: SMMEntityEntity, table: GTable[String]) {
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_powertype(entity, _record(entry)))
    }
  }

  protected final def add_powertype(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) = {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    entity.powertype(_slot_name(entry), entitytype)
  }

  /**
   * OutlineBuilderBase uses the method.
   * An entity of SMMEntityEntity should be Powertype.
   */
  def buildPowertypeKind(entity: SMMEntityEntity, table: GTable[String]) {
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_powertypekind(entity, PropertyRecord.create(entry)))
    }
  }

  protected final def add_powertypekind(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) {
    val k = SMMPowertypeKind.create(entry)
//    record_trace("TableSimpleModelMakerBuilder#add_powertypekind(%s) = %s".format(entity.name, k))
    entity.powertypeKinds += k
  }

  /**
   * OutlineBuilderBase uses the method. (XXX future)
   */
  def buildStateMachine(entity: SMMEntityEntity, table: GTable[String]) {
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_statemachine(entity, _record(entry)))
    }
  }

  /**
   * Used by add_feature.
   */
  protected final def add_statemachine(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) = {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    entity.statemachine(_slot_name(entry), entitytype)
  }

  /**
   * OutlineBuilderBase uses the method.
   * An entity of SMMEntityEntity should be StateMachine.
   */
  def buildState(entity: SMMEntityEntity, table: GTable[String]) {
    for (h <- table.headAsStringList) {
      val rows = for (row <- table.rows) yield h.zip(row)
      rows.map(entry => add_state(entity, PropertyRecord.create(entry)))
    }
  }

  protected final def add_state(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) {
    val s = SMMStateMachineState.create(entry)
    entity.statemachineStates += s
  }

  /**
   * OutlineBuilderBase uses the method.
   */
/*
  def buildDocument(entity: SMMEntityEntity, table: GTable[String]) {
    val rows = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => add_document(entity, entry))
  }

  protected final def add_document(entity: SMMEntityEntity, entry: Seq[PropertyRecord]) {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    val assoc = entity.document(_slot_name(entry), entitytype)
  }
*/
}

