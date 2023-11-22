package model.ecs.collision_handlers

import model.ecs.entities.Entity

trait CollisionHandler:
    def handleCollision(entity: Entity): Unit