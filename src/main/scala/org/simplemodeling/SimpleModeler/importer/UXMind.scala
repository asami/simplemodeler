package org.simplemodeling.SimpleModeler.importer

import org.goldenport.entities.outline._
import org.goldenport.entities.xmind._
import com.asamioffice.goldenport.text.{UString, UJavaString, CsvUtility}

/*
 * @since   Nov.  6, 2011
 *  version Nov.  6, 2011
 *  version Jan. 25, 2012
 * @version Oct. 19, 2012
 * @author  ASAMI, Tomoharu
 */
trait UXMind {
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

  def structure_node_children(aParent: TopicNode, names: List[String]): List[TopicNode] = {
    names.flatMap(structure_node_children(aParent, _))
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

object UXMind extends UXMind {
}
