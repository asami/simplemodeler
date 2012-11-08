package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov.  8, 2012
 * @version Nov.  8, 2012
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
}
