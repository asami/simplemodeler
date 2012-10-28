package org.simplemodeling.SimpleModeler.entities

import scalaz._
import Scalaz._
import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull

/*
 * @since   Dec. 15, 2011
 *  version Feb. 20, 2012
 * @version Oct. 29, 2012
 * @author  ASAMI, Tomoharu
 */
class Jpa317Aspect extends JavaAspect with JavaHelper {
  var modelEntity: SMEntity = null
  var is_logical_operation = false

  override def weaveImports() {
//    jm_import("javax.persistence.Entity")
//    jm_import("javax.persistence.Id")
//    jm_import("javax.persistence.Basic")
//    jm_import("javax.persistence.FetchType")
//    jm_import("javax.persistence.Column")
    jm_import("javax.persistence.*")
    jm_import("java.io.Serializable")
  }

  override def weaveOpenAnnotation() {
    for (j <- javaClass) {
      j match {
        case _: PowertypeJavaClassDefinition => {}
        case _ => jm_pln("@Entity")
      }
    }
  }

  override def weaveIdAttributeSlot(idAttr: PAttribute, varName: String): Boolean = {
    if (is_logical_operation) { // XXX
      jm_pln("@Id")
      jm_pln("@GeneratedValue(strategy=GenerationType.AUTO)")
      jm_pln("private %s %s;".format(idAttr.typeName, varName))
    } else {
      jm_pln("@Id")
      if (idAttr.idPolicy == SMAutoIdPolicy) {
        jm_pln("@GeneratedValue(strategy=GenerationType.AUTO)")
      }
      if (is_primitive(idAttr)) {
        jm_pln("private %s %s;".format(java_typename(idAttr), varName))
      } else {
        val datatypename = idAttr.idDatatypeName
        jm_pln("private %s %s;".format(datatypename, varName))
        jm_pln("private %s %s;".format(idAttr.typeName, id_holder_name(varName)))
      }
    }
    true
  }

  override def weavePowertypeAttributeSlot(attr: PAttribute, varName: String): Boolean = {
    val pwr = attr.modelPowertype
    jm_pln("@Enumerated")
    jm_pln("private %s %s;".format(attr.typeName, varName))
    true
  }

  override def weavePersistentAnnotation(attr: PAttribute) {
    attr.isPersistentOption match {
      case Some(true)  => {
        jm_pln("@Basic(fetch=FetchType.EAGER)")
        val nullable = Pair("nullable", attr.multiplicity match {
          case POne => "false"
          case PZeroOne => "true"
          case POneMore => "false"
          case PZeroMore => "true"
        }).some
        val props = List(nullable)
        val tostring = {
          props.flatten.map {
            case (k, v) => "%s=%s".format(k, v)
          } mkString(", ")
        }
        jm_pln("@Column(%s)", tostring)
      }
      case Some(false) => jm_pln("@Transient")
      case None        => // do nothing
    }
  }

  override def weaveNotPersistentAnnotation(attr: PAttribute) {
    jm_pln("@Transient")
  }

  override def weaveCopyConstructorAttributeBlock(attr: PAttribute, varName: String, paramName: String): Boolean = {
    if (!attr.isId) false
    else if (is_primitive(attr)) false
    else {
      attr.idPolicy match {
        case SMAutoIdPolicy => {
          jm_pln("this.%s = %s.%s;".format(varName, paramName, varName))
          jm_pln("this.%s = %s.%s;".format(id_holder_name(varName), paramName, id_holder_name(varName)))
        }
        case SMApplicationIdPolicy => {
          jm_pln("this.%s = %s.%s;".format(varName, paramName, varName))
          jm_pln("this.%s = %s.%s;".format(id_holder_name(varName), paramName, id_holder_name(varName)))
        }
      }
      true
    }
  }

  override def weavePlainConstructorAttributeBlock(attr: PAttribute, varName: String, paramName: String): Boolean = {
    if (!attr.isId) false
    else if (is_primitive(attr)) false
    else {
      println("weavePlainConstructorAttributeBlock in Jap317")
      attr.idPolicy match {
        case SMAutoIdPolicy => {
          jm_pln("this.%s = %s.value;".format(varName, paramName))
          jm_pln("this.%s = %s;".format(id_holder_name(varName), paramName))
        }
        case SMApplicationIdPolicy => {
          jm_pln("this.%s = %s.value;".format(varName, paramName))
          jm_pln("this.%s = %s;".format(id_holder_name(varName), paramName))
        }
      }
      true
    }
  }

  override def weaveIdMethods(idAttr: PAttribute, attrName: String, varName: String, paramName: String, javaType: String): Boolean = {
    if (is_logical_operation) {
      sys.error("not supported yet")
    } else {
      jm_public_method("%s get%s()".format(javaType, attrName.capitalize)) {
        if (is_primitive(idAttr)) {
          jm_return(varName)
        } else {
          jm_return(id_holder_name(varName))
        }
      }
      idAttr.idPolicy match {
        case SMAutoIdPolicy => {}
        case SMApplicationIdPolicy => {
          if (is_primitive(idAttr)) {
            jm_public_method("void set%s(%s %s)".format(attrName.capitalize, javaType, paramName)) {
              jm_pln("this.%s = %s;".format(varName, paramName))
            }
          } else {
            jm_public_method("void set%s(%s %s)".format(attrName.capitalize, javaType, paramName)) {
              jm_pln("this.%s = %s.value;".format(varName, paramName))
              jm_pln("this.%s = %s;".format(id_holder_name(varName), paramName))
            }
          }
        }
      }
    }
    true
  }

  override def objectVarName(attr: PAttribute, varName: String): Option[String] = {
    if (!attr.isId) None
    else if (is_primitive(attr)) None
    else Some(id_holder_name(varName))
  }
}
