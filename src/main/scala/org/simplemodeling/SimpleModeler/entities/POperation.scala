package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity.SMOperation

/*
 * derived from GaejConstraint since Apr. 23, 2009
 * 
 * @since   Apr. 23, 2009
 *  version Apr. 23, 2011
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
class POperation(val name: String, val in: Option[PDocumentType], val out: Option[PDocumentType]) {
  var model: SMOperation = null
}
