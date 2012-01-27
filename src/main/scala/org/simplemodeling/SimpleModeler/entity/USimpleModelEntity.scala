package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * @since   Nov. 20, 2008
 * @version Jul. 15, 2011
 * @author  ASAMI, Tomoharu
 */
object USimpleModelEntity {
  def isWellknownClass(anClass: Class[SObject]): Boolean = {
    val name = anClass.getName
    (name.startsWith("java") ||
     name.startsWith("org.goldenport.dsl.") ||
     name.startsWith("org.simplemodeling.dsl.")) &&
    !name.contains(".sample.")
  }

  def traverse(obj: Seq[SMObject], visitor: SimpleModelVisitor) {
    obj.foreach(visitor.visit(_))
  }
}
