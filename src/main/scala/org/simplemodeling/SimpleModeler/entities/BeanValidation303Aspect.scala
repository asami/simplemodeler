package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull

/*
 * @since   Dec. 15, 2011
 * @version Feb.  6, 2012
 * @author  ASAMI, Tomoharu
 */
class BeanValidation303Aspect extends JavaAspect {
  var modelEntity: SMEntity = null
  var is_logical_operation = false

  override def weaveImports() {
  }

  override def weaveIdAttributeSlot(idAttr: PAttribute, varName: String): Boolean = {
    false
  }

  override def weavePersistentAnnotation(attr: PAttribute) {
  }

  override def weaveNotPersistentAnnotation(attr: PAttribute) {
    jm_pln("@NotPersistent")
  }

  override def weaveIdMethods(idAttr: PAttribute, attrName: String, varName: String, paramName: String, javaType: String): Boolean = {
    false
  }
}

object BeanValidation303Aspect extends BeanValidation303Aspect
