package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.goldenport.recorder.GRecordable
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Jan. 11, 2013
 * @version Jan. 15, 2013
 * @author  ASAMI, Tomoharu
 */
abstract class StringExpressionBuilder(
  c: PEntityContext, e: SMExpression
) extends ExpressionBuilder(c, e) {
  type RESULT_TYPE = String

  protected def root_Result = ""
}
