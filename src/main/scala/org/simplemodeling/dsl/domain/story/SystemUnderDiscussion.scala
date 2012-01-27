package org.simplemodeling.dsl.domain.story

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain.DomainActor

/*
 * @since   Nov.  4, 2011
 * @version Nov.  4, 2011
 * @author  ASAMI, Tomoharu
 */
case class SystemUnderDiscussion() extends DomainActor {
  caption = <t>システム。</t>
  brief = <t>モデリング対象のシステム。</t>
  description = <text>
ユースケースのモデリング対象のシステム。
  </text>
}

object SystemUnderDiscussion extends SystemUnderDiscussion {
}
