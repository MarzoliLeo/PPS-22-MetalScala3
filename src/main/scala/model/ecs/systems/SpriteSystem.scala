package model.ecs.systems

import model.ecs.components.*
import model.ecs.entities.EntityManager
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.*
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.enemies.EnemyEntity

trait SpriteSystem extends SystemWithoutTime

private case class SpriteSystemImpl() extends SpriteSystem:
  def update(): Unit =
    EntityManager()
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[VelocityComponent],
        classOf[SpriteComponent]
      )
      .foreach { entity =>
        // Given instances
        given VelocityComponent = entity.getComponent[VelocityComponent].get

        entity match {
          case playerEntity: PlayerEntity =>
            val sprite = summon[VelocityComponent] match {
              case VelocityComponent(0, 0) => model.s_MarcoRossi
              case VelocityComponent(0, y) if y != 0 => model.s_MarcoRossiJump
              case VelocityComponent(x, 0) if x != 0 => model.s_MarcoRossiMove
              case VelocityComponent(x,y) if x != 0 && y != 0 => model.s_MarcoRossiJumpingMoving
            }
            playerEntity.replaceComponent(SpriteComponent(sprite))

          case playerBulletEntity: PlayerBulletEntity =>
            playerBulletEntity.replaceComponent(SpriteComponent(model.s_SmallBullet))

          case enemyBulletEntity: EnemyBulletEntity =>
            enemyBulletEntity.replaceComponent(SpriteComponent(model.s_BigBullet))

          case machineGunEntity: MachineGunEntity =>
            machineGunEntity.replaceComponent(SpriteComponent(model.s_Weapon_H))

          case boxEntity: BoxEntity =>
            boxEntity.replaceComponent(SpriteComponent(model.s_Box))

          case enemyEntity: EnemyEntity =>
            val sprite = summon[VelocityComponent] match {
              case VelocityComponent(0, 0) => model.s_EnemyCrab
              case VelocityComponent(x, 0) if x != 0 => model.s_EnemyCrabMoving
            }
            enemyEntity.replaceComponent(SpriteComponent(sprite))
        }
      }


object SpriteSystem:
  def apply(): SpriteSystem = SpriteSystemImpl()

