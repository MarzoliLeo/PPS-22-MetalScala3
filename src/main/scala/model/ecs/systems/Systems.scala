package model.ecs.systems

import javafx.scene.input.KeyCode
import model.*
import model.ecs.components.*
import model.ecs.entities.{BulletEntity, Entity, EntityManager, PlayerEntity}
import model.ecs.systems.CollisionSystem.{MovementAxis, wouldCollide}
import model.ecs.systems.Systems.shootBullet
import model.event.Event
import model.event.Event.*
import model.event.observer.Observable
import model.input.commands.*
import model.utilities.Empty
import scala.reflect.ClassTag

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

  def getComponent[T <: Component](entity: Entity)(implicit tag: ClassTag[T]): Option[T] =
    entity.getComponent(tag.runtimeClass.asInstanceOf[Class[T]]).flatMap {
      case component: T => Some(component)
      case _ => None
    }

  val bulletMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesByClass(classOf[BulletEntity]).foreach { bullet =>
      for {
        pos <- getComponent[PositionComponent](bullet)
        vel <- getComponent[VelocityComponent](bullet)
      } {
        val nextPos = PositionComponent(pos.x + vel.x, pos.y + vel.y)

        val currentSprite = getComponent[SpriteComponent](bullet)
          .getOrElse(SpriteComponent(List("sprites/MarcoRossi.png")))

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
    }

  private def shootBullet(shooter: Entity, manager: EntityManager): Unit =
    for {
      pos <- getComponent[PositionComponent](shooter)
      dir <- getComponent[DirectionComponent](shooter)
    } {
      val xVelocity = dir.d match
        case RIGHT => 20
        case LEFT  => -20
      manager.addEntity(
        BulletEntity()
          .addComponent(PositionComponent(pos.x, pos.y))
          .addComponent(VelocityComponent(xVelocity, 0))
      )
    }

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
          for {
            velocity <- getComponent[VelocityComponent](entity)
            fallCount <- getComponent[FallCountComponent](entity)
          } {
            if (velocity.y >= 0) {
              fallCount.count = 0
            } else {
              fallCount.count += 1
            }

            // Update the vertical velocity
            val newVelocity =
              velocity + VelocityComponent(0, GRAVITY_VELOCITY * elapsedTime)
            entity.replaceComponent(newVelocity)
          }
        }
    }

  val positionUpdateSystem: Long => Unit = elapsedTime =>
    EntityManager()
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[VelocityComponent],
        classOf[JumpingComponent]
      )
      .foreach { entity =>
        for {
          currentPosition <- getComponent[PositionComponent](entity)
          velocity <- getComponent[VelocityComponent](entity)
          jumping <- getComponent[JumpingComponent](entity)
        } {
          // Check if the player is touching the ground
          val isTouchingGround =
            currentPosition.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0

          val newPositionX = currentPosition.x + velocity.x * elapsedTime * 0.001
          val newPositionY = currentPosition.y + velocity.y * elapsedTime * 0.001
          // Calculate the new position based on the velocity and elapsed time
          val newPosition = PositionComponent(
            boundaryCheck(
              newPositionX,
              model.GUIWIDTH,
              HORIZONTAL_COLLISION_SIZE
            ),
            boundaryCheck(newPositionY, model.GUIHEIGHT, VERTICAL_COLLISION_SIZE)
          )
          entity.replaceComponent(newPosition)

          // Reduce the horizontal velocity by the friction factor
          val newHorizontalVelocity = velocity.x * FRICTION_FACTOR

          val newVelocity = VelocityComponent(newHorizontalVelocity, velocity.y)
          entity.replaceComponent(newVelocity)

          // If the player is touching the ground, update the JumpingComponent to false
          if (isTouchingGround) {
            val newJumping = JumpingComponent(false)
            entity.replaceComponent(newJumping)
          }

          for {
            sprite <- getComponent[SpriteComponent](entity)
          } {
            notifyObservers(
              Move(
                entity.id,
                sprite,
                newPosition,
                model.MOVEMENT_DURATION
              )
            )
          }
        }
      }

