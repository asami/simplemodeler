package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity.SMPackage

/*
 * @since   Nov. 20, 2012
 * @version Nov. 20, 2012
 * @author  ASAMI, Tomoharu
 */
class ModuleTraitScalaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  pobject: PObjectEntity,
  maker: ScalaMaker = null
) extends ScalaClassDefinition(pContext, aspects, pobject, maker) {
  scalaKind = TraitScalaKind
  useAttribute = false
}
