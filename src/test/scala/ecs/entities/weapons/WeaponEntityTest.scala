package ecs.entities.weapons

import model.ecs.components.{HealthComponent, PositionComponent, VelocityComponent}
import model.ecs.entities.Entity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{MachineGunEntity, WeaponEntity}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WeaponEntityTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  var weapon: WeaponEntity = new WeaponEntity {
    override val damage: Int = 1

    override def attack(target: Entity): Unit =
      target.getComponent[HealthComponent] match {
        case Some(health) =>
          health.currentHealth -= damage
          if (health.currentHealth < 0) health.currentHealth = 0
        case None => // Do nothing if the target doesn't have a HealthComponent
      }
  }

  "A WeaponEntity" should "have correct damage" in {
    assert(weapon.damage == 1)
  }
  it should "attack target entity" in {
    val target = PlayerEntity()
      .addComponent(PositionComponent(0, 0))
      .addComponent(HealthComponent(1))
    weapon.attack(target)

    assert(target.getComponent[HealthComponent].get.currentHealth == 0)
  }
}
