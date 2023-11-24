package model.ecs.entities.environment

import model.ecs.collision_handlers.PlayerCollisionHandler
import model.ecs.entities.Entity

case class BoxEntity() extends Entity with PlayerCollisionHandler