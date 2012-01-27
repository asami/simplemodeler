package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Nov.  8, 2008
 * Dec.  3, 2008
 */
abstract class SPath(val markRange: (String, String), val condition: String) extends SElement {
}
