package model.ecs.collision_handlers

import model.ecs.components.PositionComponent
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.weapons.{EnemyBulletEntity, PlayerBulletEntity}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionChecker
import model.{GUIWIDTH, HORIZONTAL_COLLISION_SIZE}

/** Handles collisions involving the BulletEntity.
  *
  * This trait must be mixed in with an Entity class to provide collision
  * handling functionality specific to bullet entities.
  */
trait BulletCollisionHandler extends CollisionHandler:
  self: Entity =>

  /** Removes the current entity from the entity manager.
    *
    * This method should be called when a collision occurs and the entity needs
    * to be removed from the game.
    *
    * Note that this method removes the entity immediately without any
    * additional checks.
    *
    * @note
    *   The entity will be removed if its bullet position exceeds the GUI width
    *   or if it goes below 0 in the x-axis.
    */
  override def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent] = {
    val collidingEntity = CollisionChecker
      .getCollidingEntity(this, proposedPosition)
    if collidingEntity.isEmpty && !CollisionChecker.isOutOfHorizontalBoundaries(
        proposedPosition
      )
    then Some(proposedPosition)
    else
      collidingEntity match
        case Some(collidingEntity)
            if collidingEntity.isInstanceOf[EnemyEntity] && this
              .isInstanceOf[EnemyBulletEntity] => ()
        case Some(collidingEntity)
            if collidingEntity.isInstanceOf[EnemyEntity] && this
              .isInstanceOf[PlayerBulletEntity] =>
          EntityManager().removeEntity(collidingEntity)
        case _ => ()
      EntityManager().removeEntity(this)
      None
  }
