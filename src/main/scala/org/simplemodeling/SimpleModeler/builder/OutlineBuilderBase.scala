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
 * @version Oct. 21, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * OutlineImporter is OutlineBuilderBase.
 *
 * OutlineImporter uses SimpleModeMakerBuilder as model_Builder.
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
      _mmx.usecases.foreach(_create_object(UsecaseKind, _, _build_usecase))
      _mmx.usecaseTables.foreach(create_object_table(UsecaseKind, _))
    } finally {
      outline.close();
    }
  }

  def importDslObjects: List[SObject] = {
    build_model
    model_Builder.dslObjects
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

  private def _build_usecase(source: TopicNode, target: SMMEntityEntity) {
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
    _mmx.powertypes(aNode).foreach(_build_powertype(_, target))
    _mmx.roles(aNode).foreach(_build_role(_, target))
    _mmx.states(aNode).foreach(_build_state(_, target))
    _mmx.annotations(aNode).foreach(_build_annotation(_, target))
    _mmx.primaryActors(aNode).foreach(_build_primary_actor(_, target))
    _mmx.secondaryActors(aNode).foreach(_build_secondary_actor(_, target))
    _mmx.supportingActors(aNode).foreach(_build_supporting_actor(_, target))
    _mmx.goals(aNode).foreach(_build_goal(_, target))
  }

  private def _build_property_table(table: GTable[String], target: SMMEntityEntity) {
    _table_builder.buildProperty(target, table)
  }

  private def _build_trait(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    if (!_mmx.isDefined(name)) {
      record_report("「%s」にトレイト「%s」を生成しました。".format(target.name, term))
      val part = _create_object(TraitKind, source, _build_object)
      target.narrativeOwnCompositions += Pair(term, part) // XXX
    } else {
      target.addNarrativeTrait(term)
    }
  }

  private def _build_part(source: TopicNode, target: SMMEntityEntity) {
    val term = source.title
    val name = get_name_by_term(term)
    if (_mmx.isDefinition(source)) {
      record_report("「%s」に合成対象のクラス「%s」を生成しました。".format(target.name, name))
      val part = _create_object(ResourceKind, source, _build_object)
      target.narrativeOwnCompositions += Pair(term, part)
    } else {
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

