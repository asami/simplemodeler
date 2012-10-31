package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._

/*
 * @since   Oct. 30, 2012
 * @version Oct. 31, 2012
 * @author  ASAMI, Tomoharu
 */
trait JavaClassAttributeDefinitionHelper {
  self: JavaClassAttributeDefinition =>

  def code_expression: String = {
    if (isDerive) {
      attr.deriveExpression.map(x => {
        x.drawTree
      }) | varName
    } else {
      varName
    }
  }
}
