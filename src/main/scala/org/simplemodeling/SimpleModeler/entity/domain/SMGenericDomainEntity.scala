package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * @since   Jan. 24, 2012
 * @version Jan. 25, 2012
 * @atuhor  ASAMI, Tomoharu
 */
class SMGenericDomainEntity(val dslGeneric: GenericDomainEntity) extends SMEntity(dslGeneric) {
  override def kindName: String = {
    dslGeneric.kind match {
      case x :: _ => x
      case _ => ""
    }
  }
}
