package model.ecs.collision_handlers
import model.ecs.components.*
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
      case Some(bulletEntity: PlayerBulletEntity) => ()
      case Some(weaponEntity: WeaponEntity) =>
        EntityManager().removeEntity(weaponEntity)
        this.replaceComponent(AmmoComponent(ammoBoxRefill))
        this.replaceComponent(BulletComponent(MachineGunBullet()))
      case Some(ammoBoxEntity: AmmoBoxEntity) =>
        EntityManager().removeEntity(ammoBoxEntity)
        val ammoBoxComponent: AmmoComponent = ammoBoxEntity.getComponent[AmmoComponent] match {
            case Some(ammoComponent) =>
              this.getComponent[BulletComponent] match
                case Some(currentBullet)
                    if currentBullet.bullet == MachineGunBullet() =>
                  ammoComponent
                case Some(currentBullet)
                    if currentBullet.bullet == StandardBullet() =>
                  ammoComponent.copy(0)
                case _ => throw new Exception("Bullet component not found")
            case None => throw new Exception("Ammo component not found")
          }
        val currentAmmo = this.getComponent[AmmoComponent] match {
          case Some(ammoComponent) => ammoComponent
          case None => throw new Exception("Ammo component not found")
        }
        this.replaceComponent(
          AmmoComponent(currentAmmo.ammo + ammoBoxComponent.ammo)
        )
      case Some(slugEntity: SlugEntity) =>
        EntityManager().removeEntity(slugEntity)
        this.addComponent(SlugComponent())
      case Some(boxEntity: BoxEntity) =>
        this.replaceComponent(JumpingComponent(false))
      case _ =>

