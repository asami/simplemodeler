package org.simplemodeling.SimpleModeler.importer

import org.goldenport.entities.csv.CsvEntity
import org.goldenport.entity._
import org.goldenport.entities.xmind._
import org.goldenport.entities.outline._
import org.goldenport.value.util.AnnotatedCsvTabular
import org.simplemodeling.SimpleModeler.builder.SimpleModelDslBuilder
import org.simplemodeling.SimpleModeler.builder.SimpleModelMakerBuilder
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.values.smcsv.SimpleModelCsvTabular
import org.simplemodeling.dsl.SObject
import com.asamioffice.goldenport.text.UJavaString
import org.goldenport.entities.xmind.XMindEntity
import org.goldenport.service.GServiceCall
import org.simplemodeling.SimpleModeler.builder.Policy
import org.simplemodeling.SimpleModeler.builder.XMindBuilderBase

/*
 * Derived from CsvBuilder
 * 
 * @since   Nov.  5, 2011
 *  version Dec. 11, 2011
 *  version Oct. 19, 2012
 * @version Nov.  3, 2012
 * @author  ASAMI, Tomoharu
 */
class XMindImporter(policy: Policy, val call: GServiceCall, packageName: String, xmind: XMindEntity)
    extends XMindBuilderBase(policy, packageName, xmind) {
  val model_Builder = new SimpleModelDslBuilder(xmind.entityContext, packageName, policy, None)
}
/*
class XMindImporter0(val policy: Policy, val call: GServiceCall, val packageName: String, val xmind: XMindEntity) {
  import UXMind._

  private val _model_builder = new SimpleModelDslBuilder(xmind.entityContext, packageName, policy, None)
  private lazy val _mmx = MindmapModelingXMind(xmind)

  def importDslObjects: List[SObject] = {
    xmind.open()
    try {
      _mmx.actors.foreach(_create_object(ActorKind, _, _build_object))
      _mmx.resources.foreach(_create_object(ResourceKind, _, _build_object))
      _mmx.events.foreach(_create_object(EventKind, _, _build_object))
      _mmx.roles.foreach(_create_object(RoleKind, _, _build_object))
      _mmx.rules.foreach(_create_object(RuleKind, _, _build_rule))
      _mmx.businessusecases.foreach(_create_object(BusinessusecaseKind, _, _build_businessusecase))
      _model_builder.dslObjects
    } finally {
      xmind.close();
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
