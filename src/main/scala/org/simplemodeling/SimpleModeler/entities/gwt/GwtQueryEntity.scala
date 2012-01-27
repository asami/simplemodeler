package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker}

/*
 * @since   Jun.  5, 2009
 * @version Jun.  5, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtQueryEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

import java.util.*;

public final class GwtQuery implements java.io.Serializable {
  public GwtQuery() {}
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val maker = new JavaTextMaker(
      code,
      Map("%packageName%" -> packageName))
    out.append(maker.toString)
    out.flush()
  }
}
