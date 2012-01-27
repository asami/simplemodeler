package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl

/*
 * @since   Jan. 24, 2012
 * @version Jan. 24, 2012
 * @author  ASAMI, Tomoharu
 */
class GenericDomainEntity(name: String, pkgname: String, val kind: List[String] = Nil) extends DomainEntity(name, pkgname) {
}
