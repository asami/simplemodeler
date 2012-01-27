package org.simplemodeling.SimpleModeler.entities.atom

import scala.collection.mutable.ArrayBuffer

/*
 * @since   May.  5, 2009
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class AppService() extends AppCommonAttributes {
  val workspaces = new ArrayBuffer[AppWorkspace]
  val extensionElements = new ArrayBuffer[AppExtensionElement]
}

case class AppWorkspace(val title: AtomTitle) extends AppCommonAttributes {
  val collections = new ArrayBuffer[AppCollection]
  val extensionSansTitleElements = new ArrayBuffer[AppExtensionSansTitleElement]
}

case class AppCollection(val title: AtomTitle, val href: String) extends AppCommonAttributes {
  val accepts = new ArrayBuffer[AppAccept]
  val categories = new ArrayBuffer[AppCategories]
  val extensionSansTitleElements = new ArrayBuffer[AppExtensionSansTitleElement]
}

case class AppAccept() extends AppCommonAttributes {
}

case class AppExtensionElement() {
}

case class AppExtensionSansTitleElement() {
}
