package org.simplemodeling.SimpleModeler.builder

import org.goldenport.entities.orgmode._
import org.goldenport.entities.outline.OutlineNodeShow
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/**
 * @since   Feb. 24, 2012
 * @version Feb. 27, 2012
 * @author  ASAMI, Tomoharu
 */
class OrgSimpleModelMakerBuilder(
  simpleModel: SimpleModelMakerEntity,
  policy: Policy, packageName: String, val org: OrgmodeEntity
  ) extends OutlineSimpleModelMakerBuilder(simpleModel, policy, packageName, org) {

  def build() {
    simpleModel using {
      build_model
    }
  }
}
