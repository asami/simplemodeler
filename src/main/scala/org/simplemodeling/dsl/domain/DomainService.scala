package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * @since   Sep. 11, 2008
 *  version Nov. 12, 2010
 * @version Oct. 21, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class DomainService extends SService {
  def default_service_context(operations: => Unit): SServiceContext = {
    new SServiceContext
  }

  def service_context(operations: => Unit): SServiceContext = {
    new SServiceContext
  }

  override def class_Name = "DomainService"
}
