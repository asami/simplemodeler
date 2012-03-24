package org.simplemodeling.SimpleModeler.builder

import scalaz._
import Scalaz._

/**
 * @since   Mar. 24, 2012
 * @version Mar. 25, 2012
 * @author  ASAMI, Tomoharu
 */
sealed abstract trait NaturalLabel
object KindLabel extends NaturalLabel
object ActorLabel extends NaturalLabel
object ResourceLabel extends NaturalLabel
object EventLabel extends NaturalLabel
object RoleLabel extends NaturalLabel
object SummaryLabel extends NaturalLabel
object DatatypeLabel extends NaturalLabel
object PowertypeLabel extends NaturalLabel
object StateLabel extends NaturalLabel
object StatemachineLabel extends NaturalLabel
object RuleLabel extends NaturalLabel
object ServiceLabel extends NaturalLabel
object UsecaseLabel extends NaturalLabel
object NameLabel extends NaturalLabel
object NameJaLabel extends NaturalLabel
object NameEnLabel extends NaturalLabel
object TermLabel extends NaturalLabel
object TermJaLabel extends NaturalLabel
object TermEnLabel extends NaturalLabel
object TitleLabel extends NaturalLabel
object SubtitleLabel extends NaturalLabel
object CaptionLabel extends NaturalLabel
object BriefLabel extends NaturalLabel
// object SummaryLabel extends NaturalLabel
object DescriptionLabel extends NaturalLabel
object TableNameLabel extends NaturalLabel
object ColumnNameLabel extends NaturalLabel
object AttributeLabel extends NaturalLabel
object AssociationLabel extends NaturalLabel
object AggregationLabel extends NaturalLabel
object CompositionLabel extends NaturalLabel
object BaseLabel extends NaturalLabel
object NullNaturalLabel extends NaturalLabel

object NaturalLabel {
  val kind_candidates = List("kind", "kinds", "カインド", "種別")
  val actor_candidates = List("actor", "actors", "アクター", "アクタ", "登場人物")
  val resource_candidates = List("resource", "resources", "リソース", "道具")
  val event_candidates = List("event", "events", "イベント", "出来事")
  val role_candidates = List("role", "roles", "役割")
  val summary_candidates = List("summary", "summaries", "サマリ", "サマリー", "要約")
  val powertype_candidates = List("powertype", "powertypes", "powers", "パワータイプ", "区分")
  val state_candidates = List("state", "states", "ステート", "状態")
  val statemachine_candidates = List("statemachine", "state machine", "statemachines", "state machines", "ステートマシーン", "ステートチャート", "状態機械")
  val rule_candidates = List("rule", "rules", "ルール", "規則")
  val service_candidates = List("service", "services", "サービス")
  val usecase_candidates = List("usecase", "usecases", "use case", "use cases", "ユースケース", "物語")
  val name_candidates = List("name", "names", "名前")
  val name_ja_candidates = List("name(ja)", "names(ja)", "japanese name", "日本語名")
  val name_en_candidates = List("name(en)", "names(en)", "english name", "英語名")
  val term_candidates = List("term", "terms", "用語")
  val term_ja_candidates = List("term(ja)", "terms(ja)", "japanese term", "日本語用語", "用語(日本語)")
  val term_en_candidates = List("term(en)", "terms(en)", "english term", "英語用語", "用語(英語)")
  val title_candidates = List("title", "タイトル", "表題")
  val subtitle_candidates = List("subtitle", "サブタイトル", "副題")
  val caption_candidates = List("caption")
  val brief_candidates = List("brief")
//  val summary_candidates = List("summary")
  val description_candidates = List("description", "説明")
  val tableName_candidates = List("tablename", "tablenames", "table name", "table names", "テーブル名")
  val columnName_candidates = List("columnname", "columnnames", "column name", "column names", "カラム名")
  val attribute_candidates = List("attr", "attrs", "attribute", "attributes", "属性名")
  val association_candidates = List("assoc", "assocs", "associations", "関連", "参照")
  val aggregation_candidates = List("aggregation", "aggregations", "集約", "部品")
  val composition_candidates = List("composition", "compositions", "合成", "合成集約")
  val base_candidates = List("base", "bases", "extend", "extends", "ベース", "継承", "基底")
  val datatype_candidates = List("type", "datatype", "datatypes", "data type", "data types", "型", "データ型", "データタイプ")

  def apply(string: String): NaturalLabel = {
    val a = string.toLowerCase
    if (kind_candidates.contains(a)) KindLabel
    else if (actor_candidates.contains(a)) ActorLabel
    else if (resource_candidates.contains(a)) ResourceLabel
    else if (event_candidates.contains(a)) EventLabel
    else if (role_candidates.contains(a)) RoleLabel
    else if (summary_candidates.contains(a)) SummaryLabel
    else if (powertype_candidates.contains(a)) PowertypeLabel
    else if (state_candidates.contains(a)) StateLabel
    else if (statemachine_candidates.contains(a)) StatemachineLabel
    else if (rule_candidates.contains(a)) RuleLabel
    else if (service_candidates.contains(a)) ServiceLabel
    else if (usecase_candidates.contains(a)) UsecaseLabel
    else if (name_candidates.contains(a)) NameLabel
    else if (name_ja_candidates.contains(a)) NameJaLabel
    else if (name_en_candidates.contains(a)) NameEnLabel
    else if (term_candidates.contains(a)) TermLabel
    else if (term_ja_candidates.contains(a)) TermJaLabel
    else if (term_en_candidates.contains(a)) TermEnLabel
    else if (title_candidates.contains(a)) TitleLabel
    else if (subtitle_candidates.contains(a)) SubtitleLabel
    else if (caption_candidates.contains(a)) CaptionLabel
    else if (brief_candidates.contains(a)) BriefLabel
    else if (summary_candidates.contains(a)) SummaryLabel
    else if (description_candidates.contains(a)) DescriptionLabel
    else if (tableName_candidates.contains(a)) TableNameLabel
    else if (columnName_candidates.contains(a)) ColumnNameLabel
    else if (attribute_candidates.contains(a)) AttributeLabel
    else if (association_candidates.contains(a)) AssociationLabel
    else if (aggregation_candidates.contains(a)) AggregationLabel
    else if (composition_candidates.contains(a)) CompositionLabel
    else if (base_candidates.contains(a)) BaseLabel
    else if (datatype_candidates.contains(a)) DatatypeLabel
    else NullNaturalLabel;
  }

  def isKind(string: String): Boolean = {
    kind_candidates.contains(string.toLowerCase)
  }

  def isKindKey(kv: (String, _)): Boolean = {
    isKind(kv._1)
  }

  def isName(string: String): Boolean = {
    name_candidates.contains(string.toLowerCase)
  }

  def isNameKey(kv: (String, _)): Boolean = {
    isName(kv._1)
  }

  def isDatatype(string: String): Boolean = {
    datatype_candidates.contains(string.toLowerCase)
  }
}
