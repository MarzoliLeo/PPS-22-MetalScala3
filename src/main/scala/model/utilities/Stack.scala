package model.utilities

sealed trait Stack[+T] {
  /**
   * push an element onto the stack
   * @param u the element to push
   * @tparam U the type of the element
   * @return the new stack with the element on top
   */
  def push[U >: T](u: U): Stack[U] = Cons(u, this)

  /** pop the top element off the stack
    * @return
    *   the new stack without the top element
    */
  def pop: Option[Stack[T]] = this match {
    case Empty         => None
    case Cons(_, tail) => Some(tail)
  }

  /**
   * peek at the top element of the stack
   * @return the top element of the stack
   */
  def peek: Option[T] = this match {
    case Empty         => None
    case Cons(head, _) => Some(head)
  }

  /**
   * check if the stack is empty
   * @return true if the stack is empty, false otherwise
   */
  def isEmpty: Boolean = this match {
    case Empty => true
    case _     => false
  }
}

case object Empty extends Stack[Nothing]
case class Cons[T](head: T, tail: Stack[T]) extends Stack[T]
