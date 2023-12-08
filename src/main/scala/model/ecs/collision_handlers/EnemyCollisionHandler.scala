package model.ecs.collision_handlers

import model.ecs.components.{CollisionComponent, JumpingComponent}
import model.ecs.entities.Entity
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.weapons.WeaponEntity
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

trait EnemyCollisionHandler extends BasicCollisionHandler:
  self: Entity =>
  override protected def handleSpecialCollision(collidingEntity: Option[Entity]): Unit =
    collidingEntity match
      case Some(_: BoxEntity) =>
        this.replaceComponent(CollisionComponent(true))
        this.replaceComponent(JumpingComponent(false))
      case _ => ()