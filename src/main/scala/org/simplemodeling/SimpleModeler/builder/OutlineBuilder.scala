package org.simplemodeling.SimpleModeler.builder

import scala.collection.mutable.ArrayBuffer
import com.asamioffice.goldenport.text.CsvUtility
import org.goldenport.value.GTreeNode

/*
 * @since   Feb. 22, 2012
 * @version Feb. 23, 2012
 * @author  ASAMI, Tomoharu
 */
class OutlineBuilder[T](val root: GTreeNode[T]) {
  case class EntityEntry(node: GTreeNode[T], name: String, base: String, builder: (GTreeNode[T]) => Unit)
  private val _child_entities = new ArrayBuffer[EntityEntry]()

  def registerActor(name: String, base: Option[String], builder: GTreeNode[T] => Unit) = {
    registerEntity(actors, name, base, builder)
  }

  def registerResource(name: String, base: Option[String], builder: GTreeNode[T] => Unit) = {
    registerEntity(resources, name, base, builder)
  }

  def registerEvent(name: String, base: Option[String], builder: GTreeNode[T] => Unit) = {
    registerEntity(events, name, base, builder)
  }

  def registerRole(name: String, base: Option[String], builder: GTreeNode[T] => Unit) = {
    registerEntity(roles, name, base, builder)
  }

  def registerSummary(name: String, base: Option[String], builder: GTreeNode[T] => Unit) = {
    registerEntity(summaries, name, base, builder)
  }

  def registerRule(name: String, base: Option[String], builder: GTreeNode[T] => Unit) = {
    registerEntity(rules, name, base, builder)
  }

  def registerUsecase(name: String, base: Option[String], builder: GTreeNode[T] => Unit) = {
    registerEntity(usecases, name, base, builder)
  }

  def registerEntity(parent: GTreeNode[T], name: String, base: Option[String], builder: GTreeNode[T] => Unit) {
    base match {
      case Some(b) => {
        _child_entities += (EntityEntry(parent, name, b, builder))
      }
      case None => {
        val entity = getEntity(parent, name)
        builder(entity)
      }
    }
  }

  def getEntity(node: GTreeNode[T], name: String): GTreeNode[T] = {
    val entity = node.addChild()
    entity.title = name
    entity
  }

  def findEntity(node: GTreeNode[T], name: String): Option[GTreeNode[T]] = {
    node.children.find(_.title == name) match {
      case Some(n) => Some(n)
      case None => {
        for (c <- node.children) {
          _find_entity_structure_node(c, "種類") match {
            case Some(cc) => {
              val r = findEntity(cc, name)
              if (r.isDefined) return r
            }
            case None => {}
          }
        }
        None
      }
    }
  }

  def addNname(entity: GTreeNode[T], value: String) = {
    val child = _get_entity_structure_node(entity, "注記")
    val node = child.addChild()
    node.title = "name=" + value
    node
  }

  def addNameEn(entity: GTreeNode[T], value: String) = {
    val child = _get_entity_structure_node(entity, "注記")
    val node = child.addChild()
    node.title = "name_en=" + value
    node
  }

  def addNameJa(entity: GTreeNode[T], value: String) = {
    val child = _get_entity_structure_node(entity, "注記")
    val node = child.addChild()
    node.title = "name_ja=" + value
    node
  }

  def addTerm(entity: GTreeNode[T], value: String) = {
    val child = _get_entity_structure_node(entity, "注記")
    val node = child.addChild()
    node.title = "term=" + value
    node
  }

  def addTermEn(entity: GTreeNode[T], value: String) = {
    val child = _get_entity_structure_node(entity, "注記")
    val node = child.addChild()
    node.title = "term_en=" + value
    node
  }

  def addTermJa(entity: GTreeNode[T], value: String) = {
    val child = _get_entity_structure_node(entity, "注記")
    val node = child.addChild()
    node.title = "term_ja=" + value
    node
  }

  def addCaption(entity: GTreeNode[T], value: String) = {
    val child = _get_entity_structure_node(entity, "注記")
    val node = child.addChild()
    node.title = "caption=" + value
    node
  }

  def addBrief(entity: GTreeNode[T], value: String) = {
    val child = _get_entity_structure_node(entity, "注記")
    val node = child.addChild()
    node.title = "brief=" + value
    node
  }

  def addSummary(entity: GTreeNode[T], value: String) = {
    val child = _get_entity_structure_node(entity, "注記")
    val node = child.addChild()
    node.title = "summary=" + value
    node
  }
/*
  def adddescription(entity: GTreeNode[T], value: String) = {
    val node = entity.addChild()
    node.title = value
    node
  }
*/
  def addTableName(child: GTreeNode[T], value: String) = {
    val entity = _get_entity_structure_node(child, "注記")
    val node = entity.addChild()
    node.title = "tableName=" + value
    node
  }

  def addAttrs(entity: GTreeNode[T], value: String) = {
    def add_attr(aAttr: String) {
      val child = _get_entity_structure_node(entity, "属性")
      val node = child.addChild()
      node.title = aAttr
    }

    value.split("[;, ]+").foreach(add_attr)
  }

  @deprecated
  def addParts(entity: GTreeNode[T], value: String) = {
    def add_part(aPart: String) {
      val child = _get_entity_structure_node(entity, "部品")
      val node = child.addChild()
      node.title = aPart
    }

    value.split("[;, ]+").foreach(add_part)
  }

  def addComposition(entity: GTreeNode[T], value: String, builder: GTreeNode[T] => Unit) = {
    val part = _add_structure_slot(entity, "部品", value)
    builder(part)
    part
  }

  def addAggregation(entity: GTreeNode[T], value: String) = {
    _add_structure_slot(entity, "部品", value)
  }

  def addAssociation(entity: GTreeNode[T], value: String) = {
    _add_structure_slot(entity, "参照", value)
  }

  def addBase(entity: GTreeNode[T], value: String) = {
    // do nothing
  }

  def addNameLabelsMark(aValue: String, aParent: GTreeNode[T]) {
    val (name, labels, mark) = CsvUtility.makeNameLabelsMark(aValue)
//	  println("csv utility = " + aValue + " -> " + name + "," + labels + "," + mark)
    val node = aParent.addChild()
    node.title = if (mark.isDefined) name + mark.get else name
    for (label <- labels) {
      val child = node.addChild()
      child.title = label
    }
  }

  def addPowers(entity: GTreeNode[T], value: String) = {
    def add_power(aPower: String) {
      val child = _get_entity_structure_node(entity, "区分")
      addNameLabelsMark(aPower, child)
    }
    CsvUtility.makeItems(value).foreach(add_power)
  }

  def addStates(entity: GTreeNode[T], value: String) = {
    def add_state(aState: String) {
      val child = _get_entity_structure_node(entity, "状況")
      addNameLabelsMark(aState, child)
    }
    CsvUtility.makeItems(value).foreach(add_state)
  }

  def addRoles(entity: GTreeNode[T], value: String) = {
    def add_role(aRole: String) {
      val child = _get_entity_structure_node(entity, "役割")
      val node = child.addChild()
      node.title = aRole
    }
    value.split("[;, ]+").foreach(add_role)
  }

  def actors = {
    _get_boi_structure_node("登場人物")
  }

  def resources = {
    _get_boi_structure_node("道具")
  }

  def events = {
    _get_boi_structure_node("出来事")
  }

  def roles = {
    _get_boi_structure_node("役割")
  }

  def summaries = {
    _get_boi_structure_node("要約")
  }

  def rules = {
    _get_boi_structure_node("規則")
  }

  def usecases = {
    _get_boi_structure_node("物語")
  }

  def memo = {
    _get_boi_structure_node("メモ")
  }

  private def _get_boi_structure_node(aTitle: String): GTreeNode[T] = {
    val nodeName = "[" + aTitle + "]"
    root.children.find(_.title == nodeName) match {
      case Some(node) => node
      case None => {
        val node = root.addChild()
        node.title = nodeName
        node
      }
    }
  }

  private def _get_entity_structure_node(anEntity: GTreeNode[T], aTitle: String): GTreeNode[T] = {
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

  private def _find_entity_structure_node(anEntity: GTreeNode[T], aTitle: String): Option[GTreeNode[T]] = {
    val nodeName = "[" + aTitle + "]"
    anEntity.children.find(_.title == nodeName)
  }

  private def _add_structure_slot(entity: GTreeNode[T], slotname: String, value: String) = {
    val child = _get_entity_structure_node(entity, slotname)
    val node = child.addChild()
    node.title = value
    node
  }

  def _resolve_entity(entry: EntityEntry) {
    val entity = findEntity(entry.node, entry.base) match {
      case Some(b) => {
        val n = _get_entity_structure_node(b, "種類")
        getEntity(n, entry.name)
      }
      case None => {
        println("Warning: " + entry)
        getEntity(entry.node, entry.name)
      }
    }
    entry.builder(entity)
  }

  def resolve() {
    _child_entities.foreach(_resolve_entity)
  }
}
