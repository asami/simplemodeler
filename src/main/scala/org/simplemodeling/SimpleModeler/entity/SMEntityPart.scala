package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * Jan. 19, 2009
 * Jan. 19, 2009
 */
trait SMEntityPart extends SMEntity {
}

object SMNullEntityPart extends SMEntity(NullEntity) with SMEntityPart
