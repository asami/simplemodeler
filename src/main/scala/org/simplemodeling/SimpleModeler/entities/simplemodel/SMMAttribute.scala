package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * Jan. 30, 2009
 * Feb. 20, 2009
 */
class SMMAttribute(val name: String, val attributeType: SMMObjectType) {
  var multiplicity: GRMultiplicity = GROne

  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMAttribute = {
    multiplicity = aMultiplicity
    this
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
