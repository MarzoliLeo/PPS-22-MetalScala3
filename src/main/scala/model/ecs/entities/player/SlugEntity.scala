package model.ecs.entities.player

import model.ecs.collision_handlers.*
import model.ecs.entities.Entity

case class SlugEntity() extends Entity with SlugCollisionHandler 
