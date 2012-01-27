package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc
import org.goldenport.util.Options

/*
 * @since   Sep. 21, 2008
 * @version Jul. 13, 2011
 * @author  ASAMI, Tomoharu
 */
class SPackage(aName: String) extends SElement(aName) {
  type Descriptable_TYPE = SPackage
  type Historiable_TYPE = SPackage

  var manifestOption: Option[SManifest] = None
  def manifest = manifestOption.get

  def version = manifest.version

  def getXmlNamespace: Option[String] = manifestOption.flatMap(m => Options.stringLift(m.xmlNamespace)) 
}
