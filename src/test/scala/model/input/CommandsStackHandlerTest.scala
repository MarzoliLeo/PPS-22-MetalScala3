package model.input

import model.ecs.entities.Entity
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import model.inputsQueue

class CommandsStackHandlerTest
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {

  private var handler: CommandsStackHandler = _

  override def beforeEach(): Unit = {
    handler = new CommandsStackHandler {}
    while (!inputsQueue.isEmpty)
      inputsQueue = inputsQueue.pop.get
  }

  "A CommandsStackHandler" should "push a command onto the top of the stack" in {
    val testCommand: Entity => Unit = _ => ()
    handler.handleInput(testCommand)

    inputsQueue.peek shouldEqual Some(testCommand)
  }

  it should "push multiple commands onto the stack in the correct order" in {
    val firstTestCommand: Entity => Unit = _ => ()
    val secondTestCommand: Entity => Unit = _ => ()

    handler.handleInput(firstTestCommand)
    handler.handleInput(secondTestCommand)

    inputsQueue.peek shouldEqual Some(secondTestCommand)
    inputsQueue = inputsQueue.pop.get
    inputsQueue.peek shouldEqual Some(firstTestCommand)
  }
}
