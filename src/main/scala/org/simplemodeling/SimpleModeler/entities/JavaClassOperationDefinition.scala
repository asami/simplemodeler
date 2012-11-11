package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull
import org.simplemodeling.dsl._
import java.text.SimpleDateFormat
import java.util.TimeZone

/*
 * Java Class Operation Definition
 * 
 * @since   Nov. 12, 2012
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
class JavaClassOperationDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  op: POperation,
  owner: JavaClassDefinition,
  jmaker: JavaMaker
) extends GenericClassOperationDefinition(pContext, aspects, op, owner) with JavaMakerHolder {
  jm_open(jmaker, aspects)

  def method {
    val resulttype = op.out.map(_.name) | "void"
    val methodname = op.name
    val params = op.in.map(x => {
      x.name + " in"
    }) | ""
    jm_public_method("%s %s(%s)", resulttype, methodname, params) {
      jm_UnsupportedOperationException
    }
  }
}
