package org.simplemodeling.SimpleModeler.entities

/*
 * Value Java Class Attribute Definition
 * 
 * @since   Jul.  6, 2011
 * @version Jul. 22, 2011
 * @author  ASAMI, Tomoharu
 */
class ValueJavaClassAttributeDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  attr: PAttribute,
  owner: JavaClassDefinition,
  jmaker: JavaMaker) extends JavaClassAttributeDefinition(pContext, aspects, attr, owner, jmaker) {
  isImmutable = true

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    jm_public_final_instance_variable(attr, typename, varname);
  }
}
