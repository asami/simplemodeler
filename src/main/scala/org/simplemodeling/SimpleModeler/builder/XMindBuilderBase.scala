package org.simplemodeling.SimpleModeler.builder

import org.simplemodeling.SimpleModeler.entities.simplemodel.ActorKind
import org.simplemodeling.SimpleModeler.entities.simplemodel.RoleKind
import org.simplemodeling.SimpleModeler.entities.simplemodel.SMMEntityEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel.ResourceKind
import org.goldenport.service.GServiceCall
import org.simplemodeling.SimpleModeler.entities.simplemodel.RuleKind
import org.simplemodeling.dsl.SObject
import org.simplemodeling.SimpleModeler.entities.simplemodel.UsecaseKind
import org.goldenport.entities.xmind.XMindEntity
import org.goldenport.entities.xmind.TopicNode
import org.simplemodeling.SimpleModeler.entities.simplemodel.ElementKind
import org.simplemodeling.SimpleModeler.entities.simplemodel.EventKind
import org.goldenport.recorder.Recordable
import org.simplemodeling.SimpleModeler.importer.UXMind
import org.simplemodeling.SimpleModeler.importer.MindmapModelingXMind

/*
 * Derived from XMindBuilder and XMindImporter
 * 
 * @since   Dec. 11, 2011
 *  version Feb. 27, 2012
 * @version Oct.  2, 2012
 * @author  ASAMI, Tomoharu
 */
@deprecated("use OutlineBuilderBase")
abstract class XMindBuilderBase(val policy: Policy, val packageName: String, val xmind: XMindEntity) extends Recordable {
  import UXMind._

  val entityContext = xmind.entityContext
  protected val model_Builder: SimpleModelBuilder
  private lazy val _mmx = MindmapModelingXMind(xmind)

  setup_FowardingRecorder(xmind.entityContext)

  protected final def build_model {
    xmind.open()
    try {
      _mmx.actors.foreach(_create_object(ActorKind, _, _build_object))
      _mmx.resources.foreach(_create_object(ResourceKind, _, _build_object))
      _mmx.events.foreach(_create_object(EventKind, _, _build_object))
      _mmx.roles.foreach(_create_object(RoleKind, _, _build_object))
      _mmx.rules.foreach(_create_object(RuleKind, _, _build_rule))
      _mmx.usecases.foreach(_create_object(UsecaseKind, _, _build_usecase))
    } finally {
      xmind.close();
    }
  }

  def importDslObjects: List[SObject] = {
    build_model
    model_Builder.dslObjects
  }

  private def _create_object(kind: ElementKind, source: TopicNode, builder: (TopicNode, SMMEntityEntity) => Unit) = {
    val term = source.title
    val target = model_Builder.createObject(kind, term)
    builder(source, target)
    target
  }

  private def _build_object(source: TopicNode, target: SMMEntityEntity) {
    if (target.isDerived) {
      model_Builder.makeAttributesForDerivedObject(target)
    } else {
      model_Builder.makeAttributesForBaseObject(target)
    }
    _build_object_common(source, target)
  }

  private def _build_rule(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
  }

  private def _build_usecase(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
    _mmx.scenario(source).foreach(_build_scenario(_, target))
  }

  private def _build_object_common(aNode: TopicNode, target: SMMEntityEntity) {
    _mmx.aggregations(aNode).foreach(_build_aggregation(_, target))
    _mmx.attributes(aNode).foreach(_build_attribute(_, target))
    _mmx.derivations(aNode).foreach(_build_derivation(_, target))
    _mmx.powertypes(aNode).foreach(_build_powertype(_, target))
    _mmx.roles(aNode).foreach(_build_role(_, target))
    _mmx.states(aNode).foreach(_build_state(_, target))
    _mmx.annotations(aNode).foreach(_build_annotation(_, target))
    _mmx.primaryActors(aNode).foreach(_build_primary_actor(_, target))
    _mmx.secondaryActors(aNode).foreach(_build_secondary_actor(_, target))
    _mmx.supportingActors(aNode).foreach(_build_supporting_actor(_, target))
    _mmx.goals(aNode).foreach(_build_goal(_, target))
  }

  private def _build_aggregation(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    if (_mmx.isDefinition(source)) {
      record_report("implicit composition: " + term)
      val part = _create_object(ResourceKind, source, _build_object)
      target.narrativeOwnCompositions += Pair(term, part)
    } else {
      target.addNarrativePart(term)
    }
  }

  private def _build_attribute(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeAttribute(term)
  }

  private def _build_derivation(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val obj = model_Builder.createObject(target.kind, term)
    obj.setNarrativeBase(target.term)
    _build_object(source, obj)
  }

  private def _build_powertype(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val labels = source.children.map(_.title)
    labels match {
      case Nil => target.addNarrativePowertype(term)
      case _ => target.addNarrativePowertype(term + labels.mkString("(", ";", ")"))
    }
  }

  private def _build_role(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeRole(term)
  }

  private def _build_state(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeStateTransition(term)
  }

  private def _build_annotation(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeAnnotation(term)
  }

  private def _build_primary_actor(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativePrimaryActor(term)
  }

  private def _build_secondary_actor(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeSecondaryActor(term)
  }

  private def _build_supporting_actor(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeSupportingActor(term)
  }

  private def _build_goal(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeGoal(term)
  }

  private def _build_scenario(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeScenarioStep(term)
  }
}

