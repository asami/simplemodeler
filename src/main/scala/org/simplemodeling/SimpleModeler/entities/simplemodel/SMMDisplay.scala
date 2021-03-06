package org.simplemodeling.SimpleModeler.entities.simplemodel

import org.simplemodeling.dsl.util.PropertyRecord
import org.simplemodeling.SimpleModeler.builder.NaturalLabel

/*
 * @since   Dec. 13, 2012
 * @version Jan. 10, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * The SMMDisplay is converted to a SMDisplay
 * in a SMObject construcor.
 */
case class SMMDisplay(name: String, seq: Int, entry: Seq[PropertyRecord]) {
  def get(k: String): Option[String] = {
    entry.find(_.key.equalsIgnoreCase(k)).flatMap(_.value)
  }

  def get(k: NaturalLabel): Option[String] = {
    entry.find(_.isMatch(k)).flatMap(_.value)
  }
}
