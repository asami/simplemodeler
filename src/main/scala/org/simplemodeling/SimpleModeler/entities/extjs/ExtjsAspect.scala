package org.simplemodeling.SimpleModeler.entities.extjs

import org.simplemodeling.SimpleModeler.entities.JavaScriptAspect

/**
 * @since   Apr.  7, 2012
 * @version Apr.  7, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class ExtjsAspect extends JavaScriptAspect with ExtjsMakerHolder {
  def openExtjsClass(els: ExtjsClassDefinition) {
    // XXX
  }
}
