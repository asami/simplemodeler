package org.simplemodeling.SimpleModeler.builder

import scalaz._
import Scalaz._

/*
 * @since   Mar. 24, 2012
 *  version Mar. 25, 2012
 * @version Oct.  2, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * MindmapModelingOutliner uses this class.
 */
sealed abstract trait NaturalLabel {
  def candidates: Seq[String]
  def allCandidates: Seq[String] = {
    def augumentsSpace(s: String): Seq[String] = {
      if (s.contains(" ")) {
        List(s, s.replace(" ", ""), s.replace(" ", "-"))
      } else Seq(s)
    }

    def augumentsList(s: String): Seq[String] = {
      val l = Seq(s, s + " list", s + " 一覧", s + " リスト")
      val a = s.plural(0)
      if (s == a) l :+ a else l
    }

    candidates >>= augumentsList >>= augumentsSpace
  }

  def isMatch(name: String) = {
    val n = name.toLowerCase
    allCandidates.contains(n)
  }

  def find(entries: Seq[(String, String)]): Option[String] = {
    entries.find(x => isMatch(x._1)).map(_._2)
  }
}

object KindLabel extends NaturalLabel {
  val candidates = List("kind", "カインド", "種別")
}

object EntityLabel extends NaturalLabel {
  val candidates = List("entity", "エンティティ")
}

object ActorLabel extends NaturalLabel {
  val candidates = List("actor", "アクター", "アクタ", "登場人物")
}

object ResourceLabel extends NaturalLabel {
  val candidates = List("resource", "リソース", "道具")
}

object EventLabel extends NaturalLabel {
  val candidates = List("event", "イベント", "出来事")
}

object RoleLabel extends NaturalLabel {
  val candidates = List("role", "役割")
}

object SummaryLabel extends NaturalLabel {
  val candidates = List("summary", "サマリ", "サマリー", "要約")
}

object DatatypeLabel extends NaturalLabel {
  val candidates = List("type", "data types", "型", "データ型", "データタイプ")
}

object PowertypeLabel extends NaturalLabel {
  val candidates = List("powertype", "powers", "パワータイプ", "区分")
}

object StateLabel extends NaturalLabel {
  val candidates = List("state", "ステート", "状態")
  
}

object StatemachineLabel extends NaturalLabel {
  val candidates = List("state machine", "ステート マシーン", "ステート チャート", "状態機械")
  
}

object RuleLabel extends NaturalLabel {
  val candidates = List("rule", "ルール", "規則")
  
}

object ServiceLabel extends NaturalLabel {
  val candidates = List("service", "サービス")
  
}

object UsecaseLabel extends NaturalLabel {
  val candidates = List("use case", "ユースケース", "物語")
  
}

object NameLabel extends NaturalLabel {
  val candidates = List("name", "名前")
  
}

object NameJaLabel extends NaturalLabel {
  val candidates = List("name(ja)", "names(ja)", "japanese name", "日本語名")
  
}

object NameEnLabel extends NaturalLabel {
  val candidates = List("name(en)", "names(en)", "english name", "英語名")
  
}

object TermLabel extends NaturalLabel {
  val candidates = List("term", "用語")
  
}

object TermJaLabel extends NaturalLabel {
  val candidates = List("term(ja)", "terms(ja)", "japanese term", "日本語用語", "用語(日本語)")
  
}

object TermEnLabel extends NaturalLabel {
  val candidates = List("term(en)", "terms(en)", "english term", "英語用語", "用語(英語)")
  
}

object TitleLabel extends NaturalLabel {
  val candidates = List("title", "タイトル", "表題")
  
}

object SubtitleLabel extends NaturalLabel {
  val candidates = List("subtitle", "サブタイトル", "副題")
  
}

object CaptionLabel extends NaturalLabel {
  val candidates = List("caption")
  
}

object BriefLabel extends NaturalLabel {
   val candidates = List("brief")
}
// object SummaryLabel extends NaturalLabel

object DescriptionLabel extends NaturalLabel {
  val candidates = List("description", "説明")
}

object AttributeLabel extends NaturalLabel {
  val candidates = List("attr", "attrs", "attribute", "属性名")
}

object AssociationLabel extends NaturalLabel {
  val candidates = List("assoc", "association", "関連", "参照")
}

object AggregationLabel extends NaturalLabel {
  val candidates = List("aggregation", "集約")
}

object CompositionLabel extends NaturalLabel {
  val candidates = List("composition", "合成", "合成集約")
}

/**
 * Almost aggregation.
 * Selection between composition and aggregation is evaluated later.
 */
object PartLabel extends NaturalLabel {
  val candidates = List("tool", "部品")
}

object IsaLabel extends NaturalLabel {
  val candidates = List("isa", "is-a", "subclass", "サブクラス", "種類")
}

object BaseLabel extends NaturalLabel {
  val candidates = List("base", "extend", "ベース", "継承", "基底")
}

object MultiplicityLabel extends NaturalLabel {
  val candidates = List("multiplicity", "mul", "多重度")
}

object PrimaryActorLabel extends NaturalLabel {
  val candidates = List("primary actor", "primary", "プライマリ アクタ", "主役")
}

object SecondaryActorLabel extends NaturalLabel {
  val candidates = List("secondary actor", "secondary", "セカンダリ アクタ", "相手役")
}

object SupportingActorLabel extends NaturalLabel {
  val candidates = List("supporting actor", "supporting", "サポーティング アクタ", "脇役")
}

object GoalLabel extends NaturalLabel {
  val candidates = List("goal", "ゴール", "目的")
}

object ScenarioLabel extends NaturalLabel {
  val candidates = List("scenario", "シナリオ", "脚本")
}

object AnnotationLabel extends NaturalLabel {
  val candidates = List("annotation", "注記")
}

// SQL
object TableNameLabel extends NaturalLabel {
  val candidates = List("table name", "テーブル名")
  
}

object ColumnNameLabel extends NaturalLabel {
  val candidates = List("column name", "カラム名")
  
}

object SqlDatatypeLabel extends NaturalLabel {
  val candidates = List("sql type", "sql data type",
      "SQL型", "SQLデータ型", "SQLデータタイプ")
  
}
//
object NullNaturalLabel extends NaturalLabel {
  val candidates = Nil
}

object NaturalLabel {
//  val summary_candidates = List("summary")
  val candidates = List(
    KindLabel,
    ActorLabel,
    ResourceLabel,
    EventLabel,
    RoleLabel,
    SummaryLabel,
    PowertypeLabel,
    StateLabel,
    StatemachineLabel,
    RuleLabel,
    ServiceLabel,
    UsecaseLabel,
    NameLabel,
    NameJaLabel,
    NameEnLabel,
    TermLabel,
    TermJaLabel,
    TermEnLabel,
    TitleLabel,
    SubtitleLabel,
    CaptionLabel,
    BriefLabel,
    SummaryLabel,
    DescriptionLabel,
    AttributeLabel,
    AssociationLabel,
    AggregationLabel,
    CompositionLabel,
    BaseLabel,
    IsaLabel,
    MultiplicityLabel,
    PrimaryActorLabel,
    SecondaryActorLabel,
    SupportingActorLabel,
    GoalLabel,
    ScenarioLabel,
    AnnotationLabel,
    DatatypeLabel,
    TableNameLabel,
    ColumnNameLabel,
    SqlDatatypeLabel)

  def apply(string: String): NaturalLabel = {
    candidates.find(_.isMatch(string)) | NullNaturalLabel
  }

  def isKind(string: String): Boolean = {
    KindLabel.isMatch(string)
  }

  def isKindKey(kv: (String, _)): Boolean = {
    isKind(kv._1)
  }

  def isName(string: String): Boolean = {
    NameLabel.isMatch(string)
  }

  def isNameKey(kv: (String, _)): Boolean = {
    isName(kv._1)
  }

  def isDatatype(string: String): Boolean = {
    DatatypeLabel.isMatch(string)
  }
}
