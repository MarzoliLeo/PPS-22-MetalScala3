package model.input.commands

import model.ecs.components.*
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{BombEntity, EnemyBulletEntity, PlayerBulletEntity}
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
        entity.replaceComponent(
          CollisionComponent()
        )
      case _ => ()

  def left(entity: Entity): Unit =
    val v = entity.getComponent[VelocityComponent].get
    entity.replaceComponent(DirectionComponent(LEFT))
    entity.replaceComponent(
      VelocityComponent(v.x - model.INPUT_MOVEMENT_VELOCITY, v.y)
    )
    entity.replaceComponent(CollisionComponent())

  def right(entity: Entity): Unit =
    val v = entity.getComponent[VelocityComponent].get
    entity.replaceComponent(DirectionComponent(RIGHT))
    entity.replaceComponent(
      VelocityComponent(v.x + model.INPUT_MOVEMENT_VELOCITY, v.y)
    )
    entity.replaceComponent(CollisionComponent())

  private def createBulletEntity(
      entity: Entity,
      bulletComponent: BulletComponent,
      position: PositionComponent,
      velocity: VelocityComponent,
      size: SizeComponent,
      sprite: SpriteComponent,
      direction: DirectionComponent
  ): Entity = {
    entity
      .addComponent(position)
      .addComponent(velocity)
      .addComponent(bulletComponent)
      .addComponent(size)
      .addComponent(sprite)
      .addComponent(direction)
  }

  def shoot(entity: Entity): Unit = {
    val p = entity.getComponent[PositionComponent].get
    val bulletDirection = entity.getComponent[DirectionComponent].get
    val vx = entity match {
      case _: PlayerEntity =>
        bulletDirection.d match {
          case RIGHT => model.BULLET_VELOCITY
          case LEFT  => -model.BULLET_VELOCITY
        }
      case _: EnemyEntity =>
        bulletDirection.d match {
          case RIGHT => -model.BULLET_VELOCITY
          case LEFT  => model.BULLET_VELOCITY
        }
    }

    val bulletPosition = PositionComponent(p.x + vx * 0.001, p.y)

    EntityManager.addEntity {
      entity
        .getComponent[BulletComponent]
        .getOrElse(throw new Exception) match {
        case BulletComponent(StandardBullet()) =>
          createBulletEntity(
            PlayerBulletEntity(),
            BulletComponent(StandardBullet()),
            bulletPosition,
            VelocityComponent(vx, 0),
            SizeComponent(100, 20),
            SpriteComponent(model.s_SmallBullet),
            DirectionComponent(bulletDirection.d)
          )
        case BulletComponent(EnemyBullet()) =>
          createBulletEntity(
            EnemyBulletEntity(),
            BulletComponent(EnemyBullet()),
            bulletPosition,
            VelocityComponent(vx, 0),
            SizeComponent(100, 20),
            SpriteComponent(model.s_SmallBullet),
            DirectionComponent(bulletDirection.d)
          )
        case BulletComponent(MachineGunBullet()) =>
          entity
            .getComponent[SpecialWeaponAmmoComponent]
            .getOrElse(throw new Exception) match {
            case SpecialWeaponAmmoComponent(1) =>
              entity.replaceComponent(SpecialWeaponAmmoComponent(0))
              entity.replaceComponent(BulletComponent(StandardBullet()))
            case SpecialWeaponAmmoComponent(n) =>
              entity.replaceComponent(SpecialWeaponAmmoComponent(n - 1))
          }
          createBulletEntity(
            PlayerBulletEntity(),
            BulletComponent(MachineGunBullet()),
            bulletPosition,
            VelocityComponent(vx, 0),
            SizeComponent(100, 20),
            SpriteComponent(model.s_BigBullet),
            DirectionComponent(bulletDirection.d)
          )
      }
    }
  }

  def bomb(entity: Entity): Unit =
    val pos = entity.getComponent[PositionComponent].get
    val size = entity.getComponent[SizeComponent].get
    val bulletDirection = entity.getComponent[DirectionComponent].get
    val bombPosition = bulletDirection.d match
      case RIGHT => PositionComponent(pos.x + size.width, pos.y)
      case LEFT  => PositionComponent(pos.x - size.width, pos.y)
    val vx = bulletDirection.d match
      case RIGHT => model.BOMB_VELOCITY_X
      case LEFT  => -model.BOMB_VELOCITY_X

    entity
      .getComponent[BombAmmoComponent]
      .getOrElse(throw new Exception) match
      case BombAmmoComponent(0) =>
        println("No more bombs")
      case BombAmmoComponent(n) =>
        println(s"Remaining bombs: $n")
        entity.replaceComponent(BombAmmoComponent(n - 1))
        EntityManager.addEntity {
          BombEntity()
            .addComponent(bombPosition)
            .addComponent(VelocityComponent(vx, -model.BOMB_VELOCITY_Y))
            .addComponent(GravityComponent())
            .addComponent(CollisionComponent())
            .addComponent(SizeComponent(50, 50))
            .addComponent(SpriteComponent(model.s_Bomb))
            .addComponent(bulletDirection)

        }
  def crouch(entity: Entity): Unit =
    try {
      if model.isCrouching then
        (
          entity.getComponent[SizeComponent],
          entity.getComponent[PositionComponent]
        ) match
          case (Some(size), Some(pos)) =>
            entity.replaceComponent(
              SizeComponent(size.width, size.height - model.CLUTCHFACTOR)
            )
            entity.replaceComponent(
              PositionComponent(pos.x, pos.y + model.CLUTCHFACTOR)
            )
            entity.replaceComponent(CollisionComponent())
          case _ => ()
    } finally {
      model.isCrouching = false
    }

  def standUp(entity: Entity): Unit =
    model.isCrouching = true
    (
      entity.getComponent[SizeComponent],
      entity.getComponent[PositionComponent]
    ) match
      case (Some(_), Some(pos)) =>
        entity.replaceComponent(
          SizeComponent(
            model.HORIZONTAL_COLLISION_SIZE,
            model.VERTICAL_COLLISION_SIZE
          )
        )
        entity.replaceComponent(
          PositionComponent(pos.x, pos.y - model.CLUTCHFACTOR)
        )
        entity.replaceComponent(CollisionComponent())
      case _ => ()
