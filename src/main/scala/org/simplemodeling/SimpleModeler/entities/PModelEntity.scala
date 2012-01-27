package org.simplemodeling.SimpleModeler.entities

import java.io._
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

/*
 * @since   Jul. 12, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
trait PModelEntity extends PObjectEntity {
}
