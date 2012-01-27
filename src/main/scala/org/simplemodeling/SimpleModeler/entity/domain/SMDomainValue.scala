package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * @since   Sep. 15, 2008
 * @version Jul. 17, 2011
 * @author  ASAMI, Tomoharu
 */
class SMDomainValue(val dslDomainValue: DomainValue) extends SMValue(dslDomainValue) {
  override def kindName: String = null
  override def powertypeName: String = null

  def datatype: SMDatatype = {
    attributes(0).attributeType.typeObject.asInstanceOf[SMDatatype]
  }
}
