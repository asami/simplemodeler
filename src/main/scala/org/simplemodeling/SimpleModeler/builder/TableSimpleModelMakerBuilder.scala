package org.simplemodeling.SimpleModeler.builder

import scalaz._
import Scalaz._
import org.goldenport.entities.csv._
import org.goldenport.value._
import org.goldenport.sdoc.SDoc
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.apache.commons.lang3.StringUtils.isNotBlank

/*
 * TODO refactors a relation with TabularBuilderBase.
 * 
 * @since   Mar.  6, 2012
 *  version Mar. 25, 2012
 *  version Sep. 30, 2012
 *  version Oct. 30, 2012
 * @version Nov.  4, 2012
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
    val rows: Seq[List[(String, String)]] = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => createObject(kind, entry)).toList
  }

  private def _object_kind(kind: ElementKind, entry: Seq[(String, String)]): ElementKind = {
    NaturalLabel.getObjectKind(entry) | kind
  }

  private def _object_name(entry: Seq[(String, String)]): String = {
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

  def createObject(kind: ElementKind, entry: Seq[(String, String)]): SMMEntityEntity = {
    val obj = model_Builder.createObject(_object_kind(kind, entry), _object_name(entry))
    build_object(obj, entry)
    obj
  }

  protected final def build_object(obj: SMMEntityEntity, entry: Seq[(String, String)]) {
    var is_derived = false
    for ((key, value) <- entry) {
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
  
  private def _default_columns = List("name")

  private def _columns(head: Option[List[String]]): List[String] = {
    head | _default_columns 
  }

  /*
   * build
   */
  /**
   * OutlineBuilderBase uses the method.
   */
  def buildAttribute(entity: SMMEntityEntity, table: GTable[String]) {
//    println("buildAttributeTable:" + table)
    val rows = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => add_attribute(entity, entry))
    entity.adjustAttributes // XXX more upper position.
  }

  protected final def add_attribute(obj: SMMEntityEntity, entry: Seq[(String, String)]) {
    _slot_kind(entry) match {
      case IdLabel => add_id(obj, entry)
      case _ => {
        val atype = SMMAttributeTypeSet(entry)
        val attr = obj.attribute(_slot_name(entry), atype)
        _build_attribute(attr, entry)
      }
    }
  }

  protected final def add_id(obj: SMMEntityEntity, entry: Seq[(String, String)]) {
//    println("TableSimpleModelMakerBuilder#add_id:" + entry)
    val atype = SMMAttributeTypeSet(entry)
    val attr = obj.attribute(_slot_name(entry), atype, true)
    _build_attribute(attr, entry)
  }

  private def _build_attribute(attr: SMMAttribute, entry: Seq[(String, String)]) {
    for ((key, value) <- entry) {
//      println("TableSimpleModelMakerBuilder: %s => %s".format(key, NaturalLabel(key)))
      NaturalLabel(key) match {
        case NameLabel => {}
        case TypeLabel => {}
        case DatatypeLabel => {}
        case ObjecttypeLabel => {}
        case MultiplicityLabel => attr.multiplicity = GRMultiplicity(value)
        case NameJaLabel => attr.name_ja = value
        case NameEnLabel => attr.name_en = value
        case TermLabel => attr.term = value
        case TermJaLabel => attr.term_ja = value
        case TermEnLabel => attr.term_en = value
        case TitleLabel => attr.title = value
        case SubtitleLabel => attr.subtitle = value
        case CaptionLabel => attr.caption = value
        case BriefLabel => attr.brief = value
        case SummaryLabel => attr.brief = value
        case DescriptionLabel => attr.description = value
        case DeriveLabel => {
          attr.deriveExpression = value
          println("TableSimpleModelMakerBuilder: derive = " + attr.deriveExpression)
        }
        case ColumnNameLabel => attr.columnName = value
        case SqlDatatypeLabel => attr.sqlDatatype = SMMObjectType.getSqlDataType(value)
        case x => {println("TableSimpleModelMakerBuilder: " + x)}
      }
    }
  }

/*
  private def _value_data_type(entry: Seq[(String, String)]): SMMValueDataType = {
    NaturalLabel.getObjectTypeName(entry).map(SMMObjectType.getOrUnkonwn) | SMMStringType
  }

  private def _object_type(entry: Seq[(String, String)]): SMMObjectType = {
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
    val rows = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => add_feature(entity, entry))
  }

  protected final def add_feature(entity: SMMEntityEntity, entry: Seq[(String, String)]) {
    _slot_kind(entry) match {
      case IdLabel => add_id(entity, entry)
      case AttributeLabel => add_attribute(entity, entry)
      case CompositionLabel => add_composition(entity, entry)
      case AggregationLabel => add_aggregation(entity, entry)
      case AssociationLabel => add_association(entity, entry)
      case PowertypeLabel => add_powertype(entity, entry)
      case _ => add_attribute(entity, entry)
    }
  }

  private def _slot_name(entry: Seq[(String, String)]): String = {
    NaturalLabel.getSlotName(entry) match {
      case Some(s) => s
      case None => "Unknown"
    }
  }

  private def _slot_kind(entry: Seq[(String, String)]): NaturalLabel = {
    val feature = FeatureLabel.find(entry)
    feature.collect {
      case NaturalLabel(label) => label
    } orElse {
      IdLabel.find(entry).flatMap(x => {
        isNotBlank(x).option(IdLabel)
      })
    } getOrElse AttributeLabel
  }

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildComposition(entity: SMMEntityEntity, table: GTable[String]) {
    val rows = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => add_composition(entity, entry))
  }

  protected final def add_composition(entity: SMMEntityEntity, entry: Seq[(String, String)]) {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    val assoc = entity.composition(_slot_name(entry), entitytype)
    _build_association(assoc, entry)
  }

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildAggregation(entity: SMMEntityEntity, table: GTable[String]) {
    val rows = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => add_aggregation(entity, entry))
  }

  protected final def add_aggregation(entity: SMMEntityEntity, entry: Seq[(String, String)]) {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    val assoc = entity.aggregation(_slot_name(entry), entitytype)
    _build_association(assoc, entry)
  }

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildAssociation(entity: SMMEntityEntity, table: GTable[String]) {
    val rows = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => add_association(entity, entry))
  }

  protected final def add_association(entity: SMMEntityEntity, entry: Seq[(String, String)]) {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    val assoc = entity.association(_slot_name(entry), entitytype)
    _build_association(assoc, entry)
  }

  private def _build_association(assoc: SMMAssociation, entry: Seq[(String, String)]) {
    for ((key, value) <- entry) {
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
        case CaptionLabel => assoc.caption = value
        case BriefLabel => assoc.brief = value
        case SummaryLabel => assoc.brief = value
        case DescriptionLabel => assoc.description = value
        case ColumnNameLabel => assoc.columnName = value
        case SqlDatatypeLabel => {}
        case _ => {}
      }
    }
  }

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildPowertype(entity: SMMEntityEntity, table: GTable[String]) {
    val rows = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => add_powertype(entity, entry))
  }

  protected final def add_powertype(entity: SMMEntityEntity, entry: Seq[(String, String)]) {
    val entitytype = SMMEntityTypeSet(entity.packageName, entry)
    val assoc = entity.powertype(_slot_name(entry), entitytype)
  }
}
