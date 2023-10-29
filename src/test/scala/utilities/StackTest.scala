package utilities

import model.utilities.{Cons, Empty}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StackTest extends AnyFlatSpec with Matchers {
    "A Stack" should "push an element onto the top of the stack" in {
        val stack = Cons(1, Cons(2, Empty))
        stack.push(3) shouldEqual Cons(3, Cons(1, Cons(2, Empty)))
    }

    it should "pop the top element from the stack" in {
        val stack = Cons(1, Cons(2, Empty))
        stack.pop shouldEqual Some(Cons(2, Empty))
    }

    it should "return None when popping from an empty stack" in {
        val stack = Empty
        stack.pop shouldEqual None
    }

    it should "return the top element without modifying the stack" in {
        val stack = Cons(1, Cons(2, Empty))
        stack.peek shouldEqual Some(1)
        stack shouldEqual Cons(1, Cons(2, Empty))
    }

    it should "return None when peeking an empty stack" in {
        val stack = Empty
        stack.peek shouldEqual None
    }

    it should "return true when checking if the stack is empty" in {
        val stack = Empty
        stack.isEmpty shouldEqual true
    }
}