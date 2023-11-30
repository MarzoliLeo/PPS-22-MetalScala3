package model.ecs.collision_handlers
import model.ecs.components.*
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.weapons.{AmmoBoxEntity, PlayerBulletEntity, WeaponEntity}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionChecker
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity, isImmediatelyAbove}
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}
import model.ammoBoxRefill
import model.ecs.entities.environment.BoxEntity

import scala.language.postfixOps

trait PlayerCollisionHandler extends BasicCollisionHandler:
  self: Entity =>

  override protected def handleSpecialCollision(
      collidingEntity: Option[Entity]
  ): Unit =
    collidingEntity match
      case Some(weaponEntity) if weaponEntity.isInstanceOf[WeaponEntity] =>
        EntityManager().removeEntity(weaponEntity)
        this.replaceComponent(AmmoComponent(ammoBoxRefill))
        this.replaceComponent(BulletComponent(MachineGunBullet()))
      case Some(ammoBoxEntity: AmmoBoxEntity) =>
        EntityManager().removeEntity(ammoBoxEntity)
        val ammoBoxComponent = ammoBoxEntity.getComponent[AmmoComponent] match {
          case Some(ammoComponent) => ammoComponent
          case None => throw new Exception("Ammo component not found")
        }
        val currentAmmo = this.getComponent[AmmoComponent] match {
          case Some(ammoComponent) => ammoComponent
          case None => throw new Exception("Ammo component not found")
        }
        this.replaceComponent(AmmoComponent(currentAmmo.ammo + ammoBoxComponent.ammo))
      case Some(boxEntity: BoxEntity) =>
        if isImmediatelyAbove(this, boxEntity) then
          println("Player is on top of box")
          this.replaceComponent(GravityComponent(0))
      case _ => ()