package model.ecs.entities.weapons

import model.ecs.collision_handlers.{
  BasicCollisionHandler,
  PlayerCollisionHandler
}
import model.ecs.entities.Entity

/** A weapon usable from a player. It has a damage value and can be used to
  * attack other entities
  */

//Definisco un parametro damage che potrò modificare da altri trait.
trait WeaponEntity extends Entity:
  val damage: Int

//Questo è un mixin perché è un trait che utilizza campi  e metodi di un altro trait.
trait ShootingWeaponEntity extends WeaponEntity

//Questo è a sua volta un mixin perché utilizza il trait che già era un mixin per overridare il campo damage.
case class MachineGunEntity()
    extends ShootingWeaponEntity
    with BasicCollisionHandler:
  val damage = 1
