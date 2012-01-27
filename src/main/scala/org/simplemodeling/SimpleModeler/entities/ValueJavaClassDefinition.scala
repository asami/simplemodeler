package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Jul.  6, 2011
 * @version Jul. 22, 2011
 * @author  ASAMI, Tomoharu
 */
class ValueJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false
  isImmutable = true
  override def useBuilder = false
  isData = true
  isValueEquality = true

  override protected def constructors_null_constructor {
  }

  override protected def attribute(attr: PAttribute) = {
    new ValueJavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker).withImmutable(true)
  }
}
