package org.simplemodeling.SimpleModeler.builder

import org.goldenport.entities.csv._
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.smartdox._

/**
 * @since   Mar.  4, 2012
 * @version Mar. 11, 2012
 * @author  ASAMI, Tomoharu
 */
class DoxTablesSimpleModelMakerBuilder(
  builder: SimpleModelMakerBuilder,
  policy: Policy, packageName: String
) extends TableSimpleModelMakerBuilder(builder, policy, packageName) {

  def build(kind: ElementKind, dox: Dox) {
    sys.error("Not implemented yet.")
//    val tables = _get_tables(dox)
//    tables.foreach(build_model)
  }
}
