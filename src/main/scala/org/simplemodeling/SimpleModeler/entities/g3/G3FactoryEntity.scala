package org.simplemodeling.SimpleModeler.entities.g3

import java.io._
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Jul. 12, 2011
 * @version Jul. 12, 2011
 * @author  ASAMI, Tomoharu
 */
class G3FactoryEntity(aContext: G3EntityContext) extends G3ObjectEntity(aContext) with PFactoryEntity {
}
