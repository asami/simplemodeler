package org.simplemodeling.SimpleModeler.entities.simplemodel

import org.simplemodeling.dsl.util.PropertyRecord

/*
 * @since   Dec. 13, 2012
 * @version Dec. 13, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * The SMMDisplay is converted to a SDisplay
 * in a SMMEntityEntity#display method.
 */
class SMMDisplay(val name: String, val seq: Int, val entry: Seq[PropertyRecord]) {
}
