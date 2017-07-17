package de.unitrier.st.PII.ueb08

import de.unitrier.st.PII.ueb08.ApplyFunctions._
import de.unitrier.st.PII.ueb08.Expression._

object Main {

  def main(args: Array[String]): Unit = {

    println("Aufgabe 1:\n")

    // 7 + 2 + x*3
    val myExpr = PlusExpr(IntExpr(7), PlusExpr(IntExpr(2), MultExpr(VarExpr("x"), IntExpr(3))))
    println(myExpr)
    println(asString(myExpr))
    println()

    // (2 + 3) * 10
    val myExpr2 = MultExpr(PlusExpr(IntExpr(2), IntExpr(3)), IntExpr(10))
    println(myExpr2)
    println(asString(myExpr2))
    println()

    println("----------\n")

    // x = y = 2
    val myAssign = AssignExpr(VarExpr("x"), AssignExpr(VarExpr("y"), IntExpr(2)))
    println(myAssign)
    println(asString(myAssign))
    println(eval(myAssign))
    println()

    // x = x + 1
    val myAssign2 = AssignExpr(VarExpr("x"), PlusExpr(VarExpr("x"), IntExpr(1)))
    println(myAssign2)
    println(asString(myAssign2))
    println(eval(myAssign2))
    println()

    // 7 + 2 + x * 3 = 7 + 2 + 3 * 3 = 18
    println(myExpr)
    println(asString(myExpr))
    println(eval(myExpr))
    println()


    println("\nAufgabe 2:\n")

    val a1 = Array(1, 2, 3, 4)
    val a2 = Array(1, 4, 5, 6, 7, 2, 8)
    val a3 = Array("abc", "hugo", "Scala", "a")

    println(apply((_: Int) + (_: Int), a1))
    println(apply((_: Int) * (_: Int), a1))
    println(apply((_: Int) + (_: Int), a2))
    println(apply((_: Int) * (_: Int), a2))
    println()

    def shorter(s1: String, s2: String) = if (s1.length < s2.length) s1 else s2
    def longer(s1: String, s2: String) = if (s1.length < s2.length) s2 else s1

    println(apply(shorter, a3))
    println(apply(longer, a3))
    println()

    val l1 = List(1, 2, 3, 4)
    val l2 = List(1, 4, 5, 6, 7, 2, 8)
    val l3 = List("abc", "hugo", "Scala", "a")

    println(applyRecursive((_: Int) + (_: Int), 1::2::3::4::Nil))
    println(applyRecursive((_: Int) + (_: Int), l1))
    println(applyRecursive((_: Int) * (_: Int), l1))
    println(applyRecursive((_: Int) + (_: Int), l2))
    println(applyRecursive((_: Int) * (_: Int), l2))
    println()

    println(applyRecursive(shorter, l3))
    println(applyRecursive(longer, l3))
    println()

    println(applyRecursive((_: Int) + (_: Int), List()))  // IllegalArgumentException
  }

}
