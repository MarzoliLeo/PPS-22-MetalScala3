package model.ecs.collision_handlers
import model.ecs.components.{JumpingComponent, PositionComponent, SizeComponent, VelocityComponent}
import model.ecs.entities.Entity
import model.ecs.systems.CollisionChecker
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

trait EnemyCollisionHandler extends BasicCollisionHandler:
  self: Entity =>