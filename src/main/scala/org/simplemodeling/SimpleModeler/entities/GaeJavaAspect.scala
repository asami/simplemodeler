package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull

/*
 * @since   May. 14, 2011
 *  version Jun. 26, 2011
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class GaeJavaAspect extends JavaAspect {
  var modelEntity: SMEntity = null
  var is_logical_operation = false

  override def weaveImports() {
    jm_import("javax.jdo.PersistenceManager")
    jm_import("javax.jdo.Query")
    jm_import("javax.jdo.Transaction")
    jm_import("javax.jdo.annotations.*")
    jm_import("com.google.appengine.api.datastore.*")
    jm_import("com.google.appengine.api.datastore.Key")
    jm_import("com.google.appengine.api.users.User")
  }

  override def weaveIdAttributeSlot(idAttr: PAttribute, varName: String): Boolean = {
    if (is_logical_operation) {
      jm_pln("@PrimaryKey")
      jm_pln("@Persistent")
      jm_pln("private Key key;")
      jm_pln("@Persistent")
      jm_pln("private %s %s;".format(idAttr.typeName, varName))
    } else if (modelEntity.appEngine.use_key) {
      idAttr.idPolicy match {
        case SMAutoIdPolicy => {
          jm_pln("@PrimaryKey")
          jm_pln("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
          jm_pln("private Key key;")
        }
        case SMApplicationIdPolicy => {
          jm_pln("@PrimaryKey")
          jm_pln("@Persistent")
          jm_pln("private Key key;")
        }
      }
    } else {
      idAttr.idPolicy match {
        case SMAutoIdPolicy => {
          jm_pln("@PrimaryKey")
          jm_pln("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
          jm_pln("private Long %s;".format(varName))
        }
        case SMApplicationIdPolicy => {
          jm_pln("@PrimaryKey")
          jm_pln("private String %s;".format(varName))
        }
      }
    }
    true
  }

  override def weavePersistentAnnotation(attr: PAttribute) {
    attr.isPersistentOption match {
      case Some(true)  => jm_pln("@Persistent")
      case Some(false) => jm_pln("@NotPersistent")
      case None        => // do nothing
    }
  }

  override def weaveNotPersistentAnnotation(attr: PAttribute) {
    jm_pln("@NotPersistent")
  }

  override def weaveIdMethods(idAttr: PAttribute, attrName: String, varName: String, paramName: String, javaType: String): Boolean = {
    if (is_logical_operation) {
      jm_public_method("Key getKey()", "key")()
      jm_public_method("%s get%s()".format(javaType, attrName.capitalize), varName)()
      idAttr.idPolicy match {
        case SMAutoIdPolicy => {
          jm_public_method("void set%s(Key parent_key)".format(attrName.capitalize, paramName)) {
            jm_pln("this._parent_key = parent_key;")
          }
                  /*
              buffer.method("public void set%s(String %s)".format(attrName.capitalize, paramName)) {
                pln("this.%s = %s;".format(varName, paramName))
                pln("this.key = null;")
              }
              buffer.method("public void set%s(String %s, Key parent_key)".format(attrName.capitalize, paramName)) {
                if (notNull(modelEntity.jdo.table)) {
                  buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
                } else {
                  buffer.makeVar("kind", "String", "getClass().getSimpleName()")
                }
                pln("this.%s = %s;".format(varName, paramName))
                pln("this.key = Util.allocateKey(kind, parent_key);")
              }
*/
        }
        case SMApplicationIdPolicy => {
          jm_public_method("void set%s(String %s)".format(attrName.capitalize, paramName)) {
            jm_pln("this.%s = %s;".format(varName, paramName))
          }
          jm_public_method("public void set%s(String %s, Key parent_key)".format(attrName.capitalize, paramName)) {
            jm_pln("this.%s = %s;".format(varName, paramName))
            jm_pln("this._parent_key = parent_key;")
          }
                  /*
              buffer.method("public void set%s(String %s)".format(attrName.capitalize, paramName)) {
                if (notNull(modelEntity.jdo.table)) {
                  buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
                } else {
                  buffer.makeVar("kind", "String", "getClass().getSimpleName()")
                }
                pln("this.%s = %s;".format(varName, paramName))
                pln("this.key = Util.allocateAppUuidKey(kind, %s);".format(paramName))
              }
              buffer.method("public void set%s(String %s, Key parent_key)".format(attrName.capitalize, paramName)) {
                if (notNull(modelEntity.jdo.table)) {
                  buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
                } else {
                  buffer.makeVar("kind", "String", "getClass().getSimpleName()")
                }
                pln("this.%s = %s;".format(varName, paramName))
                pln("this.key = Util.allocateAppUuidKey(kind, %s, parent_key);".format(paramName))
              }
*/
        }
      }
    } else if (modelEntity.appEngine.use_key) {
      jm_public_method("Key getKey()") {
        jm_return("key")
      }
      idAttr.idPolicy match {
        case SMAutoIdPolicy => {
          jm_public_method("Long get%s()".format(attrName.capitalize)) {
            jm_return("key.getId()")
          }
        }
        case SMApplicationIdPolicy => {
          jm_public_method("String get%s()".format(attrName.capitalize)) {
            jm_return("key.getName()")
          }
          jm_public_method("void set%s(String %s)".format(attrName.capitalize, paramName)) {
            if (notNull(modelEntity.jdo.table)) {
              jm_var_String("kind", "\"" + modelEntity.jdo.table + "\"")
            } else {
              jm_var_String("kind", "getClass().getSimpleName()")
            }
            jm_pln("this.key = KeyFactory.createAppUuidKey(kind, %s);".format(paramName))
          }
          jm_public_method("void set%s(String %s, Key parent_key)".format(attrName.capitalize, paramName)) {
            if (notNull(modelEntity.jdo.table)) {
              jm_var_String("kind", "\"" + modelEntity.jdo.table + "\"")
            } else {
              jm_var_String("kind", "getClass().getSimpleName()")
            }
            jm_pln("this.key = KeyFactory.createAppUuidKey(kind, %s, parent_key);".format(paramName))
          }
        }
      }
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
