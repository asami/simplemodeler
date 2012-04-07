package org.simplemodeling.SimpleModeler.entities.extjs

import com.asamioffice.goldenport.text.JavaScriptTextMaker

/**
 * @since   Apr.  7, 2012
 * @version Apr.  7, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsTextMaker(aTemplate: CharSequence, theReplaces: Map[String, String]
                   ) extends JavaScriptTextMaker(aTemplate, theReplaces) {
  def this() = this(null, null)
  def this(aTemplate: CharSequence) = this(aTemplate, null)
}
