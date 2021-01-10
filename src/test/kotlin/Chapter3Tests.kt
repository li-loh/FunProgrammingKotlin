
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private sealed class List<out A> {
  companion object {
    fun <A> of(vararg aa: A): List<A> {
      val tail = aa.sliceArray(1 until aa.size)
      return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
    }

    fun <A> empty(): List<A> = Nil

    fun <A> append(a1: List<A>, a2: List<A>): List<A> =
      when (a1) {
        is Nil -> a2
        is Cons -> Cons(a1.head, append(a1.tail, a2))
      }

    fun <A, B> foldRight(xs: List<A>, z: B, f: (A,B) -> B): B =
      when (xs) {
        is Nil -> z
        is Cons -> f(xs.head, foldRight(xs.tail, z, f))
        // {1,2,3}, 0, a - b
        // f(1, foldRight( {2,3}, 0, f)
        // f(1, f(2, foldRight( {3}, 0, f)
        // f(1, f(2, f(3, foldRight ({}, 0, f)
        // f(1, f(2, f(3, 0)))
        // f(1, f(2, 3))
        // f(1, -1)
        // 2
      }

    // implementation exercise 3.1
    fun <A> tail(xs: List<A>): List<A> =
      when (xs) {
        is Cons -> xs.tail
        is Nil -> throw IllegalStateException("Nil cannot have a tail")
      }

    // implementation exercise 3.2
    fun <A> setHead(xs: List<A>, x: A): List<A> =
      when (xs) {
        is Cons -> Cons(x, xs.tail)
        is Nil -> throw IllegalStateException("Cannot set a head on Nil")
      }

    // implementation exercise 3.3
    fun <A> drop(l: List<A>, n: Int): List<A> =
      if (n == 0) {
        l
      }else{
        when (l) {
          is Nil ->  throw IllegalStateException("Cannot drop on Nil")
          is Cons -> drop(l.tail, n - 1)
        }
      }

    // implementation exercise 3.4
    fun <A> dropWhile(l: List<A>, f: (A) -> Boolean): List<A> =
      when (l) {
        is Cons ->
          if (f(l.head)) {
            l.tail
          }else {
            l
          }
        is Nil -> Nil
      }

    // implementation exercise 3.5
    fun <A> init(l: List<A>): List<A> =
      when (l) {
        is Nil -> throw IllegalStateException("Cannot Init on Nil")
        is Cons ->
          if (l.tail == Nil) {
            Nil
          }else {
            Cons(l.head, init(l.tail))
          }
      }

    // implementation exercise 3.8
    fun <A> length(xs: List<A>): Int =
      foldRight(xs, 0, { _, b -> 1 + b})

    // implementation exercise 3.9
    tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B =
      when (xs) {
        is Nil -> z
        //is Cons -> f(foldLeft(xs.tail, z, f), xs.head)
        is Cons -> foldLeft(xs.tail,f(z, xs.head) ,f)
        // {1,2,3}, 0, a - b
        // foldLeft( {2,3}, 0 - 1, a - b)
        // foldLeft( {3}, -1-2, a - b)
        // foldLeft( {}, -3-3, a - b)
        // -6
      }

    // implementation exercise 3.10
    fun <A> sum(ints: List<Int>): Int =
      foldLeft(ints, 0, { a, b -> a + b })

    // implementation exercise 3.10
    fun <A> product(dbs: List<Double>): Double =
      foldLeft(dbs, 1.0, { a, b -> a * b })

    // implementation exercise 3.10
    fun <A> length2(xs: List<A>): Int =
      foldLeft(xs, 0, { a, _ -> a + 1})

    // implementation exercise 3.11
    fun <A> reverse(xs: List<A>): List<A> =
      foldLeft(xs, empty(), { a, b -> Cons(b,a) })
      // {1,2,3}
      // foldLeft(xs.tail,f(z, xs.head) ,f)
      // foldLeft({1,2,3},{},f)
      // foldLeft({2,3}, f({},1), f)
      // foldLeft({2,3}, {1}, f)
      // foldLeft({3}, f({1}, 2), f)
      // foldLeft({}, f({2,1}, 3), f)
      // foldLeft({}, {3,2,1}, f)
      // {3,2,1}

    // implementation exercise 3.12
    fun <A, B> foldright2(xs: List<A>, z: B, f: (B, A) -> B): B =
      foldLeft(xs, z, f)

    // implementation exercise 3.13
    fun<A> append2(a1: List<A>, a2: List<A>): List<A> =
      // TODO there must be a way to do this without first reversing...
      //foldLeft(reverse(a1), a2, { a, b -> Cons(b,a) })
      // answer from book:
       foldRight(a1, a2, { x, y -> Cons(x, y) })

      // foldLeft(xs.tail,f(z, xs.head) ,f)
      // {1,2}, {3,4}
      // foldLeft({1,2}, {3,4}, f)
      // foldLeft({2}, f({3,4}, 1), f)
      // foldLeft({2}, {1,3,4}, f)
      // foldLeft({}, f({1,3,4},2), f)
      // foldLeft({}, {2,1,3,4}, f)

    // implementation exercise 3.14
    fun<A> concatenate(xxs: List<List<A>>): List<A> =
      foldLeft(xxs, empty(), { xs1:List<A>, xs2:List<A> -> append2(xs1,xs2)})

    // implement exercise 3.15
    fun transform(xs: List<Int>): List<Int> =
      foldRight(xs, empty(), { x: Int, ls -> Cons(x + 1, ls)})
      // why does foldLeft return a list that is reversed? It is because
      // it transform the first element first and pushes it into the list first
      // so it ends up with a reversed list. Whereas foldRight transform and pushes
      // the last element in the list first so the list is in the expected order.
      //foldLeft(xs, empty(), { ls: List<Int>, x -> Cons(x + 1, ls)})
  }
}
private object Nil: List<Nothing>()
private data class Cons<out A>(val head: A, val tail: List<A>): List<A>()

class Chapter3Tests : FunSpec({
  context("Exercise 3.1 - implement the function tail for removing" +
    " the first element of a List") {

    test("tail() should remove the first element of the list") {
      List.tail(List.of(1,2,3,4,5)) shouldBe List.of(2,3,4,5)
      List.tail(List.of("aa", "bb", "dd", "cc")) shouldBe List.of("bb","dd","cc")
      List.tail(List.of("aa")) shouldBe Nil
    }

    test("tail() should throw an exception when performed on an empty list ") {
      shouldThrow<IllegalStateException> {
        List.tail(Nil)
      }
    }
  }

  context("Exercise 3.2 - implement the function setHead for replacing" +
    " the first element of a List with a different value") {

    test("setHead() should replace the first element in the list") {
      List.setHead(List.of(1,2,3,4,5), 6) shouldBe List.of(6,2,3,4,5)
      List.setHead(List.of("aa", "bb", "dd", "cc"),"ee") shouldBe List.of("ee","bb","dd","cc")
    }

    test("setHead() should throw exception when the list is empty") {
      shouldThrow<IllegalStateException> {
        List.setHead(Nil, 1)
      }
    }
  }

  context("Exercise 3.3 - Generalise tail to the function drop, " +
    "which removes the first n elements from a list") {

    test("drop") {
      List.drop(List.of(1,2,3,4,5),3) shouldBe List.of(4,5)
      List.drop(List.of("aa", "bb", "dd", "cc"),3) shouldBe List.of("cc")
      List.drop(List.of("aab"),1) shouldBe Nil
    }

    test("drop() should thrown an exception when the list is empty") {
      shouldThrow<IllegalStateException> {
        List.drop(Nil, 1)
      }
    }

    test("drop() should thrown an exception when the number of elements to drop is more " +
      "than the size of the list") {
      shouldThrow<IllegalStateException> {
        List.drop(List.of("aa"), 2)
      }
    }
  }

  context("Exercise 3.4 - implement dropWhile which removes elements" +
    " from the List prefix as long as they match a predicate") {

    test("dropWhile() should remove prefix from the list when matches predicate") {
      List.dropWhile(List.of(1,2,3,4,5)) { x -> x > 0 } shouldBe List.of(2,3,4,5)
      List.dropWhile(List.of("aa", "bb", "dd", "cc")) { x -> x == "aa" } shouldBe List.of("bb","dd","cc")
    }

    test("dropWhile() should not remove prefix from the list when it does not match predicate") {
      List.dropWhile(List.of("aa", "bb", "dd", "cc")) { x -> x == "ee" } shouldBe List.of("aa","bb","dd","cc")
    }

    test("dropWhile() on an empty list should return an empty list") {
      List.dropWhile(Nil) { x -> x == 1 } shouldBe Nil
    }
  }

  context("Exercise 3.5 - implement init that returns a List consisting of all but the last element" +
    " of a list") {

    test("init() should return all but the last element from the list") {
      List.init(List.of(1)) shouldBe Nil
      List.init(List.of("aa", "bb", "dd", "cc")) shouldBe List.of("aa","bb","dd")
    }

    test("init() should throw exception when given an empty list") {
      shouldThrow<IllegalStateException> {
        List.init(Nil)
      }
    }
  }

  context("Exercise 3.7 - See what happens when you pass Nil and Cons themselves to foldRight") {


    test("Verify that this behaves just like the constructor of List ") {
      val result = List.foldRight(List.of(1,2,3), List.empty<Int>(), { x, y -> Cons(x,y)})
      println(result)
    }
  }

  context("Exercise 3.8 - Compute the length of a list using foldRight") {

    test("Length of the list") {
      List.length(List.of(1,2,3,6)) shouldBe 4
      List.length(List.of("as","bd")) shouldBe 2
    }

    test("Length of the list is 0 when the list is empty") {
      List.length(List.empty<Int>()) shouldBe 0
    }
  }

  context("Exercise 3.9 - write foldLeft that is tail recursive ") {
    test("verifying how foldRight works vs foldLeft") {
      List.foldRight(List.of(1,2,3,6), 0, { a,b -> a + b }) shouldBe 12
      List.foldRight(List.of(1,2,3), 0, { a,b -> a - b }) shouldBe 2
    }

    test("foldLeft") {
      List.foldLeft(List.of(1,2,3,6), 0, { a,b -> a + b }) shouldBe 12
      List.foldLeft(List.of(1,2,3), 0, { a,b -> a - b }) shouldBe -6
    }

    test("foldLeft on an empty list should return the z value passed in") {
      List.foldLeft(List.empty<Int>(), 0, { a,b -> a + b }) shouldBe 0

    }
  }

  context("Exercise 3.10 - sum, product, length of a list using foldLeft") {

    test("sum") {
      List.sum<Int>(List.of(1,2,3,6)) shouldBe 12
    }

    test("product") {
      List.product<Int>(List.of(4.0,2.0,3.0)) shouldBe 24.0
    }

    test("foldLeft on an empty list should return the z value passed in") {
      List.foldLeft(List.empty<Int>(), 0, { a,b -> a + b }) shouldBe 0
    }

    test("Length of the list") {
      List.length2(List.of(1,2,3,6)) shouldBe 4
      List.length2(List.of("as","bd")) shouldBe 2
    }

    test("Length of the list is 0 when the list is empty") {
      List.length2(List.empty<Int>()) shouldBe 0
    }
  }

  context("Exercise 3.11 - return the reverse of a list using a fold") {
    test("reverse of a list should return a reversed list") {
      List.reverse(List.of(1,2,3,4,5)) shouldBe List.of(5,4,3,2,1)
      List.reverse(List.of("cc", "bb", "aa")) shouldBe List.of("aa", "bb", "cc")
    }

    test("reverse of an empty list should return an empty list") {
      List.reverse(List.empty<Int>()) shouldBe List.empty()
    }
  }

  context("Exercise 3.13 - implement append in terms of either foldLeft or foldRight") {
    test("appending 2 lists together should create a single list with items in those 2 lists") {
      List.append2(List.of(1,2), List.of(3,4)) shouldBe List.of(1,2,3,4)
    }

    test("appending an empty list to a non empty list should return the non empty list") {
      List.append2(List.empty(), List.of(3,4)) shouldBe List.of(3,4)
    }

    test("appending an empty list to an empty list should return an empty list") {
      List.append2(List.empty<Int>(), List.empty<Int>()) shouldBe List.empty()
    }
  }

  context("Exercise 3.14 - concatenates a list of lists into a single list") {
    test("concatenating a list of lists together should create a single list") {
      List.concatenate(List.of(List.of(1,2), List.of(3,4), List.of(5,6))) shouldBe List.of(1,2,3,4,5,6)
    }

    test("concatenating a list of empty lists should return the non empty list") {
      List.concatenate(List.of(List.empty<Int>(), List.empty(), List.empty())) shouldBe List.empty()
    }
  }

  context("Exercise 3.15 - Transform a list of integers by adding 1 to each element") {
    test("Transforms a list of transformed integers by adding 1 to each element") {
      List.transform(List.of(1,2,3,20,5,10)) shouldBe List.of(2,3,4,21,6,11)
    }

    test("Transform an empty list returns an empty list ") {
      List.concatenate(List.of(List.empty<Int>())) shouldBe List.empty()
    }
  }
})