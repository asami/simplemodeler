package org.simplemodeling.SimpleModeler.entities

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, TextBuilder, UString, Templater}

// derived from GaejServiceEntity since Apr. 23, 2009.
/*
 * @since   Apr. 23, 2011
 *  version Aug. 20, 2011
 * @version Dec.  6, 2012
 * @author  ASAMI, Tomoharu
 */
trait PServiceEntity extends PObjectEntity {
  val plainConfig = pContext.plainServiceConfig

  override protected def write_Content(out: BufferedWriter) {
    out.append(new ServiceEntityServiceCode(pContext).code())
    out.flush()
  }

  class ServiceEntityServiceCode(context: PEntityContext) extends PServletCode(context) {
    val template = """package %packageName%;

import java.util.*;
import javax.jdo.*;

public class %serviceName% {
    %contextName% context;

    void setContext(%contextName% context) {
        this.context = context;
    }

%operations%
}
"""
    coder("packageName", packageName)
    coder("serviceName", name)
    coder("contextName", pContext.contextName(packageName))
    coder("operations", make_operations)

    private def make_operations = new ServiceServletCoder {
      def code() {
        def make_operation(anOperation: POperation) {
          def make_in_param(in: Option[PDocumentType]) = {
            in match {
              case Some(docType) => docType.name + " in"
              case None => ""
            }
          }

          def make_out(out: Option[PDocumentType]) = {
            out match {
              case Some(docType) => docType.name
              case None => "void"
            }
          }

          val opName = anOperation.name
          val doMethodName = "do" + opName.capitalize
          cMethod("public " + make_out(anOperation.out) + " " + opName + "(" + make_in_param(anOperation.in) + ") throws Exception") {
            anOperation.out match {
              case Some(doc) => cAppendln("return " + doMethodName + "(in);")
              case None => cAppendln(doMethodName + "(in);")
            }
          }
          cMethod("protected " + make_out(anOperation.out) + " " + doMethodName + "(" + make_in_param(anOperation.in) + ") throws Exception") {
            cAppendln("throw new UnsupportedOperationException(\"" + opName + "\");")
          }
        }
        
        operations.foreach(make_operation)
      }
    }

    abstract class ServiceServletCoder extends Coder {
    }
  }
}

trait PRepositoryServiceEntity extends PServiceEntity
trait PEventServiceEntity extends PServiceEntity
