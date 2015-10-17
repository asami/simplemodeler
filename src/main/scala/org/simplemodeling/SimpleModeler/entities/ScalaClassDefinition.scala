package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity.SMPackage

/*
 * @since   Aug. 19, 2011
 *  version Aug. 19, 2011
 *  version Feb. 11, 2012
 *  version Nov. 22, 2012
 *  version Jan. 11, 2013
 *  version Feb. 23, 2013
 *  version Aug. 25, 2014
 * @version Oct. 17, 2015
 * @author  ASAMI, Tomoharu
 */
class ScalaClassDefinition(
  aContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  anObject: PObjectEntity,
  maker: ScalaMaker = null
) extends GenericClassDefinition(aContext, aspects, anObject) with ScalaMakerHolder {
  type ATTR_DEF = ScalaClassAttributeDefinition

  var scalaKind: ScalaClassifierKind = ClassScalaKind
  var useSimpleCompanionObject: Boolean = false

  if (maker == null) {
    sm_open(aspects)
  } else {
    sm_open(maker, aspects)
  }

  override def toText = {
    sm_to_text
  }

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new ScalaClassAttributeDefinition(pContext, aspects, attr, this, sm_maker)
  }

  override protected def pln() {
    sm_pln()
  }

  override protected def head_package {
    require(packageName != null)
    if (packageName != "") {
      sm_package(packageName)
    }
    sm_end_package_section()
  }

  override protected def head_imports_Prologue {
//    sm_import("scala.util.*")
//    sm_import("scala.math.*")
    if (isSingleton) {
      sm_import("com.google.inject.Singleton")
    }
  }

  override protected def head_imports_Object(anObject: PObjectReferenceType) {
    if (packageName != anObject.packageName) {
      sm_import(anObject.qualifiedName)
    }
  }

  override protected def head_imports_Entity(anEntity: PEntityType) {
    if (packageName != anEntity.packageName) {
      sm_import(anEntity.qualifiedName)
    }
  }

  override protected def head_imports_Epilogue() {
    sm_end_import_section()
  }

  protected def base_name: Option[String] = {
    customBaseName orElse baseObject.map(_.name)
  }

  protected def trait_names: Seq[String] = {
    mixinTraits.map(_.name) ++ customImplementNames ++ custom_Trait_Names
  }

  protected def custom_Trait_Names: Seq[String] = Nil

  override protected def class_open_body {
    if (isSingleton) {
      sm_pln("@Singleton")
    }
    sm_p(scalaKind.keyword)
    sm_p(" ")
    sm_p(name)
    if (scalaKind.isClass) {
      class_open_body_constructor
    }
    base_name match {
      case Some(bn) => {
        sm_p(" extends ")
        sm_p(bn)
        if (scalaKind.isClass) {
          class_open_body_parent_constructor
        }
        if (trait_names.nonEmpty) {
          sm_p(" with ")
          sm_p(trait_names.mkString(" with "))
        }
      }
      case None => {
        if (trait_names.nonEmpty) {
          sm_p(" extends ")
          sm_p(trait_names.mkString(" with "))
        }
      }
    }
    sm_pln(" {")
    sm_indent_up
  }

  protected def class_open_body_constructor {
    val parents = parentAttributeDefinitions.filter(!_.isInject).
      map(a => "%s: %s".format("_" + a.paramName, a.javaType))
    val owns = attributeDefinitions.filter(!_.isInject).
      map(a => "%s %s: %s".format(class_open_body_constructor_var_keyword,
                                  a.paramName, a.javaType))
    sm_param_list(parents ++ owns)
  }

  protected def class_open_body_constructor_var_keyword = {
    if (isImmutable) "val" else "var"
  }

  protected def class_open_body_parent_constructor {
    val parents = parentAttributeDefinitions.filter(!_.isInject).
      map("_" + _.paramName)
    sm_param_list(parents)
  }

  override protected def class_close_body {
    sm_indent_down
    sm_pln("}")
  }

  override protected def companions {
    if (useSimpleCompanionObject) {
      sm_pln("object %s extends %s", name, name)
    }
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
  }
}

sealed trait ScalaClassifierKind {
  def keyword: String
  def isClass: Boolean
}
object ClassScalaKind extends ScalaClassifierKind {
  def keyword = "class"
  def isClass = true
}
object ObjectScalaKind extends ScalaClassifierKind {
  def keyword = "object"
  def isClass = false
}
object TraitScalaKind extends ScalaClassifierKind {
  def keyword = "trait"
  def isClass = false
}
object SealedTraitScalaKind extends ScalaClassifierKind {
  def keyword = "sealed trait"
  def isClass = false
}
object AbstractClassScalaKind extends ScalaClassifierKind {
  def keyword = "abstract class"
  def isClass = true
}
