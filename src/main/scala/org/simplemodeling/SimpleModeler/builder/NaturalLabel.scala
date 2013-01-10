package org.simplemodeling.SimpleModeler.builder

import scalaz._
import Scalaz._
import org.simplemodeling.dsl.util.PropertyRecord
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.apache.commons.lang3.StringUtils.isNotBlank

/*
 * @since   Mar. 24, 2012
 *  version Mar. 25, 2012
 *  version Oct. 30, 2012
 *  version Nov. 26, 2012
 *  version Dec. 26, 2012
 * @version Jan. 10, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * MindmapModelingOutliner uses this class.
 */
sealed abstract trait NaturalLabel {
  /**
   * String in candiates must be lower case.
   */
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
  def findData0(entries: Seq[(String, String)]): Option[String] = {
    entries.find(x => isMatch(x._1)).map(_._2.trim).filter(isNotBlank)
  }

  def findData(entries: Seq[PropertyRecord]): Option[String] = {
    NaturalLabel.findData(this, entries)
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

case object CreateLabel extends NaturalLabel {
  val candidates = List("create", "作成")
}

case object ReadLabel extends NaturalLabel {
  val candidates = List("read", "参照")
}

case object UpdateLabel extends NaturalLabel {
  val candidates = List("update", "更新")
}

case object DeleteLabel extends NaturalLabel {
  val candidates = List("delete", "削除")
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
  val candidates = List("caption", "キャプション")
}

case object BriefLabel extends NaturalLabel {
   val candidates = List("brief", "摘要")
}
// case object SummaryLabel extends NaturalLabel

case object DescriptionLabel extends NaturalLabel {
  val candidates = List("description", "説明")
}

case object PropertyLabel extends NaturalLabel {
  val candidates = List("property", "性質")
}

case object FeatureLabel extends NaturalLabel { // TODO change semantics. current semantics delegates to member.
  val candidates = List("feature", "特性")
}

/**
 * Class slot (e.g. attribute, association)
 */
case object MemberLabel extends NaturalLabel {
  val candidates = List("member", "メンバ", "メンバー")
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

case object ActionLabel extends NaturalLabel {
  val candidates = List("action", "アクション")
}

/*
 * GUI
 */
case object DisplayLabel extends NaturalLabel {
  val candidates = List("display", "表示")
}

case object VisibilityLabel extends NaturalLabel {
  val candidates = List("visibility", "可視性")
}

case object GuiNaviLabelLabel extends NaturalLabel {
  val candidates = List("navi", "navigation", "ナビ", "ナビゲーション", "ナビ名")
}

case object GuiTabLabelLabel extends NaturalLabel {
  val candidates = List("tab", "タブ", "タブ名")
}

case object GuiViewLabel extends NaturalLabel {
  val candidates = List("gui view")
}

case object GuiTemplateLabel extends NaturalLabel {
  val candidates = List("gui template")
}

case object GuiWidgetLabel extends NaturalLabel {
  val candidates = List("gui widget")
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
      "sql 型", "sql データ型", "sql データタイプ")
}

case object SqlAutoIdLabel extends NaturalLabel {
  val candidates = List("auto id")
}

case object SqlReadOnlyLabel extends NaturalLabel {
  val candidates = List("read only")
}

case object SqlAutoCreateLabel extends NaturalLabel {
  val candidates = List("auto create")
}

case object SqlAutoUpdateLabel extends NaturalLabel {
  val candidates = List("auto update")
}

case object SqlPropertyLabel extends NaturalLabel {
  val candidates = List("sql property", "sql properties")
}

//
case object UnknownNaturalLabel extends NaturalLabel {
  val candidates = Nil
}

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
    MemberLabel,
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
    InLabel,
    OutLabel,
    CreateLabel,
    ReadLabel,
    UpdateLabel,
    DeleteLabel,
    ObjecttypeLabel,
    DisplayLabel,
    GuiNaviLabelLabel,
    GuiTabLabelLabel,
    GuiViewLabel,
    GuiTemplateLabel,
    GuiWidgetLabel,
    VisibilityLabel,
    TableNameLabel,
    ColumnNameLabel,
    SqlDatatypeLabel,
    SqlAutoIdLabel,
    SqlReadOnlyLabel,
    SqlAutoCreateLabel,
    SqlAutoUpdateLabel,
    SqlPropertyLabel
  )

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

  def getObjectKind(entry: Seq[PropertyRecord]): Option[ElementKind] = {
    getObjectKind0(entry.flatMap(_.toTuple))
  }

  def getObjectKind0(entry: Seq[(String, String)]): Option[ElementKind] = {
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

  def getObjectName(entry: Seq[PropertyRecord]): Option[String] = {
    getObjectName0(entry.flatMap(_.toTuple))
  }

  def getObjectName0(entry: Seq[(String, String)]): Option[String] = {
    val cs = Stream(NameLabel, NameJaLabel, NameEnLabel, TermLabel, TermJaLabel, TermEnLabel, ColumnNameLabel, TitleLabel, LabelLabel, CaptionLabel)
    _find_candidate(cs, entry)
  }

  private val _slot_candidates = Stream(NameLabel, NameJaLabel, NameEnLabel, TermLabel, TermJaLabel, TermEnLabel, ColumnNameLabel, TypeLabel, TitleLabel, LabelLabel, CaptionLabel)

  def getSlotName(entry: Seq[PropertyRecord]): Option[String] = {
    _find_candidate_property_record(_slot_candidates, entry)
  }

  def getSlotName0(entry: Seq[(String, String)]): Option[String] = {
    _find_candidate(_slot_candidates, entry)
  }

  def getEntityTypeName(entry: Seq[PropertyRecord]): Option[String] = {
    val cs = Stream(TypeLabel, NameLabel, TermLabel)
    val r = _find_candidate_property_record(cs, entry)
//    println("NameEnLabel#getEntityTypeName: " + entry + " => " + r)
    r
  }

  def getEntityTypeName0(entry: Seq[(String, String)]): Option[String] = {
    val cs = Stream(TypeLabel, NameLabel, TermLabel)
    _find_candidate(cs, entry)
  }

  /**
   * XXX multiple dsl files would cause empty property collision.
   */
  private def _find_candidate_property_record(cs: Stream[NaturalLabel], entry: Seq[PropertyRecord]): Option[String] = {
    cs.flatMap(findData(_, entry)).headOption
  }

  def findData(l: NaturalLabel, entry: Seq[PropertyRecord]): Option[String] = {
    entry.find(kv => {
      l.isMatch(kv.key) && (kv.value.map(isNotBlank) | false)
    }).headOption.flatMap(_.value match {
      case Some("") => None
      case Some(s) => Some(s)
      case None => None
    })
  }

  /*
   * deprecated
   */
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
