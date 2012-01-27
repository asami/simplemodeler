package org.simplemodeling.dsl.util

import org.simplemodeling.dsl._
import org.goldenport.value._

/*
 * @since   Sep. 14, 2008
 *  version Apr. 17, 2011
 * @version Dec.  6, 2011
 * @author  ASAMI, Tomoharu
 */
object UDsl {
  def getNameFromClassName(anObject: SElement): String = {
    val name = anObject.getClass.getSimpleName
    if (name.charAt(name.length - 1) != '$') name
    else name.substring(0, name.length - 1)
  }

  def guessTerm(anObject: SElement): String = {
    drop_prefix(anObject.name)
  }

  def guessTerm(aName: String): String = {
    drop_prefix(aName)
  }

  def makeReferenceName(anObject: SObject): String = {
    if (anObject.term != null) anObject.term
    else drop_prefix(anObject.name)
  }

  private def drop_prefix(aName: String): String = {
    require (aName != null, "name is null in UDsl#drop_prefx")
    if (aName.isEmpty()) return ""
    var isLatinUpper = false
    for (i <- 0 until aName.length) {
      val c = aName.charAt(i)
      if (Character.UnicodeBlock.BASIC_LATIN != Character.UnicodeBlock.of(c))
        return aName.substring(i)
      if (Character.isLowerCase(c)) {
        if (isLatinUpper) return aName.substring(i - 1)
        else aName
      }
      isLatinUpper = true
    }
    aName.substring(aName.length - 1)
  }

  // grails
  def makeRigidCamelName(aName: String): String = {
    val (prefix, body) = divide_name(aName)
    if (prefix == "") body
    else {
      val buffer = new StringBuilder
      buffer += prefix(0)
      for (i <- 1 until prefix.length) {
	buffer += prefix(i).toLowerCase
      }
      buffer.append(body)
      buffer.toString
    }
  }

  private def divide_name(aName: String): (String, String) = {
    var isLatinUpper = false
    for (i <- 0 until aName.length) {
      val c = aName.charAt(i)
      if (Character.UnicodeBlock.BASIC_LATIN != Character.UnicodeBlock.of(c))
        return (aName.substring(0, i), aName.substring(i))
      if (Character.isLowerCase(c)) {
        if (isLatinUpper) return (aName.substring(0, i - 1), aName.substring(i - 1))
        else aName
      }
      isLatinUpper = true
    }
    (aName.substring(0, aName.length - 1), aName.substring(aName.length - 1))
  }

  def printFlow(aRoot: GTreeNode[SStep]) {
    println("printFlow start: " + aRoot)
    aRoot.traverse(new GTreeVisitor[SStep] {
      override def enter(aNode: GTreeNode[SStep]) {
        val step = aNode.content
        println("enter: " + step)
      }

      override def leave(aNode: GTreeNode[SStep]) {
        val step = aNode.content
        println("leave: " + step)
      }
    })
    println("printFlow end: " + aRoot)
  }
}
