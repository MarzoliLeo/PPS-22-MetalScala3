package model.ecs.entities

import model.ecs.collision_handlers.PlayerCollisionHandler

case class EnemyEntity() extends Entity with PlayerCollisionHandler
