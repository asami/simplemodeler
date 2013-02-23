package org.simplemodeling.SimpleModeler.entities.squeryl

import scalaz._, Scalaz._
import java.io.BufferedWriter
import com.asamioffice.goldenport.text.UString
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Feb. 24, 2013
 * @version Feb. 24, 2013
 * @author  ASAMI, Tomoharu
 */
class SquerylLibraryEntity(aContext: SquerylEntityContext) extends SquerylObjectEntity(aContext) with PEntityEntity {
  override protected def class_Definition: ClassDefinition_TYPE = {
    new SquerylLibraryScalaClassDefinition(aContext, Nil, SquerylLibraryEntity.this)
  }
}

class SquerylLibraryScalaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  pobject: PObjectEntity
) extends SquerylScalaClassDefinitionBase(pContext, aspects, pobject) {
  scalaKind = ObjectScalaKind
  customImplementNames = List("Schema")

  override protected def object_auxiliary {
    _entity_resource_names.foreach(sm_pln(_))
  }

  private def _entity_resource_names: Seq[String] = {
    for (e <- domain_entities) yield {
      val varname = UString.uncapitalize(e.name)
      val tablename = pContext.sqlTableName(e)
      "val %s = table[%s](\"%s\")".format(varname, e.name, tablename)
    }
  }
}
