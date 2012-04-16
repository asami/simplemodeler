package org.simplemodeling.SimpleModeler.builder

import scalaz._
import Scalaz._
import org.goldenport.entities.csv._
import org.goldenport.value._
import org.goldenport.sdoc.SDoc
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/**
 * @since   Mar.  6, 2012
 * @version Mar. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class TableSimpleModelMakerBuilder(
  builder: SimpleModelMakerBuilder,
  policy: Policy, packageName: String
) extends TabularBuilderBase(policy, packageName) {
  val model_Builder = builder

  override protected def build_Model {}

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

  def buildObject(entity: SMMEntityEntity, table: GTable[String]) {
    val rows = for (row <- table.rows) yield {
      _columns(table.headAsStringList).zip(row)
    }
    rows.map(entry => add_attribute(entity, entry))
  }

  protected final def add_attribute(obj: SMMEntityEntity, entry: Seq[(String, String)]) {
    val attrtype: SMMObjectType = _object_type(entry)
    val attr = obj.attribute(_name(entry), attrtype)
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
}