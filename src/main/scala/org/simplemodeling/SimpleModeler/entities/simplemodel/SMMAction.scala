package org.simplemodeling.SimpleModeler.entities.simplemodel

import org.simplemodeling.dsl.util.PropertyRecord
import org.simplemodeling.SimpleModeler.builder.NaturalLabel

/*
 * @since   Jan. 10, 2013
 * @version Jan. 10, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * The SMMAction is converted to a SMAction
 * in a SMObject construcor.
 */
case class SMMAction(name: String, entry: Seq[PropertyRecord]) {
  def get(k: String): Option[String] = {
    entry.find(_.key.equalsIgnoreCase(k)).flatMap(_.value)
  }

  def getAsStrings(k: String): Option[Seq[String]] = {
    get(k).map(_.split(" ,;"))
  }

  def get(k: NaturalLabel): Option[String] = {
    entry.find(_.isMatch(k)).flatMap(_.value)
  }
}
