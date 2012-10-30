package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Jul.  9, 2011
 *  version Aug.  7, 2011
 *  version Dec. 13, 2011
 *  version Apr.  1, 2012
 * @version Oct. 30, 2012
 * @author  ASAMI, Tomoharu
 */
class EntityJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  isData = true
  useDocument = true
  useDerivedAttribute = true

  override protected def attribute(attr: PAttribute) = {
    new EntityJavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker)
  }

  override protected def to_methods_feed {
  }

  override protected def to_methods_entry {
  }
}
