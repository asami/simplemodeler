package org.simplemodeling.SimpleModeler.importer

import org.goldenport.entities.outline._
import org.goldenport.entities.xmind._

/*
 * @since   Nov.  6, 2011
 *  version Nov.  6, 2011
 *  version Jan. 25, 2012
 *  version Oct. 19, 2012
 * @version Nov.  3, 2012
 * @author  ASAMI, Tomoharu
 */
case class MindmapModelingXMind(val xmind:  XMindEntity) {
  import UXMind._

  val thema = xmind.firstThema

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

  def businessusecases = {
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
    structure_node_children(term, List("状況", "状況の変化"))
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
