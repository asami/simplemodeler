package org.simplemodeling.SimpleModeler.entities

import com.asamioffice.goldenport.text.JavaScriptTextMaker

/**
 * @since   Apr.  4, 2012
 * @version Apr.  7, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class JavaScriptAspect extends GenericAspect with JavaScriptMakerHolder {
/*
  var jsClass: Option[JavaScriptClassDefinition] = None
*/
  def open(m: JavaScriptTextMaker) {
    jm_open(m)
  }
/*
  def openJavaScriptClass(jc: JavaScriptClassDefinition) {
    jsClass = Some(jc)
  }
*/
}
