package org.simplemodeling.SimpleModeler.entities.sql

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import com.asamioffice.goldenport.text.UString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   May.  3, 2012
 *  version Jun. 16, 2012
 * @version Dec. 26, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SqlClassDefinition(
  val sqlContext: SqlEntityContext,
  anAspects: Seq[SqlAspect],
  val sqlObject: SqlObjectEntity,
  maker: JavaMaker = null
) extends GenericClassDefinition(sqlContext, anAspects, sqlObject) with JavaMakerHolder {
  type ATTR_DEF = SqlClassAttributeDefinition

  require (pobject != null, "SqlClassDefinition: sql object should not be null.")
  require (UString.notNull(sqlObject.name), "SqlClassDefinition: SqlObjectEntity.name should not be null.")

  if (maker == null) {
    jm_open(Nil) // XXX SqlAspect
  } else {
    jm_open(maker, Nil) // XXX SqlAspect
  }
  anAspects.foreach(_.openSqlClass(this))

  protected def pln() {
    jm_pln()
  }

  override def toText = {
    jm_to_text
  }

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new NullSqlClassAttributeDefinition(sqlContext, Nil, attr, this, maker) // XXX SqlAspect
  }

  override protected def class_open_body {
  }

  override protected def class_close_body {
  }

  override protected def attribute_variables_Prologue {
  }

  override protected def attribute_variables_Epilogue {
    jm_pln("CREATE TABLE %s (", sqlContext.sqlTableName(sqlObject))
    jm_indent_up
    wholeAttributeDefinitions.map { x =>
      () => x.ddl
    } intersperse {
      () => jm_pln(",")
    } map (_())
    jm_pln
    jm_indent_down
    jm_pln(");")
  }
}
