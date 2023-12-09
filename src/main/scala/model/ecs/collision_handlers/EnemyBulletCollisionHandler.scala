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

trait EnemyBulletCollisionHandler extends CollisionHandler:
  self: EnemyBulletEntity =>

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
        case Some(_: EnemyEntity) =>
          Some(proposedPosition)
        case Some(playerEntity: PlayerEntity) =>
          handlePlayerCollision(playerEntity)
        case _ =>
          EntityManager.removeEntity(this)
          None

  /** Handles player collision by removing the player entity and the caller
    * entity from the EntityManager, and returns the PositionComponent of the
    * caller entity.
    *
    * @param playerEntity
    *   The player entity to be collided with.
    * @return
    *   The PositionComponent of the caller entity.
    */
  private def handlePlayerCollision(
      playerEntity: PlayerEntity
  ): Option[PositionComponent] =
    EntityManager.removeEntity(playerEntity)
    EntityManager.removeEntity(this)
    this.getComponent[PositionComponent]