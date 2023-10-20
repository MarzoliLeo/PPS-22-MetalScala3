enum ViewAction:
  case Start
  case Exit

trait Observer:
    def update[T](event: T): Unit

class ViewController extends Observer:
    def update[T](event: T): Unit = event match
        case ViewAction.Start => println("View started")
        case ViewAction.Exit => println("View exited")