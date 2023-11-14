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

  /** Checks if there is a collision for the given entity in its new position.
    *
    * @param entity
    *   the entity to check for collision
    * @param newPosition
    *   the new position of the entity
    * @return
    *   true if there is a collision, false otherwise
    */
  private def checkCollision(
      entity: Entity,
      newPosition: PositionComponent
  ): Boolean = {
    val potentialCollisions = EntityManager().getEntitiesWithComponent(
      classOf[PositionComponent],
      classOf[SizeComponent]
    )
    potentialCollisions.exists(otherEntity =>
      !otherEntity.isSameEntity(entity) && CollisionSystem.isOverlapping(
        newPosition,
        entity.getComponent[SizeComponent].get,
        otherEntity.getComponent[PositionComponent].get,
        otherEntity.getComponent[SizeComponent].get
      )
    )
  }

  /** Updates the position of an entity based on its velocity and elapsed time.
    *
    * @param entity
    *   The entity whose position needs to be updated.
    * @param velocity
    *   The velocity component of the entity.
    * @param elapsedTime
    *   The time elapsed since the last update in milliseconds.
    * @return
    *   The updated position component of the entity.
    */
  private def updatePosition(
      entity: Entity,
      velocity: VelocityComponent,
      elapsedTime: Long
  ): PositionComponent = {
    val currentPosition = entity.getComponent[PositionComponent].get
    val newPositionX = currentPosition.x + velocity.x * elapsedTime * 0.001
    val newPositionY = currentPosition.y + velocity.y * elapsedTime * 0.001

    PositionComponent(
      boundaryCheck(
        newPositionX,
        model.GUIWIDTH,
        HORIZONTAL_COLLISION_SIZE
      ),
      boundaryCheck(newPositionY, model.GUIHEIGHT, VERTICAL_COLLISION_SIZE)
    )
  }

  /** Updates the position of entities with a PositionComponent,
    * VelocityComponent, and JumpingComponent based on the elapsed time.
    */
  val positionUpdateSystem: Long => Unit = elapsedTime =>
    EntityManager()
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[VelocityComponent],
        classOf[JumpingComponent]
      )
      .foreach { entity =>
        var velocity = entity.getComponent[VelocityComponent].get
        var newPosition = updatePosition(entity, velocity, elapsedTime)

        if (checkCollision(entity, newPosition)) {
          velocity = VelocityComponent(0, velocity.y)
          newPosition = updatePosition(entity, velocity, elapsedTime)
        }

        entity.replaceComponent(velocity)
        entity.replaceComponent(newPosition)
      }
