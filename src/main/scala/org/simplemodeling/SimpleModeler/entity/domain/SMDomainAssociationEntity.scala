package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * @since   Nov. 25, 2012
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class SMDomainAssociationEntity(val dslDomainAssociationEntity: DomainAssociationEntity) extends SMDomainEntity(dslDomainAssociationEntity) {
  override def kindName: String = "association"
  override def powertypeName: String = null
}
