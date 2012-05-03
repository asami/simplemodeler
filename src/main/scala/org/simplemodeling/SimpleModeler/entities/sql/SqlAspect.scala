package org.simplemodeling.SimpleModeler.entities.sql

import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   May.  3, 2012
 * @version May.  3, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SqlAspect extends GenericAspect with JavaMakerHolder {
  def openSqlClass(els: SqlClassDefinition) {
    // XXX
  }
}
