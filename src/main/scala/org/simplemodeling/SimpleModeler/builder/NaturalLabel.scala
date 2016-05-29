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
 *  version Jan. 17, 2013
 *  version Feb.  7, 2013
 * @version May. 29, 2016
 * @author  ASAMI, Tomoharu
 */
/**
 * MindmapModelingOutliner uses this class.
 */
sealed abstract trait NaturalLabel {
  /**
   * String in candiates must be lower case.
   */
  def candidates: Vector[String]
  lazy val allCandidates: Vector[String] = {
    def augumentsSpace(s: String): Vector[String] = {
      if (s.contains(" ")) {
        Vector(s.replace(" ", ""), s.replace(" ", "-"), s.replace(" ", "・"))
      } else Vector(s)
    }

    def augumentsList(s: String): Vector[String] = {
      Vector(s, s + " list", s + " 一覧", s + " リスト")
    }

    def augumentPlural(s: String): Vector[String] = {
      val a = s.plural(0)
      if (s == a) Vector(s) else Vector(s, a)
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
  val candidates = Vector("kind", "カインド", "種別")
}

case object TraitLabel extends NaturalLabel {
  val candidates = Vector("trait", "トレイト", "特色")
}

case object BusinessActorLabel extends NaturalLabel {
  val candidates = Vector("business actor", "ビジネス アクター", "ビジネス アクタ", "業務 アクター", "業務 アクタ")
}

case object BusinessResourceLabel extends NaturalLabel {
  val candidates = Vector("business resource", "ビジネス リソース", "業務 リソース")
}
case object BusinessEventLabel extends NaturalLabel {
  val candidates = Vector("business event", "ビジネス イベント", "業務 イベント")
}

case object EntityLabel extends NaturalLabel {
  val candidates = Vector("entity", "エンティティ")
}

case object ActorLabel extends NaturalLabel {
  val candidates = Vector("actor", "アクター", "アクタ", "登場人物")
}

case object ResourceLabel extends NaturalLabel {
  val candidates = Vector("resource", "リソース", "道具")
}

case object EventLabel extends NaturalLabel {
  val candidates = Vector("event", "イベント", "出来事")
}

case object RoleLabel extends NaturalLabel {
  val candidates = Vector("role", "役割")
}

case object SummaryLabel extends NaturalLabel {
  val candidates = Vector("summary", "サマリ", "サマリー", "要約")
}

case object TypeLabel extends NaturalLabel {
  val candidates = Vector("type", "型")
}

case object DatatypeLabel extends NaturalLabel {
  val candidates = Vector("data type", "データ型", "データタイプ")
}

case object InLabel extends NaturalLabel {
  val candidates = Vector("in", "input", "入力", "インプット")
}

case object OutLabel extends NaturalLabel {
  val candidates = Vector("out", "output", "出力", "アウトプット")
}

case object CreateLabel extends NaturalLabel {
  val candidates = Vector("create", "作成")
}

case object ReadLabel extends NaturalLabel {
  val candidates = Vector("read", "参照")
}

case object UpdateLabel extends NaturalLabel {
  val candidates = Vector("update", "更新")
}

case object DeleteLabel extends NaturalLabel {
  val candidates = Vector("delete", "削除")
}

case object ObjecttypeLabel extends NaturalLabel {
  val candidates = Vector("object type", "オブジェクト型")
}

case object PowertypeLabel extends NaturalLabel {
  val candidates = Vector("powertype", "powers", "パワータイプ", "区分")
}

case object DocumentLabel extends NaturalLabel {
  val candidates = Vector("document", "ドキュメント", "文書")
}

case object ValueLabel extends NaturalLabel {
  val candidates = Vector("value", "バリュー", "値")
}

case object LifecycleLabel extends NaturalLabel {
  val candidates = Vector("lifecycle", "ライフ サイクル")
}

case object StateLabel extends NaturalLabel {
  val candidates = Vector("state", "ステート", "状態")
}

case object StateMachineLabel extends NaturalLabel {
  val candidates = Vector("state machine", "ステート マシーン", "ステート チャート", "状態機械")
}

case object RuleLabel extends NaturalLabel {
  val candidates = Vector("rule", "ルール", "規則")
}

case object ServiceLabel extends NaturalLabel {
  val candidates = Vector("service", "サービス")
}

case object BusinessUsecaseLabel extends NaturalLabel {
  val candidates = Vector("business use case", "ビジネス ユースケース", "業務 ユースケース", "物語")
}

case object BusinessTaskLabel extends NaturalLabel {
  val candidates = Vector("business task", "ビジネス タスク", "業務 タスク")
}

case object UsecaseLabel extends NaturalLabel {
  val candidates = Vector("use case", "ユースケース", "エピソード", "挿話")
}

case object TaskLabel extends NaturalLabel {
  val candidates = Vector("task", "タスク")
}

case object NameLabel extends NaturalLabel {
  val candidates = Vector("name", "名前")
}

case object NameJaLabel extends NaturalLabel {
  val candidates = Vector("name(ja)", "names(ja)", "japanese name", "日本語名")
}

case object NameEnLabel extends NaturalLabel {
  val candidates = Vector("name(en)", "names(en)", "english name", "英語名")
}

case object TermLabel extends NaturalLabel {
  val candidates = Vector("term", "用語")
}

case object TermJaLabel extends NaturalLabel {
  val candidates = Vector("term(ja)", "terms(ja)", "japanese term", "日本語用語", "用語(日本語)")
}

case object TermEnLabel extends NaturalLabel {
  val candidates = Vector("term(en)", "terms(en)", "english term", "英語用語", "用語(英語)")
}

case object TitleLabel extends NaturalLabel {
  val candidates = Vector("title", "タイトル", "表題", "題")
}

case object SubtitleLabel extends NaturalLabel {
  val candidates = Vector("subtitle", "サブタイトル", "副題")
}

case object LabelLabel extends NaturalLabel {
  val candidates = Vector("label", "ラベル")
}

case object CaptionLabel extends NaturalLabel {
  val candidates = Vector("caption", "キャプション")
}

case object BriefLabel extends NaturalLabel {
   val candidates = Vector("brief", "摘要")
}
// case object SummaryLabel extends NaturalLabel

case object DescriptionLabel extends NaturalLabel {
  val candidates = Vector("description", "説明")
}

case object CategoryLabel extends NaturalLabel {
  val candidates = Vector("category", "カテゴリ")
}

case object AuthorLabel extends NaturalLabel {
  val candidates = Vector("author", "著者")
}

case object IconLabel extends NaturalLabel {
  val candidates = Vector("icon", "アイコン")
}

case object LogoLabel extends NaturalLabel {
  val candidates = Vector("logo", "ロゴ")
}

case object LinkLabel extends NaturalLabel {
  val candidates = Vector("link", "リンク")
}

case object ContentLabel extends NaturalLabel {
  val candidates = Vector("content", "コンテント", "コンテンツ")
}

case object CreatedLabel extends NaturalLabel {
  val candidates = Vector("created", "作成日時")
}

case object UpdatedLabel extends NaturalLabel {
  val candidates = Vector("updated", "更新日時")
}

case object DeletedLabel extends NaturalLabel {
  val candidates = Vector("deleted", "削除日時")
}

case object PropertyLabel extends NaturalLabel {
  val candidates = Vector("property", "性質")
}

case object FeatureLabel extends NaturalLabel { // TODO change semantics. current semantics delegates to member.
  val candidates = Vector("feature", "特性")
}

/**
 * Class slot (e.g. attribute, association)
 */
case object MemberLabel extends NaturalLabel {
  val candidates = Vector("member", "メンバ", "メンバー")
}

case object AttributeLabel extends NaturalLabel {
  val candidates = Vector("attr", "attribute", "属性")
}

case object IdLabel extends NaturalLabel {
  val candidates = Vector("id")
}

case object UserLabel extends NaturalLabel {
  val candidates = Vector("user", "ユーザ", "ユーザー")
}

case object AssociationLabel extends NaturalLabel {
  val candidates = Vector("assoc", "association", "関連", "参照")
}

case object AggregationLabel extends NaturalLabel {
  val candidates = Vector("aggregation", "集約")
}

case object CompositionLabel extends NaturalLabel {
  val candidates = Vector("composition", "合成", "合成集約")
}

/**
 * Almost aggregation.
 * Selection between composition and aggregation is evaluated later.
 */
case object PartLabel extends NaturalLabel {
  val candidates = Vector("tool", "部品")
}

case object SuperLabel extends NaturalLabel {
  val candidates = Vector("super", "上位")
}

case object SubLabel extends NaturalLabel {
  val candidates = Vector("sub", "下位")
}

case object IsaLabel extends NaturalLabel {
  val candidates = Vector("isa", "is-a", "subclass", "サブクラス", "種類")
}

case object BaseLabel extends NaturalLabel {
  val candidates = Vector("base", "extend", "ベース", "継承", "基底")
}

case object MultiplicityLabel extends NaturalLabel {
  val candidates = Vector("multiplicity", "mul", "多重度")
}

case object DeriveLabel extends NaturalLabel {
  val candidates = Vector("derive", "derived", "派生")
}

case object OperationLabel extends NaturalLabel {
  val candidates = Vector("operation", "method", "オペレーション", "メソッド", "操作")
}

case object PrimaryActorLabel extends NaturalLabel {
  val candidates = Vector("primary actor", "primary", "プライマリ アクタ", "主役")
}

case object SecondaryActorLabel extends NaturalLabel {
  val candidates = Vector("secondary actor", "secondary", "セカンダリ アクタ", "相手役")
}

case object SupportingActorLabel extends NaturalLabel {
  val candidates = Vector("supporting actor", "supporting", "サポーティング アクタ", "脇役")
}

case object GoalLabel extends NaturalLabel {
  val candidates = Vector("goal", "ゴール", "目的")
}

case object ScenarioLabel extends NaturalLabel {
  val candidates = Vector("scenario", "シナリオ", "脚本")
}

case object AnnotationLabel extends NaturalLabel {
  val candidates = Vector("annotation", "注記")
}

case object ActionLabel extends NaturalLabel {
  val candidates = Vector("action", "アクション")
}

/*
 * GUI
 */
case object DisplayLabel extends NaturalLabel {
  val candidates = Vector("display", "表示")
}

case object VisibilityLabel extends NaturalLabel {
  val candidates = Vector("visibility", "可視性")
}

case object GuiNaviLabelLabel extends NaturalLabel {
  val candidates = Vector("navi", "navigation", "ナビ", "ナビゲーション", "ナビ名")
}

case object GuiTabLabelLabel extends NaturalLabel {
  val candidates = Vector("tab", "タブ", "タブ名")
}

case object GuiViewLabel extends NaturalLabel {
  val candidates = Vector("gui view")
}

case object GuiTemplateLabel extends NaturalLabel {
  val candidates = Vector("gui template")
}

case object GuiWidgetLabel extends NaturalLabel {
  val candidates = Vector("gui widget")
}

/*
 * SQL
 */
case object TableNameLabel extends NaturalLabel {
  val candidates = Vector("table name", "table", "テーブル", "表", "テーブル名")
}

case object JoinLabel extends NaturalLabel {
  val candidates = Vector("join", "結合")
}

case object ColumnNameLabel extends NaturalLabel {
  val candidates = Vector("column name", "column", "カラム名", "カラム")
}

case object SqlDatatypeLabel extends NaturalLabel {
  val candidates = Vector("sql type", "sql data type",
      "sql 型", "sql データ型", "sql データタイプ")
}

case object SqlAutoIdLabel extends NaturalLabel {
  val candidates = Vector("auto id")
}

case object SqlReadOnlyLabel extends NaturalLabel {
  val candidates = Vector("read only")
}

case object SqlAutoCreateLabel extends NaturalLabel {
  val candidates = Vector("auto create")
}

case object SqlAutoUpdateLabel extends NaturalLabel {
  val candidates = Vector("auto update")
}

case object SqlPropertyLabel extends NaturalLabel {
  val candidates = Vector("sql property", "sql properties")
}

//
case object UnknownNaturalLabel extends NaturalLabel {
  val candidates = Vector.empty
}

case object NullNaturalLabel extends NaturalLabel {
  val candidates = Vector.empty
}

object NaturalLabel {
//  val summary_candidates = Vector("summary")
  val candidates = Vector(
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
    LifecycleLabel,
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
    UserLabel,
    AssociationLabel,
    AggregationLabel,
    CompositionLabel,
    PartLabel,
    SuperLabel,
    SubLabel,
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
    JoinLabel,
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
