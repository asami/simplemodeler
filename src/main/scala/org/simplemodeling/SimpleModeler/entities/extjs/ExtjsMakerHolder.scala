package org.simplemodeling.SimpleModeler.entities.extjs

import org.simplemodeling.SimpleModeler.entities.JavaScriptMakerHolder

/*
 * @since   Apr.  7, 2012
 * @version Apr.  7, 2012
 * @author  ASAMI, Tomoharu
 */
trait ExtjsMakerHolder extends JavaScriptMakerHolder {
  private var _ej_maker: ExtjsTextMaker = null

  protected def invariants_opened {
    assume(_ej_maker != null)
  }

  def ejmaker = _ej_maker

  protected def ej_open(m: ExtjsTextMaker) {
    _ej_maker = m
    jm_open(m)
  }

  protected def ej_open(aspects: Seq[ExtjsAspect]) {
    ej_open(new ExtjsTextMaker, aspects)
  }

  protected def ej_open(m: ExtjsTextMaker, aspects: Seq[ExtjsAspect]) {
    _ej_maker = m
    jm_open(m, aspects)
  }
}
