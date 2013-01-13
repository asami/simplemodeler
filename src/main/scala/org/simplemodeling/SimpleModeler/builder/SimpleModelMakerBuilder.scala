package org.simplemodeling.SimpleModeler.builder

import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.Goldenport
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.csv.CsvEntity
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import com.asamioffice.goldenport.text.{UString, UJavaString}
import org.goldenport.recorder.Recordable
import org.simplemodeling.dsl.SObject

/*
 * @since   Feb.  2, 2009
 *  version Mar.  6, 2009
 *  version Dec. 12, 2011
 *  version Feb.  8, 2012
 *  version Sep. 27, 2012
 * @version Jan. 13, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * OutlineBuilderBase uses this class to build a SimpleModel.
 */
class SimpleModelMakerBuilder(
    private val simplemodel: SimpleModelMakerEntity,
    packageName: String,
    policy: Policy,
    strategy: Option[Strategy] = None
) extends SimpleModelDslBuilder(
    simplemodel.entityContext, packageName, policy, strategy
) with Recordable {
  val packagePathname = UJavaString.packageName2pathname(packageName)

  override protected def create_Object(name: String, entity: SMMEntityEntity) {
    val pathname = packagePathname + "/" + entity.name + ".scala"
    simplemodel.setEntity(pathname, entity)
  }
}
/*
class SimpleModelMakerBuilder0(
    private val simplemodel: SimpleModelMakerEntity,
    private val packageName: String,
    private val _policy: Policy,
    private val _strategy: Option[Strategy] = None) extends SimpleModelBuilder with Recordable {
  val entityContext = simplemodel.entityContext
  val packagePathname = UJavaString.packageName2pathname(packageName)
  val entities = new HashMap[String, SMMEntityEntity]
  private def _naming_strategy = (_strategy getOrElse _policy.strategy).naming
  private def _slot_strategy = (_strategy getOrElse _policy.strategy).slot

  final def createObject(aKind: ElementKind, aName: String): SMMEntityEntity = {
    var name = _naming_strategy.makeName(aName, aKind)
    entities.get(name) match {
      case Some(entity) => entity
      case None => {
        val entity = new SMMEntityEntity(simplemodel.entityContext)
        entity.kind = aKind
        entity.name = name
        entity.term = aName
        entity.packageName = packageName
        add_object(entity)
        entities += (name -> entity)
        entity
      }
    }
  }

  final def createObject(aKind: String, aName: String): SMMEntityEntity = {
    sys.error("not implemented yet")
  }

  final def makeAttributesForBaseObject(anObj: SMMEntityEntity) {
    _slot_strategy.makeAttributesForBaseObject(anObj)
  }

  final def makeAttributesForDerivedObject(anObj: SMMEntityEntity) {
    _slot_strategy.makeAttributesForDerivedObject(anObj)
  }

  private def add_object(anObject: SMMEntityEntity) {
    val pathname = packagePathname + "/" + anObject.name + ".scala"
    simplemodel.setEntity(pathname, anObject)
  }

  def makeNarrativeAttributes(obj: SMMEntityEntity) {
    sys.error("not implemented yet.")
  }

  def dslObjects: List[SObject] = {
    sys.error("not implemented yet.")
  }
}
*/
