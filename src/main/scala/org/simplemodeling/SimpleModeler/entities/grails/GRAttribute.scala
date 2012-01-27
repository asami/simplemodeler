package org.simplemodeling.SimpleModeler.entities.grails

/*
 * Jan. 27, 2009
 * Jan. 27, 2009
 */
class GRAttribute(val name: String, val attributeType: GRPogoType) {
  var multiplicity: GRMultiplicity = GROne

  final def isHasMany = {
    multiplicity != GROne
  }

  final def isOptional = {
    multiplicity == GRZeroOne
  }

  final def isSingle = {
    multiplicity == GROne
  }

  final def isEntity = {
    attributeType.isEntity
  }
}

abstract class GRMultiplicity
class GROne extends GRMultiplicity
object GROne extends GROne
class GRZeroOne extends GRMultiplicity
object GRZeroOne extends GRZeroOne
class GROneMore extends GRMultiplicity
object GROneMore extends GROneMore
class GRZeroMore extends GRMultiplicity
object GRZeroMore extends GRZeroMore
class GRRange extends GRMultiplicity
object GRRange extends GRRange
