package org.simplemodeling.SimpleModeler.sdoc

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.simplemodeling.SimpleModeler.entity.SMObject

/*
 * @since   Dec.  6, 2008
 * @version May. 29, 2016
 * @author  ASAMI, Tomoharu
 */
class SMObjectRef(val sobject: SObject) extends SIAnchor(sobject.name) {
  unresolvedRef = new SElementRef(sobject.packageName, sobject.name)
  summary = sobject.summary

  def this(anObject: SMObject) = this(anObject.dslObject)
}
