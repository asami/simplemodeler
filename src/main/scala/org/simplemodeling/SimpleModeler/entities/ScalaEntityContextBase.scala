package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}

/*
 * @since   Aug. 20, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class ScalaEntityContextBase(ectx: GEntityContext, sctx: GServiceContext) extends PEntityContext(ectx, sctx) {
}
