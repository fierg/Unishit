package de.unitrier.st.PII.ueb08

import scala.collection.mutable

abstract class Expression

case class VarExpr(varName: String) extends Expression
case class IntExpr(value: Int) extends Expression
case class PlusExpr(e1: Expression, e2: Expression) extends Expression
case class MultExpr(e1: Expression, e2: Expression) extends Expression
case class AssignExpr(e1: VarExpr, e2: Expression) extends Expression

object Expression {
  private val lookupTable = new LookupTable()

  def asString(e: Expression): String = e match {
    case v: VarExpr => v.varName
    case i: IntExpr => "" + i.value
    case p: PlusExpr => "(" + asString(p.e1) + " + " + asString(p.e2) + ")"
    case p: MultExpr => asString(p.e1) + " * " + asString(p.e2)
    //TODO: Aufgabe 1.1
    case a: AssignExpr => asString(a.e1) + " = " + asString(a.e2)
  }

  def eval(e: Expression): Int = e match {
    //TODO: Aufgabe 1.2
    case a: AssignExpr =>  lookupTable.add(a)
    case v: VarExpr =>  lookupTable.get(v.varName)
    case i: IntExpr =>  i.value
    case p: PlusExpr => eval(p.e1) + eval(p.e2)
    case m: MultExpr => eval(m.e1) * eval(m.e2)
    
  }

  private class LookupTable {
    val lookup = new mutable.HashMap[String, Int]

    def add(a:AssignExpr): Int = {
      val value = eval(a.e2)
      lookup += a.e1.varName -> value
      value
    }

    def get(varName:String): Int = lookup(varName)
  }

}

