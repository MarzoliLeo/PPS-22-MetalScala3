package model.ecs.systems

import model.ecs.components.*
import model.ecs.entities.EntityManager
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.{PlayerEntity, SlugEntity}
import model.ecs.entities.weapons.*

trait SpriteSystem extends SystemWithoutTime

private case class SpriteSystemImpl() extends SpriteSystem:
  def update(): Unit =
    EntityManager
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[VelocityComponent],
        classOf[SpriteComponent]
      )
      .foreach { entity =>
        // Given instances
        given VelocityComponent = entity.getComponent[VelocityComponent].get
        given SizeComponent = entity.getComponent[SizeComponent].get

        entity match {
          case playerEntity: PlayerEntity =>
            var sprite = summon[VelocityComponent] match {
              case VelocityComponent(0, 0)           => model.s_MarcoRossi
              case VelocityComponent(0, y) if y != 0 => model.s_MarcoRossiJump
              case VelocityComponent(x, 0) if x != 0 => model.s_MarcoRossiMove
              case VelocityComponent(x, y) if x != 0 && y != 0 =>
                model.s_MarcoRossiJumpingMoving
            }
            sprite = summon[SizeComponent] match {
              case SizeComponent(_, y) if y < model.VERTICAL_COLLISION_SIZE =>
                model.s_MarcoRossiCluch
              case _ => sprite
            }
            if playerEntity.hasComponent(classOf[SlugComponent])
            then sprite = model.s_Slug

            playerEntity.replaceComponent(SpriteComponent(sprite))

          case playerBulletEntity: PlayerBulletEntity =>
            playerBulletEntity
              .getComponent[BulletComponent]
              .getOrElse(throw new Exception("BulletComponent not found"))
              .bullet match
              case _: MachineGunBullet =>
                playerBulletEntity.replaceComponent(
                  SpriteComponent(model.s_BigBullet)
                )
              case _: StandardBullet =>
                playerBulletEntity.replaceComponent(
                  SpriteComponent(model.s_SmallBullet)
                )
              case _ =>
                throw new Exception("Type of bullet not managed")

            
          case enemyEntity: EnemyEntity =>
            val sprite = summon[VelocityComponent] match {
              case VelocityComponent(0, 0)           => model.s_EnemyCrab
              case VelocityComponent(x, 0) if x != 0 => model.s_EnemyCrabMoving
              case _                                 => model.s_EnemyCrab
            }
            enemyEntity.replaceComponent(SpriteComponent(sprite))
            
          case _ => 
        }
      }

object SpriteSystem:
  def apply(): SpriteSystem = SpriteSystemImpl()
