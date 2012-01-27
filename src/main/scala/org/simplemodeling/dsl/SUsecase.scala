package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl.domain._
import org.goldenport.value.{GTree, PlainTree, GTreeCursor}

/*
 * generalization, include, extend
 * precedes, invoke
 *
 * TODO: includeやextendの定義はフロー中に行い、自動抽出する。
 *
 * Dec.  7, 2008
 * 
 * @since   Nov.  6, 2008
 * @version Nov.  4, 2011
 * @author  ASAMI, Tomoharu
 */
class SUsecase(name: String, pkgname: String) extends SStoryObject(name, pkgname) {
  def this() = this(null, null)
}

/*
  artifact_export
  artifact_create
  artifact_export
  artifact_read
  artifact_update
  artifact_delete
  artifact_crud
  artifact_import

  actor_primary
  optional
  zeroOrMore
  oneMore
  step
  step_actor_lookup
  step_actor_submit
  step_actor_confirm
  step_actor_invoke

  step_worker_lookup
  step_worker_submit
  step_worker_confirm
  step_worker_invoke

  step_system_create
  step_system_call
  step_system_send
  step_system_receive
  step_system_transform

  path

  step_include
*/
