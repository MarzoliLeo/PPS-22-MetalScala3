package ecs.entities.weapons

import model.ecs.components.{HealthComponent, PositionComponent, VelocityComponent}
import model.ecs.entities.Entity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{MachineGunEntity, WeaponEntity}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WeaponEntityTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  var weapon: WeaponEntity = MachineGunEntity()

  "A MachineGunEntity" should "have damage equal to 1" in {
    assert(weapon.damage == 1)
  }
}
