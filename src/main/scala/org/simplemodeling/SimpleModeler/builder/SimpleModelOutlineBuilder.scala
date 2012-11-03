package org.simplemodeling.SimpleModeler.builder

import com.asamioffice.goldenport.text.UString
import org.goldenport.value.GTreeNode
import org.goldenport.entity.GEntity
import org.goldenport.entity.content.EntityContent
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/**
 * @since   Feb. 23, 2012
 *  version Feb. 28, 2012
 * @version Nov.  3, 2012
 * @author  ASAMI, Tomoharu
 */
@deprecated("Use SimpleModelEntity to produce artifacts instead of SimpleModlMakerEntity.")
class SimpleModelOutlineBuilder[T](
  val simplemodel: SimpleModelMakerEntity,
  policy: Policy, packageName: String, root: GTreeNode[T]) {
  private val _outline_builder = new OutlineBuilder[T](root)

  def build() {
    simplemodel using {
      val tree = simplemodel.ztree
      for (n <- tree.flatten) {
        n.content match {
          case ec: EntityContent => _build_entity(ec.entity)
          case _ => {}
        }
      }
      _outline_builder.resolve()
    }
  }

  private def _build_entity(entity: GEntity) {
    entity match {
      case e: SMMEntityEntity => _build_entity(e)
      case _ => {}
    }
  }

  private def _build_entity(entity: SMMEntityEntity) {
    println("SimpleModelOutlineBuilder = " + entity)    
    entity.kind match {
      case ActorKind => _build_actor(entity)
      case ResourceKind => _build_resource(entity)
      case EventKind => _build_event(entity)
      case RoleKind => _build_role(entity)
      case SummaryKind => _build_summary(entity)
      case EntityKind => _build_plain_entity(entity)
      case RuleKind => _build_rule(entity)
      case BusinessUsecaseKind => _build_rule(entity)
      case StateMachineKind => {}
      case StateMachineStateKind => {} 
      case _ => {}
    }
  }

  private def _build_actor(entity: SMMEntityEntity) {
    _outline_builder.registerActor(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_resource(entity: SMMEntityEntity) {
    _outline_builder.registerResource(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_event(entity: SMMEntityEntity) {
    _outline_builder.registerEvent(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_role(entity: SMMEntityEntity) {
    _outline_builder.registerRole(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_summary(entity: SMMEntityEntity) {
    _outline_builder.registerSummary(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_plain_entity(entity: SMMEntityEntity) {
    // do nothing
  }

  private def _build_rule(entity: SMMEntityEntity) {
    _outline_builder.registerRule(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_businessusecase(entity: SMMEntityEntity) {
    _outline_builder.registerBusinessusecase(entity.name, entity.getBase.map(_.name),
        _build_object_body(entity, _))
  }

  private def _build_object_body(src: SMMEntityEntity, node: GTreeNode[T]) {
//    _outline_builder.addName(node, src.name)
    if (UString.notNull(src.name_en)) {
      _outline_builder.addNameEn(node, src.name_en)
    }
    if (UString.notNull(src.name_ja)) {
      _outline_builder.addNameJa(node, src.name_ja)
    }
    if (UString.notNull(src.term)) {
      _outline_builder.addTerm(node, src.term)
    }
    if (UString.notNull(src.name_en)) {
      _outline_builder.addTermEn(node, src.term_en)
    }
    if (UString.notNull(src.name_ja)) {
      _outline_builder.addTermJa(node, src.term_ja)
    }
    if (UString.notNull(src.caption)) {
      _outline_builder.addCaption(node, src.caption)
    }
    if (UString.notNull(src.brief)) {
      _outline_builder.addBrief(node, src.brief)
    }
    if (UString.notNull(src.summary)) {
       _outline_builder.addSummary(node, src.summary)
    }
    if (UString.notNull(src.tableName)) {
      _outline_builder.addTableName(node, src.name)
    }
//    _outline_builder.addStates(node, src.name)
//    _outline_builder.addRoles(node, src.name)
    for (a <- src.attributes) {
      _outline_builder.addAttrs(node, a.name)
    }
    for (c <- src.compositions) {
      _outline_builder.addComposition(node, c.name, _build_object_body(src, _))
    }
    for (a <- src.aggregations) {
      _outline_builder.addAggregation(node, a.name)
    }
    for (a <- src.associations) {
      _outline_builder.addAssociation(node, a.name)
    }
    for (p <- src.powertypes) {
      _outline_builder.addPowers(node, p.name)
    }
  }
}
