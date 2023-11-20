package model.ecs.entities.weapons

import model.ecs.entities.Entity

/** A weapon usable from a player. It has a damage value and can be used to
  * attack other entities
  */
trait WeaponEntity extends Entity:
  val damage: Int
trait ShootingWeaponEntity extends WeaponEntity

case class MachineGunEntity() extends ShootingWeaponEntity:
  val damage = 1
