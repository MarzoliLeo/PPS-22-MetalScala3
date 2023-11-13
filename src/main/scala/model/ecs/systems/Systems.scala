package model.ecs.systems

import javafx.scene.input.KeyCode
import model.ecs.components.*
import model.ecs.entities.{BulletEntity, Entity, EntityManager, PlayerEntity}
import model.ecs.systems.CollisionSystem.{MovementAxis, wouldCollide}
import model.ecs.systems.Systems.shootBullet
import model.event.Event
import model.event.Event.*
import model.event.observer.Observable
import model.input.commands.*
import model.utilities.Empty
import model.{GRAVITY_VELOCITY, JUMP_DURATION, inputsQueue}

import java.awt.Component

object Systems extends Observable[Event]:

  /** Applies a boundary check to a position value, ensuring it stays within the
    * bounds of the system.
    *
    * @param pos
    *   The position value to check.
    * @param max
    *   The maximum value allowed for the position.
    * @param size
    *   The size of the object being checked.
    * @return
    *   The new position value after the boundary check has been applied.
    */
  def boundaryCheck(pos: Double, max: Double, size: Double): Double =
    math.max(0.0, math.min(pos, max - size))

  val bulletMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesByClass(classOf[BulletEntity]).foreach { bullet =>
      val pos = bullet
        .getComponent(classOf[PositionComponent])
        .get
        .asInstanceOf[PositionComponent]
      val vel = bullet
        .getComponent(classOf[VelocityComponent])
        .get
        .asInstanceOf[VelocityComponent]
      val nextPos = PositionComponent(pos.x + vel.x, pos.y + vel.y)

      val currentSprite = bullet
        .getComponent(classOf[SpriteComponent])
        .getOrElse(SpriteComponent(List("sprites/MarcoRossi.png")))
        .asInstanceOf[SpriteComponent]

      if currentSprite.spritePath.nonEmpty then
        bullet.replaceComponent(nextPos)
        notifyObservers(
          Move(
            bullet.id,
            currentSprite,
            nextPos,
            model.MOVEMENT_DURATION
          )
        )
    }

  private def shootBullet(shooter: Entity, manager: EntityManager): Unit =
    val pos = shooter
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]
    val dir = shooter
      .getComponent(classOf[DirectionComponent])
      .get
      .asInstanceOf[DirectionComponent]
    val xVelocity = dir.d match
      case RIGHT => 20
      case LEFT  => -20
    manager.addEntity(
      BulletEntity()
        .addComponent(PositionComponent(pos.x, pos.y))
        .addComponent(VelocityComponent(xVelocity, 0))
    )

  val inputMovementSystem: Long => Unit = elapsedTime =>
    EntityManager().getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
      entity =>
        inputsQueue.peek.foreach {
          case KeyCode.W =>
            JumpCommand(model.JUMP_DURATION).execute(entity, elapsedTime)
          case KeyCode.A =>
            MoveCommand(-model.INPUT_MOVEMENT_VELOCITY, 0)
              .execute(entity, elapsedTime)
          case KeyCode.S =>
            MoveCommand(0, model.INPUT_MOVEMENT_VELOCITY)
              .execute(entity, elapsedTime)
          case KeyCode.D =>
            MoveCommand(model.INPUT_MOVEMENT_VELOCITY, 0)
              .execute(entity, elapsedTime)
          case KeyCode.SPACE => ShootCommand().execute(entity, elapsedTime)
          case _             => InvalidCommand.execute(entity, elapsedTime)
        }
        inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }

  val gravitySystem: Long => Unit = elapsedTime =>
    if (model.isGravityEnabled) {
      EntityManager()
        .getEntitiesWithComponent(
          classOf[PositionComponent],
          classOf[GravityComponent],
          classOf[ColliderComponent],
          classOf[VelocityComponent]
        )
        .foreach { entity =>
          val currentPosition = entity
            .getComponent(classOf[PositionComponent])
            .get
            .asInstanceOf[PositionComponent]
          val collider = entity
            .getComponent(classOf[ColliderComponent])
            .get
            .asInstanceOf[ColliderComponent]
          var velocity = entity
            .getComponent(classOf[VelocityComponent])
            .get
            .asInstanceOf[VelocityComponent]

          // Check if the entity is colliding below
          val collidingBelow =
            currentPosition.y + collider.size.height >= model.GUIHEIGHT

          // Set the gravity based on whether the entity is colliding below
          val gravity = if (collidingBelow) 0 else model.GRAVITY_VELOCITY

          // Update the vertical velocity
          velocity = VelocityComponent(
            velocity.x,
            velocity.y + gravity * elapsedTime * 0.001
          )

          // If the entity is at the bottom of the window, stop the fall
          if (collidingBelow) {
            velocity = VelocityComponent(velocity.x, 0)
          }

          entity.replaceComponent(velocity)
        }
    }

  val FRICTION_FACTOR = 0.50 // Define a friction factor between 0 and 1
  val VERTICAL_REDUCTION_FACTOR =
    0.99 // Define a reduction factor for negative vertical velocity

  val positionUpdateSystem: Long => Unit = elapsedTime =>
    EntityManager()
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[VelocityComponent]
      )
      .foreach { entity =>
        val currentPosition = entity
          .getComponent(classOf[PositionComponent])
          .get
          .asInstanceOf[PositionComponent]
        var velocity = entity
          .getComponent(classOf[VelocityComponent])
          .get
          .asInstanceOf[VelocityComponent]

        // Calculate the new position based on the velocity and elapsed time
        val newPosition = PositionComponent(
          currentPosition.x + velocity.x * elapsedTime * 0.001,
          currentPosition.y + velocity.y * elapsedTime * 0.001
        )
        entity.replaceComponent(newPosition)

        // Reduce the horizontal velocity by the friction factor
        val newHorizontalVelocity = velocity.x * FRICTION_FACTOR

        // Reduce the vertical velocity by the reduction factor if it's negative
        val newVerticalVelocity =
          if (velocity.y < 0) velocity.y * VERTICAL_REDUCTION_FACTOR
          else velocity.y

        velocity = VelocityComponent(newHorizontalVelocity, newVerticalVelocity)
        entity.replaceComponent(velocity)

        notifyObservers(
          Move(
            entity.id,
            entity
              .getComponent(classOf[SpriteComponent])
              .get
              .asInstanceOf[SpriteComponent],
            newPosition,
            model.MOVEMENT_DURATION
          )
        )

      }
