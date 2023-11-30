package model.ecs.collision_handlers
import model.ecs.components.*
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.weapons.{
  AmmoBoxEntity,
  PlayerBulletEntity,
  WeaponEntity
}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionChecker
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

trait PlayerCollisionHandler extends BasicCollisionHandler:
  self: Entity =>

  override protected def handleSpecialCollision(
      collidingEntity: Option[Entity]
  ): Unit =
    collidingEntity match
      case Some(weaponEntity) if weaponEntity.isInstanceOf[WeaponEntity] =>
        EntityManager().removeEntity(weaponEntity)
        this.replaceComponent(BulletComponent(MachineGunBullet()))
      case Some(ammoBoxEntity: AmmoBoxEntity) =>
        EntityManager().removeEntity(ammoBoxEntity)
        val ammoBoxComponent = ammoBoxEntity.getComponent[AmmoComponent] match {
          case Some(ammoComponent) => ammoComponent
          case None => throw new Exception("Ammo component not found")
        }
        this.replaceComponent(ammoBoxComponent)
        println("Current player ammo: " + this.getComponent[AmmoComponent].get)
      case _ => ()
