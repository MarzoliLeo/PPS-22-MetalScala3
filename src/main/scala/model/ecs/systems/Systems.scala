package model.ecs.systems
import javafx.scene.input.KeyCode
import model.ecs.components.{Component, GravityComponent, PositionComponent}
import model.ecs.entities.{Entity, EntityManager, PlayerEntity}
import model.event.Event
import model.event.Event.Move
import model.event.observer.Observable
import model.inputsQueue
import model.utilities.Empty

import java.awt.Component

// TODO: apply DRY principle when possible
object Systems extends Observable[Event] {
  
  private def moveEntity(entity: Entity, dx: Int, dy: Int): Unit =
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]
    currentPosition.x match
      case x if x < 0 =>
        val pos = PositionComponent(0, currentPosition.y)
        entity.replaceComponent(pos)
        notifyObservers(Move(entity.id, pos))
      case x if x + 100 + model.INPUT_MOVEMENT_VELOCITY > model.GUIWIDTH =>
        val pos = PositionComponent(model.GUIWIDTH - 100 - model.INPUT_MOVEMENT_VELOCITY, currentPosition.y)
        entity.replaceComponent(pos)
        notifyObservers(Move(entity.id, pos))
      case _ => 
        val pos = PositionComponent(currentPosition.x + dx, currentPosition.y + dy)
        entity.replaceComponent(pos)
        notifyObservers(Move(entity.id, pos))


  val passiveMovementSystem: EntityManager => Unit = manager =>
    manager
      .getEntitiesWithComponent(classOf[PositionComponent])
      .foreach(entity => moveEntity(entity, 1, 0))

  val inputMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesByClass(classOf[PlayerEntity]).foreach { entity =>
      inputsQueue.peek match {
        case Some(command) =>
          command match {
            case KeyCode.W => moveEntity(entity, 0, -model.JUMP_MOVEMENT_VELOCITY)
            case KeyCode.A => moveEntity(entity, -model.INPUT_MOVEMENT_VELOCITY, 0)
            case KeyCode.S => moveEntity(entity, 0, model.INPUT_MOVEMENT_VELOCITY)
            case KeyCode.D => moveEntity(entity, model.INPUT_MOVEMENT_VELOCITY, 0)
          }
        case None => ()
      }
      inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }
  
  val gravitySystem: EntityManager => Unit =
    manager =>
      manager
        .getEntitiesWithComponent(classOf[PositionComponent], classOf[GravityComponent])
        .foreach( entity => 
          val currentPosition = entity
            .getComponent(classOf[PositionComponent])
            .get
            .asInstanceOf[PositionComponent]

          val gravityToApply = entity
            .getComponent(classOf[GravityComponent])
            .get
            .asInstanceOf[GravityComponent]

          currentPosition.y match
            case y if y < 0 =>
              val pos = PositionComponent(currentPosition.x, 0)
              entity.replaceComponent(pos)
              notifyObservers(Move(entity.id, pos))
            case y if y + 100 + gravityToApply.gravity > model.GUIHEIGHT=>
              val pos = PositionComponent(currentPosition.x, model.GUIHEIGHT - 100)
              entity.replaceComponent(pos)
              notifyObservers(Move(entity.id, pos))
            case _ =>
              val pos = PositionComponent(currentPosition.x, currentPosition.y + gravityToApply.gravity)
              entity.replaceComponent(pos)
              notifyObservers(Move(entity.id, pos))
        )

}
