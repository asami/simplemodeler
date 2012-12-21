package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.goldenport.recorder.GRecordable
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec. 21, 2012
 * @version Dec. 21, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class ExpressionBuilder(val context: PEntityContext) extends GRecordable {
  setup_Recordable(context)
}
