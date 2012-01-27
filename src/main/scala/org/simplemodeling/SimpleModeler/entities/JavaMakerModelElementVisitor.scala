package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity.SimpleModelVisitor

/*
 * @since   Jul. 15, 2011
 * @version Jul. 15, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class JavaMakerModelElementVisitor(maker: JavaMaker) extends SimpleModelVisitor with JavaMakerHolder {
  jm_open(maker)
}