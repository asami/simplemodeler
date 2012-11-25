package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov. 25, 2012
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
sealed trait PParticipation {
  val source: PObjectEntity
}

case class BaseParticipation(source: PObjectEntity) extends PParticipation
case class TraitParticipation(source: PObjectEntity) extends PParticipation
case class AttributeParticipation(source: PObjectEntity, attribute: PAttribute) extends PParticipation
