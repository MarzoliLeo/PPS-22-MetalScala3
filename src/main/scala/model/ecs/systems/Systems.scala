package model.ecs.systems

import javafx.scene.input.KeyCode
import model.*
import model.ecs.components.*
import model.ecs.entities.*
import model.event.Event
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

  val bulletMovementSystem: Long => Unit = elapsedTime =>
    EntityManager().getEntitiesByClass(classOf[BulletEntity]).foreach { bullet =>
      {
        val pos = bullet.getComponent[PositionComponent].get
        val vel = bullet.getComponent[VelocityComponent].get
        val newPositionX = pos.x + vel.x * elapsedTime * 0.001
        val newPositionY = pos.y + vel.y * elapsedTime * 0.001
        // Calculate the new position based on the velocity and elapsed time
        val newPosition = PositionComponent(
          boundaryCheck(
            newPositionX,
            model.GUIWIDTH,
            HORIZONTAL_COLLISION_SIZE
          ),
          boundaryCheck(newPositionY, model.GUIHEIGHT, VERTICAL_COLLISION_SIZE)
        )
        bullet.replaceComponent(newPosition)
        //val nextPos = pos + vel
        //bullet.replaceComponent(nextPos)
      }
    }


  val inputMovementSystem: Long => Unit = elapsedTime =>
    EntityManager().getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
      entity =>
        inputsQueue.peek match
          case Some(command) => command(entity)
          case None => ()
        inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }

  val gravitySystem: Long => Unit = elapsedTime =>
    if (model.isGravityEnabled) {
      EntityManager().getEntitiesWithComponent(
          classOf[PositionComponent],
          classOf[GravityComponent],
          classOf[VelocityComponent]
        )
        .foreach { entity =>
          val velocity = entity.getComponent[VelocityComponent].get
          entity.replaceComponent(velocity + VelocityComponent(0, GRAVITY_VELOCITY * elapsedTime))
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
        val currentPosition = entity.getComponent[PositionComponent].get
        val velocity = entity.getComponent[VelocityComponent].get
        // Check if the player is touching the ground
        val isTouchingGround = currentPosition.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0
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
      }

