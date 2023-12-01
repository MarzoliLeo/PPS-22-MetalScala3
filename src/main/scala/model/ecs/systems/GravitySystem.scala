package model.ecs.systems

import model.ecs.collision_handlers.EnemyCollisionHandler
import model.ecs.components.{GravityComponent, JumpingComponent, PositionComponent, VelocityComponent}
import model.ecs.entities.EntityManager
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{EnemyBulletEntity, WeaponEntity}
import model.ecs.systems.CollisionChecker.isImmediatelyAboveAPlatform
import model.{GRAVITY_VELOCITY, VERTICAL_COLLISION_SIZE}

trait GravitySystem extends SystemWithElapsedTime

private class GravitySystemImpl extends GravitySystem:
  override def update(elapsedTime: Long): Unit =
    EntityManager()
      .getEntitiesWithComponent(
        classOf[GravityComponent],
        classOf[VelocityComponent],
        classOf[PositionComponent]
      )
      .map { entity =>
        if entity.isInstanceOf[BoxEntity] then
          throw new Exception("BoxEntity should not have gravity")

        val position = entity.getComponent[PositionComponent].get
        val velocity = entity.getComponent[VelocityComponent].get
        val isTouchingGround =
          position.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0

        (entity, velocity, isTouchingGround || isImmediatelyAboveAPlatform(entity).isDefined)
      }
      .foreach { case (entity, velocity, isOnGround) =>
        if isOnGround then
          entity
            .replaceComponent(VelocityComponent(velocity.x, 0))
            .replaceComponent(GravityComponent(0))
            .replaceComponent(JumpingComponent(false))
        else
          entity
            .replaceComponent(GravityComponent(GRAVITY_VELOCITY))
            .replaceComponent(
              velocity + VelocityComponent(
                0,
                entity.getComponent[GravityComponent].get.gravity * elapsedTime
              )
            )
      }

object GravitySystem:
  def apply(): GravitySystem = GravitySystemImpl()
