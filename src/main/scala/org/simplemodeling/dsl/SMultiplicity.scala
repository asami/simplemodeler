package org.simplemodeling.dsl

/*
 * @since   Sep. 11, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
abstract class SMultiplicity {
}

case class One() extends SMultiplicity {
}

case class ZeroOne() extends SMultiplicity {
}

case class OneMore() extends SMultiplicity {
}

case class ZeroMore() extends SMultiplicity {
}

object One extends One {
}

object ZeroOne extends ZeroOne {
}

object OneMore extends OneMore {
}

object ZeroMore extends ZeroMore {
}
