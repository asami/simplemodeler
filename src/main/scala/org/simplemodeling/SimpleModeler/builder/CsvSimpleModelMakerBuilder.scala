package org.simplemodeling.SimpleModeler.builder

import org.goldenport.entities.csv._
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/**
 * @since   Feb. 23, 2012
 * @version Feb. 24, 2012
 * @author  ASAMI, Tomoharu
 */
class CsvSimpleModelMakerBuilder(
  val simpleModel: SimpleModelMakerEntity,
  policy: Policy, packageName: String, csv: CsvEntity
) extends CsvBuilderBase(policy, packageName, csv) {
  val model_Builder = new SimpleModelMakerBuilder(simpleModel, packageName, policy)

  def build() {
    build_model
  }
}
