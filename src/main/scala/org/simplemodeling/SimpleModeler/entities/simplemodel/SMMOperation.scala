package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * @since   Nov. 12, 2012
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * The SMMOperation is converted to a SOperation
 * in a SMMEntityEntity#_build_operations method.
 */
class SMMOperation(val name: String, val inType: SMMAttributeTypeSet, val outType: SMMAttributeTypeSet) extends SMMSlot {
}
