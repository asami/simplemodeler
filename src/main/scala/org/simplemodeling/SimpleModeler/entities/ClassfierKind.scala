package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Feb. 20, 2012
 * @version Feb. 20, 2012
 * @author  ASAMI, Tomoharu
 */
sealed trait ClassifierKind {
  def keyword: String
}
object ClassClassifierKind extends ClassifierKind {
  val keyword = "class"
}
object InterfaceClassifierKind extends ClassifierKind {
  val keyword = "interface"
}
object EnumClassifierKind extends ClassifierKind {
  val keyword = "enum"
}
