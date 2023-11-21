package model.utilities

import alice.tuprolog._

/**
  * Utilities for 2p usage in scala
  */
object Scala2P {
  def extractTerm(t: Term, i: scala.Int): Term = t.asInstanceOf[Struct].getArg(i).getTerm
  implicit def intToTerm(i: scala.Int): Term = Term.createTerm(i.toString)
  implicit def doubleToTerm(d: scala.Double): Term = Term.createTerm(d.toString)
  implicit def stringToTerm(s: String): Term = Term.createTerm(s)
  implicit def setToTerm[T](s: Set[T]): Term = s.mkString("[", ",", "]")
  implicit def tuple2Term(tuple2: (scala.Double, scala.Double)): Term = Term.createTerm(tuple2.toString())
  implicit def termToDouble(t: Term): scala.Double = t.toString.toDouble
}