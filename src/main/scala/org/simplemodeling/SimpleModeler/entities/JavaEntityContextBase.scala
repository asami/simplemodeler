package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}

/*
 * @since   Apr. 23, 2011
 * @version Apr. 23, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class JavaEntityContextBase(ectx: GEntityContext, sctx: GServiceContext) extends PEntityContext(ectx, sctx) {
}
