package model.ecs.entities.enemies

import model.ecs.collision_handlers.PlayerCollisionHandler
import model.ecs.entities.Entity
import model.ecs.collision_handlers.EnemyCollisionHandler

case class EnemyEntity() extends Entity with EnemyCollisionHandler
