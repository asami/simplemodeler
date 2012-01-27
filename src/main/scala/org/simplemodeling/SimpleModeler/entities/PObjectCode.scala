package org.simplemodeling.SimpleModeler.entities

import java.io._
import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{UString, JavaTextMaker}
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

// derived from GaejObjectCode since Oct. 26, 2009.
/*
 * @since   Apr. 23, 2011
 * @version Apr. 23, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class PObjectCode(val pContext: PEntityContext) {
  val template: String
  val coders = new HashMap[String, Coder]
  val buffer = new JavaTextMaker

  protected final def doc_attr_name(attr: PAttribute) = {
    pContext.documentAttributeName(attr)
  }

  protected final def doc_var_name(attr: PAttribute) = {
    pContext.documentVariableName(attr)
  }

  protected final def id_var_name(attr: PAttribute) = {
    pContext.variableName4RefId(attr)
  }

  def coder(expr: String, string: String) {
    coder(expr, new StringCoder(string))
  }

  def coder(expr: String, coder: Coder) {
    require (coder != null)
    coders += expr -> coder
  }

  def code(): String = {
//    println("code = " + coders)
    var index = 0
    while (index != -1) {
      val next = template.indexOf('%', index)
      if (next == -1) {
        buffer.append(template.substring(index))
        index = -1
      } else {
        buffer.append(template.substring(index, next))
        val end = template.indexOf('%', next + 1)
        make_code(template.substring(next + 1, end))
        index = end + 1
//        println("next: %s, end: %s, index %s".format(next, end, index))
      }
    }
    buffer.toString
  }

  private def make_code(expr: String) {
//    println("expr = " + expr)
    coders.get(expr) match {
      case Some(coder) => coder.coding()
      case None => buffer.print("%" + expr + "%")
    }
  }

  abstract class Coder {
    final def coding() {
      code()
    }

    protected def code(): Unit

    def cAppend(text: CharSequence) {
      buffer.print(text)
    }

    def cAppendln(text: CharSequence) {
      buffer.println(text)
    }

    def cAppendln() {
      buffer.println()
    }

    def cIndentUp() {
      buffer.indentUp
    }

    def cIndentDown() {
      buffer.indentDown
    }

    def cMethod(signature: String)(content: => Unit) {
      cAppendln()
      cIndentUp
      cAppend(signature)
      cAppendln(" {")
      cIndentUp
      content
      cIndentDown
      cAppendln("}")
      cIndentDown
    }

    def cVar(varName: String, className: String) {
      buffer.makeVar(varName, className)
    }

    def cVar(varName: String, typeName: String, makeObject: String) {
      buffer.makeVar(varName, typeName, makeObject)
    }

    def cIf(condition: String)(body: => Unit) {
      cAppend("if (")
      cAppend(condition)
      cAppendln(") {")
      cIndentUp
      body
      cIndentDown
      cAppendln("}")
    }

    def cIfElse(condition: String)(body: => Unit) {
      cAppend("if (")
      cAppend(condition)
      cAppendln(") {")
      cIndentUp
      body
    }

    def cElseIfElse(condition: String)(body: => Unit) {
      cIndentDown
      cAppend("} else if (")
      cAppend(condition)
      cAppendln(") {")
      cIndentUp
      body
    }

    def cElseIf(condition: String)(body: => Unit) {
      cIndentDown
      cAppend("} else if (")
      cAppend(condition)
      cAppendln(") {")
      cIndentUp
      body
      cIndentDown
      cAppendln("}")
    }

    def cElse(body: => Unit) {
      cIndentDown
      cAppendln("} else {")
      cIndentUp
      body
      cIndentDown
      cAppendln("}")
    }

    def cMatchElse[T](elements: Seq[T])(condition: T => String)(body: T => Unit)(elseClouse: => Unit) {
      var first = true
      if (elements.length > 0) {
        for (element <- elements) {
          if (first) {
            cIfElse(condition(element)) {
              body(element)
            }
            first = false
          } else {
            cElseIfElse(condition(element)) {
              body(element)
            }
          }
        }
        cElse {
          elseClouse
        }
      } else {
        elseClouse
      }
    }

    def cFor(condition: String)(body: => Unit) {
      cAppend("for (")
      cAppend(condition)
      cAppendln(") {")
      cIndentUp
      body
      cIndentDown
      cAppendln("}")
    }

    def cFor(typeName: String, varName: String, generator: String)(body: => Unit) {
      cFor(typeName + " " + varName + ": " + generator)(body) 
    }

    def cTry(body: => Unit) {
      cAppendln("try {")
      cIndentUp
      body
    }

    def cCatch(condition: String)(body: => Unit) {
      cIndentDown
      cAppend("} catch(")
      cAppend(condition)
      cAppendln(") {")
      cIndentUp
      body
    }

    def cCatchEnd(condition: String)(body: => Unit) {
      cIndentDown
      cAppend("} catch(")
      cAppend(condition)
      cAppendln(") {")
      cIndentUp
      body
      cIndentDown
      cAppendln("}")
    }

    def cFinally(body: => Unit) {
      cIndentDown
      cAppendln("} finally {")
      cIndentUp
      body
      cIndentDown
      cAppendln("}")
    }

    def cReturn(expr: String) {
      buffer.makeReturn(expr)
    }

    def cReturn {
      buffer.makeReturn
    }

    def cReturnNull {
      buffer.makeReturn("null")
    }

    def cOutputExpr(expr: String) {
      buffer.makeAppendExpr(expr)
    }

    def cOutputExpr(varName: String, expr: String) {
      buffer.makeAppendExpr(varName, expr)
    }

    def cOutputString(string: String) {
      buffer.makeAppendString(string)
    }

    def cOutputStringln(string: String) {
      buffer.makeAppendStringln(string)
    }

    def cOutputString(varName: String, string: String) {
      buffer.makeAppendString(varName, string)
    }

    def cOutputStringln(varName: String, string: String) {
      buffer.makeAppendStringln(varName, string)
    }

    def cOutputln() {
      buffer.makeAppendln()
    }

    def cOutputln(varName: String) {
      buffer.makeAppendln(varName)
    }

    def cOutputIndentUp() {
      buffer.makeAppendIndentUp()
    }

    def cOutputIndentDown() {
      buffer.makeAppendIndentDown()
    }

    def cOutputFlush() {
      cOutputFlush("buf")
    }

    def cOutputFlush(varName: String) {
      cAppend(varName)
      cAppendln(".flush();")
    }
  }

  class StringCoder(val string: String) extends Coder {
    override def code() {
      cAppend(string)
    }
  }
}
