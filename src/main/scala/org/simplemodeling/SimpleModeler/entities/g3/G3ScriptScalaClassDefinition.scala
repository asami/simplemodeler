package org.simplemodeling.SimpleModeler.entities.g3

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.dsl.datatype.XInt
import org.simplemodeling.dsl.SDatatype
import org.simplemodeling.dsl.SDatatypeFunction
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/*
 * @since   Aug. 25, 2011
 * @version Aug. 25, 2011
 * @author  ASAMI, Tomoharu
 */
class G3ScriptScalaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  pobject: PObjectEntity
) extends G3ApplicationScalaClassDefinition(pContext, aspects, pobject) {
  customName = Some("App")

  override def head_package = null
}
