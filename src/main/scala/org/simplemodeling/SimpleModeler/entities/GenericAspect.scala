package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Aug. 19, 2011
 *  version Aug. 19, 2011
 *  version Dec. 15, 2011
 * @version Feb.  7, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class GenericAspect {
/*
  def open(m: ScalaMaker) {
    jm_open(m)
  }
*/
  def weaveImports() {}
  def weaveOpenAnnotation() {}
  def weaveIdAttributeSlot(attr: PAttribute, varName: String): Boolean = false
  def weavePersistentAnnotation(attr: PAttribute) {}
  def weaveNotPersistentAnnotation(attr: PAttribute) {}
  def weaveCopyConstructorAttributeBlock(attr: PAttribute, varName: String, paramName: String): Boolean = false
  def weavePlainConstructorAttributeBlock(attr: PAttribute, varName: String, paramName: String): Boolean = false
  def weaveIdMethods(attr: PAttribute, attrName: String, varName: String, paramName: String, scalaType: String): Boolean = false
  def objectVarName(attr: PAttribute, varName: String): Option[String] = None
}
