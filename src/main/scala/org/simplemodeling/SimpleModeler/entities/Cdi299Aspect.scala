package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull

/*
 * @since   Dec. 15, 2011
 * @version Feb.  9, 2012
 * @author  ASAMI, Tomoharu
 */
class Cdi299Aspect extends JavaAspect {
  override def weaveImports() {
    jm_import("javax.validation.constraints.*")
  }

  override def weaveAttributeSlot(attr: PAttribute, varName: String) {
    for (c <- attr.getConstraints) {
      jm_pln(c.literal)
    }
  }
}

object Cdi299Aspect extends Cdi299Aspect
