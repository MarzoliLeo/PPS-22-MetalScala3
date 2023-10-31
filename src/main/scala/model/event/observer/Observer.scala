package model.event.observer

/** A trait that defines an observer that can observe a subject of type T.
  *
  * @tparam T
  *   the type of the subject that the observer will observe.
  */
trait Observer[T]:
  /** Updates the observer with the specified subject.
    *
    * @param subject
    *   the subject to update the observer with.
    */
  def update(subject: T): Unit
