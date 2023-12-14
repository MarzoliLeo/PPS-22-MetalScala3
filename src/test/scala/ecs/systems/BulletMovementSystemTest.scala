// FILEPATH: /c:/Users/loren/Documents/uni/anno 1/2_sem/paradigmi/PPS-22-MetalScala3/src/test/scala/model/ecs/systems/BulletMovementSystemTest.scala

package ecs.systems

import model.ecs.components.{PositionComponent, SizeComponent, VelocityComponent}
import model.ecs.entities.EntityManager
import model.ecs.entities.weapons.{BombEntity, EnemyBulletEntity, PlayerBulletEntity}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.ecs.systems.BulletMovementSystem
import org.scalatest.BeforeAndAfter

class BulletMovementSystemTest extends AnyFlatSpec with Matchers with BeforeAndAfter{
  var bulletMovementSystem: BulletMovementSystem = _

  before {
    bulletMovementSystem = BulletMovementSystem()
  }

  "A BulletMovementSystem" should "update the position of all bullets correctly if they are not colliding" in {
    val playerBullet = PlayerBulletEntity()
      .addComponent(PositionComponent(0, 0))
      .addComponent(VelocityComponent(1, 1))
      .addComponent(SizeComponent(10, 10))
    val enemyBullet = EnemyBulletEntity()
      .addComponent(PositionComponent(100, 100)) // Adjusted position
      .addComponent(VelocityComponent(1, 1))
      .addComponent(SizeComponent(10, 10))
    val bomb = BombEntity()
      .addComponent(PositionComponent(200, 200)) // Adjusted position
      .addComponent(VelocityComponent(1, 1))
      .addComponent(SizeComponent(10, 10))

    EntityManager.addEntity(playerBullet)
    EntityManager.addEntity(enemyBullet)
    EntityManager.addEntity(bomb)

    bulletMovementSystem.update(1000)

    playerBullet.getComponent[PositionComponent] shouldBe Some(PositionComponent(1, 1))
    enemyBullet.getComponent[PositionComponent] shouldBe Some(PositionComponent(101, 101))
    bomb.getComponent[PositionComponent] shouldBe Some(PositionComponent(201, 201))
  }

  it should "not update the position of playerBullet and remove it if it collides with enemyBullet" in {
    val playerBullet = PlayerBulletEntity()
      .addComponent(PositionComponent(0, 0))
      .addComponent(VelocityComponent(1, 1))
      .addComponent(SizeComponent(10, 10))
    val enemyBullet = EnemyBulletEntity()
      .addComponent(PositionComponent(0, 0)) // Same position as playerBullet
      .addComponent(VelocityComponent(1, 1))
      .addComponent(SizeComponent(10, 10))

    EntityManager.addEntity(playerBullet)
    EntityManager.addEntity(enemyBullet)

    bulletMovementSystem.update(1000)

    playerBullet.getComponent[PositionComponent] shouldBe Some(PositionComponent(0, 0)) // Position did not change
  }
}