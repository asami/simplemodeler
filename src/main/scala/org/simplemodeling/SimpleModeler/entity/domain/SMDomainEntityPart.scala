package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * @since   Jan. 18, 2009
 * @version Jun. 20, 2009
 * @atuhor  ASAMI, Tomoharu
 */
class SMDomainEntityPart(val dslDomainEntityPart: DomainEntityPart) extends SMEntity(dslDomainEntityPart) with SMEntityPart {
  override def kindName: String = "part"
}
