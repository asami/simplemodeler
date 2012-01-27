package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import java.util.Locale
import org.goldenport.sdoc._
import com.asamioffice.goldenport.util.LocaleRepository

/*
 * Sep. 11, 2008
 * Feb.  9, 2009
 */
class SName {
  var name: String = _
  var term: String = _
  private val _names_locale = new LocaleRepository[String]
  private val _terms_locale = new LocaleRepository[String]

  def name_en: String = {
    _names_locale.getEnOrElse(name)
  }

  def name_en_=(aName: String) {
    _names_locale.putEn(aName)
  }

  def term_en: String = {
    _terms_locale.getEnOrElse(term)
  }

  def term_en_=(aTerm: String) = {
    _terms_locale.putEn(aTerm)
  }

  def name_ja: String = {
    _names_locale.getJaOrElse(name)
  }

  def name_ja_=(aName: String) {
    _names_locale.putJa(aName)
  }

  def term_ja: String = {
    _terms_locale.getJaOrElse(term)
  }

  def term_ja_=(aTerm: String) = {
    _terms_locale.putJa(aTerm)
  }

  override def toString: String = name
}
