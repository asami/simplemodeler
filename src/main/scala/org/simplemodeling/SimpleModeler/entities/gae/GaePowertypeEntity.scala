package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString}
import org.simplemodeling.SimpleModeler.entity.SMPowertype

/*
 * @since   Apr. 23, 2009
 * @version Oct. 26, 2009
 * @author  ASAMI, Tomoharu
 */
class GaePowertypeEntity(aContext: GEntityContext) extends GaeObjectEntity(aContext) {
  var modelPowertype: SMPowertype = null
}

