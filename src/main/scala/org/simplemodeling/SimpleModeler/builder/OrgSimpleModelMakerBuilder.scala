package org.simplemodeling.SimpleModeler.builder

import org.goldenport.entities.orgmode._
import org.goldenport.entities.outline.OutlineNodeShow
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/**
 * @since   Feb. 24, 2012
 * @version Mar. 11, 2012
 * @author  ASAMI, Tomoharu
 */
class OrgSimpleModelMakerBuilder(
  simpleModel: SimpleModelMakerEntity,
  policy: Policy, packageName: String, val org: OrgmodeEntity
  ) extends OutlineSimpleModelMakerBuilder(simpleModel, policy, packageName, org) {

  protected val table_Model_Builder = new DoxTablesSimpleModelMakerBuilder(model_Builder, policy, packageName)

  def build() {
    simpleModel using {
      build_model
    }
  }
}
