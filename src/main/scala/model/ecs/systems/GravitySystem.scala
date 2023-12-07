package model.ecs.systems

import model.ecs.collision_handlers.EnemyCollisionHandler
import model.ecs.components.{CollisionComponent, GravityComponent, JumpingComponent, PositionComponent, VelocityComponent}
import model.ecs.entities.EntityManager
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{EnemyBulletEntity, WeaponEntity}
import model.{GRAVITY_VELOCITY, VERTICAL_COLLISION_SIZE, isGravityEnabled}
import model.ecs.entities.Entity

trait GravitySystem extends SystemWithElapsedTime

private class GravitySystemImpl extends GravitySystem:
  override def update(elapsedTime: Long): Unit =
    EntityManager
      .getEntitiesWithComponent(
        classOf[GravityComponent],
        classOf[VelocityComponent],
        classOf[PositionComponent]
      )
      .map { entity =>
        val position = entity.getComponent[PositionComponent].get
        val velocity = entity.getComponent[VelocityComponent].get
        val collision = entity.getComponent[CollisionComponent].get
        val isTouchingGround = position.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0

        (entity, velocity, collision, isTouchingGround)
      }
      .foreach { case (entity, velocity, collision, isOnGround) =>
        val updatedEntity = if (isOnGround) {
          resetComponents(entity, velocity)
        } else {
          if (!collision.isColliding) {
            applyGravity(entity, velocity, elapsedTime)
          } else {
            resetGravity(entity, velocity)
          }
        }
      }

  private def resetComponents(entity: Entity, velocity: VelocityComponent): Entity =
    entity
      .replaceComponent(VelocityComponent(velocity.x, 0))
      .replaceComponent(GravityComponent(0))
      .replaceComponent(JumpingComponent(false))

  private def applyGravity(entity: Entity, velocity: VelocityComponent, elapsedTime: Long): Entity =
    entity
      .replaceComponent(GravityComponent(GRAVITY_VELOCITY))
      .replaceComponent(velocity + VelocityComponent(0.0 , entity.getComponent[GravityComponent].get.gravity * elapsedTime))

  private def resetGravity(entity: Entity, velocity: VelocityComponent): Entity =
    entity
      .replaceComponent(GravityComponent(0))
      .replaceComponent(JumpingComponent(false))
      .replaceComponent(VelocityComponent(0.0, entity.getComponent[GravityComponent].get.gravity))

object GravitySystem:
  def apply(): GravitySystem = GravitySystemImpl()