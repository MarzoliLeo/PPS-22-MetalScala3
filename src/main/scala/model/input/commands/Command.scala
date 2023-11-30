package model.input.commands

import model.ecs.components.*
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{EnemyBulletEntity, PlayerBulletEntity}
import model.ecs.entities.{Entity, EntityManager}

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
            .addComponent(PositionComponent(p.x + vx * 000.1, p.y))
            .addComponent(VelocityComponent(vx, 0))
            .addComponent(BulletComponent(StandardBullet()))
            // fixme: set a specific size for bullets
            .addComponent(SizeComponent(100, 100))
            .addComponent(SpriteComponent(model.s_SmallBullet))
            .addComponent(DirectionComponent(bulletDirection.d))
        case BulletComponent(EnemyBullet()) =>
          EnemyBulletEntity()
            .addComponent(PositionComponent(p.x + vx * 000.1, p.y))
            .addComponent(VelocityComponent(vx, 0))
            .addComponent(BulletComponent(EnemyBullet()))
            // fixme: set a specific size for bullets
            .addComponent(SizeComponent(100, 100))
            .addComponent(SpriteComponent(model.s_SmallBullet))
            .addComponent(DirectionComponent(bulletDirection.d))
        case BulletComponent(MachineGunBullet()) =>
          PlayerBulletEntity()
            .addComponent(PositionComponent(p.x + vx * 000.1, p.y))
            .addComponent(VelocityComponent(vx, 0))
            .addComponent(BulletComponent(MachineGunBullet()))
            // fixme: set a specific size for bullets
            .addComponent(SizeComponent(100, 100))
            .addComponent(SpriteComponent(model.s_BigBullet))
            .addComponent(DirectionComponent(bulletDirection.d))
    }