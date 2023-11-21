package model.ecs.systems

import javafx.scene.input.KeyCode
import model.*
import model.ecs.components.*
import model.ecs.entities.*
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{BulletEntity, MachineGunEntity, WeaponEntity}
import model.ecs.systems.CollisionSystem.{checkCollision, handleCollision}
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

  def collisionDetectionSystem(elapsedTime: Long): Unit =
    var entitiesToCheck = EntityManager().getEntitiesWithComponent(classOf[SizeComponent], classOf[PositionComponent]).toSet
    EntityManager().getEntitiesWithComponent(classOf[SizeComponent], classOf[PositionComponent], classOf[CollisionComponent]).foreach { e1 =>
      entitiesToCheck -= e1
      val pos1 = e1.getComponent[PositionComponent].get
      val size1 = e1.getComponent[SizeComponent].get
      entitiesToCheck.foreach { e2 =>
        val pos2 = e2.getComponent[PositionComponent].get
        val size2 = e2.getComponent[SizeComponent].get
        val right = pos1.x + size1.width - pos2.x // overlap between right side of e1 and left side of e2
        val left = pos1.x - pos2.x + size2.width // overlap between left side of e1 and right side of e2
        val bottom = pos1.y + size1.height - pos2.y // overlap between bottom side of e1 and top side of e2
        val top = pos1.y - pos2.y + size2.height // overlap between top side of e1 and bottom side of e2
        (right, left, bottom, top) match
          case (r, l, b, t) if r > 0 && l < 0 && b > 0 && t < 0 => // e1 colliding with e2
            e1.getComponent[CollisionComponent].get.entities += e2 // marking e1
            e2.getComponent[CollisionComponent] match
              case Some(collisionComponent) =>                     // e2 has to be marked
                collisionComponent.entities += e1                  // marking e2
              case None =>
          case _ =>
      }
    }

  def collisionHandlingSystem(elapsedTime: Long): Unit =
    EntityManager().getEntitiesWithComponent(classOf[CollisionComponent], classOf[VelocityComponent]).foreach { entity =>
      val velocity = entity.getComponent[VelocityComponent].get
      var position = entity.getComponent[PositionComponent].get
      val collisions = entity.getComponent[CollisionComponent].get.entities
      // check the velocity to know in what direction has been collided
      // resolve finding max or min accordingly that represent a surely empty space
      if velocity.x < 0 then
        val x = collisions.map(e => e.getComponent[PositionComponent].get.x + e.getComponent[SizeComponent].get.width).max
        position = PositionComponent(x, position.y)
      else if velocity.x > 0 then
        val x = collisions.map(_.getComponent[PositionComponent].get.x).min
        position = PositionComponent(x, position.y)
      if velocity.y < 0 then
        val y = collisions.map(e => e.getComponent[PositionComponent].get.y + e.getComponent[SizeComponent].get.height).max
        position = PositionComponent(position.x, y)
      else if velocity.y > 0 then
        val y = collisions.map(_.getComponent[PositionComponent].get.y).min
        position = PositionComponent(position.x, y)
      entity.replaceComponent(position)
      entity.replaceComponent(CollisionComponent(scala.collection.mutable.Set()))
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
