package org.simplemodeling.SimpleModeler.values.smcsv

import org.goldenport.entity._
import org.goldenport.entity.datasource.{GDataSource, ResourceDataSource}
import org.goldenport.entity.content.GContent
import org.goldenport.entity.locator.EntityLocator
import org.goldenport.entities.workspace.TreeWorkspaceNode
import org.goldenport.value.GTabular
import org.goldenport.value.util.AnnotatedCsvTabular
import com.asamioffice.goldenport.text.UPathString

/*
 * @since   Feb. 10, 2009
 * Feb. 27, 2009
 *  version Dec. 14, 2011
 * @version Jan. 24, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModelCsvTabular(aCsv: GTabular[String]) extends AnnotatedCsvTabular(aCsv) {
  override protected def build_Prologue {
//    prologueData += Array("#memo", "summary", "base", "parts", "attrs", "powers", "roles", "states")
//    prologueData += Array("#resource", "attrs",  "parts", "powers", "roles", "base", "summary", "states")
    prologueData += Array("#", "attrs",  "parts", "powers", "roles", "base", "summary", "states")
  }
}
