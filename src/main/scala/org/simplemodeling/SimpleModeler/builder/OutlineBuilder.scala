package org.simplemodeling.SimpleModeler.builder

import com.asamioffice.goldenport.text.CsvUtility
import org.goldenport.value.GTreeNode

/*
 * @since   Feb. 22, 2012
 * @version Feb. 22, 2012
 * @author  ASAMI, Tomoharu
 */
class OutlineBuilder[T](val root: GTreeNode[T]) {
  def setName(entity: GTreeNode[T], value: String) = {
    entity.title = value
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
      val child = _get_entity_structure_node(entity, "特徴")
      val node = child.addChild()
      node.title = aAttr
    }

    value.split("[;, ]+").foreach(add_attr)
  }

  def addParts(entity: GTreeNode[T], value: String) = {
    def add_part(aPart: String) {
      val child = _get_entity_structure_node(entity, "部品")
      val node = child.addChild()
      node.title = aPart
    }

    value.split("[;, ]+").foreach(add_part)
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
}
