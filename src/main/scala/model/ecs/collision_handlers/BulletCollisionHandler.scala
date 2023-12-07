package model.ecs.collision_handlers

import model.ecs.collision_handlers.CollisionHandler
import model.ecs.components.PositionComponent
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.EnemyBulletEntity
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionChecker

trait BulletCollisionHandler extends CollisionHandler:
  self: Entity =>

  private def handleEnemyCollision(enemyEntity: EnemyEntity): Unit =
    EntityManager.removeEntity(enemyEntity)
    EntityManager.removeEntity(this)

  private def handlePlayerCollision(playerEntity: PlayerEntity): Unit =
    if this.isInstanceOf[EnemyBulletEntity] then
      EntityManager.removeEntity(playerEntity)
      EntityManager.removeEntity(this)

  override def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent] =
    val collidingEntity = CollisionChecker
      .getCollidingEntity(this, proposedPosition)
    if collidingEntity.isEmpty && !CollisionChecker.isOutOfHorizontalBoundaries(
        proposedPosition
      )
    then Some(proposedPosition)
    else
      collidingEntity match
        case Some(enemyEntity: EnemyEntity) => handleEnemyCollision(enemyEntity)
        case Some(playerEntity: PlayerEntity) =>
          handlePlayerCollision(playerEntity)
        case _ =>
          EntityManager.removeEntity(this)
      None
