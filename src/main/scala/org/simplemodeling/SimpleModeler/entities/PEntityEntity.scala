package org.simplemodeling.SimpleModeler.entities

import java.io._
import java.util.{GregorianCalendar, TimeZone}
import java.text.SimpleDateFormat
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.PUtil._

// derived from GaejEntityEntity since Apr. 10, 2009
/*
 * @since   Apr. 23, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
trait PEntityEntity extends PEntityObjectEntity {
//  def modelEntity: SMEntity = modelObject.asInstanceOf[SMEntity]
    def modelEntity: SMEntity
}
