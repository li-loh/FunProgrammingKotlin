
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private sealed class List<out A> {
  companion object {
    fun <A> of(vararg aa: A): List<A> {
      val tail = aa.sliceArray(1 until aa.size)
      return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
    }

    fun <A> append(a1: List<A>, a2: List<A>): List<A> =
      when (a1) {
        is Nil -> a2
        is Cons -> Cons(a1.head, append(a1.tail, a2))
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
})