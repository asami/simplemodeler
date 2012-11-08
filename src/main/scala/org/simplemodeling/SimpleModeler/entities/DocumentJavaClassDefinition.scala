package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Jul.  6, 2011
 *  version Dec. 13, 2011
 * @version Nov.  9, 2012
 * @author  ASAMI, Tomoharu
 */
class DocumentJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) with SqlHelper {
  useDocument = false
  isImmutable = true
  isData = true
  isValueEquality = true
  isCustomVariableImplementation = true
  override def isDocument = true

  override protected def constructors_null_constructor {
  }

  override protected def constructors_plain_constructor {
    constructors_plain_constructor_for_document
  }

  override protected def attribute(attr: PAttribute) = {
    new DocumentJavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker).withImmutable(true)
  }

  override protected def to_methods_feed {
  }

  override protected def to_methods_entry {
  }

  override protected def object_auxiliary {
    jm_public_method("String fetchSql()") {
      jm_return(sql_select(pobject))
    }
  }
}
