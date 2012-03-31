package org.simplemodeling.SimpleModeler.entities.play

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.dsl.datatype.XInt
import org.simplemodeling.dsl.SDatatype
import org.simplemodeling.dsl.SDatatypeFunction
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/**
 * @since   Mar. 31, 2012
 * @version Mar. 31, 2012
 * @author  ASAMI, Tomoharu
 */
class PlayEntityJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends EntityJavaClassDefinition(pContext, aspects, pobject) {
  useDocument = true

  override protected def head_imports_Extension {
    jm_import("org.goldenport.play.feed.*")
  }

  override protected def to_methods_feed {
    jm_public_method("GADocumentFeed<%s> toFeed()", documentName) {
      jm_return("make_document().toFeed()");
    }
  }

  override protected def to_methods_entry {
    jm_public_method("GADocumentEntry<%s> toEntry()", documentName) {
      jm_return("make_document().toEntry()");
    }
  }
}
