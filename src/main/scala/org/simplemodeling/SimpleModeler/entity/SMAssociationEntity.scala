package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * @since   Nov. 25, 2012
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
trait SMAssociationEntity extends SMEntity {
}

object SMNullAssociationEntity extends SMEntity(NullEntity) with SMAssociationEntity
