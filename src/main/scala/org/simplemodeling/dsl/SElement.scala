package org.simplemodeling.dsl

/*
 * derived from ModelElement since Mar. 18, 2007
 *
 * @since   Sep. 21, 2008
 * @version Jul. 19, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class SElement(aName: String) extends SDescriptable with SHistoriable {
  type SElement_TYPE = SElement
  val denomination = new SName

  if (aName != null) {
    set_name(aName)
  }

  def this() = this(null)

  protected final def set_name(aName: String) {
    require (denomination.name == null)
    denomination.name = aName;
    denomination.term = util.UDsl.guessTerm(aName)
  }

  protected final def set_name_by_className {
    require (denomination.name == null)
    denomination.name = util.UDsl.getNameFromClassName(this)
    denomination.term = util.UDsl.guessTerm(this)
  }

  protected final def change_name(aName: String) {
    denomination.name = aName;
  }

  final def name: String = denomination.name

  def term_=(aTerm: String) {
    denomination.term = aTerm
  }

  def term: String = denomination.term

  def term_is(aTerm: String): SElement_TYPE = {
    term = aTerm;
    return this;
  }

  // english
  def name_en_=(aName: String) {
    denomination.name_en = aName
  }

  def name_en: String = denomination.name_en

  def name_en_is(aName: String): SElement_TYPE = {
    name_en = aName;
    return this;
  }

  def term_en_=(aTerm: String) {
    denomination.term_en = aTerm
  }

  def term_en: String = denomination.term_en

  def term_en_is(aTerm: String): SElement_TYPE = {
    term_en = aTerm;
    return this;
  }

  // japanese
  def name_ja_=(aName: String) {
    denomination.name_ja = aName
  }

  def name_ja: String = denomination.name_ja

  def name_ja_is(aName: String): SElement_TYPE = {
    name_ja = aName;
    return this;
  }

  def term_ja_=(aTerm: String) {
    denomination.term_ja = aTerm
  }

  def term_ja: String = denomination.term_ja

  def term_ja_is(aTerm: String): SElement_TYPE = {
    term_ja = aTerm;
    return this;
  }

  // XXX planning stuff

  protected def syntax_error(message: String) {
    error("syntax error = " + message)
  }
}
