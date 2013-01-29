package org.simplemodeling.SimpleModeler.entities

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

/*
 * @since   Nov.  6, 2012
 * @version Jan. 29, 2013
 * @author  ASAMI, Tomoharu
 */
/*
 * Jan. 29, 2013:
 * Trials that PTraitEntity be PEntityEntity and PDocumentEntity does not work.*/
trait PTraitEntity extends PObjectEntity { // with PEntityEntity with PDocumentEntity {
  var isDocument = false
}
