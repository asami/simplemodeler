package org.simplemodeling.SimpleModeler.converters

import scala.collection.mutable.ArrayBuffer
import org.goldenport.Goldenport
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.value.GTreeNode
import org.goldenport.entity.datasource.{NullDataSource, ResourceDataSource}
import org.goldenport.entity.content._
import org.goldenport.entities.xmind.{XMindEntity, XMindNode}
import org.goldenport.entities.orgmode._
import com.asamioffice.goldenport.text.{UString, CsvUtility}
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.values.smcsv.SimpleModelCsvTabular
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Feb. 23, 2012
 * @version Feb. 24, 2012
 * @author  ASAMI, Tomoharu
 */
class OrgXMindConverter(val policy: Policy, val packageName: String, val org: OrgmodeEntity, val projectName: String) {
  private val _entity_context = org.entityContext
  private val _simplemodel = new SimpleModelMakerEntity(_entity_context, policy)
  private val _xmind = new XMindEntity(_entity_context)

  def toXMind: XMindEntity = {
    org using {
      _build_xmind
      _xmind
    }
  }

  private def _build_xmind = {
    _xmind.open()
    val thema = _xmind.firstThema
    thema.title = projectName
    val simplemodelbuilder = new OrgSimpleModelMakerBuilder(_simplemodel, policy, packageName, org)
    simplemodelbuilder.build()
    val outlinebuilder = new SimpleModelOutlineBuilder[XMindNode](_simplemodel, policy, packageName, thema)
    outlinebuilder.build()
  }
}
