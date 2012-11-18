package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity.SMPowertype

/*
 * @since   Feb. 20, 2012
 *  version Feb. 20, 2012
 * @version Nov. 18, 2012
 * @author  ASAMI, Tomoharu
 */
class PowertypeJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PPowertypeEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  // XXX: use modelPowertype.isEditable to switch entity powertype or enum powertype.
  if (pobject.isKnowledge) {
    isData = true
    isImmutable = false
  } else {
    useDocument = false
    isImmutable = true
    isData = true
    isValueEquality = true
    classifierKind = EnumClassifierKind
  }

  override def useBuilder = false

  override protected def constructors_null_constructor {}
  override protected def constructors_copy_constructor {}
  override protected def constructors_plain_constructor {}

  override protected def attribute_variables_Prologue {
    if (pobject.isKnowledge) return
    val mpower = pobject.modelObject.asInstanceOf[SMPowertype]
    val kinds = mpower.kinds
    val labels =
      if (kinds.isEmpty) List("Unkonwn")
      else kinds.map(_.name)
    jm_pln(labels.mkString("", ", ", ";"))
  }

  override protected def object_methods_hashcode {}
  override protected def object_methods_equals {}
}
