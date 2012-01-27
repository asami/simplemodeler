package org.simplemodeling.SimpleModeler.entities

/*
 * @since   May. 14, 2011
 * @version Aug. 19, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class JavaAspect extends GenericAspect with JavaMakerHolder {
  def open(m: JavaMaker) {
    jm_open(m)
  }
/*
  def weaveImports() {}
  def weaveIdAttributeSlot(attr: PAttribute, varName: String): Boolean = false
  def weavePersistentAnnotation(attr: PAttribute) {}
  def weaveNotPersistentAnnotation(attr: PAttribute) {}
  def weaveIdMethods(attr: PAttribute, attrName: String, varName: String, paramName: String, javaType: String): Boolean = false
*/
}
