package org.simplemodeling.dsl

/*
 * @since   Mar. 21, 2011
 * @version Mar. 21, 2011
 * @author  ASAMI, Tomoharu
 */
class SPort(aName: String, val entity: SEntity, val inout: SPortInputOutput = SPortInOut) extends SElement(aName) {
}

object NullPort extends SPort(null, null)

abstract class SPortInputOutput
object SPortIn extends SPortInputOutput
object SPortOut extends SPortInputOutput
object SPortInOut extends SPortInputOutput
