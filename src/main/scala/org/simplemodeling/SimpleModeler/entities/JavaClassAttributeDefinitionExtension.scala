package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov.  8, 2012
 * @version Nov.  9, 2012
 * @author  ASAMI, Tomoharu
 */
trait JavaClassAttributeDefinitionExtension {
  self: JavaClassAttributeDefinition =>

  def ext_to_xml {
    if (isMulti) _ext_to_xml_multi
    else _ext_to_xml_single
  }

  private def _ext_to_xml_multi {
    if (isSystemType) {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_xml_multi")
        jm_pln("""USimpleModeler.toXml(buf, "%s", elem);""", xmlElementName)
      }
    } else {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_xml_multi")
        jm_pln("elem.toXmlElement(buf);")
      }
    }
  }

  private def _ext_to_xml_single {
    if (isSystemType) {
      jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_xml_single")
      jm_pln("""USimpleModeler.toXml(buf, "%s", %s);""", xmlElementName, code_get_value)
    } else {
      jm_if_not_null(code_get_value) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_xml_single")
        jm_pln("%s.toXmlElement(buf);", code_var_name)
      }
    }
  }

  // json
  def ext_to_json {
    if (isMulti) _ext_to_json_multi
    else _ext_to_json_single
  }

  private def _ext_to_json_multi {
    if (isSystemType) {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_json_multi")
          jm_pln("""USimpleModeler.toJson(buf, %s, elem);""", propertyConstantName)
      }
    } else {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_json_multi")
        jm_pln("""USimpleModeler.toJson(buf, %s, elem.toJson());""", propertyConstantName)
      }
    }
  }

  private def _ext_to_json_single {
    if (isSystemType) {
      jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_json_single")
          jm_pln("""USimpleModeler.toJson(buf, %s, %s);""", propertyConstantName, code_get_value)
    } else {
      jm_if_not_null(code_get_value) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_json_single")
        jm_pln("""USimpleModeler.toJson(buf, %s, %s.toJson());""", propertyConstantName, code_var_name)
      }
    }
  }

  // csv
  def ext_to_csv {
    if (isMulti) _ext_to_csv_multi
    else _ext_to_csv_single
  }

  private def _ext_to_csv_multi {
    if (isSystemType) {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_csv_multi")
        jm_pln("""USimpleModeler.toCsv(buf, %s, elem);""", propertyConstantName)
      }
    } else {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_csv_multi")
        jm_pln("""USimpleModeler.toCsv(buf, %s, elem.toCsv());""", propertyConstantName)
      }
    }
  }

  private def _ext_to_csv_single {
    if (isSystemType) {
      jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_csv_single")
          jm_pln("""USimpleModeler.toCsv(buf, %s, %s);""", propertyConstantName, code_get_value)
    } else {
      jm_if_not_null(code_get_value) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_csv_single")
        jm_pln("""USimpleModeler.toCsv(buf, %s, %s.toCsv());""", propertyConstantName, code_var_name)
      }
    }
  }

  // yaml
  def ext_to_yaml {
    if (isMulti) _ext_to_yaml_multi
    else _ext_to_yaml_single
  }

  private def _ext_to_yaml_multi {
    if (isSystemType) {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_yaml_multi")
          jm_pln("""USimpleModeler.toYaml(buf, %s, elem);""", propertyConstantName)
      }
    } else {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_yaml_multi")
        jm_pln("""USimpleModeler.toYaml(buf, %s, elem.toYaml());""", propertyConstantName)
      }
    }
  }

  private def _ext_to_yaml_single {
    if (isSystemType) {
      jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_yaml_single")
          jm_pln("""USimpleModeler.toYaml(buf, %s, %s);""", propertyConstantName, code_get_value)
    } else {
      jm_if_not_null(code_get_value) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_yaml_single")
        jm_pln("""USimpleModeler.toYaml(buf, %s, %s.toYaml());""", propertyConstantName, code_var_name)
      }
    }
  }

  // map
  def ext_to_map {
    if (isMulti) _ext_to_map_multi
    else _ext_to_map_single
  }

  private def _ext_to_map_multi {
    if (isSystemType) {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_map_multi")
          jm_pln("""USimpleModeler.toStringMap(buf, %s, elem);""", propertyConstantName)
      }
    } else {
      jm_for(code_element_type, "elem", code_var_name) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_map_multi")
        jm_pln("""USimpleModeler.toStringMap(buf, %s, elem.toStringMap());""", propertyConstantName)
      }
    }
  }

  private def _ext_to_map_single {
    if (isSystemType) {
      jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_map_single")
          jm_pln("""USimpleModeler.toStringMap(buf, %s, %s);""", propertyConstantName, code_get_value)
    } else {
      jm_if_not_null(code_get_value) {
        jm_mark("// JavaClassAttributeDefinitionExtension#_ext_to_map_single")
        jm_pln("""USimpleModeler.toStringMap(buf, %s, %s.toStringMap());""", propertyConstantName, code_var_name)
      }
    }
  }
}
