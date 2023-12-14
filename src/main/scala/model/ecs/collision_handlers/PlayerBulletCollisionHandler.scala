package model.ecs.collision_handlers

import model.ecs.collision_handlers.CollisionHandler
import model.ecs.components.PositionComponent
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{
  BombEntity,
  EnemyBulletEntity,
  PlayerBulletEntity
}
import model.ecs.entities.{Entity, EntityManager}

trait PlayerBulletCollisionHandler extends CollisionHandler:
  self: PlayerBulletEntity | BombEntity =>

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
        case Some(_: PlayerEntity) =>
          Some(proposedPosition)
        case Some(enemyEntity: EnemyEntity) =>
          handleEnemyCollision(enemyEntity)
        case _ =>
          EntityManager.removeEntity(this)
          None

  /** Handles enemy collision by removing the enemy entity and the caller entity
    * from the EntityManager, and returns the PositionComponent of the caller
    * entity.
    *
    * @param enemyEntity
    *   The enemy entity to be collided with.
    * @return
    *   The PositionComponent of the caller entity.
    */
  private def handleEnemyCollision(
      enemyEntity: EnemyEntity
  ): Option[PositionComponent] =
    EntityManager.removeEntity(enemyEntity)
    EntityManager.removeEntity(this)
    this.getComponent[PositionComponent]
