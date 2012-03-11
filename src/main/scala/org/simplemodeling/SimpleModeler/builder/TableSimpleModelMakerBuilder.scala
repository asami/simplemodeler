package org.simplemodeling.SimpleModeler.builder

import org.goldenport.entities.csv._
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/**
 * @since   Mar.  6, 2012
 * @version Mar. 11, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class TableSimpleModelMakerBuilder(
  builder: SimpleModelMakerBuilder,
  policy: Policy, packageName: String
) extends TabularBuilderBase(policy, packageName) {
  val model_Builder = builder

  override protected def build_Model {}
}
