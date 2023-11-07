package model.ecs.systems
import javafx.scene.input.KeyCode
import model.ecs.components.{Component, Direction, DirectionComponent, GravityComponent, LEFT, PositionComponent, RIGHT, VelocityComponent}
import model.ecs.entities.{BulletEntity, Entity, EntityManager, PlayerEntity}
import model.event.Event
import model.event.Event.Move
import model.event.observer.Observable
import model.inputsQueue
import model.utilities.Empty

import java.awt.Component
import scala.runtime.Nothing$

// TODO: apply DRY principle when possible
object Systems extends Observable[Event] {
  
  private def moveEntity(entity: Entity, dx: Int, dy: Int): Unit =
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]
    currentPosition.x match
      case x if x < 0 =>
        val pos = PositionComponent(0, currentPosition.y)
        entity.replaceComponent(pos)
        notifyObservers(Move(entity.id, pos))
      case x if x + 100 + model.INPUT_MOVEMENT_VELOCITY > model.GUIWIDTH =>
        val pos = PositionComponent(model.GUIWIDTH - 100 - model.INPUT_MOVEMENT_VELOCITY, currentPosition.y)
        entity.replaceComponent(pos)
        notifyObservers(Move(entity.id, pos))
      case _ => 
        val pos = PositionComponent(currentPosition.x + dx, currentPosition.y + dy)
        entity.replaceComponent(pos)
        notifyObservers(Move(entity.id, pos))

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

  val bulletMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesByClass(classOf[BulletEntity]).foreach { bullet =>
      val pos = bullet.getComponent(classOf[PositionComponent]).get.asInstanceOf[PositionComponent]
      val vel = bullet.getComponent(classOf[VelocityComponent]).get.asInstanceOf[VelocityComponent]
      val nextPos = PositionComponent(pos.x + vel.x, pos.y + vel.y)
      bullet.replaceComponent(nextPos)
      notifyObservers(Move(bullet.id, nextPos))
    }

  val passiveMovementSystem: EntityManager => Unit = manager =>
    manager
      .getEntitiesWithComponent(classOf[PositionComponent])
      .foreach(entity => moveEntity(entity, 1, 0))

  val inputMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesByClass(classOf[PlayerEntity]).foreach { entity =>
      inputsQueue.peek match {
        case Some(command) =>
          command match {
            case KeyCode.W => moveEntity(entity, 0, -model.JUMP_MOVEMENT_VELOCITY)
            case KeyCode.A =>
              moveEntity(entity, -model.INPUT_MOVEMENT_VELOCITY, 0)
              entity.replaceComponent(DirectionComponent(LEFT))
            case KeyCode.S => moveEntity(entity, 0, model.INPUT_MOVEMENT_VELOCITY)
            case KeyCode.D =>
              moveEntity(entity, model.INPUT_MOVEMENT_VELOCITY, 0)
              entity.replaceComponent(DirectionComponent(RIGHT))
            case KeyCode.SPACE => shootBullet(entity, manager)
          }
        case None => ()
      }
      inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }
  
  val gravitySystem: EntityManager => Unit =
    manager =>
      manager
        .getEntitiesWithComponent(classOf[PositionComponent], classOf[GravityComponent])
        .foreach( entity => 
          val currentPosition = entity
            .getComponent(classOf[PositionComponent])
            .get
            .asInstanceOf[PositionComponent]

          val gravityToApply = entity
            .getComponent(classOf[GravityComponent])
            .get
            .asInstanceOf[GravityComponent]

          currentPosition.y match
            case y if y < 0 =>
              val pos = PositionComponent(currentPosition.x, 0)
              entity.replaceComponent(pos)
              notifyObservers(Move(entity.id, pos))
            case y if y + 100 > model.GUIHEIGHT =>
              val pos = PositionComponent(currentPosition.x, model.GUIHEIGHT - 100)
              entity.replaceComponent(pos)
              notifyObservers(Move(entity.id, pos))
            case _ =>
              val pos = PositionComponent(currentPosition.x, currentPosition.y + gravityToApply.gravity)
              entity.replaceComponent(pos)
              notifyObservers(Move(entity.id, pos))
        )

}
