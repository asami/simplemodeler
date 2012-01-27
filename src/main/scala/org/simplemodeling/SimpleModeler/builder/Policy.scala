package org.simplemodeling.SimpleModeler.builder

import org.goldenport.entity.GEntityContext

/*
 * @since   Dec.  9, 2011
 * @version Dec. 11, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class Policy(protected val entity_context: GEntityContext, protected val package_name: String) {
  val strategy: Strategy
}

abstract class PolicyClass {
  def apply(context: GEntityContext, pkgname: String): Policy
}

object Policy {
  def apply(context: GEntityContext, pkgname: String)(name: String): Policy = {
    name match {
      case null => DefaultPolicy(context, pkgname)
      case "" => DefaultPolicy(context, pkgname)
      case "default" => DefaultPolicy(context, pkgname)
      case "none" => NonePolicy(context, pkgname)
      case "simplemodeling" => SimpleModelingPolicy(context, pkgname)
      // XXX otherwise
    }
  }

  def create(context: GEntityContext, pkgname: String)(name: Option[Any]): Policy = {
    name match {
      case Some(x) => apply(context, pkgname)(x.toString) 
      case None => DefaultPolicy(context, pkgname)
    }
  }
}

class DefaultPolicy(context: GEntityContext, pkgname: String) extends Policy(context, pkgname) {
  val strategy = Strategy(DefaultNamingStrategy, DefaultSlotStrategy(context, pkgname))
}

object DefaultPolicy extends PolicyClass {
  def apply(context: GEntityContext, pkgname: String) = new DefaultPolicy(context, pkgname)
}

class NonePolicy(context: GEntityContext, pkgname: String) extends Policy(context, pkgname) {
  val strategy = Strategy(NoneNamingStrategy, NoneSlotStrategy(context, pkgname))
}

object NonePolicy extends PolicyClass {
  def apply(context: GEntityContext, pkgname: String) = new NonePolicy(context, pkgname)
}

class SimpleModelingPolicy(context: GEntityContext, pkgname: String) extends Policy(context, pkgname) {
  val strategy = Strategy(SimpleModelingNamingStrategy, SimpleSlotStrategy(context, pkgname))
}

object SimpleModelingPolicy extends PolicyClass {
  def apply(context: GEntityContext, pkgname: String) = new SimpleModelingPolicy(context, pkgname)
}
