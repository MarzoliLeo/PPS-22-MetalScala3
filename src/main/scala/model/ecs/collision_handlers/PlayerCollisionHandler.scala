package model.ecs.collision_handlers
import model.ecs.components.*
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.SlugEntity
import model.ecs.entities.weapons.{
  AmmoBoxEntity,
  PlayerBulletEntity,
  WeaponEntity
}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionChecker
import model.*

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
    this.replaceComponent(SpecialWeaponAmmoComponent(ammoBoxRefill))
    this.replaceComponent(BulletComponent(MachineGunBullet()))
  }

  private def handleAmmoBoxEntityCollision(
      ammoBoxEntity: AmmoBoxEntity
  ): Unit = {
    EntityManager().removeEntity(ammoBoxEntity)
    (
      ammoBoxEntity.getComponent[SpecialWeaponAmmoComponent],
      this.getComponent[BulletComponent],
      this.getComponent[SpecialWeaponAmmoComponent],
      this.getComponent[BombAmmoComponent]
    ) match {
      case (
            Some(ammoBoxComponent),
            Some(bulletComponent),
            Some(currentAmmo),
            Some(currentBombAmmo)
          ) =>
        updateComponents(
          ammoBoxComponent,
          bulletComponent,
          currentAmmo,
          currentBombAmmo
        )
      case _ => throw Exception(s"Missing components needed for handling collision from $this")
    }
  }

  private def updateComponents(
      ammoBoxComponent: SpecialWeaponAmmoComponent,
      bulletComponent: BulletComponent,
      currentAmmo: SpecialWeaponAmmoComponent,
      currentBombAmmo: BombAmmoComponent
  ): Unit = {
    val ammoInBox = ammoBoxComponent.ammo
    bulletComponent.bullet match {
      case _: MachineGunBullet =>
        this.replaceComponent(
          SpecialWeaponAmmoComponent(currentAmmo.ammo + ammoInBox)
        )
      case _ => ()
    }
    this.replaceComponent(BombAmmoComponent(currentBombAmmo.ammo + ammoInBox))
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
