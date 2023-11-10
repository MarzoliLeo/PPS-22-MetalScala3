package model.ecs.systems

import javafx.scene.input.KeyCode
import model.ecs.components.*
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionSystem.{MovementAxis, wouldCollide}
import model.ecs.components.{Component, Direction, DirectionComponent, GravityComponent, LEFT, PositionComponent, RIGHT, VelocityComponent}
import model.ecs.entities.{BulletEntity, Entity, EntityManager, PlayerEntity}
import model.ecs.systems.Systems.shootBullet
import model.event.Event
import model.event.Event.*
import model.event.observer.Observable
import model.{JUMP_DURATION, inputsQueue}
import model.utilities.Empty
import java.awt.Component

object Systems extends Observable[Event]:

  private def updatePositionAndNotify(
      entity: Entity,
      positionComponent: PositionComponent
  ): Unit = {
    entity.replaceComponent(positionComponent)
    notifyObservers(Move(entity.id, positionComponent))
  }


  /** Applies a boundary check to a position value, ensuring it stays within the
    * bounds of the system.
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


  private def moveEntity(entity: Entity, dx: Double, dy: Double): Unit = {
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]
    val collider = entity
      .getComponent(classOf[ColliderComponent])
      .get
      .asInstanceOf[ColliderComponent]

    val movementAxis =
      if (dx != 0) MovementAxis.Horizontal else MovementAxis.Vertical
    val proposedPosition = PositionComponent(
      boundaryCheck(
        currentPosition.x + dx,
        model.GUIWIDTH,
        collider.size.width
      ),
      boundaryCheck(
        currentPosition.y + dy,
        model.GUIHEIGHT,
        collider.size.height
      )
    )

    val currentSprite = entity
      .getComponent(classOf[SpriteComponent])
      .get
      .asInstanceOf[SpriteComponent]

    if (!entity.wouldCollide(proposedPosition, movementAxis)) {
      updatePositionAndNotify(entity, proposedPosition)
    } else {
      // Handle the collision case here if needed
    }

  }

  private def jumpEntity(entity: Entity, duration: Double): Unit =
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]

    val currentSprite = entity
      .getComponent(classOf[SpriteComponent])
      .get
      .asInstanceOf[SpriteComponent]

    try {
      model.isGravityEnabled = false
      notifyObservers(Jump(entity.id, currentSprite, currentPosition, model.JUMP_MOVEMENT_VELOCITY, duration))
    } finally {
      model.isGravityEnabled = true
    }

  val bulletMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesByClass(classOf[BulletEntity]).foreach { bullet =>
      val pos = bullet.getComponent(classOf[PositionComponent]).get.asInstanceOf[PositionComponent]
      val vel = bullet.getComponent(classOf[VelocityComponent]).get.asInstanceOf[VelocityComponent]
      val nextPos = PositionComponent(pos.x + vel.x, pos.y + vel.y)
      bullet.replaceComponent(nextPos)
      notifyObservers(Move(bullet.id, nextPos))
    }


  private def shootBullet(shooter: Entity, manager: EntityManager): Unit =
    val pos = shooter.getComponent(classOf[PositionComponent]).get.asInstanceOf[PositionComponent]
    val dir = shooter.getComponent(classOf[DirectionComponent]).get.asInstanceOf[DirectionComponent]
    val xVelocity = dir.d match
      case RIGHT => 20
      case LEFT => -20
    manager.addEntity(
      BulletEntity()
        .addComponent(PositionComponent(pos.x, pos.y))
        .addComponent(VelocityComponent(xVelocity, 0))
    )

  val passiveMovementSystem: EntityManager => Unit = manager =>
    manager
      .getEntitiesWithComponent(classOf[PositionComponent])
      .foreach(entity => moveEntity(entity, 1.0, 0))

  val inputMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
      entity =>
        inputsQueue.peek.foreach {
          case KeyCode.W => jumpEntity(entity, model.JUMP_DURATION)
          case KeyCode.A => moveEntity(entity, -model.INPUT_MOVEMENT_VELOCITY, 0)
          case KeyCode.S => moveEntity(entity, 0, model.INPUT_MOVEMENT_VELOCITY)
          case KeyCode.D => moveEntity(entity, model.INPUT_MOVEMENT_VELOCITY, 0)
          case KeyCode.SPACE => shootBullet(entity, manager)
          case _ => println("[INPUT] Invalid key")
        }
        inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }

  val gravitySystem: EntityManager => Unit = manager =>
    if (model.isGravityEnabled) {manager
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
        val gravityComp = entity
          .getComponent(classOf[GravityComponent])
          .get
          .asInstanceOf[GravityComponent]
        val collider = entity
          .getComponent(classOf[ColliderComponent])
          .get
          .asInstanceOf[ColliderComponent]

        val newPosition = PositionComponent(
          currentPosition.x,
          boundaryCheck(
            currentPosition.y + gravityComp.gravity,
            model.GUIHEIGHT,
            collider.size.height
          )
        )

        if (!entity.wouldCollide(newPosition, MovementAxis.Vertical)) {
          updatePositionAndNotify(entity, newPosition)
        }
      }
