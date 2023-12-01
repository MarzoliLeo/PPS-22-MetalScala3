package model.ecs.collision_handlers

import model.ecs.components.{BulletComponent, MachineGunBullet}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.entities.weapons.WeaponEntity

trait SlugCollisionHandler extends BasicCollisionHandler:
  self: Entity =>

  override protected def handleSpecialCollision(collidingEntity: Option[Entity]): Unit =
    collidingEntity match
      case Some(weaponEntity) if weaponEntity.isInstanceOf[WeaponEntity] =>
        EntityManager().removeEntity(weaponEntity)
      case _ => ()