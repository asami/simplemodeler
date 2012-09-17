package org.simplemodeling.SimpleModeler.generators.uml

import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.entity.content._
import org.goldenport.entity.GEntityContext
import org.goldenport.entities.graphviz._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Jan. 28, 2009
 *  version Mar. 19, 2009
 * @version Sep. 18, 2012
 * ASAMI, Tomoharu
 */
class StateMachineDiagramGenerator(sm: SimpleModelEntity) extends DiagramGeneratorBase(sm) {
//  final def makeStateMachineDiagramPng(anObject: SMObject): BinaryContent = {
//    make_statemachine_diagram_png(makeStateMachineDiagramDot(anObject))
//  }

  final def makeStateMachineDiagramPng(aStateMachine: SMStateMachine): BinaryContent = {
    make_statemachine_diagram_png(makeStateMachineDiagramDot(aStateMachine))
  }

/*
  def stream2Bytes(in: java.io.InputStream) = { 2009-03-18
    val out = new java.io.ByteArrayOutputStream
    try {
      val buffer = new Array[byte](8192)
      var done = false
      while (!done) {
	println("before read")
	val size = in.read(buffer)
	println("size = " + size)
	if (size == -1) done = true
	else {
	  out.write(buffer, 0, size)
	}
      }
      out.flush()
      out.toByteArray()
    } finally {
      out.close()
    }
  }
*/

  private def make_statemachine_diagram_png(text: StringContent): BinaryContent = {
    make_diagram_png(text)
  }
/*
  private def make_statemachine_diagram_png(text: StringContent): BinaryContent = {
    val dot: Process = context.executeCommand("dot -Tpng -Kdot -q")
    val in = dot.getInputStream()
    val out = dot.getOutputStream()
//    println("start process = " + dot)
    try {
//      println("dot = " + text.string)
      text.write(out)
      out.flush
      out.close
//      val bytes = stream2Bytes(in) 2009-03-18
//      println("bytes = " + bytes.length)
//      new BinaryContent(bytes, context)
      new BinaryContent(in, context)
    } finally {
      in.close
//      err.close
//      println("finish process = " + dot)
    }
  }
*/

  final def makeStateMachineDiagramDot(aStateMachine: SMStateMachine): StringContent = {
    val graphviz = new GraphvizEntity(context)
    graphviz.open
    val graph = new GraphStateMachine(graphviz.graph, context)
    var counter = 1
    val stateIds = new HashMap[SMState, String]

    def make_id = {
      val id = "state" + counter
      counter += 1
      id
    }

    def make_subgraph_id = {
      val id = "cluster_state" + counter
      counter += 1
      id
    }

    def get_state_id(aState: SMState) = {
      if (aState.isComposite) {
	stateIds(aState) + "_start"
      } else {
	stateIds(aState)
      }
    }

    def get_state_id_from(aState: SMState) = {
      if (aState.isComposite) {
	stateIds(aState) + "_end"
      } else {
	stateIds(aState)
      }
    }

    def get_state_id_to(aState: SMState) = {
      if (aState.isComposite) {
	stateIds(aState) + "_start"
      } else {
	stateIds(aState)
      }
    }

    def build_composite_state(aState: SMState, aCompositeState: GraphCompositeState) {
      require (aState.isComposite)
      val subStates = aState.subStates
      if (subStates.isEmpty) return
      val startId = aCompositeState.startId // XXX
      val endId = aCompositeState.endId // XXX
      aCompositeState.addStart
      for (state <- subStates) {
	if (state.isComposite) {
	  val id = make_subgraph_id
	  stateIds += (state -> id)
	  val compositeState = aCompositeState.addCompositeState(state, id)
	  build_composite_state(state, compositeState)
	} else {
	  val id = make_id
	  stateIds += (state -> id)
	  aCompositeState.addState(state, id)
	}
      }
      aCompositeState.addPseudoTransition(startId, get_state_id_to(subStates(0)))
      for (state <- subStates) {
	if (state.isTerminal) {
	  aCompositeState.addPseudoTransition(get_state_id_from(state), endId)
	}
      }
      aCompositeState.addEnd
    }

    def build_composite_state_transitions(aState: SMState) {
      for (state <- aState.subStates) {
	for (transition <- state.transitions) {
	  graph.addTransition(transition, get_state_id_from(transition.preState), get_state_id_to(transition.postState))
	}
	build_composite_state_transitions(state)
      }
    }

    try {
//      println("makeStateMachineDiagramDot start") 2009-03-01
      graph.addStart("start")
      for (state <- aStateMachine.states) {
	if (state.isComposite) {
	  val id = make_subgraph_id
	  stateIds += (state -> id)
	  val compositeState = graph.addCompositeState(state, id)
	  build_composite_state(state, compositeState)
	} else {
	  val id = make_id
	  stateIds += (state -> id)
	  graph.addState(state, id)
	}
      }
      if (counter > 1) {
	graph.addPseudoTransition("start", "state1")
      }
      for (transition <- aStateMachine.transitions) {
	graph.addTransition(transition, get_state_id_from(transition.preState), get_state_id_to(transition.postState))
      }
      for (state <- aStateMachine.states if state.isComposite) {
	build_composite_state_transitions(state)
      }
      for (state <- aStateMachine.states if state.isTerminal) {
	graph.addPseudoTransition(get_state_id(state), "end")
      }
      graph.addEnd("end")
      new StringContent(graphviz.toDotText, context)
    } finally {
      graphviz.close
//      println("makeStateMachineDiagramDot end")
    }
  }
}

abstract class GraphBase {
  protected final def is_cluster_start(id: String) = {
    id.startsWith("cluster_") && id.endsWith("_start")
  }

  protected final def is_cluster_end(id: String) = {
    id.startsWith("cluster_") && id.endsWith("_end")
  }

  protected final def get_cluster_name(id: String) = {
    require (id.startsWith("cluster_"))
    if (id.endsWith("_start")) {
      id.substring(0, id.length - "_start".length)
    } else if (id.endsWith("_end")) {
      id.substring(0, id.length - "_end".length)
    } else {
      error ("illigal id = " + id)
    }
  }
}

class GraphStateMachine(val graph: GVDigraph, val context: GEntityContext) extends GraphBase {
  val windows_normal_font = "msmincho.ttf"
  val windows_bold_font = "msgothic.ttf"

//  graph.defaultGraphAttributes.layout = "circo"
  graph.defaultGraphAttributes.shape = "box"
  if (context.isPlatformWindows) {
    graph.defaultGraphAttributes.fontname = windows_normal_font
  }
  graph.defaultGraphAttributes.fontsize = "10"
  graph.defaultGraphAttributes.compound = "true"
  graph.defaultNodeAttributes.shape = "box"
  if (context.isPlatformWindows) {
    graph.defaultNodeAttributes.fontname = windows_normal_font
  }
  graph.defaultNodeAttributes.fontsize = "10"
  if (context.isPlatformWindows) {
    graph.defaultEdgeAttributes.fontname = windows_normal_font
  }
  graph.defaultEdgeAttributes.fontsize = "10"
  graph.defaultEdgeAttributes.fontcolor = "#192f60" // 紺青 こんじょう

  final def addStart(aId: String) {
    val node = new GVNode(aId)
    node.label = " "
    node.shape = "circle"
    node.style = "filled"
    node.fixedsize = "true"
    node.width = "0.3"
    node.fillcolor = "#0d0015" // 漆黒 しっこく
    graph.elements += node
    node
  }

  final def addEnd(aId: String) {
    val node = new GVNode(aId)
    node.label = " "
    node.shape = "doublecircle"
    node.style = "filled"
    node.fixedsize = "true"
    node.width = "0.25"
    node.fillcolor = "#0d0015" // 漆黒 しっこく
    graph.elements += node
    node
  }

  final def addState(aState: SMState, aId: String): GVNode = {
    require (!aState.isComposite)
    val node = new GVNode(aId)
    node.label = aState.name
    node.style = "rounded,filled"
    node.fillcolor = "#fcc800" // 向日葵色 ひまわりいろ
    graph.elements += node
    node
  }

  final def addCompositeState(aState: SMState, aId: String): GraphCompositeState = {
    require (aState.isComposite)
    val node = new GVSubgraph(aId)
    node.label = aState.name
    node.style = "rounded,filled"
    node.fillcolor = "#fef263" // 黄檗色 きはだいろ
    graph.elements += node
    new GraphCompositeState(node, context)
  }

  final def addTransition(aTransition: SMTransition, aSourceId: String, aTargetId: String) {
    def make_label = {
      aTransition.guard match {
	case guard: SMNullGuard => aTransition.event.name 
	case guard: SMGuard => aTransition.event.name + "\\n" + "[" + guard.text + "]"
      }
    }

    val edge = new GVEdge(aSourceId, "", aTargetId, "")
    edge.arrowhead = "normal"
    edge.arrowtail = "none"
    edge.label = make_label
    edge.labelfontcolor = "#640125" // 葡萄色 えびいろ
    edge.color = "#e2041b" // 猩々緋 しょうじょうひ
    if (is_cluster_end(aSourceId)) {
      edge.ltail = get_cluster_name(aSourceId)
    }
    if (is_cluster_start(aTargetId)) {
      edge.lhead = get_cluster_name(aTargetId)
    }
    graph.edges += edge
  }

  final def addPseudoTransition(aSourceId: String, aTargetId: String) {
    val edge = new GVEdge(aSourceId, "", aTargetId, "")
    edge.arrowhead = "normal"
    edge.arrowtail = "none"
    edge.color = "#2b2b2b" // 蝋色 ろういろ
    graph.edges += edge
  }
}

class GraphCompositeState(val graph: GVSubgraph, val context: GEntityContext) extends GraphBase {
  final def startId = graph.id + "_start"
  final def endId = graph.id + "_end"

  final def addStart = {
    val node = new GVNode(startId)
    node.label = " "
    node.shape = "circle"
    node.style = "filled"
    node.fixedsize = "true"
    node.width = "0.3"
    node.fillcolor = "#0d0015" // 漆黒 しっこく
    graph.elements += node
    node
  }

  final def addEnd = {
    val node = new GVNode(endId)
    node.label = " "
    node.shape = "doublecircle"
    node.style = "filled"
    node.fixedsize = "true"
    node.width = "0.25"
    node.fillcolor = "#0d0015" // 漆黒 しっこく
    graph.elements += node
    node
  }

  final def addState(aState: SMState, aId: String): GVNode = {
    require (!aState.isComposite)
    val node = new GVNode(aId)
    node.label = aState.name
    node.style = "rounded,filled"
    node.fillcolor = "#fcc800" // 向日葵色 ひまわりいろ
    graph.elements += node
    node
  }

  final def addCompositeState(aState: SMState, aId: String): GraphCompositeState = {
    require (aState.isComposite)
    val node = new GVSubgraph(aId)
    node.label = aState.name
    node.style = "rounded,filled"
    node.fillcolor = "#fef263" // 黄檗色 きはだいろ
    graph.elements += node
    new GraphCompositeState(node, context)
  }

  final def addTransition(aTransition: SMTransition, aSourceId: String, aTargetId: String) {
    def make_label = {
      aTransition.guard match {
	case guard: SMNullGuard => aTransition.event.name 
	case guard: SMGuard => aTransition.event.name + "\\n" + "[" + guard.text + "]"
      }
    }

    val edge = new GVEdge(aSourceId, "", aTargetId, "")
    edge.arrowhead = "normal"
    edge.arrowtail = "none"
    edge.label = make_label
    edge.labelfontcolor = "#640125" // 葡萄色 えびいろ
    edge.color = "#e2041b" // 猩々緋 しょうじょうひ
    if (is_cluster_end(aSourceId)) {
      edge.ltail = get_cluster_name(aSourceId)
    }
    if (is_cluster_start(aTargetId)) {
      edge.lhead = get_cluster_name(aTargetId)
    }
    graph.edges += edge
  }

  final def addPseudoTransition(aSourceId: String, aTargetId: String) {
    val edge = new GVEdge(aSourceId, "", aTargetId, "")
    edge.arrowhead = "normal"
    edge.arrowtail = "none"
    edge.color = "#2b2b2b" // 蝋色 ろういろ
    graph.edges += edge
  }
}
