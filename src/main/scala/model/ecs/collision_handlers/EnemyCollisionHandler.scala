package model.ecs.collision_handlers

import model.ecs.entities.Entity
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

trait EnemyCollisionHandler extends BasicCollisionHandler:
  self: Entity =>