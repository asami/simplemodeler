package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.goldenport.sdoc.SDoc

/*
 * Oct. 12, 2008
 * Jan. 18, 2009
 */
class SMMultiplicity(val dslMultiplicity: SMultiplicity) {
  val kind: SMMultiplicityKind = dslMultiplicity match {
      case m: One => SMMultiplicityOne
      case m: ZeroOne => SMMultiplicityZeroOne
      case m: OneMore => SMMultiplicityOneMore
      case m: ZeroMore => SMMultiplicityZeroMore
      case _ => SMMultiplicityRange
  }
  val keywords = new ArrayBuffer[SMMultiplicityKeyword]

  final def symbol: String = {
    kind match {
      case m: SMMultiplicityOne => "1"
      case m: SMMultiplicityZeroOne => "?"
      case m: SMMultiplicityOneMore => "+"
      case m: SMMultiplicityZeroMore => "*"
      case _ => "???"
    }
  }

  final def text: String = {
    kind match {
      case m: SMMultiplicityOne => "1"
      case m: SMMultiplicityZeroOne => "0..1"
      case m: SMMultiplicityOneMore => "1..*"
      case m: SMMultiplicityZeroMore => "0..*"
      case _ => "???"
    }
  }
}

//
abstract class SMMultiplicityKind

class SMMultiplicityOne extends SMMultiplicityKind
object SMMultiplicityOne extends SMMultiplicityOne

class SMMultiplicityZeroOne extends SMMultiplicityKind
object SMMultiplicityZeroOne extends SMMultiplicityZeroOne

class SMMultiplicityOneMore extends SMMultiplicityKind
object SMMultiplicityOneMore extends SMMultiplicityOneMore

class SMMultiplicityZeroMore extends SMMultiplicityKind
object SMMultiplicityZeroMore extends SMMultiplicityZeroMore

class SMMultiplicityRange extends SMMultiplicityKind
object SMMultiplicityRange extends SMMultiplicityRange

//
abstract class SMMultiplicityKeyword(val name: String)

class SMMultiplicityOrdered extends SMMultiplicityKeyword("ordered")
object SMMultiplicityOrdered extends SMMultiplicityOrdered

class SMMultiplicityUnordered extends SMMultiplicityKeyword("unordered")
object SMMultiplicityUnordered extends SMMultiplicityUnordered

class SMMultiplicityUnique extends SMMultiplicityKeyword("unique")
object SMMultiplicityUnique extends SMMultiplicityUnique

class SMMultiplicityNonunique extends SMMultiplicityKeyword("nonunique")
object SMMultiplicityNonunique extends SMMultiplicityNonunique

class SMMultiplicitySet extends SMMultiplicityKeyword("set")
object SMMultiplicitySet extends SMMultiplicitySet

class SMMultiplicityBag extends SMMultiplicityKeyword("bag")
object SMMultiplicityBag extends SMMultiplicityBag

class SMMultiplicityOrderedSet extends SMMultiplicityKeyword("ordered set")
object SMMultiplicityOrderedSet extends SMMultiplicityOrderedSet

class SMMultiplicityList extends SMMultiplicityKeyword("list")
object SMMultiplicityList extends SMMultiplicityList

class SMMultiplicitySequence extends SMMultiplicityKeyword("sequence")
object SMMultiplicitySequence extends SMMultiplicitySequence
