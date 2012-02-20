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
class Cdi299Aspect extends JavaAspect {
  override def weaveImports() {
  }

  override def weaveAttributeSlot(attr: PAttribute, varName: String) {
  }
}

object Cdi299Aspect extends Cdi299Aspect
