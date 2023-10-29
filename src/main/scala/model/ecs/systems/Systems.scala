package model.ecs.systems

import javafx.scene.input.KeyCode
import model.ecs.components.{Component, PositionComponent}
import model.ecs.entities.{BoxEntity, EntityManager, PlayerEntity}
import model.utilities.Empty
import model.inputsQueue

object Systems {

  val passiveMovementSystem: EntityManager => Unit =
    manager =>
      manager
        .getEntitiesByClass(classOf[BoxEntity])
        .foreach(entity => {
          val currentPosition: PositionComponent = entity
            .getComponent(classOf[PositionComponent])
            .getOrElse(PositionComponent(0, 0))
            .asInstanceOf[PositionComponent]
          // for immutability
          entity.replaceComponent(
            PositionComponent(currentPosition.x + 1, currentPosition.y)
          )
        })
  // a system which replaces the position of all box entities with a new position
  // based on the command on the top of inputsQueue
  val inputMovementSystem: EntityManager => Unit =
    manager =>
      manager
        .getEntitiesByClass(classOf[BoxEntity])
        .foreach(entity => {
          val currentPosition: PositionComponent = entity
            .getComponent(classOf[PositionComponent])
            .getOrElse(PositionComponent(0, 0))
            .asInstanceOf[PositionComponent]
          // right now the Box is sliding in the same direction because the head
          // of the Stack is never removed... could be useful
          inputsQueue.peek match {
            case Some(command) =>
              command match
                case KeyCode.W =>
                  entity.replaceComponent(
                    PositionComponent(currentPosition.x, currentPosition.y - 1)
                  )
                case KeyCode.A =>
                  entity.replaceComponent(
                    PositionComponent(currentPosition.x - 1, currentPosition.y)
                  )
                case KeyCode.S =>
                  entity.replaceComponent(
                    PositionComponent(currentPosition.x, currentPosition.y + 1)
                  )
                case KeyCode.D =>
                  entity.replaceComponent(
                    PositionComponent(currentPosition.x + 1, currentPosition.y)
                  )
            case None => ()
          }
          inputsQueue = inputsQueue.pop.getOrElse(Empty)
        })
}
