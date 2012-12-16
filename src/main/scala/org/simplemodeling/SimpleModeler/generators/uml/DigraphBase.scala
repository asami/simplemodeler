package org.simplemodeling.SimpleModeler.generators.uml

import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.entity.content._
import org.goldenport.entity.GEntityContext
import org.goldenport.entities.graphviz._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.flow._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import com.asamioffice.goldenport.text.UString

/*
 * Derived from Graph in ClassDiagram Generator.
 * 
 * @since   Jan. 15, 2009
 *  version Mar. 27, 2011
 *  version Nov. 20, 2011
 *  version Nov. 28, 2012
 * @version Dec. 17, 2012
 * @author  ASAMI, Tomoharu
 */
class DigraphBase(val graph: GVDigraph, val context: GEntityContext) {
  var showDerivedAttribute = false

  val windows_normal_font = "msmincho.ttf"
  val windows_bold_font = "msgothic.ttf"
  val ids = new HashMap[AnyRef, String]
  private val _cluster_input_ids = new HashMap[AnyRef, String]
  private val _cluster_output_ids = new HashMap[AnyRef, String]
  private var _counter = 0

  //  graph.defaultGraphAttributes.layout = "circo"
  //  graph.defaultGraphAttributes.shape = "box"
  graph.defaultNodeAttributes.shape = "plaintext"
  if (context.isPlatformWindows) {
    graph.defaultGraphAttributes.fontname = windows_normal_font
  }
  graph.defaultGraphAttributes.fontsize = "10"
  graph.defaultGraphAttributes.compound = "true"
  if (context.isPlatformWindows) {
    graph.defaultNodeAttributes.fontname = windows_normal_font
  }
  graph.defaultNodeAttributes.fontsize = "10"
  if (context.isPlatformWindows) {
    graph.defaultEdgeAttributes.fontname = windows_normal_font
  }
  graph.defaultEdgeAttributes.fontsize = "10"
  graph.defaultEdgeAttributes.fontcolor = "#192f60" // 紺青 こんじょう

  protected def make_id(o: AnyRef): String = {
    _counter += 1
    val id = "id" + _counter;
    ids.put(o, id)
    id
  }

  protected def make_subgraph_id(o: AnyRef): String = {
    _counter += 1
    val id = "cluster_id" + _counter;
    ids.put(o, id)
    id
  }

  protected final def set_input_id(o: AnyRef, id: String) {
    _cluster_input_ids += o -> id
  }

  protected final def set_output_id(o: AnyRef, id: String) {
    _cluster_output_ids += o -> id
  }

  protected final def get_input_id(o: AnyRef): String = {
    _cluster_input_ids.getOrElse(o, ids(o))
  }

  protected final def get_output_id(o: AnyRef): String = {
    _cluster_output_ids.getOrElse(o, ids(o))
  }

  final def addClassRoot(anObject: SMObject, aId: String) {
    val node = addClassFull(anObject, aId)
    node.root = "true"
  }

  def node_color(anObject: SMElement, node: GVNode) {
    anObject match {
      case _: SMStoryObject => {
        node.style = "filled"
        node.fillcolor = get_stereotype_color(anObject)
      }
      case _ => {
        node.bgcolor = get_stereotype_color(anObject)
      }
    }
    for (c <- get_stereotype_fontcolor(anObject)) {
      node.fontcolor = c
    }
  }

  def name_compartment(anObject: SMElement, node: GVNode) {
    val comp = new GVCompartment
    get_stereotypes(anObject).foreach(comp.addLine)
    comp.addLine(anObject.name)
    node.compartments += comp
  }

  def attribute_compartment(anObject: SMObject, node: GVNode) {
    val comp = new GVCompartment
    if (showDerivedAttribute) {
      parent_attribute_lines(comp, anObject)
    }
    for (attr <- anObject.attributes) {
      comp.addLine(attribute_line(attr)) align_is "left"
    }
    node.compartments += comp
  }

  def operation_compartment(anObject: SMObject, node: GVNode) {
  }

  final def addClassFull(anObject: SMObject, aId: String): GVNode = {
    val node = new GVNode(aId)

    anObject match {
//      case task: SMTask => {
//        node_color(anObject, node)
//        name_compartment(anObject, node)
//        node.shape = "hexagon"
//      }
      case story: SMStoryObject => {
        node_color(anObject, node)
        name_compartment(anObject, node)
        node.shape = "ellipse"
      }
      case _ => {
        node_color(anObject, node)
        name_compartment(anObject, node)
        attribute_compartment(anObject, node)
        operation_compartment(anObject, node)
      }
    }
    graph.elements += node
    node
  }

  def kind_compartment(aPowertype: SMPowertype, node: GVNode) {
    val comp = new GVCompartment
    for (kind <- aPowertype.kinds) {
      comp.addLine(kind.name) align_is "left"
    }
    node.compartments += comp
  }

  final def addClassSimple(anObject: SMObject): String = {
    val id = make_id(anObject)
    addClassSimple(anObject, id)
    id
  }

  final def addClassSimple(anObject: SMObject, aId: String) {
    val node = new GVNode(aId)

    anObject match {
//      case task: SMTask => {
//        node.shape = "hexagon"
//      }
      case story: SMStoryObject => node.shape = "ellipse"
      case _ => {}
    }
    node_color(anObject, node)
    name_compartment(anObject, node)
    graph.elements += node
  }

  final def addPowertypeFull(aPowertype: SMPowertype, aId: String) {
    val node = new GVNode(aId)

    node_color(aPowertype, node)
    name_compartment(aPowertype, node)
    if (aPowertype.isKnowledge) {
      attribute_compartment(aPowertype, node)
    } else {
      kind_compartment(aPowertype, node)
    }
    graph.elements += node
  }

  final def addPowertypeSimple(aPowertype: SMPowertype, aId: String) {
    val node = new GVNode(aId)

    node_color(aPowertype, node)
    name_compartment(aPowertype, node)
    graph.elements += node
  }

  final def addStateMachineFull(aStateMachine: SMStateMachine, aId: String) {
    val node = new GVNode(aId)

    def state_compartment {
      val comp = new GVCompartment
      for ((name, state) <- aStateMachine.stateMap) {
        comp.addLine(state.name) align_is "left"
      }
      node.compartments += comp
    }

    node_color(aStateMachine, node)
    name_compartment(aStateMachine, node)
    state_compartment
    graph.elements += node
  }

  final def addStateMachineSimple(aStateMachine: SMStateMachine, aId: String) {
    val node = new GVNode(aId)

    node_color(aStateMachine, node)
    name_compartment(aStateMachine, node)
    graph.elements += node
  }

  final def addAssociationClass(anObject: SMObject, aId: String, anAssoc: SMAssociation) {
    if (anAssoc.isComposition) {
      addClassFull(anObject, aId)
    } else if (anAssoc.isAggregation) {
      addClassFull(anObject, aId)
    } else {
      addClassSimple(anObject, aId)
    }
  }

  final def addGeneralization(aChildId: String, aParentId: String) {
    val edge = new GVEdge(aParentId, "p", aChildId, "p")
    edge.arrowhead = "none"
    edge.arrowtail = "onormal"
    edge.color = "#a22041" // 真紅 しんく
    graph.edges += edge
  }

  final def addTraitRelationship(aSourceId: String, aTargetId: String, aRel: SMTraitRelationship) {
    addTraitRelationship(aSourceId, aTargetId, aRel, false)
  }

  final def addDerivedTraitRelationship(aSourceId: String, aTargetId: String, aRel: SMTraitRelationship) {
    addTraitRelationship(aSourceId, aTargetId, aRel, true)
  }

  final def addTraitRelationship(aSourceId: String, aTargetId: String, aRel: SMTraitRelationship, aDerived: Boolean) {
    val edge = make_trait_edge(aSourceId, aTargetId)
    graph.edges += edge
  }

  final def addPlainTraitRelationship(aSourceId: String, aTargetId: String, aRel: SMTraitRelationship) {
    val edge = make_trait_edge(aSourceId, aTargetId)
    graph.edges += edge
  }

  final def addSimpleTraitRelationship(aSourceId: String, aTargetId: String, aRel: SMTraitRelationship) {
    val edge = make_trait_edge(aSourceId, aTargetId)
    graph.edges += edge
  }

  private def make_trait_edge(aSourceId: String, aTargetId: String): GVEdge = {
    val edge = new GVEdge(aTargetId, "p", aSourceId, "p")
    edge.arrowhead = "none"
    edge.arrowtail = "onormal"
    edge.color = "#640125" // 葡萄色 えびいろ
    edge
  }

  // powertype
  final def addPowertypeRelationship(aSourceId: String, aTargetId: String, aRel: SMPowertypeRelationship) {
    addPowertypeRelationship(aSourceId, aTargetId, aRel, false)
  }

  final def addDerivedPowertypeRelationship(aSourceId: String, aTargetId: String, aRel: SMPowertypeRelationship) {
    addPowertypeRelationship(aSourceId, aTargetId, aRel, true)
  }

  final def addPowertypeRelationship(aSourceId: String, aTargetId: String, aRel: SMPowertypeRelationship, aDerived: Boolean) {
    val edge = make_powertype_edge(aSourceId, aTargetId)
    if (aDerived)
      edge.taillabel = "/" + aRel.name + make_multiplicity(aRel)
    else
      edge.taillabel = aRel.name + make_multiplicity(aRel)
    graph.edges += edge
  }

  final def addPlainPowertypeRelationship(aSourceId: String, aTargetId: String, aRel: SMPowertypeRelationship) {
    val edge = make_powertype_edge(aSourceId, aTargetId)
    edge.headlabel = make_multiplicity(aRel)
    graph.edges += edge
  }

  final def addSimplePowertypeRelationship(aSourceId: String, aTargetId: String, aRel: SMPowertypeRelationship) {
    val edge = make_powertype_edge(aSourceId, aTargetId)
    graph.edges += edge
  }

  private def make_powertype_edge(aSourceId: String, aTargetId: String): GVEdge = {
    val edge = new GVEdge(aTargetId, "p", aSourceId, "p")
    edge.arrowhead = "none"
    edge.arrowtail = "normal"
    edge.color = "#ba2636" // 朱・緋 あけ
    edge
  }

  /*
   * StateMachine relationship.
   *
   * XXX: see addStateMachineRelationship(aSourceId: String, aTargetId: String).
   */
  final def addStateMachineRelationship(aSourceId: String, aTargetId: String, aRel: SMStateMachineRelationship) {
    addStateMachineRelationship(aSourceId, aTargetId, aRel, false)
  }

  final def addDerivedStateMachineRelationship(aSourceId: String, aTargetId: String, aRel: SMStateMachineRelationship) {
    addStateMachineRelationship(aSourceId, aTargetId, aRel, true)
  }

  final def addStateMachineRelationship(aSourceId: String, aTargetId: String, aRel: SMStateMachineRelationship, aDerived: Boolean) {
    val edge = make_statemachine_edge(aSourceId, aTargetId)
    edge.taillabel = aRel.name
    graph.edges += edge
  }

  final def addPlainStateMachineRelationship(aSourceId: String, aTargetId: String, aRel: SMStateMachineRelationship) {
    val edge = make_statemachine_edge(aSourceId, aTargetId)
    graph.edges += edge
  }

  final def addSimpleStateMachineRelationship(aSourceId: String, aTargetId: String, aRel: SMStateMachineRelationship) {
    val edge = make_statemachine_edge(aSourceId, aTargetId)
    graph.edges += edge
  }

  private def make_statemachine_edge(aSourceId: String, aTargetId: String): GVEdge = {
    val edge = new GVEdge(aTargetId, "p", aSourceId, "p")
    edge.arrowhead = "none"
    edge.arrowtail = "none"
    edge.color = "#44617b" // 紺鼠 こんねず
    edge
  }

  // role
  final def addRoleRelationship(aSourceId: String, aTargetId: String, aRel: SMRoleRelationship) {
    addRoleRelationship(aSourceId, aTargetId, aRel, false)
  }

  final def addDerivedRoleRelationship(aSourceId: String, aTargetId: String, aRel: SMRoleRelationship) {
    addRoleRelationship(aSourceId, aTargetId, aRel, true)
  }

  final def addRoleRelationship(aSourceId: String, aTargetId: String, aRel: SMRoleRelationship, aDerived: Boolean) {
    val edge = make_role_edge(aSourceId, aTargetId)
    if (aDerived)
      edge.taillabel = "/" + aRel.name + make_multiplicity(aRel)
    else
      edge.taillabel = aRel.name + make_multiplicity(aRel)
    graph.edges += edge
  }

  final def addPlainRoleRelationship(aSourceId: String, aTargetId: String, aRel: SMRoleRelationship) {
    val edge = make_role_edge(aSourceId, aTargetId)
    edge.headlabel = make_multiplicity(aRel)
    graph.edges += edge
  }

  final def addSimpleRoleRelationship(aSourceId: String, aTargetId: String, aRel: SMRoleRelationship) {
    val edge = make_role_edge(aSourceId, aTargetId)
    graph.edges += edge
  }

  private def make_role_edge(aSourceId: String, aTargetId: String): GVEdge = {
    val edge = new GVEdge(aTargetId, "p", aSourceId, "p")
    edge.arrowhead = "none"
    edge.arrowtail = "normal"
    edge.color = "#ba2636" // 朱・緋 あけ
    edge
  }

  final def addAssociation(aSourceId: String, aTargetId: String, anAssoc: SMAssociation) {
    addAssociation(aSourceId, aTargetId, anAssoc, false)
  }

  final def addDerivedAssociation(aSourceId: String, aTargetId: String, anAssoc: SMAssociation) {
    addAssociation(aSourceId, aTargetId, anAssoc, true)
  }

  final def addAssociation(aSourceId: String, aTargetId: String, anAssoc: SMAssociation, aDerived: Boolean) {
    val edge = make_association_edge(aSourceId, aTargetId, anAssoc)
    if (aDerived)
      edge.headlabel = "/" + anAssoc.name + make_multiplicity(anAssoc)
    else
      edge.headlabel = anAssoc.name + make_multiplicity(anAssoc)
    graph.edges += edge
  }

  final def addPlainAssociation(aSourceId: String, aTargetId: String, anAssoc: SMAssociation) {
    val edge = make_association_edge(aSourceId, aTargetId, anAssoc)
    edge.headlabel = make_multiplicity(anAssoc)
    graph.edges += edge
  }

  final def addSimpleAssociation(aSourceId: String, aTargetId: String, anAssoc: SMAssociation) {
    val edge = make_association_edge(aSourceId, aTargetId, anAssoc)
    graph.edges += edge
  }

  private def make_association_edge(aSourceId: String, aTargetId: String, anAssoc: SMAssociation): GVEdge = {
    val edge = new GVEdge(aSourceId, "p", aTargetId, "p")
    edge.arrowhead = "normal"
    if (anAssoc.isComposition) {
      edge.arrowtail = "diamond"
      edge.color = "#2f5d50" // 天鵞絨 びろうど
    } else if (anAssoc.isAggregation) {
      edge.arrowtail = "odiamond"
      edge.color = "#028760" // 常磐緑 ときわみどり
    } else {
      edge.arrowtail = "none"
      edge.color = "#68be8d" // 若竹色 わかたけいろ
    }
    edge
  }

  /*
   * StateMachine relationship.
   *
   * XXX: see addStateMachineRelationship(aSourceId: String, aTargetId: String, aRel: SMStateMachineRelationship)
   */
  final def addStateMachineRelationship(aSourceId: String, aTargetId: String) {
    addStateMachineRelationship(aSourceId, aTargetId, false)
  }

  final def addDerivedStateMachineRelationship(aSourceId: String, aTargetId: String) {
    addStateMachineRelationship(aSourceId, aTargetId, true)
  }

  final def addStateMachineRelationship(aSourceId: String, aTargetId: String, aDerived: Boolean) {
    val name = ""
    val edge = make_stateMachine_edge(aSourceId, aTargetId)
    if (aDerived)
      edge.taillabel = "/" + name // XXX
    else
      edge.taillabel = name // XXX
    graph.edges += edge
  }

  final def addPlainStateMachineRelationship(aSourceId: String, aTargetId: String) {
    val edge = make_stateMachine_edge(aSourceId, aTargetId)
    graph.edges += edge
  }

  final def addSimpleStateMachineRelationship(aSourceId: String, aTargetId: String) {
    val edge = make_stateMachine_edge(aSourceId, aTargetId)
    graph.edges += edge
  }

  private def make_stateMachine_edge(aSourceId: String, aTargetId: String): GVEdge = {
    val edge = new GVEdge(aSourceId, "p", aTargetId, "p")
    edge.arrowhead = "vee"
    edge.arrowtail = "none"
    edge.style = "dashed"
    edge.color = "#68699b" // 紅掛花色 べにかけはないろ
    edge
  }

  /*
   * Document Relationship
   */
  final def addDocumentRelationship(aSourceId: String, aTargetId: String, aRel: SMDocumentRelationship) {
    addDocumentRelationship(aSourceId, aTargetId, aRel, false)
  }

  final def addDerivedDocumentRelationship(aSourceId: String, aTargetId: String, aRel: SMDocumentRelationship) {
    addDocumentRelationship(aSourceId, aTargetId, aRel, true)
  }

  final def addDocumentRelationship(aSourceId: String, aTargetId: String, aRel: SMDocumentRelationship, aDerived: Boolean) {
    val edge = make_document_edge(aSourceId, aTargetId, aRel)
    if (aDerived)
      edge.headlabel = "/" + aRel.name
    else
      edge.headlabel = aRel.name
    graph.edges += edge
  }

  final def addPlainDocumentRelationship(aSourceId: String, aTargetId: String, aRel: SMDocumentRelationship) {
    val edge = make_document_edge(aSourceId, aTargetId, aRel)
    graph.edges += edge
  }

  final def addSimpleDocumentRelationship(aSourceId: String, aTargetId: String, aRel: SMDocumentRelationship) {
    val edge = make_document_edge(aSourceId, aTargetId, aRel)
    graph.edges += edge
  }

  private def make_document_edge(aSourceId: String, aTargetId: String, aRel: SMDocumentRelationship): GVEdge = {
    val edge = new GVEdge(aSourceId, "p", aTargetId, "p")
    edge.arrowhead = "vee"
    edge.arrowtail = "none"
    edge.style = "dashed"
    edge.color = "#c3d825" // 若草色 わかくさいろ
    edge
  }

  /*
   * Association Class relationship
   */
  final def addAssociationClassRelationship(aSourceId: String, targets: Seq[(String, SMAssociation)]) {
    addAssociationClassRelationship(aSourceId, targets, false)
  }

  final def addDerivedAssociationClassRelationship(aSourceId: String, targets: Seq[(String, SMAssociation)]) {
    addAssociationClassRelationship(aSourceId, targets, true)
  }

  final def addAssociationClassRelationship(aSourceId: String, targets: Seq[(String, SMAssociation)], aDerived: Boolean) {
    val edges = build_assoc_class_edge(aSourceId, targets)
    if (aDerived)
      for ((e, a) <- edges) {
        e.headlabel = "/" + a.name + make_multiplicity(a)
      }
    else
      for ((e, a) <- edges) {
        e.headlabel = a.name + make_multiplicity(a)
      }
  }

  final def addPlainAssociationClassRelationship(aSourceId: String, targets: Seq[(String, SMAssociation)]) {
    build_assoc_class_edge(aSourceId, targets)
  }

  final def addSimpleAssociationClassRelationship(aSourceId: String, targets: Seq[(String, SMAssociation)]) {
    build_assoc_class_edge(aSourceId, targets)
  }

  private def build_assoc_class_edge(aSourceId: String, targets: Seq[(String, SMAssociation)]): Seq[(GVEdge, SMAssociation)] = {
    val color = "#d9a62e" // 櫨染はじぞめ "#fef263" // 黄檗色きはだいろ
    def makepoint = {
      val pid = make_id(aSourceId)
      val point = new GVNode(pid)
      graph.elements += point
      point.color = color
      if (targets.length > 2)
        point.shape = "diamond"
      else
        point.shape = "point"
      pid
    }
    def makesourceline(pid: String) {
      val edge = new GVEdge(aSourceId, "p", pid, "p")
      edge.arrowhead = "none"
      edge.arrowtail = "none"
      edge.style = "dashed"
      edge.color = color
      graph.edges += edge
    }
    def maketargetlines(pid: String, targets: Seq[(String, SMAssociation)]) = {
      for ((tid, assoc) <- targets) yield {
        val edge = new GVEdge(pid, "p", tid, "p")
        edge.arrowhead = "none"
        edge.arrowtail = "none"
        edge.color = color
        graph.edges += edge
        (edge, assoc)
      }
    }

    val pid = makepoint
    makesourceline(pid)
    maketargetlines(pid, targets)
  }

  /*
   * Port relationship
   */
  final def addPortRelationship(aSourceId: String, aTargetId: String, port: SMPort) {
    addPortRelationship(aSourceId, aTargetId, port, false)
  }

  final def addDerivedPortRelationship(aSourceId: String, aTargetId: String, port: SMPort) {
    addPortRelationship(aSourceId, aTargetId, port, true)
  }

  final def addPortRelationship(aSourceId: String, aTargetId: String, port: SMPort, aDerived: Boolean) {
    val edge = make_port_edge(aSourceId, aTargetId, port)
    if (aDerived)
      edge.headlabel = "/" + port.name
    else
      edge.headlabel = port.name
    graph.edges += edge
  }

  private def make_port_edge(aSourceId: String, aTargetId: String, port: SMPort): GVEdge = {
    val edge = new GVEdge(aSourceId, "p", aTargetId, "p")
    edge.arrowhead = "vee"
    edge.arrowtail = "none"
    edge.style = "dashed"
    edge.color = "#c3d825" // 若草色 わかくさいろ
    edge
  }

  /*
   * Usecase role relationship
   */
  def addUsecaseRoleRelationship(source: String, target: String, role: String) {
    val edge = new GVEdge(source, "p", target, "p")
    edge.arrowhead = "none"
    edge.arrowtail = "none"
    edge.headlabel = role
    graph.edges += edge
  }

  def addUsecaseIncludeRelationship(source: String, target: String) {
    val edge = new GVEdge(source, "p", target, "p")
    edge.arrowhead = "open"
    edge.arrowtail = "none"
    edge.style = "dashed"
    graph.edges += edge
  }

  /*
   * Dependency relationship
   */
  def addDependencyRelationship(source: String, target: String) {
    val edge = new GVEdge(source, "p", target, "p")
    edge.arrowhead = "open"
    edge.arrowtail = "none"
    edge.style = "dashed"
    graph.edges += edge
  }

  /*
   * Utilities
   */
  private def make_multiplicity(anAssoc: SMAssociation): String = {
    make_multiplicity(anAssoc.multiplicity)
  }

  private def make_multiplicity(aRel: SMPowertypeRelationship): String = {
    make_multiplicity(aRel.multiplicity)
  }

  private def make_multiplicity(aRel: SMRoleRelationship): String = {
    make_multiplicity(aRel.multiplicity)
  }

  private def make_multiplicity(multiplicity: SMMultiplicity): String = {
    if (multiplicity.kind == SMMultiplicityOne) ""
    else "  \\n" + multiplicity.text
  }

  private def get_stereotypes(elem: SMElement): Seq[String] = {
    elem match {
//      case sm: SMStateMachine => get_stereotypes(sm)
      case o: SMObject => get_stereotypes(o)
    }
  }

  private def get_stereotypes(anObject: SMObject): Seq[String] = {
//    println("DigraphBase#get_stereotypes: = " + anObject.stereotypes.map(x => "&#171;" + x + "&#187;"))
    anObject.stereotypes.map(x => "&#171;" + x + "&#187;")
  }

/*
  private def get_stereotypes0(anObject: SMObject): Seq[String] = {
    val stereotypes = new ArrayBuffer[String]

    def add_stereotype(name: String) {
      stereotypes += "&#171;" + name + "&#187;"
    }

    anObject match {
      case bu: SMBusinessUsecase => {
        add_stereotype("business")
      }
      case bt: SMBusinessTask => {
        add_stereotype("business task")
      }
      case u: SMUsecase => ;
      case t: SMTask => add_stereotype("task");
      case b: SMBusinessActor => add_stereotype("business actor")
      case b: SMBusinessActor => add_stereotype("business actor")
      case b: SMBusinessActor => add_stereotype("business actor")
      case g: SMGenericDomainEntity => {
        if (UString.notNull(g.kindName)) {
          add_stereotype(g.kindName)
        }
      } 
      case _ => {
        if (anObject.typeName != null) {
          if (!(anObject.typeName == "entity" && anObject.kindName != null)) {
            add_stereotype(anObject.typeName)
          }
        }
        if (anObject.kindName != null) {
          add_stereotype(anObject.kindName)
        }
      }
    }
    if (anObject.powertypeName != null) {
      add_stereotype(anObject.powertypeName)
    }
    stereotypes
  }
*/

//  private def get_stereotypes(aStateMachine: SMStateMachine): Seq[String] = {
//    Array("&#171;stateMachine&#187;")
//  }

  private def get_stereotype_color(elem: SMElement): String = {
    // "extension") "#ffffff"
    // "") "#f8fbf8" /// 白磁 はくじ
    elem match {
      // Business
      case _: SMBusinessActor => "#954e2a" // 柿茶かきちゃ
      case _: SMBusinessResource => "#007bbb" // 紺碧こんぺき
      case _: SMBusinessEvent => "#c9171e" // 深緋こきひ
      case _: SMBusinessUsecase => "#4f455c" // 濃鼠 こいねず
      case _: SMBusinessTask => "#4f455c" // 濃鼠 こいねず
      // Requirement
      case _: SMRequirementUsecase => "#705b67" // 葡萄鼠 ぶどうねずみ
      case _: SMRequirementTask => "#705b67" // 葡萄鼠 ぶどうねずみ
      // Domain
      case _: SMDomainActor => "#f39800" // 金茶 きんちゃ
      case _: SMDomainRole => "#e49e61" // 小麦色 こむぎいろ
      case _: SMDomainResource => "#38a1db" // 露草色 つゆくさいろ
      case _: SMDomainEvent => "#e2041b" // 猩々緋 しょうじょうひ
      case _: SMDomainSummary => "#cc7eb1" // 菖蒲色 しょうぶいろ "#a6a5c4" // 藤鼠 ふじねず "#84a2d4" // 青藤色 あおふじいろ // "#674196" // 菖蒲色 しょうぶいろ
      case _: SMDomainAssociationEntity => "#fef263" // 黄檗色きはだいろ // "#c89932" // 山吹茶やまぶきちゃ
      case _: SMDomainEntity => "#c1e4e9" // 白藍 しらあい
      case _: SMDomainTrait => "#b3ada0" // 利休白茶
      // Domain(implicit)
      case _: SMEntityPart => "#c1e4e9" // 白藍 しらあい
      case _: SMEntity => "#c1e4e9" // 白藍 しらあい
      case _: SMStateMachine => "#f5b1aa" // 珊瑚色 さんごいろ
      case _: SMRule => "#68be8d" // 若竹色わかたけいろ "#a6a5c4" // 藤鼠 ふじねず "#93ca76" // 山吹色 やまぶきいろ 
      case _: SMService => "#ebd842" // 金糸雀色かなりあいろ
      case _: SMDatatype => "#b3ada0" // 利休白茶 りきゅうしろちゃ
      case _: SMPowertype => "#7ebea5" // 青磁色 せいじいろ
      case _: SMDocument => "#93ca76" // 淡萌黄 うすもえぎ
      case _: SMValue => "#a8bf93" // 山葵色 わさびいろ
      // System(implicit)
      case _: SMDataSource => "#d4dcda" // 薄雲鼠うすくもねず
      case _: SMDataSet => "#b3ada0" // 利休白茶りきゅうしろちゃ
      case _: SMFlow => "#d8e698" // 若菜色わかないろ
      case _ => ""
    }
  }

/*
  private def get_stereotype_color(anObject: SMObject): String = {
    if (anObject.kindName == "actor") "#f39800" // 金茶 きんちゃ
    else if (anObject.kindName == "role") "#e49e61" // 小麦色 こむぎいろ
    else if (anObject.kindName == "resource") "#38a1db" // 露草色 つゆくさいろ
    else if (anObject.kindName == "event") "#e2041b" // 猩々緋 しょうじょうひ
    else if (anObject.kindName == "summary") "#674196" // 菖蒲色 しょうぶいろ
    else if (anObject.kindName == "extension") "#ffffff"
    else if (anObject.kindName == "") "#f8fbf8" /// 白磁 はくじ
    else if (anObject.typeName == "entity") "#c1e4e9" // 白藍 しらあい
    else if (anObject.typeName == "trait") "#b3ada0" // 利休白茶
    else if (anObject.typeName == "datatype") "#b3ada0" // 利休白茶 りきゅうしろちゃ
    else if (anObject.typeName == "powertype") "#7ebea5" // 青磁色 せいじいろ
    else if (anObject.typeName == "document") "#93ca76" // 淡萌黄 うすもえぎ
    else if (anObject.typeName == "rule") "#a6a5c4" // 藤鼠 ふじねず "#93ca76" // 山吹色 やまぶきいろ
    else if (anObject.typeName == "service") "#ffffff"
    else if (anObject.typeName == "usecase") "#ffffff"
    else if (anObject.typeName == "value") "#a8bf93" // 山葵色 わさびいろ
    else if (anObject.typeName == "businessTask") "#4f455c" // 濃鼠 こいねず
    else if (anObject.typeName == "businessUsecase") "#4f455c" // 濃鼠 こいねず
    else if (anObject.typeName == "requirementUsecase") "#705b67" // 葡萄鼠 ぶどうねずみ
    else if (anObject.typeName == "datasource") "#d4dcda" // 薄雲鼠うすくもねず
    else if (anObject.typeName == "dataset") "#b3ada0" // 利休白茶りきゅうしろちゃ
    else if (anObject.typeName == "flow") "#d8e698" // 若菜色わかないろ
    else ""
  }

  private def get_stereotype_color(aStateMachine: SMStateMachine): String = {
    "#f5b1aa" // 珊瑚色 さんごいろ
  }
*/

  /*
http://www.colordic.org/w/
#745399 //江戸紫 えどむらさき
"#d3381c" // 緋色 ひいろ
"#ee7800" // 橙色 だいだいいろ
"#9790a4" // 薄鼠 うすねず
*/

  private def get_stereotype_fontcolor(elem: SMElement): Option[String] = {
    elem match {
      case _: SMStoryObject => Some("white")
      case _: SMBusinessEntity => Some("white")
      case _ => None
    }
  }

  private def parent_attribute_lines(aCompartment: GVCompartment, anObject: SMObject) {
    anObject.getBaseObject match {
      case Some(parent) => {
        parent_attribute_lines(aCompartment, parent)
        for (attr <- parent.attributes) {
          aCompartment.addLine(attribute_line(attr, true)) align_is "left"
        }
      }
      case None => //
    }
  }

  private def attribute_line(anAttr: SMAttribute): String = {
    attribute_line(anAttr, false)
  }

  private def attribute_line(anAttr: SMAttribute, aDerived: Boolean): String = {
    val buffer = new StringBuilder
    if (aDerived) {
      buffer.append("/")
    }
    buffer.append(anAttr.name)
    buffer.append(":")
    buffer.append(anAttr.attributeType.name)
    if (anAttr.multiplicity.kind != SMMultiplicityOne) {
      buffer.append("[")
      buffer.append(anAttr.multiplicity.text)
      buffer.append("]")
    }
    if (!anAttr.multiplicity.keywords.isEmpty) {
      buffer.append("{")
      buffer.append(anAttr.multiplicity.keywords.mkString(","))
      buffer.append("}")
    }
    buffer.toString
  }
}
