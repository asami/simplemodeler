package org.simplemodeling.SimpleModeler.importer

import org.goldenport.entities.outline._
import com.asamioffice.goldenport.text.CsvUtility

/*
 * Nov. 6, 2011 (derived from MindmapModelingXMind)
 * @since   Nov. 30, 2011 
 * @version Nov. 30, 2011
 * @author  ASAMI, Tomoharu
 */
class MindmapModelingOutliner(val outline:  OutlineEntityBase) {
  val thema = outline.firstThema

  def actors: List[TopicNode] = {
    structure_node_children(thema, "登場人物")
  }

  def resources = {
    structure_node_children(thema, "道具")
  }

  def events = {
    structure_node_children(thema, "出来事")
  }

  def roles = {
    structure_node_children(thema, "役割")
  }

  def rules = {
    structure_node_children(thema, "規則")
  }

  def usecases = {
    structure_node_children(thema, "物語")
  }

  def aggregations(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "部品")
  }

  def attributes(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "属性") // 特長
  }

  def derivations(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "種類")
  }

  def powertypes(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "区分")
  }

  def roles(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "役割")
  }

  def states(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "状況")
  }

  def annotations(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "注記")
  }

  def primaryActors(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "主役")
  }

  def secondaryActors(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "相手役")
  }

  def supportingActors(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "脇役")
  }

  def goals(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "目標")
  }

  def scenario(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "脚本")
  }
  def structure_node_children(aParent: TopicNode, aName: String): List[TopicNode] = {
    def is_match(aNode: OutlineNode, theNames: Seq[String]): Boolean = {
      for (name <- theNames) {
        if (aNode.title == name) return true
      }
      false
    }

    val mayNodes = aParent.children.find(is_match(_, Array(aName, "[" + aName + "]")))
    if (mayNodes.isEmpty) Nil
    else {
      mayNodes.get.children.asInstanceOf[Seq[TopicNode]].toList
    }
  }

  def name_labels_mark(aNode: OutlineNode): String = {
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
}
