package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity.SMPowertype

/*
 * @since   Feb. 20, 2012
 *  version Feb. 20, 2012
 * @version Nov.  7, 2012
 * @author  ASAMI, Tomoharu
 */
class PowertypeJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false
  isImmutable = true
  override def useBuilder = false
  isData = true
  isValueEquality = true
  classifierKind = EnumClassifierKind

  override protected def constructors_null_constructor {}
  override protected def constructors_copy_constructor {}
  override protected def constructors_plain_constructor {}

  override protected def attribute_variables_Prologue {
    val mpower = pobject.modelObject.asInstanceOf[SMPowertype]
    for (last <- mpower.kinds.lastOption;
         k <- mpower.kinds) {
      val key = k.name
      val label = k.term
      jm_p("""%s""".format(key))
      if (k eq last) {
        jm_pln(";")
      } else {
        jm_p(", ")
      }
    }
  }
/*
  protected def attribute_variables_Prologue0 {
    val mpower = pobject.modelObject.asInstanceOf[SMPowertype]
    val last = mpower.kinds.last
    for (k <- mpower.kinds) {
      val key = k.name
      val label = k.term
      jm_p("""%s("%s", "%s")""".format(key, key, label))
      if (k eq last) {
        jm_pln(";")
      } else {
        jm_pln(",")
      }
    }
  }
*/

  override protected def attribute(attr: PAttribute) = {
    new ValueJavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker).withImmutable(true)
  }

//  override protected def to_methods_string {}
  override protected def object_methods_hashcode {}
  override protected def object_methods_equals {}
}
