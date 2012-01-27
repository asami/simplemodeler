package org.simplemodeling.dsl

/*
 * Nov. 17, 2008
 * Nov. 17, 2008
 */
class SStereotype {
}

class SAssociationStereotype extends SStereotype {
}

object SAssociationStereotype extends SAssociationStereotype {
  val Primary_Actor = new SAssociationStereotype
  val Secondary_Actor = new SAssociationStereotype
}
