package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov.  8, 2012
 * @version Nov.  8, 2012
 * @author  ASAMI, Tomoharu
 */
trait JavaClassCodeUtils extends JavaCodeUtils {
  self: JavaClassDefinition =>

  final protected def code_entity_xmlString {
    jm_public_method("String entity_xmlString()") {
      jm_var_new_StringBuilder
      jm_pln("entity_asXmlString(buf);")
      jm_return_StringBuilder
    }
  }

  final protected def code_entity_asXmlString {
    jm_public_method("void entity_asXmlString(StringBuilder buf)") {
      jm_append_String("<" + xmlElementName + " xmlns=\"" + xmlNamespace + "\">")
      for (attr <- attributes) {
        jm_pln(code_get_string_element(attr) + ";")
      }
      jm_append_String("</" + xmlElementName + ">")
    }
  }
}
