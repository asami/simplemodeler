package org.simplemodeling.SimpleModeler.entities

import com.asamioffice.goldenport.text.UJavaString

/*
 * @since   Jul.  6, 2011
 *  version Dec. 13, 2011
 *  version Nov. 21, 2012
 * @version Dec.  2, 2012
 * @author  ASAMI, Tomoharu
 */
class DocumentJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PDocumentEntity
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
    jm_public_method("String relaxngSchema()") {
      jm_return(relanx_schema_literal(pobject))
    }
    jm_public_method("String sqlSelect()") {
      jm_return(sql_select_literal(pobject))
    }
  }

  protected def relanx_schema_literal(doc: PDocumentEntity) = {
    val a = relaxng.PRelaxngMaker(pContext, doc).schema
    UJavaString.stringLiteral(a.toString)
  }

  protected def relanx_element_literal(doc: PDocumentEntity) = {
    val a = relaxng.PRelaxngMaker(pContext, doc).element
    UJavaString.stringLiteral(a.toString)
  }
}
