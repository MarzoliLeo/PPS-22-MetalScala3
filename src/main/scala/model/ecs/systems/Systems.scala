package model.ecs.systems

import javafx.scene.input.KeyCode
import model.*
import model.ecs.components.*
import model.ecs.entities.*
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{BulletEntity, MachineGunEntity, WeaponEntity}
import model.ecs.systems.CollisionSystem.{checkCollision}
import model.ecs.systems.Systems.updatePosition
import model.event.Event
import model.event.observer.Observable
import model.input.commands.*
import model.utilities.Empty

object Systems extends Observable[Event]:

  /** Applies a boundary check to a position value, ensuring it stays within the
    * bounds of the system. It ensures that 'pos' is not less than 0.0 and not
    * greater than 'max - size'.
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
    EntityManager().getEntitiesByClass(classOf[BulletEntity]).foreach {
      bullet =>
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
            boundaryCheck(
              newPositionY,
              model.GUIHEIGHT,
              VERTICAL_COLLISION_SIZE
            )
          )
          bullet.replaceComponent(newPosition)

        }
    }

  val inputMovementSystem: Long => Unit = * =>
    EntityManager().getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
      entity =>
        inputsQueue.peek match
          case Some(command) => command(entity)
          case None          => ()
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
          val position = entity.getComponent[PositionComponent].get
          val velocity = entity.getComponent[VelocityComponent].get
          val isTouchingGround =
            position.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0
          if isTouchingGround then
            entity.replaceComponent(VelocityComponent(velocity.x, 0))
          else
            entity.replaceComponent(
              velocity + VelocityComponent(0, GRAVITY_VELOCITY * elapsedTime)
            )
        }
    }

  def updatePosition(entity: Entity, elapsedTime: Long): PositionComponent = {
    val currentPosition = entity
      .getComponent[PositionComponent]
      .getOrElse(throw new Exception("Position not found"))
    val velocity = entity
      .getComponent[VelocityComponent]
      .getOrElse(throw new Exception("Velocity not found"))
    val tmpPositionX: PositionComponent = PositionComponent(
      currentPosition.x + velocity.x * elapsedTime * 0.001,
      currentPosition.y
    )
    val tmpPositionY = PositionComponent(
      currentPosition.x,
      currentPosition.y + velocity.y * elapsedTime * 0.001
    )

    val newPositionX =
      if checkCollision(entity, tmpPositionX).isEmpty then tmpPositionX.x
      else currentPosition.x

    val newPositionY =
      if checkCollision(entity, tmpPositionY).isEmpty then tmpPositionY.y
      else
        entity.replaceComponent(JumpingComponent(false))
        currentPosition.y

    PositionComponent(
      boundaryCheck(newPositionX, model.GUIWIDTH, HORIZONTAL_COLLISION_SIZE),
      boundaryCheck(newPositionY, model.GUIHEIGHT, VERTICAL_COLLISION_SIZE)
    )
  }

  private def updateVelocity(entity: Entity): VelocityComponent = {
    val velocity = entity
      .getComponent[VelocityComponent]
      .getOrElse(throw new Exception("Velocity not found"))

    val newHorizontalVelocity = velocity.x * FRICTION_FACTOR match {
      case x if -0.1 < x && x < 0.1 => 0.0
      case x                        => x
    }

    entity match {
      case _: PlayerEntity =>
        val sprite = velocity match {
          case VelocityComponent(0, 0)           => model.marcoRossiSprite
          case VelocityComponent(_, y) if y != 0 => model.marcoRossiJumpSprite
          case VelocityComponent(x, y) if x != 0 && y == 0 =>
            model.marcoRossiMoveSprite
        }
        entity.replaceComponent(SpriteComponent(sprite))
      case _: MachineGunEntity =>
        entity.replaceComponent(SpriteComponent("sprites/h.png"))
      case _: BoxEntity =>
        entity.replaceComponent(SpriteComponent("sprites/box.jpg"))
    }

    VelocityComponent(newHorizontalVelocity, velocity.y)
  }

  private def updateJumpingState(entity: Entity): Unit = {
    if entity.hasComponent(classOf[PlayerComponent])
    then
      val currentPosition = entity
        .getComponent[PositionComponent]
        .getOrElse(throw new Exception("Position not found"))
      val velocity = entity
        .getComponent[VelocityComponent]
        .getOrElse(throw new Exception("Velocity not found"))
      val isTouchingGround =
        currentPosition.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0
      if (isTouchingGround)
        // fixme: gravity should be subjective 
        model.isGravityEnabled = false
        entity.replaceComponent(JumpingComponent(false))
      else
        model.isGravityEnabled = true
        entity.getComponent[JumpingComponent].get
  }

  val positionUpdateSystem: Long => Unit = elapsedTime =>
    EntityManager()
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[VelocityComponent],
        classOf[JumpingComponent]
      )
      .foreach { entity =>

        val newPosition = updatePosition(entity, elapsedTime)

        val newVelocity = updateVelocity(entity)
        entity.replaceComponent(newVelocity)

        updateJumpingState(entity)
        entity.replaceComponent(newPosition)
      }
