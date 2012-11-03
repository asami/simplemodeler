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
 * @since   Dec. 11, 2011
 *  version Feb. 27, 2012
 *  version Apr. 21, 2012
 *  version Sep. 30, 2012
 *  version Oct. 26, 2012
 * @version Nov.  4, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * OutlineImporter is OutlineBuilderBase.
 *
 * OutlineImporter uses SimpleModeMakerBuilder(SimpleModelDslBuilder)
 * as model_Builder.
 */
abstract class OutlineBuilderBase(val policy: Policy, val packageName: String, val outline: OutlineEntityBase) extends Recordable with UseTerm {
  import UXMind._

  val entityContext = outline.entityContext
  protected val model_Builder: SimpleModelMakerBuilder
  private lazy val _mmx = new MindmapModelingOutliner(outline)
  private lazy val _table_builder = new TableSimpleModelMakerBuilder(model_Builder, policy, packageName)

  setup_FowardingRecorder(outline.entityContext)

  protected final def build_model {
    outline.open()
    try {
      _mmx.traits.foreach(_create_object(TraitKind, _, _build_object))
      _mmx.traitTables.foreach(create_object_table(TraitKind, _))
      _mmx.businessActors.foreach(_create_object(BusinessActorKind, _, _build_object))
      _mmx.businessActorTables.foreach(create_object_table(BusinessActorKind, _))
      _mmx.businessResources.foreach(_create_object(BusinessResourceKind, _, _build_object))
      _mmx.businessResourceTables.foreach(create_object_table(BusinessResourceKind, _))
      _mmx.businessEvents.foreach(_create_object(BusinessEventKind, _, _build_object))
      _mmx.businessEventTables.foreach(create_object_table(BusinessEventKind, _))
      _mmx.actors.foreach(_create_object(ActorKind, _, _build_object))
      _mmx.actorTables.foreach(create_object_table(ActorKind, _))
      _mmx.resources.foreach(_create_object(ResourceKind, _, _build_object))
      _mmx.resourceTables.foreach(create_object_table(ResourceKind, _))
      _mmx.events.foreach(_create_object(EventKind, _, _build_object))
      _mmx.eventTables.foreach(create_object_table(EventKind, _))
      _mmx.roles.foreach(_create_object(RoleKind, _, _build_object))
      _mmx.roleTables.foreach(create_object_table(RoleKind, _))
      _mmx.powertypes.foreach(_create_powertype)
      _mmx.powertypeTables.foreach(create_object_table(PowertypeKind, _))
      _mmx.rules.foreach(_create_object(RuleKind, _, _build_rule))
      _mmx.ruleTables.foreach(create_object_table(RuleKind, _))
      _mmx.businessusecases.foreach(_create_object(BusinessUsecaseKind, _, _build_businessusecase))
      _mmx.businessusecaseTables.foreach(create_object_table(BusinessUsecaseKind, _))
      _mmx.businesstasks.foreach(_create_object(BusinessTaskKind, _, _build_businesstask))
      _mmx.businesstaskTables.foreach(create_object_table(BusinessTaskKind, _))
      _mmx.usecases.foreach(_create_object(UsecaseKind, _, _build_usecase))
      _mmx.usecaseTables.foreach(create_object_table(UsecaseKind, _))
      _mmx.tasks.foreach(_create_object(TaskKind, _, _build_task))
      _mmx.taskTables.foreach(create_object_table(TaskKind, _))
    } finally {
      outline.close();
    }
  }

  def importDslObjects: List[SObject] = {
    build_model
    val objs = model_Builder.dslObjects
    println("OutlineEntityBase:importDslObjects: %s".format(objs.map(x => {
      "%s[%s]".format(x.name, x.getAttributes.map(a => {
        "%s;%s".format(a.name, a.kind)
      }))
    })))
    objs
  }

  private def _create_object(kind: ElementKind, source: TopicNode, builder: (TopicNode, SMMEntityEntity) => Unit) = {
//    println("_create_object: %s %s".format(kind, source.title))
    val name = get_name_by_term(source.title)
    val target = model_Builder.createObject(kind, name)
    builder(source, target)
    target
  }

  private def _create_powertype(source: TopicNode) {
    val name = get_name_by_term(source.title)
    val target = model_Builder.createObject(PowertypeKind, name)
    _build_object(source, target)
  }

  protected def create_object_table(kind: ElementKind, table: GTable[String]) = {
    _table_builder.createObjects(kind, table)
//    val objs = _table_builder.createObjects(kind, table)
//    objs.foreach(_adjust_object)
  }

  private def _build_object(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
//    _adjust_object(target)
  }
/*
  private def _adjust_object(target: SMMEntityEntity) {
    if (target.isDerived) {
      model_Builder.makeAttributesForDerivedObject(target)
    } else {
      model_Builder.makeAttributesForBaseObject(target)
    }
  }
*/

  private def _build_rule(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
  }

  private def _build_businessusecase(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
    _mmx.scenario(source).foreach(_build_scenario(_, target))
  }

  private def _build_businesstask(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
    _mmx.scenario(source).foreach(_build_scenario(_, target))
  }

  private def _build_usecase(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
    _mmx.scenario(source).foreach(_build_scenario(_, target))
  }

  private def _build_task(source: TopicNode, target: SMMEntityEntity) {
    _build_object_common(source, target)
    _mmx.scenario(source).foreach(_build_scenario(_, target))
  }

  private def _build_object_common(aNode: TopicNode, target: SMMEntityEntity) {
    _mmx.propertyTables(aNode).foreach(_build_property_table(_, target))
    _mmx.traits(aNode).foreach(_build_trait(_, target))
    _mmx.parts(aNode).foreach(_build_part(_, target))
    _mmx.compositions(aNode).foreach(_build_composition(_, target))
    _mmx.aggregations(aNode).foreach(_build_aggregation(_, target))
    _mmx.associations(aNode).foreach(_build_association(_, target))
    _mmx.featureTables(aNode).foreach(_build_feature_table(_, target))
    _mmx.compositionTables(aNode).foreach(_build_composition_table(_, target))
    _mmx.aggregationTables(aNode).foreach(_build_aggregation_table(_, target))
    _mmx.associationTables(aNode).foreach(_build_association_table(_, target))
    _mmx.attributes(aNode).foreach(_build_attribute(_, target))
    _mmx.attributeTables(aNode).foreach(_build_attribute_table(_, target))
    _mmx.derivations(aNode).foreach(_build_derivation(_, target))
//    _mmx.derivationTables(aNode).foreach(_build_derivation_table(_, target))
    _mmx.powertypes(aNode).foreach(_build_powertype(_, target))
    _mmx.powertypeTables(aNode).foreach(_build_powertype_table(_, target))
    _mmx.roles(aNode).foreach(_build_role(_, target))
//    _mmx.roleTables(aNode).foreach(_build_role_table(_, target))
    _mmx.states(aNode).foreach(_build_state(_, target))
//    _mmx.stateTables(aNode).foreach(_build_state_table(_, target))
    _mmx.annotations(aNode).foreach(_build_annotation(_, target))
//    _mmx.annotationTables(aNode).foreach(_build_annotation_table(_, target))
    _mmx.businessusecases(aNode).foreach(_build_businessusecase_aggregations(_, target))
//    _mmx.businessusecaseTables(aNode).foreach(_build_businessusecase_table(_, target))
    _mmx.businesstasks(aNode).foreach(_build_businesstask_aggregations(_, target))
//    _mmx.businesstaskTables(aNode).foreach(_build_businesstask_table(_, target))
    _mmx.usecases(aNode).foreach(_build_usecase_aggregations(_, target))
//    _mmx.usecaseTables(aNode).foreach(_build_usecase_table(_, target))
    _mmx.tasks(aNode).foreach(_build_task_aggregations(_, target))
//    _mmx.taskTables(aNode).foreach(_build_task_table(_, target))
    _mmx.primaryActors(aNode).foreach(_build_primary_actor(_, target))
    _mmx.secondaryActors(aNode).foreach(_build_secondary_actor(_, target))
    _mmx.supportingActors(aNode).foreach(_build_supporting_actor(_, target))
//    _mmx.actorTables(aNode).foreach(_build_actor_table(_, target))
    _mmx.goals(aNode).foreach(_build_goal(_, target))
//    _mmx.goalTables(aNode).foreach(_build_goal_table(_, target))
  }

  private def _build_property_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildProperty(target, table)
  }

  private def _build_trait(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    if (!_mmx.isDefined(name)) {
      record_report("「%s」にトレイト「%s」を生成しました。".format(target.name, term))
      val part = _create_object(TraitKind, source, _build_businessusecase)
      target.narrativeOwnCompositions += Pair(term, part) // XXX
    } else {
      target.addNarrativeTrait(term)
    }
  }

  private def _build_part(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    // when a topic has a concrete definition, a relationship with the topic is assumed as composition.
    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
      record_report("「%s」に合成対象のクラス「%s」を生成しました。".format(target.name, name))
      val part = _create_object(ResourceKind, source, _build_object)
      target.narrativeOwnCompositions += Pair(term, part)
    } else {
      // virtually addressed as aggregation.
      target.addNarrativePart(term)
    }
  }

  private def _build_composition(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に合成対象のクラス「%s」を生成しました。".format(target.name, name))
      val part = _create_object(EntityPartKind, source, _build_object)
      target.narrativeOwnCompositions += Pair(term, part)
    } else {
      target.narrativeCompositions += name
    }
  }

  private def _build_aggregation(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.narrativeAggregations += term
  }

  private def _build_association(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.narrativeAssociations += term
  }

  private def _build_feature_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildFeature(target, table)
  }

  private def _build_composition_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildComposition(target, table)
  }

  private def _build_aggregation_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildAggregation(target, table)
  }

  private def _build_association_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildAssociation(target, table)
  }

  private def _build_powertype_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildPowertype(target, table)
  }

  private def _build_attribute(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeAttribute(term)
  }

  private def _build_attribute_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildAttribute(target, table)
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

  private def _build_businessusecase_aggregations(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    println("_build_businessusecase_aggregations: " + term)
    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
      record_report("「%s」に合成対象のビジネス・ユースケース「%s」を生成しました。".format(target.name, name))
      val part = _create_object(BusinessUsecaseKind, source, _build_object)
      target.narrativeOwnBusinessUsecases += Pair(term, part)
    } else {
      target.addNarrativeBusinessUsecase(term)
    }
  }

  private def _build_businesstask_aggregations(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    println("_build_businesstask_aggregations: " + term)
    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
      record_report("「%s」に合成対象のビジネス・タスク「%s」を生成しました。".format(target.name, name))
      val part = _create_object(BusinessTaskKind, source, _build_object)
      target.narrativeOwnBusinessTasks += Pair(term, part)
    } else {
      target.addNarrativeBusinessTask(term)
    }
  }

  private def _build_usecase_aggregations(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    println("_build_usecase_aggregations: " + term)
    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
      record_report("「%s」に合成対象のユースケース「%s」を生成しました。".format(target.name, name))
      val part = _create_object(UsecaseKind, source, _build_object)
      target.narrativeOwnUsecases += Pair(term, part)
    } else {
      target.addNarrativeUsecase(term)
    }
  }

  private def _build_task_aggregations(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    println("_build_task_aggregations: " + term)
    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
      record_report("「%s」に合成対象のタスク「%s」を生成しました。".format(target.name, name))
      val part = _create_object(TaskKind, source, _build_object)
      target.narrativeOwnTasks += Pair(term, part)
    } else {
      target.addNarrativeTask(term)
    }
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

