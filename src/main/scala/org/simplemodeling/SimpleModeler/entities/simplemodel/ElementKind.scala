package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * @since   Dec.  8, 2011
 *  version Dec.  8, 2011
 *  version Jan. 24, 2012
 * @version Oct. 12, 2012
 * @author  ASAMI, Tomoharu
 */
sealed abstract class ElementKind {
  def isIdNeeded: Boolean = false
}

case class GenericKind(name: String) extends ElementKind

object NoneKind extends ElementKind 
object ActorKind extends ElementKind {
  override def isIdNeeded = true
}
object ResourceKind extends ElementKind {
  override def isIdNeeded = true
}
object EventKind extends ElementKind {
  override def isIdNeeded = true
}
object RoleKind extends ElementKind {
  override def isIdNeeded = true
}
object SummaryKind extends ElementKind {
  override def isIdNeeded = true
}
object EntityKind extends ElementKind {
  override def isIdNeeded = true
}
object EntityPartKind extends ElementKind
object RuleKind extends ElementKind
object IdKind extends ElementKind
object NameKind extends ElementKind
object PowertypeKind extends ElementKind
object UsecaseKind extends ElementKind
object StateMachineKind extends ElementKind
object StateMachineStateKind extends ElementKind
