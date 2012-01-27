package org.simplemodeling.SimpleModeler.generators.uml

import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.entity.content._
import org.goldenport.entity.GEntityContext
import org.goldenport.entities.graphviz._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.flow._
import org.goldenport.recorder.Recordable

/*
 * @since   Mar. 21, 2011
 *  version Mar. 26, 2011
 * @version Jan. 24, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class DiagramGeneratorBase(val simpleModel: SimpleModelEntity) extends Recordable {
  val context = simpleModel.entityContext

  setup_FowardingRecorder(context)

  protected final def make_diagram_png(text: StringContent): BinaryContent = {
    val dot: Process = context.executeCommand("dot -Tpng -Kdot -q")
    val in = dot.getInputStream()
    val out = dot.getOutputStream()
//    record_trace("start process = " + dot)
    try {
      record_trace("dot = " + text.string)
      text.write(out)
      out.flush
      out.close
//      val bytes = stream2Bytes(in) 2009-03-18
//      record_trace("bytes = " + bytes.length)
//      new BinaryContent(bytes, context)
      new BinaryContent(in, context)
    } finally {
      in.close
//      err.close
//      record_trace("finish process = " + dot)
    }
  }
}
