package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull
import org.simplemodeling.dsl._
import java.text.SimpleDateFormat
import java.util.TimeZone

/*
 * @since   Nov. 12, 2012
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
class ServiceJavaClassOperationDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  op: POperation,
  owner: JavaClassDefinition,
  jmaker: JavaMaker
) extends JavaClassOperationDefinition(pContext, aspects, op, owner, jmaker) with JavaMakerHolder with JavaCodeUtils {
  override def method {
    val qname = owner.qualifiedName
    val resulttype = op.out.map(_.name) | "Void"
    val methodname = op.name
    val bodymethodname = methodname + "Body"
    val implmethodname = methodname + "Impl"
    val params = op.in.map(x => {
      x.name + " in"
    }) | ""
    jm_public_method("Response<%s> %s(Request<%s> in)", resulttype, methodname, params) {
      if (op.out.isDefined) {
        code_request_try_return_failure(qname, methodname, "in") {
          "%s(in)".format(bodymethodname)
        }
      } else {
        code_request_try_ok_failure(qname, methodname, "in") {
          jm_pln("%s(in)".format(bodymethodname))
        }
      }
    }
    jm_public_method("Response<%s> %s(Request<%s> in)", resulttype, bodymethodname, params) {
      if (op.out.isDefined) {
        jm_return("%s(in)".format(implmethodname))
      } else {
        jm_pln("%s(in)".format(implmethodname))
      }
    }
    jm_protected_method("%s %s(%s) throws Exception", resulttype, implmethodname, params) {
      jm_UnsupportedOperationException
    }
  }
}
