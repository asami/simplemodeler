package org.simplemodeling.dsl

/*
 * @since   Nov. 12, 2012
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * SEntityDocument is a special document which will be converted SMDocument
 * for an Entity.
 */
class SEntityDocument(aName: String, aPkgName: String) extends SDocument(aName, aPkgName) with SAttributeType {
  def this() = this(null, null)
  def this(aName: String) = this(aName, null)
}
