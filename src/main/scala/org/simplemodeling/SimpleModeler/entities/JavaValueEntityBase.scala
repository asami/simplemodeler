package org.simplemodeling.SimpleModeler.entities

import java.io._
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.PUtil._

/*
 * @since   Apr. 24, 2011
 * @version May. 14, 2011
 * @author  ASAMI, Tomoharu
 */
class JavaValueEntityBase(aContext: JavaEntityContextBase) extends JavaObjectEntityBase(aContext) {
}
