package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * @since   Dec.  8, 2011
 *  version Dec.  8, 2011
 * @version Jan. 24, 2012
 * @author  ASAMI, Tomoharu
 */
sealed abstract class ElementKind {
}

case class GenericKind(name: String) extends ElementKind

object NoneKind extends ElementKind 
object ActorKind extends ElementKind
object ResourceKind extends ElementKind
object EventKind extends ElementKind
object RoleKind extends ElementKind
object SummaryKind extends ElementKind
object EntityKind extends ElementKind
object RuleKind extends ElementKind
object IdKind extends ElementKind
object NameKind extends ElementKind
object PowertypeKind extends ElementKind
object UsecaseKind extends ElementKind
object StateMachineKind extends ElementKind
object StateMachineStateKind extends ElementKind
