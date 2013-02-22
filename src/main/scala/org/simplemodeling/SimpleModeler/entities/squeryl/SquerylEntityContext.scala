package org.simplemodeling.SimpleModeler.entities.squeryl

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}

/*
 * @since   Feb. 22, 2013
 * @version Feb. 22, 2013
 * @author  ASAMI, Tomoharu
 */
class SquerylEntityContext(ectx: GEntityContext, sctx: GServiceContext) extends ScalaEntityContextBase(ectx, sctx) {
}
