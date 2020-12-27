import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// Used in Exercise 2.2
val <T> List<T>.tail: List<T> get() = drop(1)
val <T> List<T>.head: T get() = first()

class Chapter2Tests : FunSpec({

  context("Exercise 2.1 - fibonacci numbers") {

    // implementation
     fun fib(i: Int): Int {
      tailrec fun go (n: Int, s0: Int, s1: Int): Int {
        if (n == 1) {
          return s0;
        }
        return go(n - 1, s1, s0 + s1)
      }

      return go(i, 0, 1)
    }

    test("fibonacci sequence") {
      fib(1) shouldBe 0
      fib(2) shouldBe 1
      fib(3) shouldBe 1
      fib(4) shouldBe 2
      fib(5) shouldBe 3
      fib(10) shouldBe 34
    }
  }

  context("Exercise 2.2 - implement isSorted which checks whether a singly linked list" +
    " is sorted according to a given comparison function") {

    // implementation
    fun <A> isSorted(aa: List<A>, order: (A, A) -> Boolean): Boolean {

      // Using recursive loops
      // tailrec fun loop(n: Int): Boolean =
      //   when {
      //     n == aa.size - 1 -> true
      //     !order(aa[n], aa[n+ 1]) -> false
      //     else -> loop(n + 1)
      //   }
      //
      // return loop(0)
      tailrec fun go(item: A, remainder: List<A>): Boolean =
        when {
          remainder.size == 0 -> true
          !order(item, remainder.head) -> false
          else -> go(remainder.head, remainder.tail)
        }
      return aa.isEmpty() || go(aa.head, aa.tail)
    }

    test("isSorted") {
      isSorted(listOf("aa", "bb", "cc")) { x: String, y: String -> x < y } shouldBe true
      isSorted(listOf(4, 5, 6)) { x: Int, y: Int -> x < y } shouldBe true
      isSorted(listOf("aa", "dd", "cc")) { x: String, y: String -> x < y } shouldBe false
      isSorted(listOf(9, 5, 6)) { x: Int, y: Int -> x < y } shouldBe false
      isSorted(listOf()) { x: Int, y: Int -> x < y } shouldBe true
      isSorted(listOf(1)) { x: Int, y: Int -> x < y } shouldBe true
    }
  }

  context("Exercise 2.3 - currying: converts a function f of two arguments " +
    "into a function of one argument that partially applies f") {

    fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C =
      { a: A -> {b: B -> f(a,b)} }

    test("curry") {
      curry { i: Int, j: Int -> i == j } shouldBe true
    }
  }

  context("Exercise 2.4 - uncurrying: reverses the transformation of curry") {

    fun <A, B, C> uncurry(f: (A) -> (B) -> (C)): (A, B) -> C =
      { a: A, b: B -> f(a)(b) }
  }

  context("Exercise 2.5 - higher order function that composes two functions") {

    fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C =
      { a:A -> f(g(a)) }
  }

})