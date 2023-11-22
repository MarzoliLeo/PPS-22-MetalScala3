package model.ecs.systems

import javafx.scene.input.KeyCode
import model.*
import model.ecs.components.*
import model.ecs.entities.*
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{BulletEntity, MachineGunEntity, WeaponEntity}
import model.ecs.systems.CollisionSystem.checkCollision
import model.ecs.systems.Systems.getUpdatedPosition
import model.event.Event
import model.event.observer.Observable
import model.input.commands.*
import model.utilities.Empty

import scala.reflect.ClassTag

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
        given position: PositionComponent =
          bullet.getComponent[PositionComponent].get
        given velocity: VelocityComponent =
          bullet.getComponent[VelocityComponent].get
        val newPosition = getUpdatedPosition(bullet, elapsedTime)

        // Check if the bullet is in position.x + size = model.GUIWIDTH
        if (newPosition.x + HORIZONTAL_COLLISION_SIZE >= model.GUIWIDTH) ||
          (newPosition.x <= 0) ||
          checkCollision(bullet, newPosition + velocity).nonEmpty
        then
          EntityManager().removeEntity(bullet)
        else bullet.replaceComponent(newPosition)
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

  def getUpdatedPosition(entity: Entity, elapsedTime: Long)(using
      position: PositionComponent,
      velocity: VelocityComponent
  ): PositionComponent = {
    val newPositionX = position.x + velocity.x * elapsedTime * 0.001
    val newPositionY = position.y + velocity.y * elapsedTime * 0.001

    val finalPositionX =
      if checkCollision(entity, PositionComponent(newPositionX, position.y)).isEmpty then newPositionX
      else {
        if (entity.isInstanceOf[BulletEntity]) EntityManager().removeEntity(entity)
        position.x
      }

    val finalPositionY =
      if checkCollision(entity, PositionComponent(finalPositionX, newPositionY)).isEmpty then newPositionY
      else {
        if (entity.isInstanceOf[BulletEntity]) EntityManager().removeEntity(entity)
        position.y
      }

    PositionComponent(
      boundaryCheck(finalPositionX, model.GUIWIDTH, HORIZONTAL_COLLISION_SIZE),
      boundaryCheck(finalPositionY, model.GUIHEIGHT, VERTICAL_COLLISION_SIZE)
    )
  }

  private def getUpdatedVelocity(entity: Entity)(using
      velocity: VelocityComponent
  ): VelocityComponent = {
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
      case _: BulletEntity =>
        entity.replaceComponent(SpriteComponent("sprites/Bullet.png"))
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
        given position: PositionComponent =
          entity.getComponent[PositionComponent].get
        given velocity: VelocityComponent =
          entity.getComponent[VelocityComponent].get

        entity.replaceComponent(getUpdatedVelocity(entity))
        updateJumpingState(entity)
        entity.replaceComponent(getUpdatedPosition(entity, elapsedTime))
      }
