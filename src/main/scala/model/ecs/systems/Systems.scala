package model.ecs.systems

import javafx.scene.input.KeyCode
import model.*
import model.ecs.components.*
import model.ecs.entities.*
import model.event.Event
import model.event.Event.Move
import model.event.observer.Observable
import model.input.commands.*
import model.utilities.Empty

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
      {
        val pos = bullet.getComponent[PositionComponent].get
        val vel = bullet.getComponent[VelocityComponent].get
        val nextPos = pos + vel

        val currentSprite = bullet
          .getComponent[SpriteComponent]
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
    val pos = shooter.getComponent[PositionComponent].get
    val dir = shooter.getComponent[DirectionComponent].get
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
          classOf[VelocityComponent]
        )
        .foreach { entity =>
          val velocity = entity.getComponent[VelocityComponent].get
          entity.replaceComponent(
            velocity + VelocityComponent(0, GRAVITY_VELOCITY * elapsedTime)
          )
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
        val currentPosition = entity
          .getComponent[PositionComponent]
          .getOrElse(
            throw new Exception("Position not found")
          )
        var velocity = entity
          .getComponent[VelocityComponent]
          .getOrElse(throw new Exception("Velocity not found"))
        val size = entity.getComponent[SizeComponent].get

        // Check if the player is touching the ground
        val isTouchingGround =
          currentPosition.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0
        var newPositionX = currentPosition.x + velocity.x * elapsedTime * 0.001
        var newPositionY = currentPosition.y + velocity.y * elapsedTime * 0.001

        // Calculate the new position based on the velocity and elapsed time
        var newPosition = PositionComponent(
          boundaryCheck(
            newPositionX,
            model.GUIWIDTH,
            HORIZONTAL_COLLISION_SIZE
          ),
          boundaryCheck(newPositionY, model.GUIHEIGHT, VERTICAL_COLLISION_SIZE)
        )

        // Check for potential collisions
        val potentialCollisions = EntityManager().getEntitiesWithComponent(
          classOf[PositionComponent],
          classOf[SizeComponent]
        )
        val willCollide = potentialCollisions.exists(otherEntity =>
          !otherEntity.isSameEntity(entity) && CollisionSystem.isOverlapping(
            newPosition,
            size,
            otherEntity.getComponent[PositionComponent].get,
            otherEntity.getComponent[SizeComponent].get
          )
        )

        // If a collision would occur, set velocity.x to 0
        if (willCollide) {
          velocity = VelocityComponent(0, velocity.y)
          newPositionX = currentPosition.x + velocity.x * elapsedTime * 0.001
          newPosition = PositionComponent(
            boundaryCheck(
              newPositionX,
              model.GUIWIDTH,
              HORIZONTAL_COLLISION_SIZE
            ),
            boundaryCheck(newPositionY, model.GUIHEIGHT, VERTICAL_COLLISION_SIZE)
          )
        }

        // Reduce the horizontal velocity by the friction factor
        val newHorizontalVelocity = velocity.x * FRICTION_FACTOR

        val newVelocity = VelocityComponent(newHorizontalVelocity, velocity.y)
        entity.replaceComponent(newVelocity)

        entity.replaceComponent(newPosition)

        // If the player is touching the ground, update the JumpingComponent to false
        if (isTouchingGround) {
          val newJumping = JumpingComponent(false)
          entity.replaceComponent(newJumping)
        }
        val sprite = entity.getComponent[SpriteComponent].get
        notifyObservers {
          Move(
            entity.id,
            sprite,
            newPosition,
            model.MOVEMENT_DURATION
          )
        }
      }
