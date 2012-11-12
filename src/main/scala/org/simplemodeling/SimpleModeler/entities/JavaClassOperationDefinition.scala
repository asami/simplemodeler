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

  private def _dt2docname(t: PDocumentType): String = {
    t.document.fold(_.name, "UnknownDocument-" + t.name)
  }

  val methodname = op.name
  val resultType = op.out.map(_dt2docname)
  val paramType = op.in.map(_dt2docname)
  val effectiveResultTypeName = resultType | "void"
  val effectiveParamLists = paramType.map(x => {
    x + " in"
  }) | ""

  def method {
    jm_public_method("%s %s(%s)", effectiveResultTypeName, methodname,
                     effectiveParamLists) {
      jm_UnsupportedOperationException
    }
  }
}
