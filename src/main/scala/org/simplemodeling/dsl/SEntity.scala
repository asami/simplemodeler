package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/*
 * Nov.  6, 2009
 * @since   Sep. 10, 2008
 * @version Sep. 18, 2011
 * @author  ASAMI, Tomoharu
 */
class SEntity(name: String, pkgname: String) extends SObject(name, pkgname) {
  def this() = this(null, null)

  // @deprecated
  val id = attribute.candidate("id", IdAttributeKind)
  val Id = id
  val ID = id

  // id
  def attribute_id: SAttribute = attribute_id('id)
  def attribute_id(symbol: Symbol): SAttribute = attribute_id(symbol.name)
  def attribute_id(name: String): SAttribute = attribute.create(name, XLong) kind_is IdAttributeKind idPolicy_is AutoIdPolicy
  // name
  def attribute_name: SAttribute = attribute_name('name)
  def attribute_name(symbol: Symbol): SAttribute = attribute_name(symbol.name)
  def attribute_name(name: String): SAttribute = attribute.create(name, XString) kind_is NameAttributeKind
  // user
  def attribute_user: SAttribute = attribute_user('user)
  def attribute_user(symbol: Symbol): SAttribute = attribute_user(symbol.name)
  def attribute_user(name: String): SAttribute = attribute.create(name, XUser) kind_is UserAttributeKind
  // title
  def attribute_title: SAttribute = attribute_title('title)
  def attribute_title(symbol: Symbol): SAttribute = attribute_title(symbol.name)
  def attribute_title(name: String): SAttribute = attribute.create(name, XString) kind_is TitleAttributeKind
  // subtitle
  def attribute_subtitle: SAttribute = attribute_subtitle('subtitle)
  def attribute_subtitle(symbol: Symbol): SAttribute = attribute_subtitle(symbol.name)
  def attribute_subtitle(name: String): SAttribute = attribute.create(name, XString) kind_is SubTitleAttributeKind
  // summary
  def attribute_summary: SAttribute = attribute_summary('summary)
  def attribute_summary(symbol: Symbol): SAttribute = attribute_summary(symbol.name)
  def attribute_summary(name: String): SAttribute = attribute.create(name, XString) kind_is SummaryAttributeKind
  // category
  def attribute_category: SAttribute = attribute_category('category)
  def attribute_category(symbol: Symbol): SAttribute = attribute_category(symbol.name)
  def attribute_category(name: String): SAttribute = attribute.create(name, XCategory, ZeroMore) kind_is CategoryAttributeKind
  // categories
  def attribute_categories: SAttribute = attribute_categories('categories)
  def attribute_categories(symbol: Symbol): SAttribute = attribute_categories(symbol.name)
  def attribute_categories(name: String): SAttribute = attribute.create(name, XCategory) kind_is CategoryAttributeKind
  // author
  def attribute_author: SAttribute = attribute_author('author)
  def attribute_author(symbol: Symbol): SAttribute = attribute_author(symbol.name)
  def attribute_author(name: String): SAttribute = attribute.create(name, XString) kind_is AuthorAttributeKind
  // icon
  def attribute_icon: SAttribute = attribute_icon('icon)
  def attribute_icon(symbol: Symbol): SAttribute = attribute_icon(symbol.name)
  def attribute_icon(name: String): SAttribute = attribute.create(name, XString) kind_is IconAttributeKind
  // logo
  def attribute_logo: SAttribute = attribute_logo('logo)
  def attribute_logo(symbol: Symbol): SAttribute = attribute_logo(symbol.name)
  def attribute_logo(name: String): SAttribute = attribute.create(name, XString) kind_is LogoAttributeKind
  // link
  def attribute_link: SAttribute = attribute_link('link)
  def attribute_link(symbol: Symbol): SAttribute = attribute_link(symbol.name)
  def attribute_link(name: String): SAttribute = attribute.create(name, XLink) kind_is LinkAttributeKind
  // content
  def attribute_content: SAttribute = attribute_content('content)
  def attribute_content(symbol: Symbol): SAttribute = attribute_content(symbol.name)
  def attribute_content(name: String): SAttribute = attribute.create(name, XText) kind_is ContentAttributeKind
  // published
  def attribute_created: SAttribute = attribute_created('created)
  def attribute_created(symbol: Symbol): SAttribute = attribute_created(symbol.name)
  def attribute_created(name: String): SAttribute = attribute.create(name, XDateTime) kind_is CreatedAttributeKind
  // updated
  def attribute_updated: SAttribute = attribute_updated('updated)
  def attribute_updated(symbol: Symbol): SAttribute = attribute_updated(symbol.name)
  def attribute_updated(name: String): SAttribute = attribute.create(name, XDateTime) kind_is UpdatedAttributeKind
  // contributor
  // rights
  // source

  def composition_comment(aSymbol: Symbol, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    composition_comment(aSymbol.name, anEntity, aMultiplicity)
  }

  def composition_comment(aName: String, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    association.create(aName, anEntity, aMultiplicity) composition_is true atomLinkRel_is "comment"
  }

  val sql = new Sql
  val jdo = new JdoPersistenceCapable
  val appEngine = new AppEngine
}

object NullEntity extends SEntity

class Sql {
  var table: String = null
}

class JdoPersistenceCapable {
  var detachable: Boolean = true
  var catalog: String = null
  var schema: String = null
  var table: String = null
}

class AppEngine {
  var use_key: Boolean = false
  var use_owned_property: Boolean = true
  var logical_operation: Boolean = false
}
