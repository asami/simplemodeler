package org.simplemodeling.SimpleModeler.entities

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.PUtil._

// derived from GaejConstraint since Apr. 10, 2009
/*
 * @since   Apr. 23, 2011
 * @version Nov.  2, 2012
 * @author  ASAMI, Tomoharu
 */
trait PDocumentEntity extends PObjectEntity {
  /**
   * Currently no one uses the variable.
   */
  var modelDocumentOption: Option[SMDocument] = None
  /**
   * Referenced by GenericClassAttributeDefinition#is_composition_property,
   * 
   * Setted by GenericClassDefinition constructor.
   */
  var modelEntityOption: Option[SMEntity] = None

  protected final def id_var_name(attr: PAttribute) = {
    pContext.variableName4RefId(attr)
  }

  protected final def id_attr_type_name(attr: PAttribute) = {
    pContext.attributeTypeName4RefId(attr)
  }

  override protected def write_Content(out: BufferedWriter) {
  }
}
