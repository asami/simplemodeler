package org.simplemodeling.SimpleModeler.entities.simplemodel

import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.FileDataSource
import org.goldenport.entity.content.GContent
import org.goldenport.entity.locator.EntityLocator
import org.goldenport.sdoc.structure._
import org.goldenport.entities.workspace.TreeWorkspaceNode
import org.goldenport.value.GTreeBase
import com.asamioffice.goldenport.text.{UJavaString, UString}
import org.simplemodeling.SimpleModeler.builder.SimpleModelMakerBuilder
import org.simplemodeling.SimpleModeler.builder.Policy
import org.simplemodeling.SimpleModeler.builder.UseTerm

/*
 * @since   Jan. 30, 2009
 *  version Nov.  5, 2011
 *  version Dec. 11, 2011
 * @version Feb.  7, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModelMakerEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext, val policy: Policy)
    extends GTreeContainerEntityBase(aIn, aOut, aContext) with UseTerm {
  type DataSource_TYPE = GDataSource
  override type TreeNode_TYPE = GTreeContainerEntityNode
  override def is_Text_Output = true

  def this(aDataSource: GDataSource, aContext: GEntityContext, policy: Policy) = this(aDataSource, aDataSource, aContext, policy)
  def this(aContext: GEntityContext, policy: Policy) = this(null, aContext, policy)

  override protected def open_Entity_Create() {
    set_root(new TreeWorkspaceNode(null, this))
  }

  final def build {
    def build_package(aNode: GTreeContainerEntityNode) {
      val packageName = get_classifier_name(aNode)
      val builder = new SimpleModelMakerBuilder(this, packageName, policy)
      val entities = new ArrayBuffer[SMMEntityEntity]
      for (child <- aNode.children) {
        child.entity match {
          case Some(entity: SMMEntityEntity) => entities += entity
          case None                          => build_package(child)
        }
      }
      if (!entities.isEmpty) {
        def make_manifest {
          val manifest = new SMMManifestEntity(entityContext)
          manifest.objects ++= entities
          manifest.packageName = packageName
          val manifestNode = aNode.setChild("MANIFEST.scala")
          manifestNode.setEntity(manifest)
        }

        def resolve_base {
          for (entity <- entities) {
            if (entity.narrativeBase != "") {
              entity.base = get_entity_by_term(entity.narrativeBase)
            }
          }
        }

        def resolve_powertypes {
          for (entity <- entities) {
            for (term <- entity.narrativePowertypes) {
              val (name, target, multiplicity) = get_powertype_by_term(term)
              entity.powertype(name, target) multiplicity_is multiplicity
            }
          }
        }

        def resolve_roles {
          for (entity <- entities) {
            for (term <- entity.narrativeRoles) {
              val (name, target, multiplicity) = get_role_by_term(term)
              entity.role(name, target) multiplicity_is multiplicity
            }
          }
        }

        def resolve_attributes {
          for (entity <- entities) {
            for (term <- entity.narrativeAttributes) {
              val (name, target, multiplicity) = get_attribute_by_term(term)
              entity.attribute(name, target) multiplicity_is multiplicity
            }
          }
        }

//        def resolve_associations {
        def resolve_aggregations {
          for (entity <- entities) {
            for (term <- entity.narrativeParts) {
              val (name, target, multiplicity) = get_association_by_term(term)
              entity.aggregation(name, target) multiplicity_is multiplicity
            }
          }
        }

        def resolve_statemachines {
          for (entity <- entities) {
            for (term <- entity.narrativeStateMachines) {
              val (name, target, multiplicity) = get_statemachine_by_term(term)
              entity.statemachine(name, target) multiplicity_is multiplicity
            }
          }
        }

        def resolve_annotations {
          for (entity <- entities) {
            for (term <- entity.narrativeAnnotations) {
              val (name, value) = get_annotation_by_term(term)
              entity.annotation(name, value)
            }
          }
        }

        def resolve_primary_actors {
          for (entity <- entities) {
            for (term <- entity.narrativePrimaryActors) {
              val (name, target, multiplicity) = get_association_by_term(term)
              entity.primaryActor(name, target) multiplicity_is multiplicity
            }
          }
        }

        def resolve_secondary_actors {
          for (entity <- entities) {
            for (term <- entity.narrativeSecondaryActors) {
              val (name, target, multiplicity) = get_association_by_term(term)
              entity.secondaryActor(name, target) multiplicity_is multiplicity
            }
          }
        }

        def resolve_supporting_actors {
          for (entity <- entities) {
            for (term <- entity.narrativeSupportingActors) {
              val (name, target, multiplicity) = get_association_by_term(term)
              entity.supportingActor(name, target) multiplicity_is multiplicity
            }
          }
        }

        def resolve_scenario_steps {
          for (entity <- entities) {
            for (term <- entity.narrativeScenarioSteps) {
              get_weak_association_by_term(term) match {
                case Some((name, target, multiplicity)) => {
                  entity.scenarioStep(name, target) multiplicity_is multiplicity
                }
                case None => {
                  record_warning("Scenario step %s is not an event entity.", term)
                }
              }
            }
          }
        }

        def get_entity_by_term(aTerm: String): SMMEntityEntity = {
          get_entity_by_term_in_entities(entities, aTerm) getOrElse {
            record_warning("Term is not found: %s, creates a resource entity implicitly.", aTerm)
            builder.createObject(ResourceKind, aTerm)
          }
        }

        def maybe_entity_by_term(aTerm: String): Option[SMMEntityEntity] = {
          get_entity_by_term_in_entities(entities, aTerm)
        }
        
/*
        def get_entity_by_term(aTerm: String): SMMEntityEntity = {
          get_entity_by_entity_name(get_type_name_by_term(aTerm))
        }

        def get_entity_by_entity_name(name: String): SMMEntityEntity = {
          for (entity <- entities) {
            if (entity.name == name) {
              return entity
            }
          }
          record_warning("Term is not found: %s, creates a resource entity implicitly.", name)
          builder.createObject(ResourceKind, name)
        }

        def maybe_entity_by_term(aTerm: String): Option[SMMEntityEntity] = {
          maybe_entity_by_entity_name(get_type_name_by_term(aTerm))
        }

        def maybe_entity_by_entity_name(name: String): Option[SMMEntityEntity] = {
          for (entity <- entities) {
            if (entity.term == name) {
              return Some(entity)
            }
          }
          None
        }
*/
        def get_powertype_by_term(aTerm: String): (String, SMMPowertypeType, GRMultiplicity) = {
          val name = get_name_by_term(aTerm)
          val labels = get_labels_by_term(aTerm)
          val mutiplicity = get_multiplicity_by_term(aTerm)
          val powertype = new SMMPowertypeType("DP" + name, packageName)
          powertype.term = name
          powertype.instances ++= labels
          (name, powertype, mutiplicity)
        }

        def get_role_by_term(aTerm: String): (String, SMMEntityEntity, GRMultiplicity) = {
          (get_name_by_term(aTerm), get_entity_by_term(aTerm), get_multiplicity_by_term(aTerm))
        }

        def get_attribute_by_term(aTerm: String): (String, SMMObjectType, GRMultiplicity) = {
          (get_name_by_term(aTerm), get_attribute_type_by_term(aTerm), get_multiplicity_by_term(aTerm))
        }

        def get_association_by_term(aTerm: String): (String, SMMEntityEntity, GRMultiplicity) = {
          (get_name_by_term(aTerm), get_entity_by_term(aTerm), get_multiplicity_by_term(aTerm))
        }

        def get_weak_association_by_term(aTerm: String): Option[(String, SMMEntityEntity, GRMultiplicity)] = {
          maybe_entity_by_term(aTerm) match {
            case Some(entity) => Some((get_name_by_term(aTerm), entity, get_multiplicity_by_term(aTerm))) 
            case None => None
          }
        }

        def get_statemachine_by_term(aTerm: String): (String, SMMStateMachineType, GRMultiplicity) = {
          val name = get_name_by_term(aTerm)
          val states = get_labels_by_term(aTerm)
          //	  println("statemachine = " + aTerm + ", states = " + states) 2009-02-26
          val mutiplicity = get_multiplicity_by_term(aTerm)
          val statemachine = new SMMStateMachineType("DM" + UString.capitalize(name), packageName)
          statemachine.term = name
          statemachine.states ++= states.map(s => (s, "DMS" + UString.capitalize(s)))
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

        make_manifest
        resolve_base
        resolve_powertypes
        resolve_roles
        resolve_attributes
//        resolve_associations
        resolve_aggregations
        resolve_statemachines
        resolve_primary_actors
        resolve_secondary_actors
        resolve_supporting_actors
        resolve_scenario_steps
      }
    }

    root.children.foreach(build_package)
  }

  private def get_classifier_name(aNode: GTreeContainerEntityNode) = {
    aNode.pathname match {
      case null     => ""
      case ""       => ""
      case pathname => UJavaString.pathname2classifierName(pathname)
    }
  }

  private def record_warning(message: String, args: Any*) {
    aContext.record_warning(message, args: _*)
  }
}
/*
class SimpleModelMakerEntityClass extends GEntityClass {
  type Instance_TYPE = SimpleModelMakerEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "simpleModel.d"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new SimpleModelMakerEntity(aDataSource, aContext))
}
*/
