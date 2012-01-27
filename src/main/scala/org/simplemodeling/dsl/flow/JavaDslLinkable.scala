package org.simplemodeling.dsl.flow

/**
 * @since   Mar.  6, 2011
 * @version Mar. 13. 2011
 * @author  ASAMI, Tomoharu
 */
trait JavaDslLinkable {
  implicit def string2holder(name: String): ClassHolder = {
    new StringClassHolder(name)
  }

  implicit def class2holder(klass: Class[_]): ClassHolder = {
    new ClassClassHolder(klass)
  }

  var java_dsl_class: ClassHolder = null
}

abstract class ClassHolder
class StringClassHolder(val name: String) extends ClassHolder
class ClassClassHolder(val klass: Class[_]) extends ClassHolder
