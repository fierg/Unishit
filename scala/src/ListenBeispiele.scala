
object ListenBeispiele {

  def main(args:Array[String]): Unit = {
    println(length(List(1,2,3)));

    println(find(3,List(1,2,3)));

    println(find("a",List("b","c","a")));

    println(removeDuplicates(List(1,2,3,1,2,3,1,2,3)));
  }

  def length[A](list : List[A]) : Int =  list match {
   case _ :: tail => 1 + length(tail)
   case Nil => 0
  }

  def find[A](elem: A, list : List[A]) : Boolean =
    list match {
    case x::_ if x==elem => true
    case _::tail => find(elem,tail)
    case Nil => false
 }

 def removeDuplicates[A](list : List[A]) : List[A] =
   list match {
     case x::tail => if (find(x,tail)) removeDuplicates(tail)
                      else x::removeDuplicates(tail)
     case Nil => Nil
    }
 }