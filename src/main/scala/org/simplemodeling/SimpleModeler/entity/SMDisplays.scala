package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.SimpleModeler.entities.simplemodel.SMMDisplay

import org.simplemodeling.SimpleModeler.builder.NaturalLabel

/*
 * @since   Dec. 14, 2012
 * @version Dec. 14, 2012
 * @author  ASAMI, Tomoharu
 */
case class SMDisplays(displays: Seq[SMMDisplay]) {
  def get(name: String): Option[SMMDisplay] = {
    displays.find(_.name == name)
  }

  def get(name: String, key: String): Option[String] = {
    get(name).flatMap(_.get(key))
  }

  def get(name: String, natural: NaturalLabel): Option[String] = {
    get(name).flatMap(_.get(natural))
  }
}
