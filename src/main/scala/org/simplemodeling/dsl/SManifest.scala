package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * @since   Oct. 26, 2008
 * @version Nov.  4, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class SManifest extends SElement {
  type Descriptable_TYPE = SManifest
  type Historiable_TYPE = SManifest
  private val _objects = new ArrayBuffer[SObject]
  private val _packages = new ArrayBuffer[String]

  var version: String = ""
  var xmlNamespace: String = ""

  final def objects(theObjects: SObject*) {
    _objects ++= theObjects
  }

  final def objects: Seq[SObject] = _objects

  final def usecases(theObjects: SObject*) {
    _objects ++= theObjects
  }

  final def packages(thePackages: String*) {
    _packages ++= thePackages
  }

  final def packageNames: Seq[String] = _packages
}
