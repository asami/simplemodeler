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
 *  version Nov. 24, 2012
 * @version Dec. 13, 2012
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
      _mmx.summarys.foreach(_create_object(SummaryKind, _, _build_object))
      _mmx.summaryTables.foreach(create_object_table(SummaryKind, _))
      _mmx.associationEntities.foreach(_create_object(AssociationEntityKind, _, _build_object))
      _mmx.associationEntityTables.foreach(create_object_table(AssociationEntityKind, _))
      _mmx.powertypes.foreach(_create_powertype)
      _mmx.powertypeTables.foreach(create_object_table(PowertypeKind, _))
      _mmx.statemachines.foreach(_create_statemachine)
      _mmx.statemachineTables.foreach(create_object_table(StateMachineKind, _))
      _mmx.documents.foreach(_create_document)
      _mmx.documentTables.foreach(create_object_table(DocumentKind, _))
      _mmx.values.foreach(_create_value)
      _mmx.valueTables.foreach(create_object_table(ValueKind, _))
      _mmx.services.foreach(_create_service)
      _mmx.serviceTables.foreach(create_object_table(ServiceKind, _))
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
//    println("OutlineEntityBase:importDslObjects: %s".format(objs.map(x => {
//      "%s[%s]".format(x.name, x.getAttributes.map(a => {
//        "%s;%s".format(a.name, a.kind)
//      }))
//    })))
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
    val term = source.title
    val name = get_name_by_term(term)
    val labels = get_labels_by_term(term)
    val target = model_Builder.createObject(PowertypeKind, name)
    _build_object(source, target)
    for (k <- labels) {
      target.powertypeKinds += SMMPowertypeKind.create(k)
    }
  }

  private def _create_statemachine(source: TopicNode) {
    val name = get_name_by_term(source.title)
    val target = model_Builder.createObject(StateMachineKind, name)
    _build_object(source, target)
  }

  private def _create_document(source: TopicNode) {
    val name = get_name_by_term(source.title)
    val target = model_Builder.createObject(DocumentKind, name)
    _build_object(source, target)
  }

  private def _create_value(source: TopicNode) {
    val name = get_name_by_term(source.title)
    val target = model_Builder.createObject(ValueKind, name)
    _build_object(source, target)
  }

  private def _create_service(source: TopicNode) {
    val name = get_name_by_term(source.title)
    val target = model_Builder.createObject(ServiceKind, name)
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
    _mmx.displayTables(aNode).foreach(_build_display_table(_, target))
    _mmx.powertypes(aNode).foreach(_build_powertype(_, target))
    _mmx.powertypeTables(aNode).foreach(_build_powertype_table(_, target))
    _mmx.kinds(aNode).foreach(_build_kind(_, target))
    _mmx.kindTables(aNode).foreach(_build_kind_table(_, target))
    _mmx.statemachines(aNode).foreach(_build_statemachine(_, target))
    _mmx.statemachineTables(aNode).foreach(_build_statemachine_table(_, target))
    _mmx.states(aNode).foreach(_build_state(_, target))
    _mmx.stateTables(aNode).foreach(_build_state_table(_, target))
    _mmx.documents(aNode).foreach(_build_document(_, target))
    _mmx.documentTables(aNode).foreach(_build_document_table(_, target))
    _mmx.roles(aNode).foreach(_build_role(_, target))
//    _mmx.roleTables(aNode).foreach(_build_role_table(_, target))
    _mmx.operations(aNode).foreach(_build_operation(_, target))
    _mmx.operationTables(aNode).foreach(_build_operation_table(_, target))
    _mmx.annotations(aNode).foreach(_build_annotation(_, target))
//    _mmx.annotationTables(aNode).foreach(_build_annotation_table(_, target))
    _mmx.businessusecases(aNode).foreach(_build_businessusecase_compositions(_, target))
//    _mmx.businessusecaseTables(aNode).foreach(_build_businessusecase_table(_, target))
    _mmx.businesstasks(aNode).foreach(_build_businesstask_compositions(_, target))
//    _mmx.businesstaskTables(aNode).foreach(_build_businesstask_table(_, target))
    _mmx.usecases(aNode).foreach(_build_usecase_compositions(_, target))
//    _mmx.usecaseTables(aNode).foreach(_build_usecase_table(_, target))
    _mmx.tasks(aNode).foreach(_build_task_compositions(_, target))
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
//    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
    if (!_mmx.isDefined(name)) {
      _record_implicit_define("リソースエンティティ", target.name, name)
      // _create_object registered the created object in global space.
      val part = _create_object(ResourceKind, source, _build_object)
//      target.narrativeOwnCompositions += Pair(term, part)
      target.addNarrativePart(term)
    } else {
      _record_duplicate_define(source, target.name, name)
      // virtually addressed as aggregation.
      target.addNarrativePart(term)
    }
  }

  private def _record_implicit_define(kind: String, target: String, source: String) {
    record_report("「%s」に合成対象の%s「%s」を生成しました。".format(target, kind, source))
  }

  private def _record_duplicate_define(topic: TopicNode, target: String, source: String) {
    if (_mmx.isDefinition(topic)) {
      record_warning("エンティティ「%s」は存在しているので「%s」内での定義は無視されました。", target, source)
    }
  }

  private def _build_composition(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に合成対象のクラス「%s」を生成しました。".format(target.name, name))
      // _create_object registered the created object in global space.
      val part = _create_object(EntityPartKind, source, _build_object)
      target.narrativeCompositions += term
//      target.narrativeOwnCompositions += Pair(term, part)
    } else {
      _record_duplicate_define(source, target.name, name)
      target.narrativeCompositions += term
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

  private def _build_kind_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildPowertypeKind(target, table)
  }

  private def _build_statemachine_table(table: GTable[String], target: SMMEntityEntity) {
//    _table_builder.buildStatemachine(target, table)
  }

  private def _build_state_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildState(target, table)
  }


  private def _build_document_table(table: GTable[String], target: SMMEntityEntity) {
//    _table_builder.buildDocument(target, table)
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

  private def _build_display_table(table: GTable[String], target: SMMEntityEntity) {
    println("OutlineEntityBase#_build_display_table = %s".format(target.name))
    _table_builder.buildDisplay(target, table)
  }

  private def _build_powertype(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val labels = source.children.map(_.title)
    labels match {
      case Nil => target.addNarrativePowertype(term)
      case _ => target.addNarrativePowertype(term + labels.mkString("(", ";", ")"))
    }
  }

  private def _build_kind(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val labels = source.children.map(_.title)
    labels match {
      case Nil => target.addNarrativeKind(term)
      case _ => target.addNarrativeKind(term + labels.mkString("(", ";", ")"))
    }
  }

  private def _build_statemachine(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val labels = source.children.map(_.title)
    labels match {
      case Nil => target.addNarrativeStateMachine(term)
      case _ => target.addNarrativeStateMachine(term + labels.mkString("(", ";", ")"))
    }
  }

  private def _build_state(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeStateTransition(term)
  }
/*
  private def _build_state(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val labels = source.children.map(_.title)
    labels match {
      case Nil => target.addNarrativeState(term)
      case _ => target.addNarrativeState(term + labels.mkString("(", ";", ")"))
    }
  }
*/

  private def _build_document(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    if (!_mmx.isDefined(name)) {
      _record_implicit_define("ドキュメント", target.name, name)
      // _create_object registered the created object in global space.
      val part = _create_object(DocumentKind, source, _build_object)
//      target.addNarrativeDocument(term)
    } else {
      _record_duplicate_define(source, target.name, name)
//      target.addNarrativeDocument(term)
    }
  }

  private def _build_role(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeRole(term)
  }

  private def _build_operation(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeOperation(term)
  }

  private def _build_operation_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildOperation(target, table)
  }

  private def _build_annotation(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    target.addNarrativeAnnotation(term)
  }

  private def _build_businessusecase_aggregations(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
//    println("_build_businessusecase_aggregations: " + term)
//    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に集約対象のビジネス・ユースケース「%s」を生成しました。".format(target.name, name))
      val part = _create_object(BusinessUsecaseKind, source, _build_object)
      target.narrativeOwnBusinessUsecases += Pair(term, part)
    } else {
      _record_duplicate_define(source, target.name, name)
//      target.addNarrativeBusinessUsecase(term)
      target.narrativeAggregations += term
    }
  }

  private def _build_businesstask_aggregations(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
//    println("_build_businesstask_aggregations: " + term)
//    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に集約対象のビジネス・タスク「%s」を生成しました。".format(target.name, name))
      val part = _create_object(BusinessTaskKind, source, _build_object)
      target.narrativeOwnBusinessTasks += Pair(term, part)
    } else {
      _record_duplicate_define(source, target.name, name)
//      target.addNarrativeBusinessTask(term)
      target.narrativeAggregations += term
    }
  }

  private def _build_usecase_aggregations(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
//    println("_build_usecase_aggregations: " + term)
//    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に集約対象のユースケース「%s」を生成しました。".format(target.name, name))
      val part = _create_object(UsecaseKind, source, _build_object)
      target.narrativeOwnUsecases += Pair(term, part)
    } else {
      _record_duplicate_define(source, target.name, name)
//      target.addNarrativeUsecase(term)
      target.narrativeAggregations += term
    }
  }

  private def _build_task_aggregations(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
//    println("_build_task_aggregations: " + term)
//    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に集約対象のタスク「%s」を生成しました。".format(target.name, name))
      val part = _create_object(TaskKind, source, _build_object)
      target.narrativeOwnTasks += Pair(term, part)
    } else {
      _record_duplicate_define(source, target.name, name)
//      target.addNarrativeTask(term)
      target.narrativeAggregations += term
    }
  }

  private def _build_businessusecase_compositions(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
//    println("_build_businessusecase_compositions: " + term)
//    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に合成対象のビジネス・ユースケース「%s」を生成しました。".format(target.name, name))
      val part = _create_object(BusinessUsecaseKind, source, _build_object)
      target.narrativeOwnBusinessUsecases += Pair(term, part)
    } else {
//      target.addNarrativeBusinessUsecase(term)
      target.narrativeCompositions += term
    }
  }

  private def _build_businesstask_compositions(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
//    println("_build_businesstask_compositions: " + term)
//    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に合成対象のビジネス・タスク「%s」を生成しました。".format(target.name, name))
      val part = _create_object(BusinessTaskKind, source, _build_object)
      target.narrativeOwnBusinessTasks += Pair(term, part)
    } else {
      _record_duplicate_define(source, target.name, name)
//      target.addNarrativeBusinessTask(term)
      target.narrativeCompositions += term
    }
  }

  private def _build_usecase_compositions(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
//    println("_build_usecase_compositions: " + term)
//    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に合成対象のユースケース「%s」を生成しました。".format(target.name, name))
      val part = _create_object(UsecaseKind, source, _build_object)
      target.narrativeOwnUsecases += Pair(term, part)
    } else {
      _record_duplicate_define(source, target.name, name)
//      target.addNarrativeUsecase(term)
      target.narrativeCompositions += term
    }
  }

  private def _build_task_compositions(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
//    println("_build_task_compositions: " + term)
//    if (_mmx.isDefinition(source)) { // XXX name conflict, use "!_mmx.isDefined(name)" ?
    if (!_mmx.isDefined(name)) {
      record_report("「%s」に合成対象のタスク「%s」を生成しました。".format(target.name, name))
      val part = _create_object(TaskKind, source, _build_object)
      target.narrativeOwnTasks += Pair(term, part)
    } else {
      _record_duplicate_define(source, target.name, name)
//      target.addNarrativeTask(term)
      target.narrativeCompositions += term
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

