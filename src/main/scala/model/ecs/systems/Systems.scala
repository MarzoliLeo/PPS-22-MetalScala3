package model.ecs.systems
import javafx.scene.input.KeyCode
import model.ecs.components.{Component, PositionComponent}
import model.ecs.entities.{BoxEntity, Entity, EntityManager, PlayerEntity}
import model.inputsQueue
import model.utilities.Empty

object Systems {

  private def moveEntity(entity: Entity, dx: Int, dy: Int): Unit = {
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .getOrElse(PositionComponent(0, 0))
      .asInstanceOf[PositionComponent]
    entity.replaceComponent(
      PositionComponent(currentPosition.x + dx, currentPosition.y + dy)
    )
  }

  val passiveMovementSystem: EntityManager => Unit = manager =>
    manager
      .getEntitiesByClass(classOf[BoxEntity])
      .foreach(entity => moveEntity(entity, 1, 0))

  val inputMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesByClass(classOf[BoxEntity]).foreach { entity =>
      inputsQueue.peek match {
        case Some(command) =>
          command match {
            case KeyCode.W => moveEntity(entity, 0, -1)
            case KeyCode.A => moveEntity(entity, -1, 0)
            case KeyCode.S => moveEntity(entity, 0, 1)
            case KeyCode.D => moveEntity(entity, 1, 0)
          }
        case None => ()
      }
      inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }
}
