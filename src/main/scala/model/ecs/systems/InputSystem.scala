package model.ecs.systems

import model.ecs.components.PlayerComponent
import model.ecs.entities.EntityManager
import model.inputsQueue
import model.utilities.Empty

trait InputSystem extends SystemWithoutTime

private class InputSystemImpl extends InputSystem:
  override def update(): Unit =
    EntityManager().getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
      entity =>
        inputsQueue.peek match
          case Some(command) => command(entity)
          case None => ()
        inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }

object InputSystem:
  def apply(): InputSystem = InputSystemImpl()
