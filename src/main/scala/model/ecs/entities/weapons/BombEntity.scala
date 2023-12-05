package model.ecs.entities.weapons

import model.ecs.collision_handlers.BulletCollisionHandler
import model.ecs.entities.Entity

case class BombEntity() extends Entity with BulletCollisionHandler
