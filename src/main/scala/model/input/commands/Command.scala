package model.input.commands

import model.ecs.components.*
import model.ecs.entities.{BulletEntity, Entity, EntityManager, PlayerEntity}
import model.ecs.systems.CollisionSystem.{MovementAxis, wouldCollide}
import model.ecs.systems.Systems.{boundaryCheck, gravitySystem, notifyObservers}

object Command:
  def jump(entity: Entity): Unit =
    entity.getComponent[JumpingComponent] match
      case Some(JumpingComponent(false)) =>
        val v = entity.getComponent[VelocityComponent].get
        entity.replaceComponent(VelocityComponent(v.x , -model.JUMP_MOVEMENT_VELOCITY))
        entity.replaceComponent(JumpingComponent(true))
      case _ => ()

  def left(entity: Entity): Unit =
    val v = entity.getComponent[VelocityComponent].get
    entity.replaceComponent(DirectionComponent(LEFT))
    entity.replaceComponent(VelocityComponent(v.x - model.INPUT_MOVEMENT_VELOCITY, v.y))

  def right(entity: Entity): Unit =
    val v = entity.getComponent[VelocityComponent].get
    entity.replaceComponent(DirectionComponent(RIGHT))
    entity.replaceComponent(VelocityComponent(v.x + model.INPUT_MOVEMENT_VELOCITY, v.y))

  def shoot(entity: Entity): Unit =
    val p = entity.getComponent[PositionComponent].get
    val bulletDirection = entity.getComponent[DirectionComponent].get
    val vx = bulletDirection.d match
      case RIGHT => model.BULLET_VELOCITY
      case LEFT => -model.BULLET_VELOCITY
    EntityManager().addEntity {
      BulletEntity()
        .addComponent(PositionComponent(p.x, p.y))
        .addComponent(VelocityComponent(vx, 0))
        .addComponent(SpriteComponent(model.bulletSprite))
        .addComponent(DirectionComponent(bulletDirection.d))
    }