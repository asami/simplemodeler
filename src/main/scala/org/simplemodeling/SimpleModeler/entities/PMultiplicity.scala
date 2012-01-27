package org.simplemodeling.SimpleModeler.entities

/**
 * @since   Apr. 23, 2011
 * @version Apr. 23, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class PMultiplicity

class POne extends PMultiplicity
object POne extends POne
class PZeroOne extends PMultiplicity
object PZeroOne extends PZeroOne
class POneMore extends PMultiplicity
object POneMore extends POneMore
class PZeroMore extends PMultiplicity
object PZeroMore extends PZeroMore
class PRange extends PMultiplicity
