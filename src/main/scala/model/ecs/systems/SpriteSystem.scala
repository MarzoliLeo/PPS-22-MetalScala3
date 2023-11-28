package model.ecs.systems

import model.ecs.components.{PositionComponent, SpriteComponent, VelocityComponent}
import model.ecs.entities.EntityManager
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{BulletEntity, MachineGunEntity}
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
        //given PositionComponent = entity.getComponent[PositionComponent].get
        given VelocityComponent = entity.getComponent[VelocityComponent].get

        entity match {
          case playerEntity: PlayerEntity =>
            val sprite = summon[VelocityComponent] match {
              case VelocityComponent(0, 0) => model.marcoRossiSprite
              case VelocityComponent(_, y) if y != 0 => model.marcoRossiJumpSprite
              case VelocityComponent(x, 0) if x != 0 => model.marcoRossiMoveSprite
            }
            playerEntity.replaceComponent(SpriteComponent(sprite))

          case bulletEntity: BulletEntity =>
            bulletEntity.replaceComponent(SpriteComponent("sprites/Bullet.png"))

          case machineGunEntity: MachineGunEntity =>
            machineGunEntity.replaceComponent(SpriteComponent("sprites/h.png"))

          case boxEntity: BoxEntity =>
            boxEntity.replaceComponent(SpriteComponent("sprites/box.jpg"))

          case enemyEntity: EnemyEntity =>
            enemyEntity.replaceComponent(SpriteComponent("sprites/MarcoRossi.png"))
        }
      }


object SpriteSystem:
  def apply(): SpriteSystem = SpriteSystemImpl()

