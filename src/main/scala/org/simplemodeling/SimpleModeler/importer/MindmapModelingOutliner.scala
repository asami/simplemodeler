package org.simplemodeling.SimpleModeler.importer

import scalaz._
import Scalaz._
import org.goldenport.z._
import org.goldenport.Z._
import org.goldenport.sdoc._
import org.goldenport.entities.outline._
import org.goldenport.value.{GTable, PlainTable}
import com.asamioffice.goldenport.text.CsvUtility
import org.smartdox._
import org.simplemodeling.SimpleModeler.builder._

/*
 * Nov. 6, 2011 (derived from MindmapModelingXMind)
 * @since   Nov. 30, 2011 
 *  version Apr.  8, 2012
 *  version Oct. 21, 2012
 *  version Nov. 25, 2012
 *  version Dec. 19, 2012
 * @version Jan. 10, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * Builds a MindmapModeling model from an outline data by OutlineEntityBase.
 *
 * Concrete classes of OutlineEntityBase are [TODO] at the moment.
 *
 * OutlineBuilderBase uses this class.
 */
class MindmapModelingOutliner(val outline: OutlineEntityBase) extends UseTerm {
  val thema = outline.firstThema // TODO variation point

  def businessActors: List[TopicNode] = {
    structure_node_children(thema, BusinessActorLabel)
  }

  def businessActorTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(BusinessActorLabel)
  }

  def businessResources = {
    structure_node_children(thema, BusinessResourceLabel)
  }

  def businessResourceTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(BusinessResourceLabel)
  }

  def businessEvents = {
    structure_node_children(thema, BusinessEventLabel)
  }

  def businessEventTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(BusinessEventLabel)
  }

  //
  def entityTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(EntityLabel)
  }

  def traits: List[TopicNode] = {
    structure_node_children(thema, TraitLabel)
  }

  def traitTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(TraitLabel)
  }

  def actors: List[TopicNode] = {
    structure_node_children(thema, ActorLabel)
  }

  def actorTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(ActorLabel)
  }

  def resources = {
    structure_node_children(thema, ResourceLabel)
  }

  def resourceTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(ResourceLabel)
  }

  def events = {
    structure_node_children(thema, EventLabel)
  }

  def eventTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(EventLabel)
  }

  def roles = {
    structure_node_children(thema, RoleLabel)
  }

  def roleTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(RoleLabel)
  }

  def summarys = {
    structure_node_children(thema, SummaryLabel)
  }

  def summaryTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(SummaryLabel)
  }

  def associationEntities = {
    structure_node_children(thema, AssociationLabel)
  }

  def associationEntityTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(AssociationLabel)
  }

  def powertypes = {
    structure_node_children(thema, PowertypeLabel)
  }

  def powertypeTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(PowertypeLabel)
  }

  def statemachines = {
    structure_node_children(thema, StateMachineLabel)
  }

  def statemachineTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(StateMachineLabel)
  }

  def documents = {
    structure_node_children(thema, DocumentLabel)
  }

  def documentTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(DocumentLabel)
  }

  def values = {
    structure_node_children(thema, ValueLabel)
  }

  def valueTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(ValueLabel)
  }

  def services = {
    structure_node_children(thema, ServiceLabel)
  }

  def serviceTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(ServiceLabel)
  }

  def rules = {
    structure_node_children(thema, RuleLabel)
  }

  def ruleTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(RuleLabel)
  }

  def businessusecases = {
    structure_node_children(thema, BusinessUsecaseLabel)
  }

  def businessusecaseTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(BusinessUsecaseLabel)
  }

  def businesstasks = {
    structure_node_children(thema, BusinessTaskLabel)
  }

  def businesstaskTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(BusinessTaskLabel)
  }

  def usecases = {
    structure_node_children(thema, UsecaseLabel)
  }

  def usecaseTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(UsecaseLabel)
  }

  def tasks = {
    structure_node_children(thema, TaskLabel)
  }

  def taskTables: List[GTable[String]] = {
    _structure_node_thema_boi_tables(TaskLabel)
  }

  /*
   * structure nodes
   */
  def propertyTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, PropertyLabel)
  }

  def traits(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, TraitLabel)
  }

  def parts(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, PartLabel)
  }

  def featureTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, FeatureLabel)
  }

  def compositions(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, CompositionLabel)
  }

  def compositionTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, CompositionLabel)
  }

  def aggregations(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, AggregationLabel)
  }

  def aggregationTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, AggregationLabel)
  }

  def associations(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, AssociationLabel)
  }

  def associationTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, AssociationLabel)
  }

  def attributes(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, AttributeLabel) // 特長
  }

  def attributeTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, AttributeLabel)
  }

  def derivations(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, IsaLabel)
  }

  def derivationTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, IsaLabel)
  }

  def displays(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, DisplayLabel)
  }

  def displayTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, DisplayLabel)
  }

  def actionTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, ActionLabel)
  }

  def powertypes(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, PowertypeLabel)
  }

  def powertypeTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, PowertypeLabel)
  }

  def kinds(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, KindLabel)
  }

  def kindTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, KindLabel)
  }

  def statemachines(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, StateMachineLabel)
  }

  def statemachineTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, StateMachineLabel)
  }

  def documents(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, DocumentLabel)
  }

  def documentTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, DocumentLabel)
  }

  def values(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, ValueLabel)
  }

  def valueTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, ValueLabel)
  }

  def roles(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, RoleLabel)
  }

  def roleTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, RoleLabel)
  }

  def summarys(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, SummaryLabel)
  }

  def summaryTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, SummaryLabel)
  }

  def rules(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, RuleLabel)
  }

  def ruleTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, RuleLabel)
  }

  def services(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, ServiceLabel)
  }

  def serviceTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, ServiceLabel)
  }

  def states(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, StateLabel)
  }

  def stateTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, StateLabel)
  }

  def operations(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, OperationLabel)
  }

  def operationTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, OperationLabel)
  }

  def operationIns(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, InLabel)
  }

  def operationInTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, InLabel)
  }

  def operationOuts(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, OutLabel)
  }

  def operationOutTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, OutLabel)
  }

  def operationCreates(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, CreateLabel)
  }

  def operationCreateTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, CreateLabel)
  }

  def operationReads(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, ReadLabel)
  }

  def operationReadTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, ReadLabel)
  }

  def operationUpdates(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, UpdateLabel)
  }

  def operationUpdateTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, UpdateLabel)
  }

  def operationDeletes(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, DeleteLabel)
  }

  def operationDeleteTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, DeleteLabel)
  }

  def annotations(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, AnnotationLabel)
  }

  def annotationTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, AnnotationLabel)
  }

  def businessusecases(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, BusinessUsecaseLabel)
  }

  def businessusecaseTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, BusinessUsecaseLabel)
  }

  def businesstasks(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, BusinessTaskLabel)
  }

  def businesstaskTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, BusinessTaskLabel)
  }

  def usecases(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, UsecaseLabel)
  }

  def usecaseTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, UsecaseLabel)
  }

  def tasks(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, TaskLabel)
  }

  def taskTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, TaskLabel)
  }

  def primaryActors(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, PrimaryActorLabel)
  }

  def primaryActorTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, PrimaryActorLabel)
  }

  def secondaryActors(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, SecondaryActorLabel)
  }

  def secondaryActorTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, SecondaryActorLabel)
  }

  def supportingActors(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, SupportingActorLabel)
  }

  def supportingActorTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, SupportingActorLabel)
  }

  def actorTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, ActorLabel)
  }

  def goals(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, GoalLabel)
  }

  def goalTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, GoalLabel)
  }

  def scenario(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, ScenarioLabel)
  }

  def scenarioTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, ScenarioLabel)
  }

  /*
   *
   */
  def boi(name: NaturalLabel): Seq[TopicNode] = {
    structure_node(thema, name)
  }

  def structure_node(aParent: OutlineNode, label: NaturalLabel): Seq[TopicNode] = {
    structure_node(aParent, _is_match(label) _)
  }

/*
  def boi(name: String): Option[TopicNode] = {
    structure_node(thema, name)
  }

  def structure_node(aParent: OutlineNode, aName: String): Option[TopicNode] = {
    structure_node(aParent, List(aName))
  }

  def structure_node(aParent: OutlineNode, names: Seq[String]): Option[TopicNode] = {
    structure_node(aParent, _is_match(names, _))
  }
)
*/

  protected final def structure_node(aParent: OutlineNode, ismatch: OutlineNode => Boolean): Seq[TopicNode] = {
    aParent.children.collect {
      case x: TopicNode if ismatch(x) => x
    }
  }

  protected final def structure_node_children(aParent: OutlineNode, label: NaturalLabel): List[TopicNode] = {
    structure_node_children(aParent, _is_match(label) _)
  }

  private def _is_match(label: NaturalLabel)(node: OutlineNode): Boolean = {
    _is_match(label, node.title)
  }

  private def _is_match(label: NaturalLabel, title: String): Boolean = {
    label.startsWith(_normalize_title(title))
  }

  private def _isignore(x: Char) = {
    x == ' ' || x == '(' || x == ')' || x == '[' || x == ']' || x == '{' || x == '}' ||
    x == '\n' || x == '\r' || x == '\t' || x == '\f'
  }

  private def _normalize_title(s: String): String = {
    s.dropWhile(_isignore).reverse.dropWhile(_isignore).reverse
  }

/*
  def structure_node_children(aParent: OutlineNode, aName: String): List[TopicNode] = {
    structure_node_children(aParent, List(aName))
  }

  def structure_node_children(aParent: OutlineNode, names: Seq[String]): List[TopicNode] = {
    structure_node_children(aParent, _is_match(names, _))
  }

  private def _is_match(names: Seq[String], node: OutlineNode): Boolean = {
    val candidates = names.flatMap(n => List(n, "[" + n + "]"))
    candidates.exists(_ == node.title)
  }
*/

  protected final def structure_node_children(aParent: OutlineNode, ismatch: OutlineNode => Boolean): List[TopicNode] = {
    val mayNodes = aParent.children.filter(ismatch).toList
    mayNodes.flatMap(x => {
      x.children.asInstanceOf[Seq[TopicNode]].toList
    })
  }

/*
  private def _structure_node_tables(node: OutlineNode, name: String): List[GTable[String]] = {
    _structure_node_tables(node, List(name))
  }
*/

  private def _to_gtable(src: Table): GTable[String] = {
    val r = new PlainTable[String]
    _to_gtable_head(src, r)
    _to_gtable_body(src, r)
    _to_gtable_foot(src, r)
    r
  }

  private def _to_gtable_head(src: Table, r: PlainTable[String]) {
    for (sh <- src.head) {
      val hd = new PlainTable[SDoc]
      val width = src.width
      val height = sh.height
      for (y <- 0 until height; x <- 0 until width) {
        val d: String = sh.getData(x, y)
        hd.put(x, y, d)
      }
      r.setHead(hd)
    }
  }

  private def _to_gtable_body(src: Table, r: PlainTable[String]) {
    val width = src.width
    val height = src.body.height
    for (y <- 0 until height; x <- 0 until width) {
      r.put(x, y, src.body.getData(x, y))
    }
  }

  private def _to_gtable_foot(src: Table, r: PlainTable[String]) {
    for (h <- src.foot) {
      val hd = new PlainTable[SDoc]
      val width = src.width
      val height = h.height
      for (y <- 0 until height; x <- 0 until width) {
        hd.put(x, y, h.getData(x, y))
      }
// XXX
//      r.setFoot(hd)
    }
  }

  protected final def _structure_node_thema_boi_tables(
    label: NaturalLabel
  ): List[GTable[String]] = {
    _structure_node_thema_boi_tables(label, label)
  }

  protected final def _structure_node_thema_boi_tables(
    boilabel: NaturalLabel, tablelabel: NaturalLabel
  ): List[GTable[String]] = {
    List(boi(boilabel), Seq(thema)).flatten.flatMap {
      _structure_node_tables(_, tablelabel)
    }
  }

  protected final def _structure_node_tables(node: OutlineNode, label: NaturalLabel): List[GTable[String]] = {
    def isaccept(t: Table): Boolean = {
//      println(t.caption + "/" + candidates)
      t.caption.map(c => _is_match(label, c.toText())) | false
    }
    val t = node.doc.tree
    ZTrees.collect(t) {
      case t => t.rootLabel
    }.toList.collect {
      case t: Table if isaccept(t) => t
    }.map(_to_gtable)
  }

/*
  private def _structure_node_tables(node: OutlineNode, names: List[String]): List[GTable[String]] = {
    val candidates: List[String] = names.flatMap(n => List(n, n + "一覧")) 
    def isaccept(t: Table): Boolean = {
//      println(t.caption + "/" + candidates)
      t.caption.map(c => candidates.contains(c.toText())) | false
    }
    val t = node.doc.tree
    ZTrees.collect(t) {
      case t => t.rootLabel
    }.toList.collect {
      case t: Table if isaccept(t) => t 
    }.map(_to_gtable)
  }
*/

  protected final def name_labels_mark(aNode: OutlineNode): String = {
    val (term, mayMultiplicity) = CsvUtility.makeNameMark(aNode.title)
    if (aNode.isEmpty) {
      term + (if (mayMultiplicity.isDefined) mayMultiplicity.get)
    } else {
      val buf = new StringBuilder
      buf.append(term)
      buf.append('(')
      buf.append(aNode.children.map(_.title).mkString(";"))
      buf.append(')')
      if (mayMultiplicity.isDefined) {
        buf.append(mayMultiplicity.get)
      }
      buf.toString
    }
  }

  /**
   * OutlineBuilderBase#_build_part uses the method.
   */
  def isDefinition(term: TopicNode): Boolean = {
    aggregations(term).nonEmpty ||
    attributes(term).nonEmpty ||
    derivations(term).nonEmpty ||
    displays(term).nonEmpty ||
    powertypes(term).nonEmpty ||
    roles(term).nonEmpty ||
    states(term).nonEmpty ||
    annotations(term).nonEmpty ||
    primaryActors(term).nonEmpty ||
    secondaryActors(term).nonEmpty ||
    supportingActors(term).nonEmpty ||
    goals(term).nonEmpty ||
    scenario(term).nonEmpty
  }

  /**
   * OutlineBuilderBase uses the method.
   */
  def isDefined(name: String): Boolean = {
    val a: List[TopicNode] = traits ::: actors ::: resources ::: events ::: roles ::: summarys ::: rules ::: services ::: businessusecases ::: businesstasks ::: usecases ::: tasks
    val b = entityTables ::: traitTables ::: actorTables ::: resourceTables ::: eventTables ::: roleTables ::: summaryTables ::: ruleTables ::: serviceTables ::: businessusecaseTables ::: businesstaskTables ::: usecaseTables ::: taskTables
    _is_defined_in_boi(name, a) || _is_defined_in_table(name, b)
  }

  private def _is_defined_in_boi(name: String, nodes: List[TopicNode]): Boolean = {
    nodes.exists(_is_defined_in_boi(name, _))
  }

  private def _is_defined_in_boi(name: String, node: TopicNode): Boolean = {
    name == get_name_by_term(node.title)
  }

  private def _is_defined_in_table(name: String, tables: List[GTable[String]]): Boolean = {
    false // XXX
  }
}
