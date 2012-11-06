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
) extends InterfaceJavaClassDefinition(pContext, aspects, pobject) {
}
