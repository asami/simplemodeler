package org.simplemodeling.SimpleModeler.entities

import scalaz._
import Scalaz._
import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull

/*
 * @since   Dec. 15, 2011
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class Jpa317Aspect extends JavaAspect {
  var modelEntity: SMEntity = null
  var is_logical_operation = false

  override def weaveImports() {
    jm_import("javax.persistence.Entity")
    jm_import("javax.persistence.Id")
    jm_import("javax.persistence.Basic")
    jm_import("javax.persistence.FetchType")
    jm_import("javax.persistence.Column")
    jm_import("java.io.Serializable")
  }

  override def weaveOpenAnnotation() {
    jm_pln("@Entity")
  }

  override def weaveIdAttributeSlot(idAttr: PAttribute, varName: String): Boolean = {
    if (is_logical_operation) { // XXX
      jm_pln("@Id")
      jm_pln("@GeneratedValue(strategy=GenerationType.AUTO)")
      jm_pln("private %s %s;".format(idAttr.typeName, varName))
    } else {
      idAttr.idPolicy match {
        case SMAutoIdPolicy => {
          jm_pln("@Id")
          jm_pln("@GeneratedValue(strategy=GeneratorType.AUTO)")
          jm_pln("private Long %s;".format(varName))
        }
        case SMApplicationIdPolicy => {
          jm_pln("@Id")
          jm_pln("private String %s;".format(varName))
        }
      }
    }
    true
  }

  override def weavePersistentAnnotation(attr: PAttribute) {
    attr.isPersistentOption match {
      case Some(true)  => {
        jm_pln("@Basic(FetchType.EAGER)")
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

  override def weaveIdMethods(idAttr: PAttribute, attrName: String, varName: String, paramName: String, javaType: String): Boolean = {
    if (is_logical_operation) {
      sys.error("not supported yet")
    } else {
      jm_public_method("%s get%s()".format(javaType, attrName.capitalize)) {
        jm_return(varName)
      }
      idAttr.idPolicy match {
        case SMAutoIdPolicy => {}
        case SMApplicationIdPolicy => {
          jm_public_method("public void set%s(%s %s)".format(attrName.capitalize, javaType, paramName)) {
            jm_pln("this.%s = %s;".format(varName, paramName))
          }
        }
      }
    }
    true
  }
}

object Jpa317Aspect extends Jpa317Aspect
