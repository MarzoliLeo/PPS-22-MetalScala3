package model.ecs.entities.enemies

import model.ecs.collision_handlers.PlayerCollisionHandler
import model.ecs.entities.Entity

case class EnemyEntity() extends Entity with PlayerCollisionHandler
