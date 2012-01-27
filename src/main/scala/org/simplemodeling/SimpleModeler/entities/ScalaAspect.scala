package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Aug. 19, 2011
 * @version Aug. 19, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class ScalaAspect extends GenericAspect with ScalaMakerHolder {
  def open(m: ScalaMaker) {
    sm_open(m)
  }
}
