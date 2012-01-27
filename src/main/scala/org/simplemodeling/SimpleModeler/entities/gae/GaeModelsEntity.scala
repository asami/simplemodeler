package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString}

/*
 * @since   Mar.  9, 2009
 * @version Oct. 26, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeModelsEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GEntity(aIn, aOut, aContext) {
  type DataSource_TYPE = GDataSource

  var packageName: String = ""
  var xmlNamespace: String = ""
  val entities = new ArrayBuffer[GaeEntityEntity]
  val parts = new ArrayBuffer[GaeEntityPartEntity]
  val powertypes = new ArrayBuffer[GaePowertypeEntity]

  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: GEntityContext) = this(null, aContext)

  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  private val prologue = """#!/usr/bin/env python
# -*- coding: utf-8 -*-

import types
import logging
import datetime
from google.appengine.ext import db
from %packageName%.models import *

def make_text(value):
  return value

def make_dateTime(dt, d, t, now):
  if dt:
    return dt
  elif now:
    return datetime.datetime.now()
  elif d and t:
    return datetime.datetime(d.year, d.month, d.day, t.hour, t.minute, t.second)
  else:
    return datetime.datetime.now()

def make_reference(key_name):
  logging.debug('key_name = ' + key_name)
  if key_name.isdigit():
    entity = db.Model.get_by_id(int(key_name))
  else:
    entity = db.Model.get_by_key_name(key_name)
  return entity.key()
"""

  override protected def write_Content(out: BufferedWriter) {
    val buffer = new AppendableTextBuilder(out)

    def make_prologue {
      buffer.append(prologue, Map("%packageName%" -> packageName))
    }

    def make_reference_methods { // sync GaeDomainServiceEntity
      entities.foreach(make_reference_method)
    }

    def make_reference_method(anObject: GaeObjectEntity) { // sync GaeDomainServiceEntity
      val entityName = anObject.name
      val capitalizedTerm = UString.capitalize(anObject.term)
      buffer.println()
      buffer.print("def make_reference_")
      buffer.print(capitalizedTerm)
      buffer.println("(key_name):")
      buffer.indentUp
      buffer.println("if key_name.isdigit():")
      buffer.indentUp
      buffer.print("entity = ")
      buffer.print(entityName)
      buffer.println(".get_by_id(int(key_name))")
      buffer.indentDown
      buffer.println("else:")
      buffer.indentUp
      buffer.print("entity = ")
      buffer.print(entityName)
      buffer.println(".get_by_key_name(key_name)")
      buffer.indentDown
      buffer.println("return entity.key()")
      buffer.indentDown
    }

/*
    buffer.println("#!/usr/bin/env python")
    buffer.println("# -*- coding: utf-8 -*-")
    buffer.println()
    buffer.println("import types")
    buffer.println("import logging")
    buffer.println("import datetime")
    buffer.println("from google.appengine.ext import db")
    buffer.print("from ")
    buffer.print(packageName)
    buffer.println(" import *")
    buffer.println()
*/
    make_prologue
    make_reference_methods
    for (obj <- entities) {
      buffer.println()
      obj.writeDocument(buffer)
    }
    for (obj <- entities) {
      buffer.println()
      obj.writeModel(buffer)
    }
    buffer.flush
  }

  final def addEntity(anObj: GaeEntityEntity) {
//    println("add object = " + anObj.qualifiedName + "(" + anObj + ")") 2009-03-10
    entities += anObj
  }

/*
  final def findEntity(aQName: String): Option[GaeEntityEntity] = {
//    println("entities = " + entities)
//    for (obj <- entities) {
//      println("Gae object = " + obj.qualifiedName + "(" + aQName + ")")
//    }
    entities.find(_.qualifiedName == aQName)
  }
*/

  final def addEntityPart(aPart: GaeEntityPartEntity) {
    parts += aPart
  }

  final def addPowertype(aPowertype: GaePowertypeEntity) {
    powertypes += aPowertype
  }

  final def findObject(aQName: String): Option[GaeObjectEntity] = {
    entities.find(_.qualifiedName == aQName) match {
      case None => {
        parts.find(_.qualifiedName == aQName) match {
          case None => {
            powertypes.find(_.qualifiedName == aQName)
          }
          case part => part
        }
      }
      case entity => entity
    }
  }
}

class GaeModelsEntityClass extends GEntityClass {
  type Instance_TYPE = GaeModelsEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "py"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GaeModelsEntity(aDataSource, aContext))
}
