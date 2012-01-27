package org.simplemodeling.SimpleModeler.entities

import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity.SMPackage

/*
 * Created at Tomamu
 * 
 * @since   Sep.  1, 2011
 * @version Sep.  1, 2011
 * @author  ASAMI, Tomoharu
 */
class JavaInterfaceDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity,
  maker: JavaMaker = null
) extends JavaClassDefinition(pContext, aspects, pobject) {
  isInterface = true
  useDocument = false
}
