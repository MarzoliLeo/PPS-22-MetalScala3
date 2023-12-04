package model.ecs.collision_handlers
import model.ecs.components._
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.SlugEntity
import model.ecs.entities.weapons.{AmmoBoxEntity, PlayerBulletEntity, WeaponEntity}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionChecker
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE, ammoBoxRefill, isGravityEnabled}

trait PlayerCollisionHandler extends BasicCollisionHandler:
  self: Entity =>

  override protected def handleSpecialCollision(
      collidingEntity: Option[Entity]
  ): Unit =
    collidingEntity match
      case Some(_: PlayerBulletEntity) => ()
      case Some(weaponEntity: WeaponEntity) =>
        handleWeaponEntityCollision(weaponEntity)
      case Some(ammoBoxEntity: AmmoBoxEntity) =>
        handleAmmoBoxEntityCollision(ammoBoxEntity)
      case Some(slugEntity: SlugEntity) =>
        handleSlugEntityCollision(slugEntity)
      case Some(boxEntity: BoxEntity) =>
        handleBoxEntityCollision(boxEntity)
      case _ =>

  private def handleWeaponEntityCollision(weaponEntity: WeaponEntity): Unit = {
    EntityManager().removeEntity(weaponEntity)
    this.replaceComponent(AmmoComponent(ammoBoxRefill))
    this.replaceComponent(BulletComponent(MachineGunBullet()))
  }

  private def handleAmmoBoxEntityCollision(ammoBoxEntity: AmmoBoxEntity): Unit = {
    EntityManager().removeEntity(ammoBoxEntity)
    for {
      ammoBoxComponent <- ammoBoxEntity.getComponent[AmmoComponent]
      currentBullet <- this.getComponent[BulletComponent]
      currentAmmo <- this.getComponent[AmmoComponent]
    } yield {
      val newAmmoBoxComponent = currentBullet.bullet match {
        case MachineGunBullet() => ammoBoxComponent
        case StandardBullet() => ammoBoxComponent.copy(0)
      }
      this.replaceComponent(AmmoComponent(currentAmmo.ammo + newAmmoBoxComponent.ammo))
    }
  }

  private def handleSlugEntityCollision(slugEntity: SlugEntity): Unit = {
    EntityManager().removeEntity(slugEntity)
    this.addComponent(SlugComponent())
  }

  private def handleBoxEntityCollision(boxEntity: BoxEntity): Unit = {
    for {
      boxPosition <- boxEntity.getComponent[PositionComponent]
      thisPosition <- this.getComponent[PositionComponent]
      if boxPosition.y > thisPosition.y + VERTICAL_COLLISION_SIZE
    } yield {
      this.replaceComponent(CollisionComponent(true))
      this.replaceComponent(JumpingComponent(false))
    }
  }