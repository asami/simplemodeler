package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.expr.JavaExpressionBuilder

/*
 * @since   Nov.  8, 2012
 * @version Dec. 21, 2012
 * @author  ASAMI, Tomoharu
 */
trait JavaClassAttributeCodeUtils extends JavaCodeUtils {
  self: JavaClassAttributeDefinition =>

  protected final def code_get_value: String = {
    code_get_value(this)
  }

  protected final def code_var_name: String = {
    code_var_name(this)
  }

  protected final def code_element_type: String = {
    if (isDocument) {
      attr.attributeType match {
        case e: PEntityType => code_single_document_type(e)
        case _ => java_element_type
      }
    } else {
      java_element_type
    }
  }

  protected final def code_container_type: String = {
    if (isDocument) {
      attr.attributeType match {
        case e: PEntityType => code_multi_document_type(e)
        case _ => java_element_type
      }
    } else {
      java_type
    }
  }

  def code_expression: String = {
    if (isDerive) {
      attr.deriveExpression.map(code_expression) | varName
    } else {
      varName
    }
  }

  def code_expression(expr: PExpression): String = {
    code_expression(expr.model)
  }

  def code_expression(expr: SMExpression): String = {
    val builder = new JavaExpressionBuilder(pContext, jowner, this, expr)
    builder.build
  }
}
