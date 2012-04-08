package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}

/*
 * @since   Apr.  8, 2012
 * @version Apr.  8, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class JavaScriptEntityContextBase(ectx: GEntityContext, sctx: GServiceContext) extends PEntityContext(ectx, sctx) {
}
