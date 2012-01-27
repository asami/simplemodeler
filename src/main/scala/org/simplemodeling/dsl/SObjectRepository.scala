package org.simplemodeling.dsl

import scala.collection.mutable._
import java.util.Locale
import org.goldenport.sdoc._

/*
 * Sep. 10, 2008
 * Dec. 22, 2008
 */
class SObjectRepository {
  private val _objects = new HashMap[String, SObject]

  final def register(anObject: SObject): Boolean = {
    val qName = anObject.qualifiedName
//    print("SObjectRepository.register ( " + qName + ") = ")
    if (_objects.contains(qName)) {
//      println("false")
      false
    } else {
      _objects.put(qName, anObject)
//      println("true")
      true
    }
  }

  final def getObject(qName: String): SObject = {
    _objects.get(qName) match {
      case Some(obj: SObject) => obj
      case None => error("no object = " + qName)
    }
  }

/*
  final def isStub(anObject: SObject) = {
    _objects.get((anObject.name, anObject.packageName)) match {
      case Some(obj: SObject) => !(anObject eq obj)
      case None => false
    }
  }
*/
}

object SObjectRepository extends SObjectRepository
