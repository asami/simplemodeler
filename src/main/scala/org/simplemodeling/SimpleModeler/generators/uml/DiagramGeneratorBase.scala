package org.simplemodeling.SimpleModeler.generators.uml

import java.io.{InputStream, OutputStream, IOException}
import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.entity.content._
import org.goldenport.entity.GEntityContext
import org.goldenport.entities.graphviz._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.flow._
import org.goldenport.recorder.Recordable
import org.goldenport.util.MimeType

/*
 * @since   Mar. 21, 2011
 *  version Mar. 26, 2011
 *  version Sep. 18, 2012
 * @version Oct.  5, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class DiagramGeneratorBase(val simpleModel: SimpleModelEntity) extends Recordable {
  val context = simpleModel.entityContext

  setup_FowardingRecorder(context)

  protected final def make_diagram_png(text: StringContent, name: Option[String] = None): BinaryContent = {
    val layout = "dot"
    var in: InputStream = null
    var out: OutputStream = null
    try {
      val dot: Process = context.executeCommand("dot -Tpng -K%s -q".format(layout))
      in = dot.getInputStream()
      out = dot.getOutputStream()
//    record_trace("start process = " + dot)
      record_trace("dot = " + text.string)
      text.write(out)
      out.flush
      out.close
//      val bytes = stream2Bytes(in) 2009-03-18
//      record_trace("bytes = " + bytes.length)
//      new BinaryContent(bytes, context)
      name match {
        case Some(s) => BinaryContent.createInputStream(in, context, s, MimeType.image_png)
        case None => BinaryContent.createInputStream(in, context, null, MimeType.image_png)
      }
    } catch {
      case e: IOException => {
        // Cannot run program "dot": error=2, No such file or directory
        throw new IOException("graphvizのdotコマンドが動作しませんでした。\ngraphvizについてはhttp://www.graphviz.org/を参照してください。\n[詳細エラー: %s]".format(e.getMessage))
      }
    } finally {
      if (in != null) in.close
//      err.close
//      record_trace("finish process = " + dot)
    }
  }
}
