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

/**
 * Nov. 6, 2011 (derived from MindmapModelingXMind)
 * @since   Nov. 30, 2011 
 * @version Apr.  8, 2012
 * @author  ASAMI, Tomoharu
 */
class MindmapModelingOutliner(val outline: OutlineEntityBase) {
  val thema = outline.firstThema

  def actors: List[TopicNode] = {
    structure_node_children(thema, "登場人物")
  }

  def actorTables: List[GTable[String]] = {
    val a = boi("登場人物")
    val b = thema
    val r = List(boi("登場人物"), thema.some).flatten.flatMap {
      _structure_node_tables(_, "登場人物")
    }
//    println(r)
    r
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

  def aggregationTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, "部品")
  }

  def attributes(term: TopicNode): List[TopicNode] = {
    structure_node_children(term, "属性") // 特長
  }

  def attributeTables(term: TopicNode): List[GTable[String]] = {
    _structure_node_tables(term, "属性")
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

  def boi(name: String): Option[TopicNode] = {
    structure_node(thema, name)
  }

  def structure_node(aParent: OutlineNode, aName: String): Option[TopicNode] = {
    structure_node(aParent, List(aName))
  }

  def structure_node(aParent: OutlineNode, names: Seq[String]): Option[TopicNode] = {
    structure_node(aParent, _is_match(names, _))
  }

  def structure_node(aParent: OutlineNode, ismatch: OutlineNode => Boolean): Option[TopicNode] = {
    aParent.children.find(ismatch).asInstanceOf[Option[TopicNode]]
  }

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
  
  def structure_node_children(aParent: OutlineNode, ismatch: OutlineNode => Boolean): List[TopicNode] = {
    val mayNodes = aParent.children.find(ismatch)
    if (mayNodes.isEmpty) Nil
    else {
      mayNodes.get.children.asInstanceOf[Seq[TopicNode]].toList
    }
  }

  private def _structure_node_tables(node: OutlineNode, name: String): List[GTable[String]] = {
    _structure_node_tables(node, List(name))
  }

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
        val d: String = sh.getText(x, y)
        hd.put(x, y, d)
      }
      r.setHead(hd)
    }
  }

  private def _to_gtable_body(src: Table, r: PlainTable[String]) {
    val width = src.width
    val height = src.body.height
    for (y <- 0 until height; x <- 0 until width) {
      r.put(x, y, src.body.getText(x, y))
    }
  }

  private def _to_gtable_foot(src: Table, r: PlainTable[String]) {
    for (h <- src.foot) {
      val hd = new PlainTable[SDoc]
      val width = src.width
      val height = h.height
      for (y <- 0 until height; x <- 0 until width) {
        hd.put(x, y, h.getText(x, y))
      }
// XXX
//      r.setFoot(hd)
    }
  }

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

  def isDefinition(term: TopicNode): Boolean = {
    aggregations(term).nonEmpty ||
    attributes(term).nonEmpty ||
    derivations(term).nonEmpty ||
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
}
