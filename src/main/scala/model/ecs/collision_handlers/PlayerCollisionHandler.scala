package model.ecs.collision_handlers
import model.ecs.components.*
import model.ecs.entities.player.SlugEntity
import model.ecs.entities.weapons.{PlayerBulletEntity, WeaponEntity}
import model.ecs.entities.{Entity, EntityManager}
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

trait PlayerCollisionHandler extends BasicCollisionHandler:
  self: Entity =>

  override protected def handleSpecialCollision(collidingEntity: Option[Entity]): Unit =
    collidingEntity match
      case Some(weaponEntity) if weaponEntity.isInstanceOf[WeaponEntity] =>
        EntityManager().removeEntity(weaponEntity)
        this.replaceComponent(BulletComponent(MachineGunBullet()))
      case Some(slugEntity) if slugEntity.isInstanceOf[SlugEntity] =>
        EntityManager().removeEntity(slugEntity)
        this.addComponent(SlugComponent())
      case _ => ()