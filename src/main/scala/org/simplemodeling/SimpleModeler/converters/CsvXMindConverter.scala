package org.simplemodeling.SimpleModeler.converters

import scala.collection.mutable.ArrayBuffer
import org.goldenport.Goldenport
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.value.GTreeNode
import org.goldenport.entity.datasource.{NullDataSource, ResourceDataSource}
import org.goldenport.entity.content._
import org.goldenport.entities.csv.CsvEntity
import org.goldenport.entities.xmind.{XMindEntity, XMindNode}
import com.asamioffice.goldenport.text.{UString, CsvUtility}
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.values.smcsv.SimpleModelCsvTabular
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Feb.  3, 2009
 *  version Nov. 13, 2010
 * @version Feb. 23, 2012
 * @author  ASAMI, Tomoharu
 */
class CsvXMindConverter(policy: Policy, packageName: String, csv: CsvEntity, val projectName: String)
    extends CsvBuilderBase(policy, packageName, csv) {
  val entityContext = csv.entityContext
  val xmind = new XMindEntity(entityContext)

  val simplemodel = new SimpleModelMakerEntity(entityContext, policy)
  val model_Builder = new SimpleModelMakerBuilder(simplemodel, packageName, policy)
  xmind.open()
  val thema = xmind.firstThema
  private val _outline_builder = new OutlineBuilder[XMindNode](thema)

  def toXMind: XMindEntity = {
    csv using {
      _build_xmind
    }
  }

  private def _build_xmind = {
    thema.title = projectName
    simplemodel.using {
      xmind.open()
      _build
//      _resolve
      xmind ensuring (_.isOpened)
    }
  }

  private def _build {
//    implicit val s = org.goldenport.entity.content.GContentShow
    implicit val s = new org.goldenport.value.GTreeNodeShow[org.goldenport.entity.content.GContent]
    build_model
    simplemodel.build
    val tree = simplemodel.ztree
    println(tree.drawTree)
    for (n <- tree.flatten) {
      n.content match {
        case ec: EntityContent => _build_entity(ec.entity)
        case _ => {}
      }
    }
    _outline_builder.resolve()
  }

  private def _build_entity(entity: GEntity) {
    entity match {
      case e: SMMEntityEntity => _build_entity(e)
      case _ => {}
    }
  }

  private def _build_entity(entity: SMMEntityEntity) {
    println("build = " + entity)    
    entity.kind match {
      case ActorKind => _build_actor(entity)
      case ResourceKind => _build_resource(entity)
      case EventKind => _build_event(entity)
      case RoleKind => _build_role(entity)
      case SummaryKind => _build_summary(entity)
      case EntityKind => _build_plain_entity(entity)
      case RuleKind => _build_rule(entity)
      case UsecaseKind => _build_rule(entity)
      case StateMachineKind => {}
      case StateMachineStateKind => {} 
      case _ => {}
    }
  }

  private def _build_actor(entity: SMMEntityEntity) {
    _outline_builder.registerActor(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_resource(entity: SMMEntityEntity) {
    _outline_builder.registerResource(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_event(entity: SMMEntityEntity) {
    _outline_builder.registerEvent(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_role(entity: SMMEntityEntity) {
    _outline_builder.registerRole(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_summary(entity: SMMEntityEntity) {
    _outline_builder.registerSummary(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_plain_entity(entity: SMMEntityEntity) {
    // do nothing
  }

  private def _build_rule(entity: SMMEntityEntity) {
    _outline_builder.registerRule(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_usecase(entity: SMMEntityEntity) {
    _outline_builder.registerUsecase(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_object_body(src: SMMEntityEntity, node: GTreeNode[XMindNode]) {
//    _outline_builder.addName(node, src.name)
    if (UString.notNull(src.name_en)) {
      _outline_builder.addNameEn(node, src.name_en)
    }
    if (UString.notNull(src.name_ja)) {
      _outline_builder.addNameJa(node, src.name_ja)
    }
    if (UString.notNull(src.term)) {
      _outline_builder.addTerm(node, src.term)
    }
    if (UString.notNull(src.name_en)) {
      _outline_builder.addTermEn(node, src.term_en)
    }
    if (UString.notNull(src.name_ja)) {
      _outline_builder.addTermJa(node, src.term_ja)
    }
    if (UString.notNull(src.caption)) {
      _outline_builder.addCaption(node, src.caption)
    }
    if (UString.notNull(src.brief)) {
      _outline_builder.addBrief(node, src.brief)
    }
    if (UString.notNull(src.summary)) {
       _outline_builder.addSummary(node, src.summary)
    }
    if (UString.notNull(src.tableName)) {
      _outline_builder.addTableName(node, src.name)
    }
//    _outline_builder.addStates(node, src.name)
//    _outline_builder.addRoles(node, src.name)
    for (a <- src.attributes) {
      _outline_builder.addAttrs(node, a.name)
    }
    for (c <- src.compositions) {
      _outline_builder.addComposition(node, c.name, _build_object_body(src, _))
    }
    for (a <- src.aggregations) {
      _outline_builder.addAggregation(node, a.name)
    }
    for (a <- src.associations) {
      _outline_builder.addAssociation(node, a.name)
    }
    for (p <- src.powertypes) {
      _outline_builder.addPowers(node, p.name)
    }
  }

/*
  def get_entity_structure_node(anEntity: XMindNode, aTitle: String): XMindNode = {
    val nodeName = "[" + aTitle + "]"
    anEntity.children.find(_.title == nodeName) match {
      case Some(node) => node
      case None => {
        val node = anEntity.addChild()
        node.title = nodeName
        node
      }
    }
  }

  def find_entity_structure_node(anEntity: XMindNode, aTitle: String): Option[XMindNode] = {
    val nodeName = "[" + aTitle + "]"
    anEntity.children.find(_.title == nodeName)
  }

  def find_child_in_tree(aParent: XMindNode, aTitle: String): Option[XMindNode] = {
    aParent.children.find(_.title == aTitle) match {
      case Some(node) => Some(node)
      case None => {
        for (entity <- aParent.children) {
          find_entity_structure_node(entity, "種類") match {
            case Some(structureNode) => {
              find_child_in_tree(structureNode, aTitle) match {
                case Some(node2) => Some(node2)
                case None        => //
              }
            }
            case None => //
          }
        }
        None
      }
    }
  }

  def get_entity_node(aTitle: String): XMindNode = {
    def find_child(aParent: XMindNode): Option[XMindNode] = {
      find_child_in_tree(aParent, aTitle)
    }

    find_child(get_actors_node) match {
      case Some(node) => return node
      case None       => //
    }
    find_child(get_resources_node) match {
      case Some(node) => return node
      case None       => //
    }
    find_child(get_events_node) match {
      case Some(node) => return node
      case None       => //
    }
    find_child(get_roles_node) match {
      case Some(node) => return node
      case None       => //
    }
    error("syntax error")
  }

  private def _resolve {
    for ((baseName, derivedName, y) <- generalization_candidates) {
      val base = get_entity_node(baseName)
      val parent = get_entity_structure_node(base, "種類")
      val node = parent.addChild()
      node.title = derivedName
      node
      build_body(node, y)
    }
  }
*/
}

class CsvXMindConverter0(val csv: CsvEntity, val projectName: String) {
  val entityContext = csv.entityContext
  csv.open()
//  println("CsvXMind csv = " + csv.width + "/" + csv.height) 2009-02-07
  val tabular = new SimpleModelCsvTabular(csv)
  tabular.build
//  println("CsvXMind tabular = " + tabular.width + "/" + tabular.height)
  csv.close()

  final def toXMind: XMindEntity = {
/* 2009-02-05
    val prototype = new ResourceDataSource("org/simplemodeling/SimpleModeler/entities/xmind/prototype.xmind", entityContext)
    println("prototype = " + prototype.isExist)
    val xmind = new XMindEntity(prototype, NullDataSource, entityContext)
*/
    val generalization_candidates = new ArrayBuffer[(String, String, Int)]
    val xmind = new XMindEntity(entityContext)
    xmind.open()
    val thema = xmind.firstThema
    thema.title = projectName

    def get_actors_node = {
      get_boi_structure_node("登場人物")
    }

    def get_resources_node = {
      get_boi_structure_node("道具")
    }

    def get_events_node = {
      get_boi_structure_node("出来事")
    }

    def get_roles_node = {
      get_boi_structure_node("役割")
    }

    def get_rules_node = {
      get_boi_structure_node("規則")
    }

    def get_usecases_node = {
      get_boi_structure_node("物語")
    }

    def get_memo_node = {
      get_boi_structure_node("メモ")
    }

    def get_boi_structure_node(aTitle: String): XMindNode = {
      val nodeName = "[" + aTitle + "]"
      thema.children.find(_.title == nodeName) match {
	case Some(node) => node
	case None => {
	  val node = thema.addChild()
	  node.title = nodeName
	  node
	}
      }
    }

    def get_entity_structure_node(anEntity: XMindNode, aTitle: String): XMindNode = {
      val nodeName = "[" + aTitle + "]"
      anEntity.children.find(_.title == nodeName) match {
	case Some(node) => node
	case None => {
	  val node = anEntity.addChild()
	  node.title = nodeName
	  node
	}
      }
    }

    def find_entity_structure_node(anEntity: XMindNode, aTitle: String): Option[XMindNode] = {
      val nodeName = "[" + aTitle + "]"
      anEntity.children.find(_.title == nodeName)
    }

    def find_child_in_tree(aParent: XMindNode, aTitle: String): Option[XMindNode] = {
      aParent.children.find(_.title == aTitle) match {
	case Some(node) => Some(node)
	case None => {
	  for (entity <- aParent.children) {
	    find_entity_structure_node(entity, "種類") match {
	      case Some(structureNode) => {
		find_child_in_tree(structureNode, aTitle) match {
		  case Some(node2) => Some(node2)
		  case None => //
		}
	      }
	      case None => //
	    }
	  }
	  None
	}
      }
    }

    def get_entity_node(aTitle: String): XMindNode = {
      def find_child(aParent: XMindNode): Option[XMindNode] = {
	find_child_in_tree(aParent, aTitle)
      }

      find_child(get_actors_node) match {
	case Some(node) => return node
	case None => //
      }
      find_child(get_resources_node) match {
	case Some(node) => return node
	case None => //
      }
      find_child(get_events_node) match {
	case Some(node) => return node
	case None => //
      }
      find_child(get_roles_node) match {
	case Some(node) => return node
	case None => //
      }
      error("syntax error")
    }

    val actors = get_actors_node
    val resources = get_resources_node
    val events = get_events_node
    val roles = get_roles_node
    val rules = get_rules_node
    val usecases = get_usecases_node
    val memo = get_memo_node

    def add_generalization(aBase: String, aDerived: String, y: Int) {
      generalization_candidates += ((aBase, aDerived, y))
    }

    def find_base(y: Int): Option[String] = {
      for (x <- 1 until tabular.width) {
	val annotation = tabular.annotations.get(x, y)
	if (annotation.key == "base") {
	  val value = tabular.get(x, y)
	  if (value != null && value != "") return Some(value)
	  else return None
	}
      }
      None
    }

    def build_line(y: Int) {
      find_base(y) match {
	case Some(base) => {
	  add_generalization(base, tabular.get(0, y), y)
	}
	case None => build_entity(y)
      }
    }

    def build_entity(y: Int) {
      val name = tabular.get(0, y)
      //      println("Csv2Xmind = " + name) 2009-02-07

      def add_actor = {
	val node = actors.addChild()
	node.title = name
	node
      }

      def add_resource = {
	val node = resources.addChild()
	node.title = name
	node
      }

      def add_event = {
	val node = events.addChild()
	node.title = name
	node
      }

      def add_role = {
	val node = roles.addChild()
	node.title = name
	node
      }

      def add_rule = {
	val node = rules.addChild()
	node.title = name
	node
      }

      def add_usecase = {
	val node = usecases.addChild()
	node.title = name
	node
      }

      def add_memo = {
	val node = memo.addChild()
	node.title = name
	node
      }

      val obj = tabular.annotations.get(0, y).key match {
	case "actor" => add_actor
	case "resource" => add_resource
	case "event" => add_event
	case "role" => add_role
	case "rule" => add_rule
	case "usecase" => add_usecase
	case "memo" => add_memo
	case _ => add_memo
      }
      build_body(obj, y)
    }

    def build_body(obj: XMindNode, y: Int) {
      for (x <- 1 until tabular.width) {
	val annotation = tabular.annotations.get(x, y)
	val value = tabular.get(x, y)

	def get_structure_node(aTitle: String): XMindNode = {
	  get_entity_structure_node(obj, aTitle)
	}

	def add_name {
	  val parent = get_structure_node("注記")
	  val node = parent.addChild()
	  node.title = "name=" + value
	  node
	}

	def add_name_en {
	  val parent = get_structure_node("注記")
	  val node = parent.addChild()
	  node.title = "name_en=" + value
	  node
	}

	def add_name_ja {
	  val parent = get_structure_node("注記")
	  val node = parent.addChild()
	  node.title = "name_ja=" + value
	  node
	}

	def add_term_en {
	  val parent = get_structure_node("注記")
	  val node = parent.addChild()
	  node.title = "term_en=" + value
	  node
	}

	def add_term_ja {
	  val parent = get_structure_node("注記")
	  val node = parent.addChild()
	  node.title = "term_ja=" + value
	  node
	}

	def add_caption {
	  val parent = get_structure_node("注記")
	  val node = parent.addChild()
	  node.title = "caption=" + value
	  node
	}

	def add_brief {
	  val parent = get_structure_node("注記")
	  val node = parent.addChild()
	  node.title = "brief=" + value
	  node
	}

	def add_summary {
	  val parent = get_structure_node("注記")
	  val node = parent.addChild()
	  node.title = "summary=" + value
	  node
	}

	def add_description {
	  val node = obj.addChild()
	  node.title = value
	  node
	}

	def add_tableName {
	  val parent = get_structure_node("注記")
	  val node = parent.addChild()
	  node.title = "tableName=" + value
	  node
	}

	def add_attrs {
	  def add_attr(aAttr: String) {
	    val parent = get_structure_node("特徴")
	    val node = parent.addChild()
	    node.title = aAttr
	  }

	  value.split("[;, ]+").foreach(add_attr)
	}

	def add_parts {
	  def add_part(aPart: String) {
	    val parent = get_structure_node("部品")
	    val node = parent.addChild()
	    node.title = aPart
	  }

	  value.split("[;, ]+").foreach(add_part)
	}

	def add_base {
	  // do nothing
	}

	def add_nameLabelsMark(aValue: String, aParent: XMindNode) {
	  val (name, labels, mark) = CsvUtility.makeNameLabelsMark(aValue)
//	  println("csv utility = " + aValue + " -> " + name + "," + labels + "," + mark)
	  val node = aParent.addChild()
	  node.title = if (mark.isDefined) name + mark.get else name
	  for (label <- labels) {
	    val child = node.addChild()
	    child.title = label
	  }
	}

	def add_powers {
	  def add_power(aPower: String) {
	    val parent = get_structure_node("区分")
	    add_nameLabelsMark(aPower, parent)
	  }

	  CsvUtility.makeItems(value).foreach(add_power)
	}

	def add_states {
	  def add_state(aState: String) {
	    val parent = get_structure_node("状況")
	    add_nameLabelsMark(aState, parent)
	  }

	  CsvUtility.makeItems(value).foreach(add_state)
	}

	def add_roles {
	  def add_role(aRole: String) {
	    val parent = get_structure_node("役割")
	    val node = parent.addChild()
	    node.title = aRole
	  }

	  value.split("[;, ]+").foreach(add_role)
	}

	if (value != null && value != "" && annotation != null) {
	  annotation.key match {
	    case "name" => add_name
	    case "name_en" => add_name_en
	    case "name_ja" => add_name_ja
	    case "term_en" => add_term_en
	    case "term_ja" => add_term_ja
	    case "caption" => add_caption
	    case "brief" => add_brief
	    case "summary" => add_summary
	    case "description" => add_description
	    case "tableName" => add_tableName
	    case "attrs" => add_attrs
	    case "parts" => add_parts
	    case "base" => add_base
	    case "powers" => add_powers
	    case "states" => add_states
	    case "roles" => add_roles
	  }
	}
      }
    }

    def build {
      for (y <- 0 until tabular.height) {
	build_line(y)
      }
    }

    def resolve {
      for ((baseName, derivedName, y) <- generalization_candidates) {
	val base = get_entity_node(baseName)
	val parent = get_entity_structure_node(base, "種類")
	val node = parent.addChild()
	node.title = derivedName
	node
	build_body(node, y)
      }
    }

    build
    resolve
    xmind ensuring (_.isOpened)
  }
}
