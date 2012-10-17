package org.simplemodeling.SimpleModeler.generators.uml

import java.io.{InputStream, OutputStream, IOException}
import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.entity.content._
import org.goldenport.entity.GEntityContext
import org.goldenport.entities.graphviz._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.flow._
import org.simplemodeling.dsl.SExecutionStep
import org.simplemodeling.dsl.SStep
import org.goldenport.Strings

/*
 * @since   Jan. 15, 2009
 *  version Nov. 20, 2011
 *  version Sep. 18, 2012
 * @version Oct. 16, 2012
 * @author  ASAMI, Tomoharu
 */
class ClassDiagramGenerator(sm: SimpleModelEntity) extends DiagramGeneratorBase(sm) {
  final def makeClassDiagramPng(aPackage: SMPackage, aThema: String): BinaryContent = {
    make_class_diagram_png(makeClassDiagramDot(aPackage, aThema))
  }

  final def makeClassDiagramPng(anObject: SMObject, aThema: String): BinaryContent = {
    make_class_diagram_png(makeClassDiagramDot(anObject, aThema))
  }

  private def make_class_diagram_png(text: StringContent): BinaryContent = {
    make_diagram_png(text, Some("classdiagram.png"))
  }
/*
  private def make_class_diagram_png(text: StringContent): BinaryContent = {
    val layout = "dot"
    var in: InputStream = null
    var out: OutputStream = null
    try {
      val dot: Process = context.executeCommand("dot -Tpng -K%s -q".format(layout))
      in = dot.getInputStream()
      out = dot.getOutputStream()
//      println("dot(class diagram) = " + text.string)
      text.write(out)
      out.flush
      out.close
      //      val b = com.asamioffice.goldenport.io.UIO.stream2Bytes(in)
      //      println("b size = " + b.length)
      BinaryContent(in, context, "classdiagram.png", Strings.mimetype.image_png)
    } catch {
      case e: IOException => {
        // Cannot run program "dot": error=2, No such file or directory
        throw new IOException("graphvizのdotコマンドが動作しませんでした。graphvizについてはhttp://www.graphviz.org/を参照してください。(詳細エラー:%s)".format(e.getMessage))
      }
    } finally {
      if (in != null) {
        in.close
      }
      //      println("finish process = " + dot)
    }
  }
*/

  final def makeClassDiagramDot(aPackage: SMPackage, aThema: String): StringContent = {
    val graphviz = new GraphvizEntity(context)
    graphviz.open
    try {
      val graph = new Graph(graphviz.graph, context)
      val classes: Seq[SMObject] = aPackage.children.collect {
        case entity: SMEntity       => entity
        case tr: SMTrait => tr
        case rule: SMRule if !rule.associations.isEmpty => rule
        case powertype: SMPowertype => powertype
        case service: SMService     => service
        case flow: SMFlow           => flow
        case uc: SMUsecase          => uc
      }
//      println("makeClassDiagramDot: " + classes)
      //        filter(child => child.isInstanceOf[SMEntity] || child.isInstanceOf[SMRule] || child.isInstanceOf[SMPowertype] || child.isInstanceOf[SMService]).map(_.asInstanceOf[SMObject])

      var counter = 1
      val ids = new HashMap[SMObject, String]

      def get_id(anObject: SMObject): String = {
        try {
          ids.get(anObject).get
        } catch {
          case e: NoSuchElementException => {
            context.record_trace("No id = " + anObject.packageName + "/" + anObject.name)
            throw e
          }
          case e => throw e
        }
      }

      def add_object(anObject: SMObject) {
//        println("add_object: " + anObject.name)
        val id = "class" + counter
        counter += 1
        ids.put(anObject, id)
        anObject match {
          case powertype: SMPowertype => {
            aThema match {
              case "perspective" => graph.addPowertypeSimple(powertype, id)
              case "hilight"     => graph.addPowertypeSimple(powertype, id)
              case "detail"      => graph.addPowertypeFull(powertype, id)
              case _             => sys.error("illegal thema: " + aThema)
            }
          }
          case _ => {
            aThema match {
              case "perspective" => graph.addClassSimple(anObject, id)
              case "hilight"     => graph.addClassSimple(anObject, id)
              case "detail"      => graph.addClassFull(anObject, id)
              case _             => sys.error("illegal thema: " + aThema)
            }
          }
        }
      }

      def add_relationship(anObject: SMObject) {
        def add_generalization_relationships(obj: SMObject) {
          obj.getBaseObject match {
            case Some(parent) => {
              val childId = ids.get(obj).get
              val parentId = ids.get(parent).get
              graph.addGeneralization(childId, parentId)
            }
            case None => //
          }
        }

        def add_trait_relationships(obj: SMObject) {
          for (rel <- obj.traits) {
            val target = rel.mixinTrait
//            println("add_trait_relationships: " + ids + " / " + target)
            val sourceId = get_id(obj)
            val targetId = get_id(target)
            aThema match {
              case "perspective" => graph.addSimpleTraitRelationship(sourceId, targetId, rel)
              case "hilight"     => graph.addPlainTraitRelationship(sourceId, targetId, rel)
              case "detail"      => graph.addTraitRelationship(sourceId, targetId, rel)
              case _             => graph.addSimpleTraitRelationship(sourceId, targetId, rel)
            }
          }
        }

        def add_powertype_relationships(aSource: SMObject) {
          for (rel <- aSource.powertypes) {
            val target = rel.powertype
            val sourceId = ids.get(aSource).get
            val targetId = ids.get(target).get
            aThema match {
              case "perspective" => graph.addSimplePowertypeRelationship(sourceId, targetId, rel)
              case "hilight"     => graph.addPlainPowertypeRelationship(sourceId, targetId, rel)
              case "detail"      => graph.addPowertypeRelationship(sourceId, targetId, rel)
              case _             => graph.addSimplePowertypeRelationship(sourceId, targetId, rel)
            }
          }
        }

        def add_role_relationships(aSource: SMObject) {
          for (rel <- aSource.roles) {
            val target = rel.role
            val sourceId = ids.get(aSource).get
            val targetId = ids.get(target).get
            aThema match {
              case "perspective" => graph.addSimpleRoleRelationship(sourceId, targetId, rel)
              case "hilight"     => graph.addPlainRoleRelationship(sourceId, targetId, rel)
              case "detail"      => graph.addRoleRelationship(sourceId, targetId, rel)
              case _             => graph.addSimpleRoleRelationship(sourceId, targetId, rel)
            }
          }
        }

        def add_association_relationships(aSource: SMObject) {
          for (assoc <- aSource.associations) {
            val target = assoc.associationType.typeObject
            val sourceId = get_id(aSource)
            val targetId = get_id(target)
            aThema match {
              case "perspective" => graph.addSimpleAssociation(sourceId, targetId, assoc)
              case "hilight"     => graph.addPlainAssociation(sourceId, targetId, assoc)
              case "detail"      => graph.addAssociation(sourceId, targetId, assoc)
              case _             => graph.addSimpleAssociation(sourceId, targetId, assoc)
            }
          }
        }

        def add_usecase_association_relationships(aSource: SMObject) {
          for (assoc <- aSource.associations) {
            val target = assoc.associationType.typeObject
            val sourceId = get_id(aSource)
            val targetId = get_id(target)
            aThema match {
              case "perspective" => graph.addUsecaseRoleRelationship(sourceId, targetId, assoc.name)
              case "hilight"     => graph.addUsecaseRoleRelationship(sourceId, targetId, assoc.name)
              case "detail"      => graph.addUsecaseRoleRelationship(sourceId, targetId, assoc.name)
              case _             => graph.addUsecaseRoleRelationship(sourceId, targetId, assoc.name)
            }
          }
        }

        def add_usecase_relationships(aSource: SMObject) {
          aSource match {
            case usecase: SMStoryObject => {
              for (step <- usecase.basicFlow;
                   entity <- step.entities) {
                val sourceid = get_id(aSource)
                val targetid = get_id(entity)
                aThema match {
                  case "perspective" => graph.addUsecaseRoleRelationship(sourceid, targetid, entity.term)
                  case "hilight"     => graph.addUsecaseRoleRelationship(sourceid, targetid, entity.term)
                  case "detail"      => graph.addUsecaseRoleRelationship(sourceid, targetid, entity.term)
                  case _             => graph.addUsecaseRoleRelationship(sourceid, targetid, entity.term)
                }
              }
              // collect steps primary / secondary actors.
              // be supporting actors in case of not usecases primary actors or secondary actors, 
/*
	      basicFlow
	      extensionFlow
	      alternateFlow
	      exceptionFlow
*/
            }
            case _ => //
          }
        }

        add_generalization_relationships(anObject)
        add_trait_relationships(anObject)
        add_powertype_relationships(anObject)
        add_role_relationships(anObject)
        if (anObject.isInstanceOf[SMUsecase]) {
          add_usecase_association_relationships(anObject)
        } else {
          add_association_relationships(anObject)
        }
        add_usecase_relationships(anObject)
      }

      classes.foreach(add_object)
      classes.foreach(add_relationship)
      new StringContent(graphviz.toDotText, context)
    } finally {
      graphviz.close
    }
  }

  final def makeClassDiagramDot(anObject: SMObject, aThema: String): StringContent = {
    val graphviz = new GraphvizEntity(context)
    graphviz.open
    try {
      val graph = new Graph(graphviz.graph, context)
      aThema match {
        case "hilight" => graph.showDerivedAttribute = true
        case _         => //
      }

      def add_parent_objects(anObject: SMObject, aChildId: String, aDepth: Int) {
        def add_parent_with_association(aParent: SMObject, aParentId: String) {
          graph.addClassFull(aParent, aParentId)
          add_association_objects(aParent, aParentId)
        }

        anObject.getBaseObject match {
          case Some(parent) => {
            val parentId = "parent" + aDepth
            aThema match {
              case "hilight"     => graph.addClassSimple(parent, parentId)
              case "perspective" => add_parent_with_association(parent, parentId)
              case "detail"      => add_parent_with_association(parent, parentId)
              case _             => add_parent_with_association(parent, parentId)
            }
            add_parent_objects(parent, parentId, aDepth + 1)
            graph.addGeneralization(aChildId, parentId)
          }
          case None => //
        }
      }

      def add_child_objects(anObject: SMObject, aParentId: String) {
        var counter = 1
        for (child <- anObject.derivedObjects) {
          val childId = "child" + counter
          counter += 1
          graph.addClassSimple(child, childId)
          add_child_objects(child, childId)
          graph.addGeneralization(childId, aParentId)
        }
      }

      def add_powertype_objects(anObject: SMObject, aObjectId: String) {
        var counter = 1

        def process_object(aSource: SMObject, aSourceId: String, aDerived: Boolean) {
          add_derivation_powertype_objects(aSource, aSourceId)
          for (rel <- aSource.powertypes) {
            val targetId = "powertype" + counter
            counter += 1
            val target = rel.powertype
            graph.addPowertypeFull(target, targetId)
            if (aDerived)
              graph.addDerivedPowertypeRelationship(aSourceId, targetId, rel)
            else
              graph.addPowertypeRelationship(aSourceId, targetId, rel)
          }
        }

        def add_derivation_powertype_objects(aSource: SMObject, aSourceId: String) {
          aSource.getBaseObject match {
            case Some(parent) => process_object(parent, aSourceId, true)
            case None         => //
          }
        }

        process_object(anObject, aObjectId, false)
      }

      def add_role_objects(anObject: SMObject, aObjectId: String) {
        var counter = 1

        def process_object(aSource: SMObject, aSourceId: String, aDerived: Boolean) {
          add_derivation_role_objects(aSource, aSourceId)
          for (rel <- aSource.roles) {
            val targetId = "role" + counter
            counter += 1
            val target = rel.role
            graph.addClassSimple(target, targetId)
            if (aDerived)
              graph.addDerivedRoleRelationship(aSourceId, targetId, rel)
            else
              graph.addRoleRelationship(aSourceId, targetId, rel)
          }
        }

        def add_derivation_role_objects(aSource: SMObject, aSourceId: String) {
          aSource.getBaseObject match {
            case Some(parent) => process_object(parent, aSourceId, true)
            case None         => //
          }
        }

        process_object(anObject, aObjectId, false)
      }

      def add_association_objects(anObject: SMObject, aObjectId: String) {
        var counter = 1

        def process_object(aSource: SMObject, aSourceId: String, aDerived: Boolean) {
          def add_relate_stateMachines(anTarget: SMObject, aTargetId: String) {
            def process_object(aSmOwner: SMObject, aSmOwnerId: String, aDerived: Boolean) {
              add_derivation_statemachine_objects(aSmOwner, aSmOwnerId)
              for (stateMachine <- aSmOwner.stateMachines) {
                if (stateMachine.isReceiveEvent(aSource)) {
                  val smId = aSmOwnerId + "association" + "statemachine" + counter
                  counter += 1
                  val sm = stateMachine
                  aThema match {
                    case "hilight" => graph.addStateMachineSimple(sm, smId)
                    case "perspective" => {
                      graph.addStateMachineFull(sm, smId)
                    }
                    case "detail" => {
                      graph.addStateMachineFull(sm, smId)
                    }
                  }
                  if (aDerived)
                    graph.addDerivedStateMachineRelationship(aSmOwnerId, smId)
                  else
                    graph.addStateMachineRelationship(aSmOwnerId, smId)
                }
              }
            }

            def add_derivation_statemachine_objects(aSmOwner: SMObject, aSmOwnerId: String) {
              aSmOwner.getBaseObject match {
                case Some(parent) => process_object(parent, aSmOwnerId, true)
                case None         => //
              }
            }

            process_object(anTarget, aTargetId, false)
          }

          aThema match {
            case "hilight" => add_derivation_association_objects(aSource, aSourceId)
            case _         => //
          }
          for (assoc <- aSource.associations) {
            val targetId = aSourceId + "association" + counter
            counter += 1
            val target = assoc.associationType.typeObject
            graph.addAssociationClass(target, targetId, assoc)
            add_relate_stateMachines(target, targetId)
            if (aSource.isInstanceOf[SMUsecase]) {
              graph.addUsecaseRoleRelationship(aSourceId, targetId, assoc.name)
            } else {
              if (aDerived)
                graph.addDerivedAssociation(aSourceId, targetId, assoc)
              else
                graph.addAssociation(aSourceId, targetId, assoc)
            }
            if (assoc.isComposition) {
              process_object(target, targetId, aDerived)
            }
          }
        }

        def add_derivation_association_objects(aSource: SMObject, aSourceId: String) {
          aSource.getBaseObject match {
            case Some(parent) => process_object(parent, aSourceId, true)
            case None         => //
          }
        }

        process_object(anObject, aObjectId, false)
      }

      /* 2009-01-20
      def add_association_objects(anObject: SMObject, aSourceId: String) {
	var counter = 1

	def add_derivation_association_objects(aSource: SMObject) {
	  aSource.getBaseObject match {
	    case Some(parent) => {
	      add_derivation_association_objects(parent)
	      for (assoc <- parent.associations) {
		val targetId = "association" + counter
		counter += 1
		val target = assoc.associationType.typeObject
		graph.addAssociationClass(target, targetId, assoc)
		graph.addDerivedAssociation(aSourceId, targetId, assoc)
		if (assoc.isComposition) {
		  for (assoc2 <- target.associations) {
		    val targetId2 = "association" + counter
		    counter += 1
		    val target2 = assoc2.associationType.typeObject
		    graph.addAssociationClass(target2, targetId2, assoc2)
		    graph.addDerivedAssociation(targetId, targetId2, assoc2)
		  }
		}
	      }
	    }
	    case None => //
	  }
	}

	add_derivation_association_objects(anObject)
	for (assoc <- anObject.associations) {
	  val targetId = "association" + counter
	  counter += 1
	  val target = assoc.associationType.typeObject
	  graph.addAssociationClass(target, targetId, assoc)
	  graph.addAssociation(aSourceId, targetId, assoc)
	  if (assoc.isComposition) {
	    for (assoc2 <- target.associations) {
	      val targetId2 = "association" + counter
	      counter += 1
	      val target2 = assoc2.associationType.typeObject
	      graph.addAssociationClass(target2, targetId2, assoc2)
	      graph.addAssociation(targetId, targetId2, assoc2)
	    }
	  }
	}
      }
*/
      def add_stateMachines(anObject: SMObject, aObjectId: String) {
        var counter = 1

        def add_income_events(aStateMachine: SMStateMachine, aSourceId: String) {
          for (event <- aStateMachine.getEvents) {
            val targetId = "statemachine" + counter
            counter += 1
            graph.addClassSimple(event, targetId)
            graph.addSimpleStateMachineRelationship(aSourceId, targetId)
          }
        }

        def process_object(aSource: SMObject, aSourceId: String, aDerived: Boolean) {
          add_derivation_statemachine_objects(aSource, aSourceId)
          for (stateMachine <- aSource.stateMachines) {
            val targetId = "statemachine" + counter
            counter += 1
            val target = stateMachine
            aThema match {
              case "hilight" => graph.addStateMachineSimple(target, targetId)
              case "perspective" => {
                graph.addStateMachineFull(target, targetId)
                add_income_events(stateMachine, targetId)
              }
              case "detail" => {
                graph.addStateMachineFull(target, targetId)
                add_income_events(stateMachine, targetId)
              }
            }
            if (aDerived)
              graph.addDerivedStateMachineRelationship(aSourceId, targetId)
            else
              graph.addStateMachineRelationship(aSourceId, targetId)
          }
        }

        def add_derivation_statemachine_objects(aSource: SMObject, aSourceId: String) {
          aSource.getBaseObject match {
            case Some(parent) => process_object(parent, aSourceId, true)
            case None         => //
          }
        }

        process_object(anObject, aObjectId, false)
      }

      def add_document_objects(anObject: SMObject, aObjectId: String) {
        var counter = 1

        def process_object(aSource: SMObject, aSourceId: String, aDerived: Boolean) {
          add_derivation_document_objects(aSource, aSourceId)
          for (rel <- aSource.documents) {
            val targetId = "document" + counter
            counter += 1
            val target = rel.document
            aThema match {
              case "hilight" => graph.addClassSimple(target, targetId)
              case "perspective" => {
                graph.addClassFull(target, targetId)
              }
              case "detail" => {
                graph.addClassFull(target, targetId)
              }
            }
            if (aDerived)
              graph.addDerivedDocumentRelationship(aSourceId, targetId, rel)
            else
              graph.addDocumentRelationship(aSourceId, targetId, rel)
          }
        }

        def add_derivation_document_objects(aSource: SMObject, aSourceId: String) {
          aSource.getBaseObject match {
            case Some(parent) => process_object(parent, aSourceId, true)
            case None         => //
          }
        }

        process_object(anObject, aObjectId, false)
      }

      def add_port_objects(anObject: SMObject, aObjectId: String) {
        var counter = 1

        def process_object(aSource: SMObject, aSourceId: String, aDerived: Boolean) {
          add_derivation_document_objects(aSource, aSourceId)
          for (port <- aSource.ports) {
            val targetId = "port" + counter
            counter += 1
            val target = port.entityType.typeObject;
            aThema match {
              case "hilight" => graph.addClassSimple(target, targetId)
              case "perspective" => {
                graph.addClassFull(target, targetId)
              }
              case "detail" => {
                graph.addClassFull(target, targetId)
              }
            }
            if (aDerived)
              graph.addDerivedPortRelationship(aSourceId, targetId, port)
            else
              graph.addPortRelationship(aSourceId, targetId, port)
          }
        }

        def add_derivation_document_objects(aSource: SMObject, aSourceId: String) {
          aSource.getBaseObject match {
            case Some(parent) => process_object(parent, aSourceId, true)
            case None         => //
          }
        }

        process_object(anObject, aObjectId, false)
      }

      graph.addClassRoot(anObject, "primary")
      add_parent_objects(anObject, "primary", 1)
      add_child_objects(anObject, "primary")
      add_powertype_objects(anObject, "primary")
      add_role_objects(anObject, "primary")
      add_association_objects(anObject, "primary")
      add_stateMachines(anObject, "primary")
      add_document_objects(anObject, "primary")
      add_port_objects(anObject, "primary")
      new StringContent(graphviz.toDotText, context)
    } finally {
      graphviz.close
    }
  }
}

class Graph(g: GVDigraph, c: GEntityContext) extends DigraphBase(g, c) {
}
