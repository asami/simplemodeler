package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * @since   Oct. 16, 2012
 * @version Nov.  9, 2012
 * @author  ASAMI, Tomoharu
 */
class SMDomainTrait(val dslDomainTrait: DomainTrait) extends SMTrait(dslDomainTrait) {
  var isDocument = false // XXX DocumentTrait?
}

object SMDomainTrait {
  /**
   * Used by SimpleModel2ProgramRealmTransformerBase#make_trait_document
   */
  def create(name: String, pkgname: String, source: SMObject) = {
//    println("SMDomainTrait#create(%s): = %s".format(name, source.attributes))
    val tr = new SMDomainTrait(new DomainTrait(name, pkgname))
    tr.importOwnDefinition(source)
//    println("SMDomainTrait#create2(%s): = %s".format(name, tr.attributes))
    tr
  }

  def createDocument(name: String, pkgname: String, source: SMObject) = {
    val d = create(name, pkgname, source)
    d.isDocument = true
    d
  }
}
