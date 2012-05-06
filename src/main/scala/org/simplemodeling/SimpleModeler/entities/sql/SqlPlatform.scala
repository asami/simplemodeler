package org.simplemodeling.SimpleModeler.entities.sql

import org.goldenport.service.GServiceContext
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.entities.PEntityContext
import org.simplemodeling.SimpleModeler.transformers.sql.SimpleModel2SqlRealmTransformer

/**
 * @since   May.  5, 2012
 * @version May.  6, 2012
 * @author  ASAMI, Tomoharu
 */
object SqlPlatform {
  def create(pc: PEntityContext): SqlRealmEntity = {
    create(pc.model, pc, pc.serviceContext)
  }

  def create(sm: SimpleModelEntity, serviceContext: GServiceContext): SqlRealmEntity = {
    create(sm, sm.entityContext, serviceContext)
  }

  def create(sm: SimpleModelEntity, entityContext: GEntityContext, serviceContext: GServiceContext): SqlRealmEntity = {
    println("SqlPlatform.create: start")
    sm.print
    println("SqlPlatform.create: end")
    val sm2sql = new SimpleModel2SqlRealmTransformer(sm, serviceContext)
    sm2sql.transform
  }
}
