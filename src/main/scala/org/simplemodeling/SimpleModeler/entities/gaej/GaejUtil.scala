package org.simplemodeling.SimpleModeler.entities.gaej

/*
 * @since   Jul.  3, 2009
 * @version Oct. 30, 2009
 * @author  ASAMI, Tomoharu
 */
object  GaejUtil {
  def java_type(anAttr: GaejAttribute) = {
    anAttr.typeName
  }

  def java_element_type(anAttr: GaejAttribute) = {
    anAttr.elementTypeName
  }

  def jdo_type(anAttr: GaejAttribute) = {
    anAttr.jdoTypeName
  }

  def jdo_element_type(anAttr: GaejAttribute) = {
    anAttr.jdoElementTypeName
  }

  def java_doc_type(anAttr: GaejAttribute) = {
    if (anAttr.isHasMany) {
      "List<" + java_doc_element_type(anAttr) + ">"
    } else {
      java_doc_element_type(anAttr)
    }
  }

  def java_doc_element_type(anAttr: GaejAttribute) = {
    anAttr.attributeType match {
      case e: GaejEntityType => {
        e.entity.documentName
      }
      case p: GaejEntityPartType => {
        p.part.documentName
      }
      case p: GaejPowertypeType => "String"
      case _ => java_element_type(anAttr)
    }
  }

/*
  def doc_attr_name(attr: GaejAttribute) = {
    attr.attributeType match {
      case e: GaejEntityType => e.entity.idName
      case _ => attr.name
    }
  }
*/
}
