package de.unitrier.st.PII.ueb08

object ApplyFunctions {

  def apply[T](merge: (T, T) => T, array: Array[T]): T = {
    //TODO: Aufgabe 2.1
    var a = array(0)
    for (i <- 1 to array.length - 1) {
      a = merge(a, array(i))
    }
    return a
  }

  //TODO: Aufgabe 2.2 (applyRecursive)
  def applyRecursive[T](merge: (T, T) => T, list: List[T]): T = list match {
    case head :: Nil => head
    case head :: tail => merge(head, applyRecursive(merge, tail))
    case Nil => throw new IllegalArgumentException("xxx")

  }

}