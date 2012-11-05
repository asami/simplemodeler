package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Nov.  6, 2012
 * @version Nov.  6, 2012
 * @author  ASAMI, Tomoharu
 */
class TraitJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  isData = false
  useDocument = false

  override protected def attribute(attr: PAttribute) = {
    new JavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker)
  }

  override protected def to_methods_feed {
  }

  override protected def to_methods_entry {
  }
}
