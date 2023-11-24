package model.ecs.entities.player

import model.ecs.collision_handlers.{PlayerCollisionHandler, CollisionHandler}
import model.ecs.components.Component
import model.ecs.entities.Entity

case class PlayerEntity() extends Entity with PlayerCollisionHandler