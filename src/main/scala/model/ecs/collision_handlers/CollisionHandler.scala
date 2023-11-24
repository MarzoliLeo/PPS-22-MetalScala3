package model.ecs.collision_handlers

import model.ecs.components.PositionComponent
import model.ecs.entities.Entity

trait CollisionHandler:
  self: Entity => // Each implementation of CollisionHandler must be extended by an Entity
  /** Handles a collision event.
    *
    * @param proposedPosition
    *   The proposed position before the collision is handled.
    * @return
    *   An optional PositionComponent representing the new position after
    *   handling the collision. Returns None if no position update is necessary.
    */
  def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent]
