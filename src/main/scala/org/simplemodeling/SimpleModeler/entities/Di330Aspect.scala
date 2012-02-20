package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull

/*
 * http://java.dzone.com/articles/what-relation-betwe-there
 *
 * @since   Dec. 15, 2011
 * @version Feb. 19, 2012
 * @author  ASAMI, Tomoharu
 */
class Di330Aspect extends JavaAspect {
  override def weaveImports() {
  }

  override def weaveIdAttributeSlot(idAttr: PAttribute, varName: String): Boolean = {
    false
  }

  override def weavePersistentAnnotation(attr: PAttribute) {
  }

  override def weaveNotPersistentAnnotation(attr: PAttribute) {
  }

  override def weaveIdMethods(idAttr: PAttribute, attrName: String, varName: String, paramName: String, javaType: String): Boolean = {
    false
  }
}

object Di330Aspect extends Di330Aspect
