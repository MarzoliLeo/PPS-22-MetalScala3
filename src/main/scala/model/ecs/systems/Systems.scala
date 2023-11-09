package model.ecs.systems

import javafx.scene.input.KeyCode
import javafx.util.Pair
import model.ecs.components.*
import model.ecs.entities.{Entity, EntityManager, PlayerEntity}
import model.event.Event
import model.event.Event.Move
import model.event.observer.Observable
import model.inputsQueue
import model.utilities.Empty

// TODO: apply DRY principle when possible
object Systems extends Observable[Event]:

  private def updatePositionAndNotify(
      entity: Entity,
      positionComponent: PositionComponent,
      eventType: Event
  ): Unit = {
    entity.replaceComponent(positionComponent)
    notifyObservers(eventType)
  }

  private def moveEntity(entity: Entity, dx: Int, dy: Int): Unit = {
    val currentPosition: PositionComponent = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]

    val proposedPosition = currentPosition.x match {
      case x if x < 0 =>
        PositionComponent(0, currentPosition.y)

      case x if x + 100 + model.INPUT_MOVEMENT_VELOCITY > model.GUIWIDTH =>
        PositionComponent(
          model.GUIWIDTH - 100 - model.INPUT_MOVEMENT_VELOCITY,
          currentPosition.y
        )
      case _ =>
        PositionComponent(currentPosition.x + dx, currentPosition.y + dy)
    }

    if (!CollisionSystem.wouldCollide(entity, proposedPosition)) {
      updatePositionAndNotify(
        entity,
        proposedPosition,
        Move(entity.id, proposedPosition)
      )
    } else {
      // Handle the collision case here if needed
    }
  }

  val passiveMovementSystem: EntityManager => Unit = manager =>
    manager
      .getEntitiesWithComponent(classOf[PositionComponent])
      .foreach(entity => moveEntity(entity, 1, 0))

  val inputMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
      entity =>
        inputsQueue.peek match {
          case Some(command) =>
            command match {
              case KeyCode.W =>
                moveEntity(entity, 0, -model.JUMP_MOVEMENT_VELOCITY)
              case KeyCode.A =>
                moveEntity(entity, -model.INPUT_MOVEMENT_VELOCITY, 0)
              case KeyCode.S =>
                moveEntity(entity, 0, model.INPUT_MOVEMENT_VELOCITY)
              case KeyCode.D =>
                moveEntity(entity, model.INPUT_MOVEMENT_VELOCITY, 0)
            }
          case None => ()
        }
        inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }

  val gravitySystem: EntityManager => Unit =
      manager =>
        manager
          .getEntitiesWithComponent(
            classOf[PositionComponent],
            classOf[GravityComponent],
            classOf[ColliderComponent]
          )
          .foreach { entity =>
            val currentPosition = entity
              .getComponent(classOf[PositionComponent])
              .get
              .asInstanceOf[PositionComponent]

            val gravityToApply = entity
              .getComponent(classOf[GravityComponent])
              .get
              .asInstanceOf[GravityComponent]

            val newPosition = currentPosition.y match {
              case y if y < 0 =>
                PositionComponent(currentPosition.x, 0)
              case y if y + 100 + gravityToApply.gravity > model.GUIHEIGHT =>
                PositionComponent(currentPosition.x, model.GUIHEIGHT - 100)
              case _ =>
                PositionComponent(
                  currentPosition.x,
                  currentPosition.y + gravityToApply.gravity
                )
            }

            if (!CollisionSystem.wouldCollide(entity, newPosition)) {
              updatePositionAndNotify(
                entity,
                newPosition,
                Move(entity.id, newPosition)
              )
            }
          }
