package org.simplemodeling.SimpleModeler.builder

import scalaz._
import Scalaz._
import scala.collection.mutable.HashMap
import org.goldenport.entity._
import org.simplemodeling.dsl.SObject
import com.asamioffice.goldenport.text.UJavaString
import com.asamioffice.goldenport.text.UString
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.dsl.{SEntity, STrait, SPowertype, SUsecase}
import org.goldenport.recorder.ForwardingRecorder
import org.goldenport.recorder.Recordable

/*
 * @since   Sep. 15, 2011
 *  version Dec. 11, 2011
 *  version Feb.  8, 2012
 *  version Sep. 29, 2012
 *  version Oct. 21, 2012
 * @version Nov.  5, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * SimpleModelMakerBuilder extends this class and override the create_Object method
 * to save a created object in the SimpleModelMakerEntity.
 */
class SimpleModelDslBuilder(
    private val entityContext: GEntityContext, 
    private val packageName: String,
    private val _policy: Policy,
    private val _strategy: Option[Strategy])
    extends SimpleModelBuilder with Recordable with UseTerm {
  private val packagePathname = UJavaString.packageName2pathname(packageName)
  private val entities = new HashMap[String, SMMEntityEntity]
  private def _naming_strategy = (_strategy getOrElse _policy.strategy).naming
  private def _slot_strategy = (_strategy getOrElse _policy.strategy).slot

  setup_FowardingRecorder(entityContext)

  def dslObjects: List[SObject] = {
    import org.simplemodeling.dsl.domain._
    println("dslObjects = " + entities)
    _resolve_entities()
    _adjust_entities()
    val entitylist = entities.values.toList 
    println("dslObjects = " + entities)
    val objs: List[SObject] = entitylist.flatMap(_.createSObjects)
    println("SimpleModelDslBuilder# names: %s".format(objs.map(x => x.name + "/" + x)))
    val names = objs.map(_.name)
    val sentities: List[SObject] = objs collect {
      case e: SEntity => e // include STrait
      case p: SPowertype => p
    }
    val tuples: List[(String, SObject)] = sentities.map(_.name) zip sentities
    val entitymap: Map[String, SObject] = tuples.toMap
    entitylist.foreach(_.buildSObjects(entitymap))
    objs
  }

  /*  
  final def createObject(aKind: String, aName: String): SMMEntityEntity = {
    var name = {
        val prefix = aKind match {
          case "actor" => "DEA"
          case "resource" => "DER"
          case "event" => "DEE"
          case "role" => "DEO"
          case "summary" => "DES"
          case "entity" => "DE"
          case "usecase" => "BU"
          case _ => error("not implemented yet = " + aKind)
        }
        prefix + UString.capitalize(aName)
    }

    def createobject(): SObject = {
      aKind match {
        case "actor" => new DomainActor() {
          set_name(packageName, aName)
        }
        case "resource" => new DomainResource {
          set_name(packageName, aName)
        }
        case "event" => new DomainEvent {
          set_name(packageName, aName)
        }
        case "role" => new DomainRole {
          set_name(packageName, aName)
        }
        case "summary" => new DomainSummary {
          set_name(packageName, aName)
        }
        case "entity" => new DomainEntity {
          set_name(packageName, aName)
        }
        case "usecase" => new RequirementUsecase {
          set_name(packageName, aName)
        }
        case _ => error("not implemented yet = " + aKind)
      }
    }

    def buildobject(obj: SObject): SObject = {
      obj.term = aName
      objects += aName -> obj
      obj
    }

    objects.get(name) match {
    case Some(obj) => obj
    case None => buildobject(createobject())
    }
  }
*/

  /**
   * In debug, compare SMMEntityEntity#createSObjects.
   */
  final def createObject(aKind: ElementKind, aName: String): SMMEntityEntity = {
    val name = _naming_strategy.makeName(aName, aKind)
//    println("createObject(%s, %s, %s)".format(aKind, aName, name))
    entities.get(name) match {
      case Some(entity) => entity
      case None => {
        val entity = new SMMEntityEntity(entityContext)
        entity.kind = aKind
        entity.name = name
        entity.term = aName
        entity.packageName = packageName
        entities += (name -> entity)
        create_Object(name, entity)
        entity
      }
    }
  }

  protected def create_Object(name: String, entity: SMMEntityEntity) {}

  final def createObject(aKind: String, aName: String): SMMEntityEntity = {
    val k = if (UString.isNull(aKind)) {
      NoneKind
    } else {
      GenericKind(aKind)
    }
    createObject(k, aName)
  }

  final def makeAttributesForBaseObject(anObj: SMMEntityEntity) {
    _slot_strategy.makeAttributesForBaseObject(anObj)
  }

  final def makeAttributesForDerivedObject(anObj: SMMEntityEntity) {
    _slot_strategy.makeAttributesForDerivedObject(anObj)
  }

  def makeNarrativeAttributes(obj: SMMEntityEntity) {
    for (na <- obj.narrativeAttributes) {
      obj.attribute(na);
    }
  }

  /*
   * Resolve
   */
  private def _resolve_entities() {
    println("_resolve_entities: " + entities.values.map(_.name))
    entities.values.foreach(_resolve_entity)
  }

  private def _resolve_entity(entity: SMMEntityEntity) {
    if (entity.narrativeBase != "") {
      entity.base = get_entity_by_term(entity.narrativeBase)
//      println("narrativeBase = " + entity.name + " / " + entity.narrativeBase + "/" + entity.base)
    }
    for (term <- entity.narrativeTraits) {
      entity.mixinTrait(get_trait_by_term(term))
    }
    for (term <- entity.narrativePowertypes) {
      val (name, target, multiplicity) = get_powertype_by_term(term)
      entity.powertype(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeRoles) {
      val (name, target, multiplicity) = get_role_by_term(term)
      entity.role(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeAttributes) {
      val (name, target, multiplicity) = get_attribute_by_term(term)
      entity.attribute(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeParts) { // TODO 
      val (name, target, multiplicity) = get_association_by_term(term)
      entity.aggregation(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeCompositions) {
      val (name, target, multiplicity) = get_association_by_term(term)
      entity.composition(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeAggregations) {
      val (name, target, multiplicity) = get_association_by_term(term)
      entity.aggregation(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeAssociations) {
      val (name, target, multiplicity) = get_association_by_term(term)
      entity.association(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeStateTransitions) {
      val (name, target, multiplicity) = get_association_by_term(term)
      entity.association(name, target) multiplicity_is multiplicity // XXX association for state transition
    }
    for (term <- entity.narrativeStateMachines) {
      val (name, target, multiplicity) = get_statemachine_by_term(term)
      entity.statemachine(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeAnnotations) {
      val (name, value) = get_annotation_by_term(term)
      entity.annotation(name, value)
    }
    for (term <- entity.narrativeBusinessUsecases) {
      val (name, target) = get_businessusecase_by_term(term)
      entity.aggregation(name, target)
    }
    for (term <- entity.narrativePrimaryActors) {
      val (name, target, multiplicity) = get_association_by_term(term)
      entity.primaryActor(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeSecondaryActors) {
      val (name, target, multiplicity) = get_association_by_term(term)
      entity.secondaryActor(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeSupportingActors) {
      val (name, target, multiplicity) = get_association_by_term(term)
      entity.supportingActor(name, target) multiplicity_is multiplicity
    }
    for (term <- entity.narrativeScenarioSteps) {
      get_weak_association_by_term(term) match {
        case Some((name, target, multiplicity)) => {
          entity.scenarioStep(name, target) multiplicity_is multiplicity
        }
        case None => {
//          record_warning("Scenario step %s is not an event entity.", term)
          record_warning("脚本のステップ「%s」はユースケース(物語、挿話)、タスク、イベント(出来事)ではありません。", term)
        }
      }
    }
    for ((term, part) <- entity.narrativeOwnCompositions) {
      val name = get_name_by_term(term)
      val mul = get_multiplicity_by_term(term)
      entity.compositionOwn(name, part) multiplicity_is mul
    }
    println("SimpleModelDslBuilder#narrativeOwnBusinessusecases: " + entity.narrativeOwnBusinessUsecases)
    for ((term, part) <- entity.narrativeOwnBusinessUsecases) {
      println("SimpleModelDslBuilder#narrativeOwnBusinessusecases: " + term)
      val name = get_name_by_term(term)
      entity.compositionOwn(name, part)
    }
    println("SimpleModelDslBuilder#narrativeOwnBusinesstasks: " + entity.narrativeOwnBusinessTasks)
    for ((term, part) <- entity.narrativeOwnBusinessTasks) {
      println("SimpleModelDslBuilder#narrativeOwnBusinesstasks: " + term)
      val name = get_name_by_term(term)
      entity.compositionOwn(name, part)
    }
    println("SimpleModelDslBuilder#narrativeOwnUsecases: " + entity.narrativeOwnUsecases)
    for ((term, part) <- entity.narrativeOwnUsecases) {
      println("SimpleModelDslBuilder#narrativeOwnUsecases: " + term)
      val name = get_name_by_term(term)
      entity.compositionOwn(name, part)
    }
    println("SimpleModelDslBuilder#narrativeOwnTasks: " + entity.narrativeOwnTasks)
    for ((term, part) <- entity.narrativeOwnTasks) {
      println("SimpleModelDslBuilder#narrativeOwnTasks: " + term)
      val name = get_name_by_term(term)
      entity.compositionOwn(name, part)
    }
    println("SimpleModelDslBuilder#privateObjects: " + entity.privateObjects.map(_.name))
    entity.privateObjects.foreach(_resolve_entity)
    entity.isResolved = true
  }

  /*
   * Adjust after resolving
   */
  private def _adjust_entities() {
    entities.values.foreach(_adjust_entity)
  }

  private def _adjust_entity(target: SMMEntityEntity) {
    if (target.isDerived) {
      makeAttributesForDerivedObject(target)
    } else {
      makeAttributesForBaseObject(target)
    }
  }

  /*
   * Utilities
   */
  def get_entity_by_term(aTerm: String): SMMEntityEntity = {
    get_entity_by_term_in_entities(entities.values, aTerm) getOrElse {
//      record_warning("Term is not found: %s, creates a resource entity implicitly.", aTerm)
      record_warning("用語「%s」の定義が見つからなかったのでリソース(道具)を自動で作成します。", aTerm)
//      println("get_entity_by_term(%s): %s".format(aTerm, entities.values.map(x => x.name + "/" + x.term )))
      val entity = createObject(ResourceKind, aTerm)
      _resolve_entity(entity)
      entity
    }
  }

  def maybe_entity_by_term(aTerm: String): Option[SMMEntityEntity] = {
    get_entity_by_term_in_entities(entities.values, aTerm)
  }

/*  
  def get_entity_by_term(aTerm: String): SMMEntityEntity = {
    get_entity_by_entity_name(get_type_name_by_term(aTerm))
  }

  def get_entity_by_entity_name(name: String): SMMEntityEntity = {
    for (entity <- entities.values) {
      if (entity.term == name) {
        return entity
      }
    }
    record_warning("Term is not found: %s, creates a resource entity implicitly.", name)
    createObject(ResourceKind, name)
  }

  def get_part_entity_by_term(aTerm: String): Option[SMMEntityEntity] = {
    get_part_entity_by_entity_name(get_type_name_by_term(aTerm))
  }

  def get_part_entity_by_entity_name(name: String): Option[SMMEntityEntity] = {
    for (entity <- entities.values) {
      if (entity.term == name) {
        return None
      }
    }
    createObject(BusinessusecaseKind, name).some
  }

  def maybe_entity_by_term(aTerm: String): Option[SMMEntityEntity] = {
    maybe_entity_by_entity_name(get_type_name_by_term(aTerm))
  }

  def maybe_entity_by_entity_name(name: String): Option[SMMEntityEntity] = {
    for (entity <- entities.values) {
      if (entity.term == name) {
        return Some(entity)
      }
    }
    None
  }
*/
  def get_trait_by_term(aTerm: String): SMMEntityEntity = {
    get_entity_by_term(aTerm)
  }

  def get_powertype_by_term(aTerm: String): (String, SMMPowertypeType, GRMultiplicity) = {
    val term = get_name_by_term(aTerm)
    val name = _naming_strategy.makeName(term, PowertypeKind)
    val labels = get_labels_by_term(aTerm)
    val mutiplicity = get_multiplicity_by_term(aTerm)
    val powertype = new SMMPowertypeType(name, packageName)
    powertype.term = term
    powertype.instances ++= labels
    (name, powertype, mutiplicity)
  }

  def get_role_by_term(aTerm: String): (String, SMMEntityEntity, GRMultiplicity) = {
    (get_name_by_term(aTerm), get_entity_by_term(aTerm), get_multiplicity_by_term(aTerm))
  }

  def get_attribute_by_term(aTerm: String): (String, SMMAttributeTypeSet, GRMultiplicity) = {
    (get_name_by_term(aTerm), get_attribute_type_by_term(aTerm), get_multiplicity_by_term(aTerm))
  }

  def get_association_by_term(aTerm: String): (String, SMMEntityEntity, GRMultiplicity) = {
    (get_name_by_term(aTerm), get_entity_by_term(aTerm), get_multiplicity_by_term(aTerm))
  }

/*
  def get_composition_by_term(aTerm: String): Option[EntityLink] = {
    val name = get_name_by_term(aTerm)
    val entity = get_part_entity_by_term(aTerm)
    val mul = get_multiplicity_by_term(aTerm)
    entity.map(EntityLink(name, _, mul))
  }
*/

  def get_weak_association_by_term(aTerm: String): Option[(String, SMMEntityEntity, GRMultiplicity)] = {
    maybe_entity_by_term(aTerm) match {
      case Some(entity) => Some((get_name_by_term(aTerm), entity, get_multiplicity_by_term(aTerm)))
      case None         => None
    }
  }

  def get_statemachine_by_term(aTerm: String): (String, SMMStateMachineType, GRMultiplicity) = {
    val name = get_name_by_term(aTerm)
    val states = get_labels_by_term(aTerm)
    //	  println("statemachine = " + aTerm + ", states = " + states) 2009-02-26
    val mutiplicity = get_multiplicity_by_term(aTerm)
    val statemachine = new SMMStateMachineType(_naming_strategy.makeName(name, StateMachineKind), packageName)
    statemachine.term = name
    statemachine.states ++= states.map(s => (s, _naming_strategy.makeName(s, StateMachineStateKind)))
    (name, statemachine, mutiplicity)
  }

  def get_annotation_by_term(aTerm: String): (String, String) = {
    val index = aTerm.indexOf('=')
    if (index == -1) {
      ("", "")
    } else {
      (aTerm.substring(0, index).trim, aTerm.substring(index + 1).trim)
    }
  }

  def get_businessusecase_by_term(aTerm: String): (String, SMMEntityEntity) = {
    get_entity_by_term_in_entities(entities.values, aTerm) getOrElse {
      record_warning("用語「%s」の定義が見つからなかったのでビジネス・ユースケースを自動で作成します。", aTerm)
      val entity = createObject(BusinessUsecaseKind, aTerm)
      _resolve_entity(entity)
      entity
    }
    (get_name_by_term(aTerm), get_entity_by_term(aTerm))
  }

  def get_businesstask_by_term(aTerm: String): (String, SMMEntityEntity) = {
    get_entity_by_term_in_entities(entities.values, aTerm) getOrElse {
      record_warning("用語「%s」の定義が見つからなかったのでビジネス・タスクを自動で作成します。", aTerm)
      val entity = createObject(BusinessTaskKind, aTerm)
      _resolve_entity(entity)
      entity
    }
    (get_name_by_term(aTerm), get_entity_by_term(aTerm))
  }

  def get_usecase_by_term(aTerm: String): (String, SMMEntityEntity) = {
    get_entity_by_term_in_entities(entities.values, aTerm) getOrElse {
      record_warning("用語「%s」の定義が見つからなかったのでビジネス・ユースケースを自動で作成します。", aTerm)
      val entity = createObject(UsecaseKind, aTerm)
      _resolve_entity(entity)
      entity
    }
    (get_name_by_term(aTerm), get_entity_by_term(aTerm))
  }

  def get_task_by_term(aTerm: String): (String, SMMEntityEntity) = {
    get_entity_by_term_in_entities(entities.values, aTerm) getOrElse {
      record_warning("用語「%s」の定義が見つからなかったのでタスクを自動で作成します。", aTerm)
      val entity = createObject(TaskKind, aTerm)
      _resolve_entity(entity)
      entity
    }
    (get_name_by_term(aTerm), get_entity_by_term(aTerm))
  }
}

object SimpleModelDslBuilder {
}

// 2012-01-25
case class EntityLink(name: String, entity: SMMEntityEntity, multiplicity: GRMultiplicity)
