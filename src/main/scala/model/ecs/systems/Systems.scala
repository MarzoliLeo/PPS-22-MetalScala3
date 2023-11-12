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
import model.{JUMP_DURATION, inputsQueue}

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

  val inputMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
      entity =>
        inputsQueue.peek.foreach {
          case KeyCode.W => JumpCommand(model.JUMP_DURATION).execute(entity, manager)
          case KeyCode.A => MoveCommand(-model.INPUT_MOVEMENT_VELOCITY, 0).execute(entity, manager)
          case KeyCode.S => MoveCommand(0, model.INPUT_MOVEMENT_VELOCITY).execute(entity, manager)
          case KeyCode.D => MoveCommand(model.INPUT_MOVEMENT_VELOCITY, 0).execute(entity, manager)
          case KeyCode.SPACE => ShootCommand().execute(entity, manager)
          case _ => InvalidCommand.execute(entity, manager)
        }
        inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }

  val gravitySystem: EntityManager => Unit = manager =>
    if (model.isGravityEnabled) {
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

          entity.replaceComponent(newPosition)
          notifyObservers(
            Gravity(
              entity.id,
              newPosition,
            )
          )
        }
    }
