package org.simplemodeling.SimpleModeler.service

import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.Goldenport
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.xmind._
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.builder.SimpleModelMakerBuilder
import com.asamioffice.goldenport.text.{UString, UJavaString, CsvUtility}
import org.goldenport.recorder.Recordable
import org.simplemodeling.SimpleModeler.builder.Policy
import org.simplemodeling.SimpleModeler.builder.XMindBuilderBase

/*
 * @since   Feb.  1, 2009
 *  version Jun. 21, 2009
 * @version Dec. 11, 2011
 * @author  ASAMI, Tomoharu
 */
class XMindBuilder(val project: ProjectRealmEntity, packageName: String, xmind: XMindEntity,
    policy: Policy) extends XMindBuilderBase(policy, packageName, xmind) {

  val simplemodel = new SimpleModelMakerEntity(entityContext, policy)  
  val model_Builder = new SimpleModelMakerBuilder(simplemodel, packageName, policy)

  def build {
    simplemodel.open()
    xmind.open()
    build_model
    xmind.close()
    simplemodel.build
    project.copyIn("src/main/scala", simplemodel)
    simplemodel.close()
  }
}

class XMindBuilder0(val project: ProjectRealmEntity, val packageName: String, val xmind: XMindEntity,
    val policy: Policy) extends Recordable {
  val entityContext = project.entityContext
  val simplemodel = new SimpleModelMakerEntity(entityContext, policy)
  val builder = new SimpleModelMakerBuilder(simplemodel, packageName, policy)
  val packagePathname = UJavaString.packageName2pathname(packageName)

  final def build {
    simplemodel.open()
    xmind.open()

    //    println("XMindBuilder = " + xmind.toPrettyXml) 2009-02-21

    val thema = xmind.firstThema

    def get_actors: Seq[TopicNode] = {
      get_structure_node_children(thema, "登場人物")
    }

    def get_resources = {
      get_structure_node_children(thema, "道具")
    }

    def get_events = {
      get_structure_node_children(thema, "出来事")
    }

    def get_roles = {
      get_structure_node_children(thema, "役割")
    }

    def get_rules = {
      get_structure_node_children(thema, "規則")
    }

    def get_usecases = {
      get_structure_node_children(thema, "物語")
    }

    for (actor <- get_actors) {
      val term = actor.title
      val obj = builder.createObject(ActorKind, term)
      build_object(obj, actor)
    }
    for (resource <- get_resources) {
      val term = resource.title
      val obj = builder.createObject(ResourceKind, term)
      build_object(obj, resource)
    }
    for (event <- get_events) {
      val term = event.title
      val obj = builder.createObject(EventKind, term)
      build_object(obj, event)
    }
    for (role <- get_roles) {
      val term = role.title
      val obj = builder.createObject(RoleKind, term)
      build_object(obj, role)
    }
    for (rule <- get_rules) {
      val term = rule.title
      val obj = builder.createObject(RuleKind, term)
      build_object(obj, rule)
    }
    for (usecase <- get_usecases) {
      val term = usecase.title
      val obj = builder.createObject(UsecaseKind, term)
      _build_usecase(obj, usecase)
    }

    xmind.close()
    simplemodel.build
    project.copyIn("src/main/scala", simplemodel)
    simplemodel.close()
  }

  private def get_structure_node_children(aParent: TopicNode, aName: String): Seq[TopicNode] = {
    def is_match(aNode: XMindNode, theNames: Seq[String]): Boolean = {
      for (name <- theNames) {
        if (aNode.title == name) return true
      }
      false
    }

    val mayNodes = aParent.children.find(is_match(_, Array(aName, "[" + aName + "]")))
    if (mayNodes.isEmpty) Nil
    else {
      mayNodes.get.children.asInstanceOf[Seq[TopicNode]]
    }
  }

  private def make_nameLabelsMark(aNode: XMindNode): String = {
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

  private def build_object(anObject: SMMEntityEntity, aNode: TopicNode) {
    if (anObject.isDerived) {
      builder.makeAttributesForDerivedObject(anObject)
    } else {
      builder.makeAttributesForBaseObject(anObject)
    }
    _build_object_common(anObject, aNode)
  }

  private def _build_object_common(anObject: SMMEntityEntity, aNode: TopicNode) {
    val parts = get_structure_node_children(aNode, "部品")
    for (part <- parts) {
      val title = part.title
      anObject.addNarrativePart(title)
    }
    val attrs = get_structure_node_children(aNode, "特徴")
    for (attr <- attrs) {
      val title = attr.title
      anObject.addNarrativeAttribute(title)
    }
    val attrs2 = get_structure_node_children(aNode, "属性")
    for (attr <- attrs2) {
      val title = attr.title
      anObject.addNarrativeAttribute(title)
    }
    val deriveds = get_structure_node_children(aNode, "種類")
    for (derived <- deriveds) {
      val term = derived.title
      val obj = builder.createObject(anObject.kind, term)
      obj.setNarrativeBase(anObject.term)
      build_object(obj, derived)
    }
    val powertypes = get_structure_node_children(aNode, "区分")
    for (powertype <- powertypes) {
      anObject.addNarrativePowertype(make_nameLabelsMark(powertype))
    }
    val roles = get_structure_node_children(aNode, "役割")
    for (role <- roles) {
      val title = role.title
      anObject.addNarrativeRole(title)
    }
    val stateMachines = get_structure_node_children(aNode, "状況")
    for (stateMachine <- stateMachines) {
      anObject.addNarrativeStateMachine(make_nameLabelsMark(stateMachine))
    }
    val annotations = get_structure_node_children(aNode, "注記")
    for (annotation <- annotations) {
      val title = annotation.title
      anObject.addNarrativeAnnotation(title)
    }
    val primaries = get_structure_node_children(aNode, "主役")
    for (primary <- primaries) {
      anObject.addNarrativePrimaryActor(primary.title)
    }
    val secondaries = get_structure_node_children(aNode, "相手役")
    for (secondary <- secondaries) {
      anObject.addNarrativeSecondaryActor(secondary.title)
    }
    val supportings = get_structure_node_children(aNode, "脇役")
    for (supporting <- supportings) {
      anObject.addNarrativeSupportingActor(supporting.title)
    }
    val goals = get_structure_node_children(aNode, "目標")
    for (goal <- goals) {
      anObject.addNarrativeGoal(goal.title)
    }
    val status = get_structure_node_children(aNode, "状況")
    for (state <- status) {
      anObject.addNarrativeStateTransition(state.title)
    }
    val status2 = get_structure_node_children(aNode, "状況の変化")
    for (state2 <- status2) {
      anObject.addNarrativeStateTransition(state2.title)
    }
  }

  private def _build_usecase(anObject: SMMEntityEntity, aNode: TopicNode) {
    _build_object_common(anObject, aNode)
    val scenario = get_structure_node_children(aNode, "脚本")
    for (step <- scenario) {
      anObject.addNarrativeScenarioStep(step.title) // XXX
    }
  }
}
