package org.simplemodeling.SimpleModeler.builder

import scalaz._
import Scalaz._
import org.goldenport.z._
import org.goldenport.Z._
import org.goldenport.value.GTable
import org.goldenport.entities.orgmode._
import org.goldenport.entities.outline._
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/**
 * @since   Feb. 27, 2012
 * @version Mar. 11, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class OutlineSimpleModelMakerBuilder(
  val simpleModel: SimpleModelMakerEntity,
  policy: Policy, packageName: String,
  entity: OutlineEntityBase) extends OutlineBuilderBase(policy, packageName, entity){

  val model_Builder = new SimpleModelMakerBuilder(simpleModel, packageName, policy)
  protected val table_Model_Builder: TableSimpleModelMakerBuilder

  override protected def create_Object_Table(kind: ElementKind, table: GTable[String]) {
    sys.error("not implemented yet")
  }


/*
  protected def build_model(entity: OutlineEntityBase) {
    val tree = entity.ztree
    println(tree.drawTree(OutlineNodeShow))

  }
*/
}
