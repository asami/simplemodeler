package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString}
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainEntityPart

/*
 * @since   Jun. 20, 2009
 * @version Jun. 20, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeEntityPartEntity(aContext: GEntityContext) extends GaeObjectEntity(aContext) {
  var modelEntityPart: SMDomainEntityPart = null
}
