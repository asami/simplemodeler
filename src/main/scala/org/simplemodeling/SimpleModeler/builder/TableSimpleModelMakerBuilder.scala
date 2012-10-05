package org.simplemodeling.SimpleModeler.builder

import scalaz._
import Scalaz._
import org.goldenport.entities.csv._
import org.goldenport.value._
import org.goldenport.sdoc.SDoc
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/*
 * TODO refactors a relation with TabularBuilderBase.
 * 
 * @since   Mar.  6, 2012
 *  version Mar. 25, 2012
 *  version Sep. 30, 2012
 * @version Oct.  5, 2012
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

  def createObject(kind: ElementKind, entry: Seq[(String, String)]): SMMEntityEntity = {
    val obj = model_Builder.createObject(_kind(kind, entry), _name(entry))
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

  /**
   * OutlineBuilderBase uses the method.
   */
  def buildAttribute(entity: SMMEntityEntity, table: GTable[String]) {
    println("buildAttribute:" + table)
    val rows = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => add_attribute(entity, entry))
  }

  protected final def add_attribute(obj: SMMEntityEntity, entry: Seq[(String, String)]) {
    val otype: SMMObjectType = _object_type(entry)
    val attr = obj.attribute(_name(entry), otype)
    for ((key, value) <- entry) {
      NaturalLabel(key) match {
        case NameLabel => {}
        case DatatypeLabel => {}
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
        case ColumnNameLabel => attr.columnName = value
        case SqlDatatypeLabel => attr.sqlDatatype = SMMObjectType.get(value)
        case _ => {}
      }
    }
  }

  private def _object_type(entry: Seq[(String, String)]): SMMObjectType = {
    val a = entry.find(f => is_type_field(f._1)).flatMap(t => SMMObjectType.get(t._2))
    a | SMMStringType
  }

  protected def is_type_field(string: String): Boolean = {
    NaturalLabel.isDatatype(string)
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
    val entitytype: SMMEntityType = _entity_type(entry)
    val assoc = entity.composition(_name(entry), entitytype)
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
    val entitytype: SMMEntityType = _entity_type(entry)
    val assoc = entity.aggregation(_name(entry), entitytype)
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
    val entitytype: SMMEntityType = _entity_type(entry)
    val assoc = entity.association(_name(entry), entitytype)
    _build_association(assoc, entry)
  }

  private def _build_association(assoc: SMMAssociation, entry: Seq[(String, String)]) {
    for ((key, value) <- entry) {
      NaturalLabel(key) match {
        case NameLabel => {}
        case DatatypeLabel => {}
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
        case SqlDatatypeLabel => assoc.sqlDatatype = SMMObjectType.get(value)
        case _ => {}
      }
    }
  }

  private def _entity_type(entry: Seq[(String, String)]): SMMEntityType = {
    val name = NameLabel.find(entry)
    val term = TermLabel.find(entry)
    name match {
      case Some(n) => {
        val etype = new SMMEntityType(n, packageName)
        term.foreach(x => etype.term = x)
        etype
      }
      case None => sys.error("not implemented yet.")
    }
  }
}
