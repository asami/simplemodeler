package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity.SMStateMachine

/*
 * @since   Nov. 14, 2012
 * @version Nov. 18, 2012
 * @author  ASAMI, Tomoharu
 */
class StateMachineJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PStateMachineEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  val isKnowledge = false // XXX
  if (isKnowledge) {
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
    if (isKnowledge) return
    val sm = pobject.modelObject.asInstanceOf[SMStateMachine]
    val states = sm.states
    val labels =
      if (states.isEmpty) List("Unkonwn")
      else states.map(_.name)
    jm_pln(labels.mkString("", ", ", ";"))
  }

  override protected def object_methods_hashcode {}
  override protected def object_methods_equals {}
}
