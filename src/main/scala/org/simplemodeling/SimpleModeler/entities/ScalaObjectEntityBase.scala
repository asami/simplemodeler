package org.simplemodeling.SimpleModeler.entities

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.goldenport.util.Booleans
import java.io.BufferedWriter
import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.entity._
import java.text.SimpleDateFormat
import java.util.TimeZone
import com.asamioffice.goldenport.text.UString.notNull

/*
 * @since   Aug. 20, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class ScalaObjectEntityBase(val scalaContext: ScalaEntityContextBase)
    extends PObjectEntity(scalaContext) {

  val fileSuffix = "scala"
}
