package org.simplemodeling.SimpleModeler.builder

import org.goldenport.value.GTable
import org.goldenport.recorder.Recordable
import org.goldenport.service.GServiceCall
import org.goldenport.entities.outline._
import org.simplemodeling.dsl.SObject
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.importer.UXMind
import org.simplemodeling.SimpleModeler.importer.MindmapModelingOutliner

/*
 * Derived from XMindBuilder and XMindImporter
 * Derived XMindBuilderBase
 *
 *  since   Dec. 11, 2011
 * @since   Feb. 27, 2012
 * @version Mar. 21, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class OutlineBuilderBase(val policy: Policy, val packageName: String, val outline: OutlineEntityBase) extends Recordable {
  import UXMind._

  val entityContext = outline.entityContext
  protected val model_Builder: SimpleModelMakerBuilder
  private lazy val _mmx = new MindmapModelingOutliner(outline)
  private lazy val _table_builder = new TableSimpleModelMakerBuilder(model_Builder, policy, packageName)

  setup_FowardingRecorder(outline.entityContext)

  protected final def build_model {
    outline.open()
    try {
      _mmx.actors.foreach(_create_object(ActorKind, _, _build_object))
      _mmx.actorTables.foreach(create_object_table(ActorKind, _))
      _mmx.resources.foreach(_create_object(ResourceKind, _, _build_object))
      _mmx.events.foreach(_create_object(EventKind, _, _build_object))
      _mmx.roles.foreach(_create_object(RoleKind, _, _build_object))
      _mmx.rules.foreach(_create_object(RuleKind, _, _build_rule))
      _mmx.usecases.foreach(_create_object(UsecaseKind, _, _build_usecase))
    } finally {
      outline.close();
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

  protected def create_object_table(kind: ElementKind, table: GTable[String]) = {
    _table_builder.createObjects(kind, table)
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
    _mmx.aggregationTables(aNode).foreach(_build_aggregation_table(_, target))
    _mmx.attributes(aNode).foreach(_build_attribute(_, target))
    _mmx.attributeTables(aNode).foreach(_build_attribute_table(_, target))
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
      target.narrativeCompositions += Pair(term, part)
    } else {
      target.addNarrativePart(term)
    }
  }

  private def _build_aggregation_table(table: GTable[String], target: SMMEntityEntity) {
    println("build_aggregation")
    sys.error("not implemented yet")
  }

  private def _build_attribute(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeAttribute(term)
  }

  private def _build_attribute_table(table: GTable[String], target: SMMEntityEntity) {
    println("OutlineBuilderBase: build_attribute")
    _table_builder.buildObject(target, table)
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

