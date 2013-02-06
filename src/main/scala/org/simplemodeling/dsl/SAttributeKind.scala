package org.simplemodeling.dsl

/*
 * @since   Sep. 12, 2008
 *  version Oct. 24, 2009
 * @version Feb.  6, 2013
 * @author  ASAMI, Tomoharu
 */
class SAttributeKind {
}

class NullAttributeKind extends SAttributeKind
object NullAttributeKind extends NullAttributeKind
class IdAttributeKind extends SAttributeKind
object IdAttributeKind extends IdAttributeKind
class NameAttributeKind extends SAttributeKind
object NameAttributeKind extends NameAttributeKind
class LogicalDeleteAttributeKind extends SAttributeKind
object LogicalDeleteAttributeKind extends LogicalDeleteAttributeKind

/**
 * Googleアカウントと連結。(OpenIdは?)
 */
class UserAttributeKind extends SAttributeKind
object UserAttributeKind extends UserAttributeKind

/**
 * atom:feed/atom:title
 * atom:entry/atom:title
 * app:workspace/atom:title
 * app:collection/atom:title
 */
class TitleAttributeKind extends SAttributeKind
object TitleAttributeKind extends TitleAttributeKind

/**
 * atom:feed/atom:subtitle
 */
class SubTitleAttributeKind extends SAttributeKind
object SubTitleAttributeKind extends SubTitleAttributeKind

/**
 * atom:entry/atom:summary
 */
class SummaryAttributeKind extends SAttributeKind
object SummaryAttributeKind extends SummaryAttributeKind

/**
 * atom:entry/atom:author
 * atom:feed/atom:author
 *
 * Googleアカウントと連結。(OpenIdは?)
 */
class AuthorAttributeKind extends SAttributeKind
object AuthorAttributeKind extends AuthorAttributeKind

/**
 * atom:entry/atom:category*
 * atom:feed/atom:category*
 */
class CategoryAttributeKind extends SAttributeKind
object CategoryAttributeKind extends CategoryAttributeKind

/**
 * app:collection/app:categories*
 * schemeごとにcategoryの集合を定義
 * 
 */
class CategoriesAttributeKind extends SAttributeKind
object CategoriesAttributeKind extends CategoriesAttributeKind

/**
 * atom:feed/atom:icon
 * アイコン画像の管理ロジックを自動生成。
 */
class IconAttributeKind extends SAttributeKind
object IconAttributeKind extends IconAttributeKind

/**
 * atom:feed/atom:logo
 * ロゴ画像の管理ロジックを自動生成。
 */
class LogoAttributeKind extends SAttributeKind
object LogoAttributeKind extends LogoAttributeKind

/**
 * atom:entry/atom:link
 * atom:feed/atom:link
 */
class LinkAttributeKind extends SAttributeKind
object LinkAttributeKind extends LinkAttributeKind

/**
 * atom:entry/atom:content
 */
class ContentAttributeKind extends SAttributeKind
object ContentAttributeKind extends ContentAttributeKind

/**
 * atom:entry/atom:published
 */
class CreatedAttributeKind extends SAttributeKind
object CreatedAttributeKind extends CreatedAttributeKind

/**
 * atom:entry/atom:updated
 * atom:feed/atom:updated
 */
class UpdatedAttributeKind extends SAttributeKind
object UpdatedAttributeKind extends UpdatedAttributeKind

/**
 * Used for logical delete.
 */
class DeletedAttributeKind extends SAttributeKind
object DeletedAttributeKind extends UpdatedAttributeKind
