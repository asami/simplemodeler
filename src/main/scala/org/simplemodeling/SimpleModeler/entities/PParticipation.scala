package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov. 25, 2012
 * @version Dec. 22, 2012
 * @author  ASAMI, Tomoharu
 */
sealed trait PParticipation {
  val source: PObjectEntity
}

case class BaseParticipation(source: PObjectEntity) extends PParticipation
case class TraitParticipation(source: PObjectEntity) extends PParticipation
/**
 * SimpleModel2ProgramRealmTransformerBase sets AttributeParticipation.
 */
case class AttributeParticipation(source: PObjectEntity, attribute: PAttribute) extends PParticipation
