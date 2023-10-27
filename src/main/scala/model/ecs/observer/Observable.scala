package model.ecs.observer

/** A trait that defines an observable object that can be observed by observers
  * of type [[Observer]] with type parameter T.
  *
  * @tparam T
  *   the type of the subject that observers will observe.
  */
trait Observable[T]:
  private var observers: List[Observer[T]] = List()

  /** Adds an observer to the list of observers.
    *
    * @param observer
    *   the observer to add.
    */
  def addObserver(observer: Observer[T]): Unit =
    observers = observer :: observers

  /** Removes an observer from the list of observers.
    *
    * @param observer
    *   the observer to remove.
    */
  def removeObserver(observer: Observer[T]): Unit =
    observers = observers.filterNot(_ == observer)

  /** Notifies all observers in the list of observers with the specified
    * subject.
    *
    * @param subject
    *   the subject to notify observers with.
    */
  def notifyObservers(subject: T): Unit =
    observers.foreach(_.update(subject))
