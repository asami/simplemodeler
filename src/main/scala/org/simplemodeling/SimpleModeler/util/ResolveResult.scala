package org.simplemodeling.SimpleModeler.util

import scalaz._, Scalaz._

/*
 * @since   Nov. 18, 2012
 * @version May. 29, 2016
 * @author  ASAMI, Tomoharu
 */
sealed trait ResolveResult { // XXX more suitable name
  self =>

  type MSG = ResolveMessage

  def isEmpty: Boolean
  def map(f: MSG => MSG): ResolveResult
  def flatMap(f: MSG => ResolveResult): ResolveResult
  def filter(p: MSG => Boolean): ResolveResult
  def filterNot(p: MSG => Boolean): ResolveResult
  def withFilter(p: MSG => Boolean): WithFilter = new WithFilter(p)
  def foreach[U](f: MSG => U): Unit
  def join(a: ResolveResult): ResolveResult

  class WithFilter(p: MSG => Boolean) {
    def map[B](f: MSG => MSG): ResolveResult = self filter p map f
    def flatMap[B](f: MSG => ResolveResult): ResolveResult = self filter p flatMap f
    def foreach[U](f: MSG => U): Unit = self filter p foreach f
    def withFilter(q: MSG => Boolean): WithFilter = new WithFilter(x => p(x) && q(x))
  }
}

case object ResolveSuccess extends ResolveResult {
  def isEmpty: Boolean = true
  def map(f: MSG => MSG): ResolveResult = this
  def flatMap(f: MSG => ResolveResult): ResolveResult = this
  def filter(p: MSG => Boolean): ResolveResult = this
  def filterNot(p: MSG => Boolean): ResolveResult = this
  def foreach[U](f: MSG => U): Unit = {}
  def join(a: ResolveResult): ResolveResult = a
}

case class ResolveError(errors: Seq[ResolveMessage]) extends ResolveResult {
  require (errors.nonEmpty)
  def isEmpty: Boolean = true
  def map(f: MSG => MSG): ResolveResult = {
    ResolveError(errors.map(f))
  }
  def flatMap(f: MSG => ResolveResult): ResolveResult = {
    val a = errors.flatMap(x => {
      f(x) match {
        case ResolveSuccess => Nil
        case e: ResolveError => e.errors
      }
    })
    ResolveResult(a)
  }
  def filter(p: MSG => Boolean): ResolveResult =
    ResolveResult(errors.filter(p))
  def filterNot(p: MSG => Boolean): ResolveResult =
    ResolveResult(errors.filterNot(p))
  def foreach[U](f: MSG => U): Unit = errors.foreach(f)
  def join(a: ResolveResult): ResolveResult = {
    a match {
      case ResolveSuccess => this
      case e: ResolveError => ResolveResult(errors ++ e.errors)
    }
  }
}

sealed trait ResolveMessage {
  def qname: String
}

case class ResolveNotMatch(qname: String, notmatch: String) extends ResolveMessage {
}

case class ResolveNotFound(qname: String) extends ResolveMessage {
}

trait ResolveResults {  
  // implicit def ResolveResultZero: Zero[ResolveResult] = zero(ResolveSuccess)
  // implicit def ResolveResultSemigroup: Semigroup[ResolveResult] = semigroup((a, b) => a.join(b))
  implicit def ResolveResultMonoid: Monoid[ResolveResult] = new Monoid[ResolveResult] {
    def zero = ResolveSuccess
    def append(lhs: ResolveResult, rhs: => ResolveResult): ResolveResult = lhs.join(rhs)
  }
}  

object ResolveResult extends ResolveResults {
  def apply(a: Seq[ResolveMessage]) = {
    if (a.isEmpty) ResolveSuccess
    else ResolveError(a)
  }

  def notMatch(qname: String, notmatch: String) =
    ResolveError(Seq(ResolveNotMatch(qname, notmatch)))

  def notFound(qname: String) =
    ResolveError(Seq(ResolveNotFound(qname)))
}
