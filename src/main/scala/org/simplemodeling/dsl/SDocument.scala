package org.simplemodeling.dsl

/*
 * Sep. 11, 2008
 * Jan.  9, 2009
 */
class SDocument(aName: String, aPkgName: String) extends SObject(aName, aPkgName) with SAttributeType {
  def this() = this(null, null)
  def this(aName: String) = this(aName, null)

  if (aName == "Aliveness") sys.error("???")
}

object NullDocument extends SDocument
