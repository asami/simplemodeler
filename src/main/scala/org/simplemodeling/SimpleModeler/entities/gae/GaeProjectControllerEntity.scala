package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, StringTextBuilder, UString, UJavaString}

/*
 * @since   Apr.  6, 2009
 * @version Apr. 23, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeProjectControllerEntity(val models: Seq[GaeModelsEntity], aContext: GEntityContext) extends GEntity(aContext) {
  type DataSource_TYPE = GDataSource

  override def is_Text_Output = true

  val entities = for (model <- models; obj <- model.entities) yield obj

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val prologue = """#!/usr/bin/env python
import os
import cgi

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db
from google.appengine.ext.webapp import template

%importControllers%

class MainPage(webapp.RequestHandler):
  def get(self):
    path = os.path.join(os.path.dirname(__file__), 'index.html')
    self.response.out.write(template.render(path, {}))

application = webapp.WSGIApplication(
                                     [('/', MainPage),
%setControllers%                                     ],
                                     debug=True)

def main():
  run_wsgi_app(application)

if __name__ == "__main__":
  main()
"""

  override protected def write_Content(out: BufferedWriter) {
    val buffer = new AppendableTextBuilder(out)

    def make_prologue {
      buffer.append(prologue, Map("%importControllers%" -> make_import_controllers,
                                  "%setControllers%" -> make_set_controllers
				  ))
    }

    make_prologue
    buffer.flush
  }

  private def make_import_controllers: String = {
    val buf = new StringTextBuilder
    for (obj <- entities) {
      buf.print("from ")
      buf.print(obj.packageName)
      buf.print(".controller.")
      buf.print(obj.name)
      buf.print(" import ")
      buf.print(obj.name)
      buf.println("Controller")
    }
    buf.toString
  }

  private def make_set_controllers: String = {
    val buf = new StringTextBuilder
    for (obj <- entities) {
      buf.print("                                      ")
      buf.print("('")
      buf.print("/")
      buf.print(obj.term_en)
      buf.print(".*', ")
      buf.print(obj.name)
      buf.print("Controller)")
      if (obj != entities.last) {
        buf.print(",")
      }
      buf.println()
    }
    buf.toString
  }
}

class GaeProjectControllerEntityClass extends GEntityClass {
  type Instance_TYPE = GaeControllerEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "py"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = error("not supported yet")
}
