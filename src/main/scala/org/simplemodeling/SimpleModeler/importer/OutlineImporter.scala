package org.simplemodeling.SimpleModeler.importer

import org.goldenport.entity._
import org.goldenport.value.util.AnnotatedCsvTabular
import org.simplemodeling.SimpleModeler.builder.OutlineBuilderBase
import org.simplemodeling.SimpleModeler.builder.SimpleModelDslBuilder
import org.simplemodeling.SimpleModeler.builder.SimpleModelMakerBuilder
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.values.smcsv.SimpleModelCsvTabular
import org.simplemodeling.dsl.SObject
import com.asamioffice.goldenport.text.UJavaString
import org.goldenport.entities.outline._
import org.goldenport.service.GServiceCall
import org.simplemodeling.SimpleModeler.builder.Policy

/*
 * Derived from XMindBuilder
 * 
 * @since   Nov. 30, 2011
 *  version Dec. 10, 2011
 *  version Mar. 17, 2012
 * @version Nov.  3, 2012
 * @author  ASAMI, Tomoharu
 */
class OutlineImporter(policy: Policy, val call: GServiceCall, packageName: String, outline: OutlineEntityBase)
    extends OutlineBuilderBase(policy, packageName, outline) {

  val makerentity = new SimpleModelMakerEntity(outline.entityContext, policy) // XXX
  makerentity.open() // XXX
  val model_Builder = new SimpleModelMakerBuilder(makerentity, packageName, policy, None)
}

/*
class OutlineImporter0(val policy: Policy, val call: GServiceCall, val packageName: String, val outline: OutlineEntityBase) {
  import UXMind._

  private val _model_builder = new SimpleModelDslBuilder(outline.entityContext, packageName, policy, None)
  private lazy val _outliner = new MindmapModelingOutliner(outline)

  def importDslObjects: List[SObject] = {
    outline.open()
    try {
      _outliner.actors.foreach(_create_object(ActorKind, _, _build_object))
      _outliner.resources.foreach(_create_object(ResourceKind, _, _build_object))
      _outliner.events.foreach(_create_object(EventKind, _, _build_object))
      _outliner.roles.foreach(_create_object(RoleKind, _, _build_object))
      _outliner.rules.foreach(_create_object(RuleKind, _, _build_rule))
      _outliner.businessusecases.foreach(_create_object(BusinessUsecaseKind, _, _build_businessusecase))
      _model_builder.dslObjects
    } finally {
      outline.close();
    }
  }

  private def _create_object(kind: ElementKind, source: TopicNode, builder: (TopicNode, SMMEntityEntity) => Unit) {
    val term = source.title
    val target = _model_builder.createObject(kind, term)
    builder(source, target)
  }

  private def _build_object(source: TopicNode, target: SMMEntityEntity) {
    if (target.isDerived) {
      _model_builder.makeAttributesForDerivedObject(target)
    } else {
      _model_builder.makeAttributesForBaseObject(target)
    }
    _build_object_common(source, target)
  }

  private def _build_rule(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
  }

  private def _build_businessusecase(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
    _outliner.scenario(source).foreach(_build_scenario(_, target))
  }

  private def _build_object_common(aNode: TopicNode, target: SMMEntityEntity) {
    _outliner.aggregations(aNode).foreach(_build_aggregation(_, target))
    _outliner.attributes(aNode).foreach(_build_attribute(_, target))
    _outliner.derivations(aNode).foreach(_build_derivation(_, target))
    _outliner.powertypes(aNode).foreach(_build_powertype(_, target))
    _outliner.roles(aNode).foreach(_build_role(_, target))
    _outliner.states(aNode).foreach(_build_state(_, target))
    _outliner.annotations(aNode).foreach(_build_annotation(_, target))
    _outliner.primaryActors(aNode).foreach(_build_primary_actor(_, target))
    _outliner.secondaryActors(aNode).foreach(_build_secondary_actor(_, target))
    _outliner.supportingActors(aNode).foreach(_build_supporting_actor(_, target))
    _outliner.goals(aNode).foreach(_build_goal(_, target))
  }

  private def _build_aggregation(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativePart(term)
  }

  private def _build_attribute(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeAttribute(term)
  }

  private def _build_derivation(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val obj = _model_builder.createObject(target.kind, term)
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
*/
