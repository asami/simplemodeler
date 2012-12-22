package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec. 21, 2012
 * @version Dec. 22, 2012
 * @author  ASAMI, Tomoharu
 */
class JavaExpressionBuilder(
  c: PEntityContext,
  val klass: JavaClassDefinition,
  val attr: JavaClassAttributeDefinition,
  expr: SMExpression
) extends ExpressionBuilder(c, expr) {
  val attributes = klass.wholeAttributeDefinitions.map(_.attr)
}
