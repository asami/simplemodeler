package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UJavaString, UString}

/*
 * @since   Mar. 23, 2009
 * @version Apr. 23, 2009
 * @author  ASAMI, Tomoharu
 */
// XXX DomainEntityServiceEntity
class GaeDomainServiceEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GEntity(aIn, aOut, aContext) {
  type DataSource_TYPE = GDataSource

  var packageName: String = ""
  val entities = new ArrayBuffer[GaeEntityEntity]

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
"""

/*
def make_text(value):
  return value

def make_dateTime(dt, d, t, now):
  return datetime.datetime.now()

def make_reference(key_name):
  if key_name.isdigit():
    entity = db.Model.get_by_id(int(key_name))
  else:
    entity = db.Model.get_by_key_name(key_name)
  return entity.key()
*/

  override protected def write_Content(out: BufferedWriter) {
    val domainName = {
      if (packageName == "") ""
      else UJavaString.qname2simpleName(packageName)
    }
    val buffer = new AppendableTextBuilder(out)
    val serviceName = "DS" + UString.capitalize(domainName) + "DomainService"

    def make_prologue {
      buffer.append(prologue, Map("%packageName%" -> packageName))
    }

    def make_reference_methods { // sync GaeModelsEntity
      entities.foreach(make_reference_method)
    }

    def make_reference_method(anObject: GaeEntityEntity) { // sync GaeModelsEntity
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

    def make_documents {
      for (obj <- entities) {
        buffer.println()
        obj.writeDocument(buffer)
      }
    }

    def make_queries {
      entities.foreach(make_query)
    }

    def make_query(anObject: GaeEntityEntity) {
      val entityName = anObject.name
      val capitalizedTerm = UString.capitalize(anObject.term)
      val queryName = UString.capitalize(anObject.term) + "Query"
      buffer.println()
      buffer.print("class ")
      buffer.print(queryName)
      buffer.println(":")
      buffer.indentUp
      buffer.println("def fetch(self, limit, offset=0):")
      buffer.indentUp
      buffer.print("query = ")
      buffer.print(entityName)
      buffer.println(".query()")
      buffer.println("results = query.fetch(limit, offset)")
      buffer.println("return map((lambda x: x._document()), results)")
      buffer.indentDown
      buffer.indentDown
    }

    def make_service {
      buffer.println()
      buffer.print("class ")
      buffer.print(serviceName)
      buffer.println(":")
      buffer.indentUp
      entities.foreach(make_object_crud_operation)
      buffer.indentDown
    }

    def make_object_crud_operation(anObject: GaeEntityEntity) {
      val entityName = anObject.name
      val capitalizedTerm = UString.capitalize(anObject.term)
      val queryName = UString.capitalize(anObject.term) + "Query"
      val idName = anObject.attributes.find(_.isId) match {
	case Some(attr) => attr.name
	case None => null
      }

      def make_value_method(attr: GaeAttribute) = {
        attr.attributeType match {
          case t: GaeDateTimeType => { // sync GaeEntityEntity
            buffer.print("make_dateTime(")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print(", ")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_date, ")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_time, ")
            buffer.print("doc.")
            buffer.print(attr.name)
            buffer.print("_now")
            buffer.print(")")
          }
          case e: GaeEntityType => {
            buffer.print("make_reference_")
            buffer.print(UString.capitalize(e.entity.term))
            buffer.print("(doc.")
            buffer.print(attr.name)
            buffer.print(")")
          }
          case _ => {
            buffer.print("make_text(doc.")
            buffer.print(attr.name)
            buffer.print(")") // XXX or "?temporary?"
          }
        }
/*
	if (attr.isId) {
	  buffer.print(" or '*temporary*'")
	} else {
	  buffer.print(" or '?temporary?'")
	}
*/
      }

      def create_operation {
        buffer.println()
        buffer.print("def create")
        buffer.print(capitalizedTerm)
        buffer.println("(self, doc=None, key_name=None):")
        buffer.indentUp
        buffer.println("if not key_name:")
        buffer.indentUp
        buffer.println("if doc:")
        buffer.indentUp
        buffer.print("if doc.")
        buffer.print(idName) // XXX null case
        buffer.println(":")
        buffer.indentUp
        buffer.print("key_name = doc.")
        buffer.print(idName)
        buffer.println()
	buffer.indentDown
	buffer.indentDown
	buffer.indentDown
        buffer.print("entity = ")
        buffer.print(entityName)
        buffer.print("(key_name=key_name")
        for (attr <- anObject.attributes) {
          buffer.print(", ")
          buffer.print(attr.name)
          buffer.print(" = ")
          make_value_method(attr)
        }
        buffer.println(")")
	buffer.println("entity._update(doc)")
        buffer.println("entity.put()")
	buffer.print("if entity.")
	buffer.print(idName)
	buffer.println(" != '%s' % entity.key().id_or_name():")
	buffer.indentUp
	buffer.print("entity.")
	buffer.print(idName)
	buffer.println(" = '%s' % entity.key().id_or_name()")
        buffer.println("entity.put()")
	buffer.indentDown
        buffer.println("return entity._document()")
        buffer.indentDown
      }

      def get_entity_block {
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
      }

      def read_operation {
        buffer.println()
        buffer.print("def read")
        buffer.print(capitalizedTerm)
        buffer.println("(self, key_name):")
        buffer.indentUp
	get_entity_block
        buffer.println("return entity._document()")
        buffer.indentDown
      }

      def update_operation {
        buffer.println()
        buffer.print("def update")
        buffer.print(capitalizedTerm)
        buffer.println("(self, doc):")
        buffer.indentUp
	buffer.print("key_name = doc.")
	buffer.println(idName)
	get_entity_block
        buffer.println("entity._update(doc)")
        buffer.println("entity.put()")
        buffer.indentDown
      }

      def delete_operation {
        buffer.println()
        buffer.print("def delete")
        buffer.print(capitalizedTerm)
        buffer.println("(self, key_name):")
        buffer.indentUp
	get_entity_block
        buffer.println("entity.delete()")
        buffer.indentDown
      }

      def query_operation {
        buffer.println()
        buffer.print("def query")
        buffer.print(capitalizedTerm)
        buffer.println("(self):")
        buffer.indentUp
        buffer.print("return ")
        buffer.print(queryName)
        buffer.println("()")
        buffer.indentDown
      }

      create_operation
      read_operation
      update_operation
      delete_operation
      query_operation
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
    buffer.println(".models import *")
*/
    make_prologue
//    make_reference_methods
//    make_documents
    make_queries
    make_service
    buffer.flush
  }

  final def addEntity(anObj: GaeEntityEntity) {
//    println("add entity = " + anObj.qualifiedName + "(" + anObj + ")") 2009-03-10
    entities += anObj
  }

  final def findEntity(aQName: String): Option[GaeEntityEntity] = {
//    println("entities = " + entities)
//    for (obj <- entities) {
//      println("Gae entity = " + obj.qualifiedName + "(" + aQName + ")")
//    }
    entities.find(_.qualifiedName == aQName)
  }

  final def getEntity(aQName: String): GaeEntityEntity = {
    findEntity(aQName).get
  }
}
