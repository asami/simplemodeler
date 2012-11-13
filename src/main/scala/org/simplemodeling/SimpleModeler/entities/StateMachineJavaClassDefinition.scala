package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity.SMStateMachine

/*
 * @since   Nov. 14, 2012
 * @version Nov. 14, 2012
 * @author  ASAMI, Tomoharu
 */
class StateMachineJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  // XXX: use modelStateMachine.isEditable to switch entity powertype or enum powertype.
  if (pobject.hasInheritance) {
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
    if (pobject.hasInheritance) return
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
