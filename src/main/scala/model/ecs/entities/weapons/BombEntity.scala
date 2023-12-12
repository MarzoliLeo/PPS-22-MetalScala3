package model.ecs.entities.weapons

import model.ecs.collision_handlers.PlayerBulletCollisionHandler
import model.ecs.entities.Entity

case class BombEntity() extends Entity with PlayerBulletCollisionHandler
