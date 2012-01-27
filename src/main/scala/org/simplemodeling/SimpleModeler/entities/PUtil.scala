package org.simplemodeling.SimpleModeler.entities

// derived from GaejConstraint since Jul.  3, 2009.
/*
 * @since   Jul.  3, 2009
 * @version Apr. 23, 2011
 * @author  ASAMI, Tomoharu
 */
object PUtil {
  def java_type(anAttr: PAttribute) = {
    anAttr.typeName
  }

  def java_element_type(anAttr: PAttribute) = {
    anAttr.elementTypeName
  }

  def jdo_type(anAttr: PAttribute) = {
    anAttr.jdoTypeName
  }

  def jdo_element_type(anAttr: PAttribute) = {
    anAttr.jdoElementTypeName
  }

  def java_doc_type(anAttr: PAttribute) = {
    if (anAttr.isHasMany) {
      "List<" + java_doc_element_type(anAttr) + ">"
    } else {
      java_doc_element_type(anAttr)
    }
  }

  def java_doc_element_type(anAttr: PAttribute) = {
    anAttr.attributeType match {
      case e: PEntityType => {
        e.entity.documentName
      }
      case p: PEntityPartType => {
        p.part.documentName
      }
      case p: PPowertypeType => "String"
      case _ => java_element_type(anAttr)
    }
  }

/*
  def doc_attr_name(attr: PAttribute) = {
    attr.attributeType match {
      case e: PEntityType => e.entity.idName
      case _ => attr.name
    }
  }
*/
}
