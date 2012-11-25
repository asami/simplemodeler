package org.simplemodeling.SimpleModeler.builder

import scalaz._
import Scalaz._
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.apache.commons.lang3.StringUtils.isNotBlank

/*
 * @since   Mar. 24, 2012
 *  version Mar. 25, 2012
 *  version Oct. 30, 2012
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * MindmapModelingOutliner uses this class.
 */
sealed abstract trait NaturalLabel {
  def candidates: Seq[String]
  lazy val allCandidates: Seq[String] = {
    def augumentsSpace(s: String): Seq[String] = {
      if (s.contains(" ")) {
        List(s.replace(" ", ""), s.replace(" ", "-"), s.replace(" ", "・"))
      } else Seq(s)
    }

    def augumentsList(s: String): Seq[String] = {
      Seq(s, s + " list", s + " 一覧", s + " リスト")
    }

    def augumentPlural(s: String): Seq[String] = {
      val a = s.plural(0)
      if (s == a) Seq(s) else Seq(s, a)
    }

    candidates >>= augumentPlural >>= augumentsList >>= augumentsSpace
  }

  def isMatch(name: String) = {
    val n = name.trim.toLowerCase.replace(" ", "")
    allCandidates.contains(n)
  }
//  } ensuring( x => {
//    println("NaturalLabel[%s][%s]#isMatch(%s) = %s".format(this, allCandidates, name, x))
//    println("NaturalLabel[%s]#isMatch(%s) = %s".format(this, name, x))
//    true
//  })

  def startsWith(name: String) = {
    val n = name.trim.toLowerCase.replace(" ", "")
    allCandidates.exists(n.startsWith)
  }

  def find(entries: Seq[(String, String)]): Option[String] = {
    entries.find(x => isMatch(x._1)).map(_._2)
  }

  /**
   * In case of blank data, result is 'not find'.
   * Data is trimed.
   */
  def findData(entries: Seq[(String, String)]): Option[String] = {
    entries.find(x => isMatch(x._1)).map(_._2.trim).filter(isNotBlank)
  }
}

case object KindLabel extends NaturalLabel {
  val candidates = List("kind", "カインド", "種別")
}

case object TraitLabel extends NaturalLabel {
  val candidates = List("trait", "トレイト", "特色")
}

case object BusinessActorLabel extends NaturalLabel {
  val candidates = List("business actor", "ビジネス アクター", "ビジネス アクタ", "業務 アクター", "業務 アクタ")
}

case object BusinessResourceLabel extends NaturalLabel {
  val candidates = List("business resource", "ビジネス リソース", "業務 リソース")
}
case object BusinessEventLabel extends NaturalLabel {
  val candidates = List("business event", "ビジネス イベント", "業務 イベント")
}

case object EntityLabel extends NaturalLabel {
  val candidates = List("entity", "エンティティ")
}

case object ActorLabel extends NaturalLabel {
  val candidates = List("actor", "アクター", "アクタ", "登場人物")
}

case object ResourceLabel extends NaturalLabel {
  val candidates = List("resource", "リソース", "道具")
}

case object EventLabel extends NaturalLabel {
  val candidates = List("event", "イベント", "出来事")
}

case object RoleLabel extends NaturalLabel {
  val candidates = List("role", "役割")
}

case object SummaryLabel extends NaturalLabel {
  val candidates = List("summary", "サマリ", "サマリー", "要約")
}

case object TypeLabel extends NaturalLabel {
  val candidates = List("type", "型")
}

case object DatatypeLabel extends NaturalLabel {
  val candidates = List("data type", "データ型", "データタイプ")
}

case object InLabel extends NaturalLabel {
  val candidates = List("in", "input", "入力", "インプット")
}

case object OutLabel extends NaturalLabel {
  val candidates = List("out", "output", "出力", "アウトプット")
}

case object ObjecttypeLabel extends NaturalLabel {
  val candidates = List("object type", "オブジェクト型")
}

case object PowertypeLabel extends NaturalLabel {
  val candidates = List("powertype", "powers", "パワータイプ", "区分")
}

case object DocumentLabel extends NaturalLabel {
  val candidates = List("document", "ドキュメント", "文書")
}

case object ValueLabel extends NaturalLabel {
  val candidates = List("value", "バリュー", "値")
}

case object StateLabel extends NaturalLabel {
  val candidates = List("state", "ステート", "状態")
}

case object StateMachineLabel extends NaturalLabel {
  val candidates = List("state machine", "ステート マシーン", "ステート チャート", "状態機械")
}

case object RuleLabel extends NaturalLabel {
  val candidates = List("rule", "ルール", "規則")
}

case object ServiceLabel extends NaturalLabel {
  val candidates = List("service", "サービス")
}

case object BusinessUsecaseLabel extends NaturalLabel {
  val candidates = List("business use case", "ビジネス ユースケース", "業務 ユースケース", "物語")
}

case object BusinessTaskLabel extends NaturalLabel {
  val candidates = List("business task", "ビジネス タスク", "業務 タスク")
}

case object UsecaseLabel extends NaturalLabel {
  val candidates = List("use case", "ユースケース", "エピソード", "挿話")
}

case object TaskLabel extends NaturalLabel {
  val candidates = List("task", "タスク")
}

case object NameLabel extends NaturalLabel {
  val candidates = List("name", "名前")
}

case object NameJaLabel extends NaturalLabel {
  val candidates = List("name(ja)", "names(ja)", "japanese name", "日本語名")
}

case object NameEnLabel extends NaturalLabel {
  val candidates = List("name(en)", "names(en)", "english name", "英語名")
}

case object TermLabel extends NaturalLabel {
  val candidates = List("term", "用語")
}

case object TermJaLabel extends NaturalLabel {
  val candidates = List("term(ja)", "terms(ja)", "japanese term", "日本語用語", "用語(日本語)")
}

case object TermEnLabel extends NaturalLabel {
  val candidates = List("term(en)", "terms(en)", "english term", "英語用語", "用語(英語)")
}

case object TitleLabel extends NaturalLabel {
  val candidates = List("title", "タイトル", "表題", "題")
}

case object SubtitleLabel extends NaturalLabel {
  val candidates = List("subtitle", "サブタイトル", "副題")
}

case object LabelLabel extends NaturalLabel {
  val candidates = List("label", "ラベル")
}

case object CaptionLabel extends NaturalLabel {
  val candidates = List("caption")
}

case object BriefLabel extends NaturalLabel {
   val candidates = List("brief")
}
// case object SummaryLabel extends NaturalLabel

case object DescriptionLabel extends NaturalLabel {
  val candidates = List("description", "説明")
}

case object PropertyLabel extends NaturalLabel {
  val candidates = List("property", "性質")
}

case object FeatureLabel extends NaturalLabel { // feature is used as class slot (e.g. attribute, association) at the time.
  val candidates = List("feature", "特性")
}

case object AttributeLabel extends NaturalLabel {
  val candidates = List("attr", "attribute", "属性")
}

case object IdLabel extends NaturalLabel {
  val candidates = List("id")
}

case object AssociationLabel extends NaturalLabel {
  val candidates = List("assoc", "association", "関連", "参照")
}

case object AggregationLabel extends NaturalLabel {
  val candidates = List("aggregation", "集約")
}

case object CompositionLabel extends NaturalLabel {
  val candidates = List("composition", "合成", "合成集約")
}

/**
 * Almost aggregation.
 * Selection between composition and aggregation is evaluated later.
 */
case object PartLabel extends NaturalLabel {
  val candidates = List("tool", "部品")
}

case object IsaLabel extends NaturalLabel {
  val candidates = List("isa", "is-a", "subclass", "サブクラス", "種類")
}

case object BaseLabel extends NaturalLabel {
  val candidates = List("base", "extend", "ベース", "継承", "基底")
}

case object MultiplicityLabel extends NaturalLabel {
  val candidates = List("multiplicity", "mul", "多重度")
}

case object DeriveLabel extends NaturalLabel {
  val candidates = List("derive", "derived", "派生")
}

case object OperationLabel extends NaturalLabel {
  val candidates = List("operation", "method", "オペレーション", "メソッド", "操作")
}

case object PrimaryActorLabel extends NaturalLabel {
  val candidates = List("primary actor", "primary", "プライマリ アクタ", "主役")
}

case object SecondaryActorLabel extends NaturalLabel {
  val candidates = List("secondary actor", "secondary", "セカンダリ アクタ", "相手役")
}

case object SupportingActorLabel extends NaturalLabel {
  val candidates = List("supporting actor", "supporting", "サポーティング アクタ", "脇役")
}

case object GoalLabel extends NaturalLabel {
  val candidates = List("goal", "ゴール", "目的")
}

case object ScenarioLabel extends NaturalLabel {
  val candidates = List("scenario", "シナリオ", "脚本")
}

case object AnnotationLabel extends NaturalLabel {
  val candidates = List("annotation", "注記")
}

/*
 * GUI
 */
case object NaviLabelLabel extends NaturalLabel {
  val candidates = List("navi", "navigation", "ナビ", "ナビゲーション", "ナビ名")
}

case object TabLabelLabel extends NaturalLabel {
  val candidates = List("tab", "タブ", "タブ名")
}

/*
 * SQL
 */
case object TableNameLabel extends NaturalLabel {
  val candidates = List("table name", "table", "テーブル", "表", "テーブル名")
}

case object ColumnNameLabel extends NaturalLabel {
  val candidates = List("column name", "column", "カラム名", "カラム")
}

case object SqlDatatypeLabel extends NaturalLabel {
  val candidates = List("sql type", "sql data type",
      "SQL型", "SQLデータ型", "SQLデータタイプ")
  
}
//
case object NullNaturalLabel extends NaturalLabel {
  val candidates = Nil
}

object NaturalLabel {
//  val summary_candidates = List("summary")
  val candidates = List(
    KindLabel,
    TraitLabel,
    ActorLabel,
    ResourceLabel,
    EventLabel,
    RoleLabel,
    SummaryLabel,
    PowertypeLabel,
    DocumentLabel,
    ValueLabel,
    StateLabel,
    StateMachineLabel,
    RuleLabel,
    ServiceLabel,
    BusinessUsecaseLabel,
    NameLabel,
    NameJaLabel,
    NameEnLabel,
    TermLabel,
    TermJaLabel,
    TermEnLabel,
    TitleLabel,
    SubtitleLabel,
    LabelLabel,
    CaptionLabel,
    BriefLabel,
    SummaryLabel,
    DescriptionLabel,
    PropertyLabel,
    FeatureLabel,
    AttributeLabel,
    IdLabel,
    AssociationLabel,
    AggregationLabel,
    CompositionLabel,
    BaseLabel,
    IsaLabel,
    MultiplicityLabel,
    DeriveLabel,
    OperationLabel,
    PrimaryActorLabel,
    SecondaryActorLabel,
    SupportingActorLabel,
    GoalLabel,
    ScenarioLabel,
    AnnotationLabel,
    TypeLabel,
    DatatypeLabel,
    ObjecttypeLabel,
    TableNameLabel,
    ColumnNameLabel,
    SqlDatatypeLabel)

  def apply(string: String): NaturalLabel = {
    unapply(string) | NullNaturalLabel
  }

  def unapply(string: String): Option[NaturalLabel] = {
    candidates.find(_.isMatch(string))
  }
/*
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
*/
/*
  def isDatatype(string: String): Boolean = {
    DatatypeLabel.isMatch(string)
  }
*/
  def getLabel(s: String): Option[NaturalLabel] = {
    candidates.find(_.isMatch(s))
  }

  def getObjectTypeName(entry: Seq[(String, String)]): Option[String] = {
    val cs = Stream(ObjecttypeLabel, TypeLabel, DatatypeLabel, SqlDatatypeLabel)
    _find_candidate(cs, entry)
  }

  def getObjectKind(entry: Seq[(String, String)]): Option[ElementKind] = {
    val cs = Stream(KindLabel)
    cs.flatMap(c => {
      entry.find(kv => {
        c.isMatch(kv._1) && isNotBlank(kv._2)
      })
    }).headOption.flatMap(t => NaturalLabel(t._2) match {
      case ActorLabel => ActorKind.some
      case ResourceLabel => ResourceKind.some
      case EventLabel => EventKind.some
      case RoleLabel => RoleKind.some
      case RuleLabel => RuleKind.some
      case BusinessUsecaseLabel => BusinessUsecaseKind.some
      case _ => none
    })
  }

  def getObjectName(entry: Seq[(String, String)]): Option[String] = {
    val cs = Stream(NameLabel, NameJaLabel, NameEnLabel, TermLabel, TermJaLabel, TermEnLabel, ColumnNameLabel, TitleLabel, LabelLabel, CaptionLabel)
    _find_candidate(cs, entry)
  }

  def getSlotName(entry: Seq[(String, String)]): Option[String] = {
    val cs = Stream(NameLabel, NameJaLabel, NameEnLabel, TermLabel, TermJaLabel, TermEnLabel, ColumnNameLabel, TypeLabel, TitleLabel, LabelLabel, CaptionLabel)
    _find_candidate(cs, entry)
  }

  def getEntityTypeName(entry: Seq[(String, String)]): Option[String] = {
    val cs = Stream(TypeLabel, NameLabel, TermLabel)
    _find_candidate(cs, entry)
  }

  private def _find_candidate(cs: Stream[NaturalLabel], entry: Seq[(String, String)]): Option[String] = {
    cs.flatMap(c => {
      entry.find(kv => {
        c.isMatch(kv._1) && isNotBlank(kv._2)
      })
//    }).map(x => { println("match = " + x); x
    }).headOption.flatMap(_._2 match {
      case "" => None
      case s => Some(s)
    })
  }
//  } ensuring (x => {
//    println("_find_candidate: " + entry + " => " + x)
//    true
//  })
}
