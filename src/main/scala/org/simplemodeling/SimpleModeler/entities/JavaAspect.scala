package org.simplemodeling.SimpleModeler.entities

/*
 * @since   May. 14, 2011
 *  version Aug. 19, 2011
 * @version Feb. 20, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class JavaAspect extends GenericAspect with JavaMakerHolder {
  var javaClass: Option[JavaClassDefinition] = None

  def open(m: JavaMaker) {
    jm_open(m)
  }

  def openJavaClass(jc: JavaClassDefinition) {
    javaClass = Some(jc)
  }
/*
  def weaveImports() {}
  def weaveIdAttributeSlot(attr: PAttribute, varName: String): Boolean = false
  def weavePersistentAnnotation(attr: PAttribute) {}
  def weaveNotPersistentAnnotation(attr: PAttribute) {}
  def weaveIdMethods(attr: PAttribute, attrName: String, varName: String, paramName: String, javaType: String): Boolean = false
*/
}
