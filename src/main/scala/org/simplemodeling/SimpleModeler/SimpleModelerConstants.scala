package org.simplemodeling.SimpleModeler

/**
 * @since   Jun. 17, 2012
 * @version Jun. 17, 2012
 * @auther  ASAMI, Tomoharu
 */
trait SimpleModelerConstants {
  val DEFAULT_PACKAGE_NAME = "app"
  val DEFAULT_MODEL_KIND = "model"
  val DEFAULT_VIEW_KIND = "view"
  val DEFAULT_CONTROLLER_KIND = "controller"
  val DEFAULT_DOC_KIND = "doc"
  val DEFAULT_STORE_KIND = "store"
}

object SimpleModelerConstants extends SimpleModelerConstants {
}
