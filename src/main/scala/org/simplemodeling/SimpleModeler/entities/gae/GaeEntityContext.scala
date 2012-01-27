package org.simplemodeling.SimpleModeler.entities.gae

import org.goldenport.entity._

/*
 * @since   Apr.  7, 2009
 * @version Apr.  7, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeEntityContext(aContext: GEntityContext) extends GSubEntityContext(aContext) {
  final def getEntityBaseName(anEntity: GaeObjectEntity) = {
    anEntity.term_en
  }

  final def getEntityCntrollerPath(anEntity: GaeObjectEntity) = {
    "/" + getEntityBaseName(anEntity)
  }
}

