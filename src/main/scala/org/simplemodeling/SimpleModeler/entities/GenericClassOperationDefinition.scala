package org.simplemodeling.SimpleModeler.entities

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull
import org.simplemodeling.dsl._
import java.text.SimpleDateFormat
import java.util.TimeZone

/* 
 * @since   Nov. 11, 2012
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class GenericClassOperationDefinition(
  val pContext: PEntityContext,
  val aspects: Seq[GenericAspect],
  val op: POperation,
  val owner: GenericClassDefinition
) {
  def method: Unit
}

object NullClassOperationDefinition extends GenericClassOperationDefinition(null, Nil, null, null) {
  def method { }
}
