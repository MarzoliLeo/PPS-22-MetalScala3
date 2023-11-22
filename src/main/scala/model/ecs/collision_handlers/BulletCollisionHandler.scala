package model.ecs.collision_handlers

import model.ecs.entities.Entity
import model.ecs.entities.EntityManager

trait BulletCollisionHandler() extends CollisionHandler:
  def handleCollision(entity: Entity): Unit =
    EntityManager().removeEntity(entity)
