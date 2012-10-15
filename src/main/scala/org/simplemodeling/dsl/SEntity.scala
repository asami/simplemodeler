package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/*
 * Nov.  6, 2009
 * @since   Sep. 10, 2008
 *  version Sep. 18, 2011
 * @version Oct. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class SEntity(name: String, pkgname: String) extends SObject(name, pkgname) {
  def this() = this(null, null)

  // @deprecated
  val id = attribute.candidate("id", IdAttributeKind)
  val Id = id
  val ID = id

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
