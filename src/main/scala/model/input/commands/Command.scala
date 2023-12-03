package model.input.commands

import model.ecs.components.*
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{EnemyBulletEntity, PlayerBulletEntity}
import model.ecs.entities.{Entity, EntityManager}

import scala.util.Try

object Command:
  def jump(entity: Entity): Unit =
    entity.getComponent[JumpingComponent] match
      case Some(JumpingComponent(false)) =>
        val v = entity.getComponent[VelocityComponent].get
        entity.replaceComponent(
          VelocityComponent(v.x, -model.JUMP_MOVEMENT_VELOCITY)
        )
        entity.replaceComponent(JumpingComponent(true))
      case _ => ()

  def left(entity: Entity): Unit =
    val v = entity.getComponent[VelocityComponent].get
    entity.replaceComponent(DirectionComponent(LEFT))
    entity.replaceComponent(
      VelocityComponent(v.x - model.INPUT_MOVEMENT_VELOCITY, v.y)
    )

  def right(entity: Entity): Unit =
    val v = entity.getComponent[VelocityComponent].get
    entity.replaceComponent(DirectionComponent(RIGHT))
    entity.replaceComponent(
      VelocityComponent(v.x + model.INPUT_MOVEMENT_VELOCITY, v.y)
    )

  def shoot(entity: Entity): Unit =
    val p = entity.getComponent[PositionComponent].get
    val bulletDirection = entity.getComponent[DirectionComponent].get
    val vx = entity match
      case _: PlayerEntity =>
        bulletDirection.d match
          case RIGHT => model.BULLET_VELOCITY
          case LEFT  => -model.BULLET_VELOCITY
      case _: EnemyEntity =>
        bulletDirection.d match
          case RIGHT => -model.BULLET_VELOCITY
          case LEFT  => model.BULLET_VELOCITY

    EntityManager().addEntity {
      entity.getComponent[BulletComponent].getOrElse(throw new Exception) match
        case BulletComponent(StandardBullet()) =>
          PlayerBulletEntity()
            .addComponent(PositionComponent(p.x + vx * 0.1, p.y))
            .addComponent(VelocityComponent(vx, 0))
            .addComponent(BulletComponent(StandardBullet()))
            // fixme: set a specific size for bullets
            .addComponent(SizeComponent(100, 100))
            .addComponent(SpriteComponent(model.s_SmallBullet))
            .addComponent(DirectionComponent(bulletDirection.d))
        case BulletComponent(EnemyBullet()) =>
          EnemyBulletEntity()
            .addComponent(PositionComponent(p.x + vx * 0.1, p.y))
            .addComponent(VelocityComponent(vx, 0))
            .addComponent(BulletComponent(EnemyBullet()))
            // fixme: set a specific size for bullets
            .addComponent(SizeComponent(100, 100))
            .addComponent(SpriteComponent(model.s_SmallBullet))
            .addComponent(DirectionComponent(bulletDirection.d))
        case BulletComponent(MachineGunBullet()) =>
          entity.getComponent[AmmoComponent].getOrElse(throw new Exception) match
            case AmmoComponent(1) =>
              entity.replaceComponent(AmmoComponent(0))
              entity.replaceComponent(BulletComponent(StandardBullet()))
            case AmmoComponent(n) =>
              entity.replaceComponent(AmmoComponent(n - 1))
          PlayerBulletEntity()
            .addComponent(PositionComponent(p.x + vx * 0.001, p.y))
            .addComponent(VelocityComponent(vx, 0))
            .addComponent(BulletComponent(MachineGunBullet()))
            // fixme: set a specific size for bullets
            .addComponent(SizeComponent(100, 100))
            .addComponent(SpriteComponent(model.s_BigBullet))
            .addComponent(DirectionComponent(bulletDirection.d))
    }

  def crouch(entity: Entity): Unit =
    try{
      if model.isCrouching then
        (entity.getComponent[SizeComponent], entity.getComponent[PositionComponent]) match
          case (Some(size), Some(pos))  =>
            entity.replaceComponent(SizeComponent(size.width, size.height - model.CLUTCHFACTOR))
            entity.replaceComponent(PositionComponent(pos.x, pos.y + model.CLUTCHFACTOR))
          case _ => ()
    }
    finally {
      model.isCrouching = false
    }

  def standUp(entity: Entity): Unit =
    model.isCrouching = true
    (entity.getComponent[SizeComponent], entity.getComponent[PositionComponent]) match
      case (Some(size), Some(pos)) =>
        entity.replaceComponent(SizeComponent(model.HORIZONTAL_COLLISION_SIZE, model.VERTICAL_COLLISION_SIZE))
        entity.replaceComponent(PositionComponent(pos.x, pos.y - model.CLUTCHFACTOR))
      case _ => ()


