package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov.  8, 2012
 * @version Nov.  8, 2012
 * @author  ASAMI, Tomoharu
 */
trait JavaClassAttributeDefinitionExtension {
  self: JavaClassAttributeDefinition =>

  def ext_to_xml {
    if (isSystemType) {
      jm_pln("""USimpleModeler.toXml(buf, "%s", %s);""", xmlElementName, code_get_value)
    } else {
      jm_if_not_null(code_get_value) {
        jm_pln("%s.toXmlElement(buf);", code_var_name)
      }
    }
  }
}
