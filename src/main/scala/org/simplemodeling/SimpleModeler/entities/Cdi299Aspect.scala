package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull

/*
 * @since   Dec. 15, 2011
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class Cdi299Aspect extends JavaAspect {
  var modelEntity: SMEntity = null
  var is_logical_operation = false

  override def weaveImports() {
    jm_import("javax.jdo.PersistenceManager")
    jm_import("javax.jdo.Query")
    jm_import("javax.jdo.Transaction")
    jm_import("javax.jdo.annotations.*")
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

object Cdi299Aspect extends Cdi299Aspect
