package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder}

/*
 * Mar. 10, 2009
 * Apr.  7, 2009
 * ASAMI, Tomoharu
 */
class GaeControllerEntity(val entity: GaeObjectEntity, val gaeContext: GaeEntityContext) extends GEntity(gaeContext) {
  type DataSource_TYPE = GDataSource

  override def is_Text_Output = true

  val view_dir = "../view/" + entity.name
  val base_name = gaeContext.getEntityBaseName(entity)
  val controller_path = gaeContext.getEntityCntrollerPath(entity)
  val capitalized_base_name = UString.capitalize(base_name)
  val home_action = "/"
  val create_action = controller_path + "/create"
  val destroy_action = controller_path + "/destroy"
  val edit_action = controller_path + "/edit"
  val index_action = controller_path + "/index"
  val new_action = controller_path + "/new"
  val search_action = controller_path + "/search"
  val show_action = controller_path + "/show"
  val update_action = controller_path + "/update"
  val service_name = {
    val domainName = {
      if (entity.packageName == "") ""
      else UJavaString.qname2simpleName(entity.packageName)
    }
    "DS" + UString.capitalize(domainName) + "DomainService"
  } // XXX GaeDomainServiceEntity
  val doc_name = entity.documentName
  val id_name = {
    entity.attributes.find(_.isId) match {
      case Some(attr) => attr.name
      case None => ""
    }
  }    
  val create_operation_name = "create" + capitalized_base_name
  val read_operation_name = "read" + capitalized_base_name
  val update_operation_name = "update" + capitalized_base_name
  val delete_operation_name = "delete" + capitalized_base_name
  val query_operation_name = "query" + capitalized_base_name

  private val entity_refs = new ArrayBuffer[(String, String)]

  def setEntityRefs(entityRefs: Seq[(String, String)]) {
    entity_refs ++= entityRefs
  }

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val prologue = """#!/usr/bin/env python
# -*- coding: utf-8 -*-

import types
import logging
import datetime
import time
import os
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from %packageName% import *

def text_to_dateTime(text):
  try:
    if len(text) == 0:
      return None
    dt = time.strptime(text, '%Y-%m-%dT%H:%M:%S')
    return datetime.datetime(dt[0], dt[1], dt[2], dt[3], dt[4], dt[5])
  except ValueError:
    return None

def text_to_date(text):
  try:
    if len(text) == 0:
      return None
    dt = time.strptime(text, '%Y-%m-%d')
    return datetime.date(dt[0], dt[1], dt[2])
  except ValueError:
    return None

def text_to_time(text):
  try:
    if len(text) == 0:
      return None
    if text[0] == 'T':
      text = text[1:]
    dt = time.strptime(text, '%H:%M:%S')
    return datetime.time(dt[3], dt[4], dt[5])
  except ValueError:
    return None

def text_to_bool(text):
  if text == 'True' or text == 'true' or text == '1':
    return True
  else:
    return False

class %name%Controller(webapp.RequestHandler):
  def action_position(self):
    return 2

  def home_after_task(self):
    return self.get_action('index')

  def home_after_create(self):
    return self.home_after_task()

  def home_after_destroy(self):
    return self.home_after_task()

  def home_after_update(self):
    return self.home_after_task()

  def view_path(self, action):
    return '%viewDir%/' + action + '.html'

  def get_action(self, action):
    return self.get_actions()[action]

  def get_actions(self):
    return {'home': '%homeAction%',
            'create': '%createAction%',
            'destroy': '%destroyAction%',
            'edit': '%editAction%',
            'index': '%indexAction%',
            'new': '%newAction%',
            'search': '%searchAction%',
            'show': '%showAction%',
            'update': '%updateAction%' }

%getEntity%

  def get(self):
    path = self.request.path.split('/')
    if len(path) <= self.action_position():
      action = 'index'
    else:
      action = path[self.action_position()]
    if action == '':
      action = 'index'
    if action == 'create':
      pass
    elif action == 'destroy':
      pass
    elif action == 'edit':
      self.edit()
    elif action == 'index':
      self.index()
    elif action == 'new':
      self.new()
    elif action == 'search':
      self.search()
    elif action == 'show':
      self.show()
    elif action == 'update':
      pass
    elif action == 'list.json':
      self.list_json()
    else:
      raise Exception('?')

  def post(self):
    action = self.request.path.split('/')[self.action_position()]
    if action == '':
      action = 'index'
    if action == 'create':
      self.create()
    elif action == 'destroy':
      self.destroy()
    elif action == 'edit':
      pass
    elif action == 'index':
      pass
    elif action == 'new':
      pass
    elif action == 'search':
      pass
    elif action == 'show':
      pass
    elif action == 'update':
      self.update()
    elif action == 'list.json':
      pass
    else:
      raise Exception('?')
"""

  override protected def write_Content(out: BufferedWriter) {
    val buffer = new AppendableTextBuilder(out)

    def make_prologue {
      buffer.append(prologue, Map("%name%" -> entity.name,
                                  "%packageName%" -> entity.packageName,
				  "%viewDir%" -> view_dir,
                                  "%homeAction%" -> home_action,
				  "%createAction%" -> create_action,
				  "%destroyAction%" -> destroy_action,
				  "%editAction%" -> edit_action,
				  "%indexAction%" -> index_action,
				  "%newAction%" -> new_action,
				  "%searchAction%" -> search_action,
				  "%showAction%" -> show_action,
				  "%updateAction%" -> update_action,
                                  "%getEntity%" -> create_get_entity
				  ))
    }

    def create_get_entity = {
      val b = new StringTextBuilder
      b.indentUp
      b.println("def get_entity(self):")
      b.indentUp
      b.print("return {")
      for ((qname, baseName) <- entity_refs) {
        b.print("'")
        b.print(qname)
        b.print("': '")
        b.print("/" + baseName + "/list.json") // XXX
        b.print("'")
        if (entity != entity_refs.last) {
          b.println(",")
        }
      }
      b.println("}")
      b.indentDown
      b.indentDown
      b.toString
    }

    def make_service_create {
      buffer.print("service = ")
      buffer.print(service_name) // DSStoreDomainService
      buffer.println("()")
    }

    def make_set_request_to_doc_non_id_attributes {
      for (attr <- entity.attributes if !attr.isId) {
        attr.attributeType match {
          case t: GaeDateTimeType => {
	    buffer.print("doc.")
	    buffer.print(attr.name)
	    buffer.print(" = text_to_dateTime(self.request.get('")
	    buffer.print(attr.name)
	    buffer.println("'))")
	    buffer.print("doc.")
	    buffer.print(attr.name)
	    buffer.print("_date = text_to_date(self.request.get('")
	    buffer.print(attr.name)
	    buffer.println("_date'))")
	    buffer.print("doc.")
	    buffer.print(attr.name)
	    buffer.print("_time = text_to_time(self.request.get('")
	    buffer.print(attr.name)
	    buffer.println("_time'))")
	    buffer.print("doc.")
	    buffer.print(attr.name)
	    buffer.print("_now = text_to_bool(self.request.get('")
	    buffer.print(attr.name)
	    buffer.println("_now'))")
          }
          case e: GaeEntityType => {
	    buffer.print("doc.")
	    buffer.print(attr.name)
	    buffer.print(" = self.request.get('")
	    buffer.print(attr.name)
	    buffer.println("')")
          }
          case _ => {
	    buffer.print("doc.")
	    buffer.print(attr.name)
	    buffer.print(" = self.request.get('")
	    buffer.print(attr.name)
	    buffer.println("')")
          }
        }
      }
    }

    def make_read_doc_by_key {
      buffer.print("doc = service.")
      buffer.print(read_operation_name) // readGoods
      buffer.println("(key)")
    }      

    def make_query_docs {
      buffer.print("query = service.")
      buffer.print(query_operation_name) // queryGoods")
      buffer.println("()")
      buffer.println("docs = query.fetch(10)") // XXX
    }

    def make_template_values_docs_actions {
      buffer.print("template_values = {'docs': docs, ")
      make_action_list_in_template
      buffer.println("}")
    }

    def make_template_values_doc_actions {
      buffer.print("template_values = {'doc': doc, ")
      make_entity_list_in_template
      make_action_list_in_template
      buffer.println("}")
    }

    def make_template_values_actions {
      buffer.print("template_values = {")
      make_entity_list_in_template
      make_action_list_in_template
      buffer.println("}")
    }

    def make_entity_list_in_template {
      for ((qname, _) <- entity_refs) {
        buffer.print("'entity_")
        buffer.print(qname.replace('.', '_'))
        buffer.print("': self.get_entity()['")
        buffer.print(qname)
        buffer.print("']")
        buffer.print(", ")
      }
    }

    def make_action_list_in_template {
      buffer.print("'action_home': self.get_action('home')")
      buffer.print(", ")
      buffer.print("'action_create': self.get_action('create')")
      buffer.print(", ")
      buffer.print("'action_destroy': self.get_action('destroy')")
      buffer.print(", ")
      buffer.print("'action_edit': self.get_action('edit')")
      buffer.print(", ")
      buffer.print("'action_index': self.get_action('index')")
      buffer.print(", ")
      buffer.print("'action_new': self.get_action('new')")
      buffer.print(", ")
      buffer.print("'action_search': self.get_action('search')")
      buffer.print(", ")
      buffer.print("'action_show': self.get_action('show')")
      buffer.print(", ")
      buffer.print("'action_update': self.get_action('update')")
    }

    def make_create {
      buffer.println("def create(self):")
      buffer.indentUp
      make_service_create
      buffer.print("doc = ")
      buffer.print(doc_name) // DDGoods
      buffer.println("()")
      buffer.print("doc.")
      buffer.print(id_name)
      buffer.print(" = self.request.get('")
      buffer.print(id_name)
      buffer.println("')")
      make_set_request_to_doc_non_id_attributes
      buffer.print("service.")
      buffer.print(create_operation_name) // createGoods
      buffer.println("(doc)")
      buffer.println("self.redirect(self.home_after_create())")
      buffer.indentDown
    }

    def make_destroy {
      buffer.println("def destroy(self):")
      buffer.indentUp
      make_service_create
      buffer.println("key = self.request.get('key')")
      buffer.print("service.")
      buffer.print(delete_operation_name) // deleteGoods
      buffer.println("(key)")
      buffer.println("self.redirect(self.home_after_destroy())")
      buffer.indentDown
    }

    def make_edit {
      buffer.println("def edit(self):")
      buffer.indentUp
      make_service_create
      buffer.println("key = self.request.get('key')")
      make_read_doc_by_key
      make_template_values_doc_actions
      buffer.println("path = os.path.join(os.path.dirname(__file__), self.view_path('edit'))")
      buffer.println("self.response.out.write(template.render(path, template_values))")
      buffer.indentDown
    }

    def make_index {
      buffer.println("def index(self):")
      buffer.indentUp
      make_service_create
      make_query_docs
      make_template_values_docs_actions
      buffer.println("path = os.path.join(os.path.dirname(__file__), self.view_path('index'))")
      buffer.println("self.response.out.write(template.render(path, template_values))")
      buffer.indentDown
    }

    def make_new {
      buffer.println("def new(self):")
      buffer.indentUp
      make_template_values_actions
      buffer.println("path = os.path.join(os.path.dirname(__file__), self.view_path('new'))")
      buffer.println("self.response.out.write(template.render(path, template_values))")
      buffer.indentDown
    }

    def make_search {
      buffer.println("def search(self):")
      buffer.indentUp
      buffer.println("pass")
      buffer.indentDown
    }

    def make_show {
      buffer.println("def show(self):")
      buffer.indentUp
      make_service_create
      buffer.println("key = self.request.get('key')")
      make_read_doc_by_key
      make_template_values_doc_actions
      buffer.println("path = os.path.join(os.path.dirname(__file__), self.view_path('show'))")
      buffer.println("self.response.out.write(template.render(path, template_values))")
      buffer.indentDown
    }

    def make_update {
      buffer.println("def update(self):")
      buffer.indentUp
      make_service_create
      buffer.println("key = self.request.get('key')")
      make_read_doc_by_key
      make_set_request_to_doc_non_id_attributes
      buffer.print("service.")
      buffer.print(update_operation_name) // updateGoods
      buffer.println("(doc)")
      buffer.println("self.redirect(self.home_after_update())")
      buffer.indentDown
    }

    def make_list_json {
      buffer.println("def list_json(self):")
      buffer.indentUp
      make_service_create
      make_query_docs
      buffer.println("json = \"{ identifier: 'name',\\nitems: [\\n\"")
      buffer.println("for doc in docs:")
      buffer.indentUp
      buffer.println("json += \"    { name: '\"")
      buffer.print("json += ")
      buffer.print("doc.")
      buffer.println(id_name)
      buffer.println("json += \"', label: '\"")
      buffer.print("json += ")
      buffer.print("doc.")
      buffer.println(id_name)
      buffer.println("json += \"'}\"")
      buffer.println("json += \",\\n\"")
      buffer.indentDown
      buffer.println("json += \"]}\\n\"")
      buffer.println("self.response.out.write(json)")
      buffer.indentDown
    }

    make_prologue
    buffer.indentUp
    buffer.println()
    make_create
    buffer.println()
    make_destroy
    buffer.println()
    make_edit
    buffer.println()
    make_index
    buffer.println()
    make_new
    buffer.println()
    make_search
    buffer.println()
    make_show
    buffer.println()
    make_update
    buffer.println()
    make_list_json
    buffer.indentDown
    buffer.flush
  }
}

class GaeControllerEntityClass extends GEntityClass {
  type Instance_TYPE = GaeControllerEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "py"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = error("not supported yet")
}
