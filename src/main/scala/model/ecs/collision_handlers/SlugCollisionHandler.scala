package model.ecs.collision_handlers

import model.ecs.components.{BulletComponent, CollisionComponent, JumpingComponent, MachineGunBullet}
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.entities.weapons.WeaponEntity

trait SlugCollisionHandler extends BasicCollisionHandler:
  self: Entity =>

  override protected def handleSpecialCollision(collidingEntity: Option[Entity]): Unit =
    collidingEntity match
      case Some(weaponEntity: WeaponEntity) =>
        EntityManager().removeEntity(weaponEntity)
      case Some(boxEntity: BoxEntity) =>
        this.replaceComponent(CollisionComponent(true))
        this.replaceComponent(JumpingComponent(false))
      case _ => ()