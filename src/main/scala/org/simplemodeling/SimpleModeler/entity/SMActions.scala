package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.SimpleModeler.entities.simplemodel.SMMAction
import org.simplemodeling.SimpleModeler.builder.NaturalLabel

/*
 * @since   Jan. 10, 2013
 * @version Jan. 10, 2013
 * @author  ASAMI, Tomoharu
 */
case class SMActions(actions: Seq[SMMAction]) {
  def get(name: String): Option[SMMAction] = {
    actions.find(_.name == name)
  }

  def get(name: String, key: String): Option[String] = {
    get(name).flatMap(_.get(key))
  }

  def get(name: String, natural: NaturalLabel): Option[String] = {
    get(name).flatMap(_.get(natural))
  }
}
