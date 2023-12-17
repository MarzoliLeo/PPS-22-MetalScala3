package model.ecs.systems

import model.ecs.components.{CollisionComponent, GravityComponent, JumpingComponent, PositionComponent, VelocityComponent}
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.{Entity, EntityManager}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.ecs.systems.GravitySystem
import model.ecs.entities.player.PlayerEntity

class GravitySystemTest extends AnyFlatSpec with Matchers {
    val gravitySystem: GravitySystem = GravitySystem()

    "A GravitySystem" should "update entities correctly" in {
      val gravitySystem = GravitySystem()
      val boxEntity = BoxEntity()
        .addComponent(VelocityComponent(0, 0))
        .addComponent(GravityComponent(0))
        .addComponent(PositionComponent(0, 0))
        .addComponent(CollisionComponent())

      EntityManager.addEntity(boxEntity)

      val elapsedTime = 1000
      gravitySystem.update(elapsedTime)

      boxEntity.getComponent[GravityComponent] shouldBe Some(GravityComponent(model.GRAVITY_VELOCITY))
      boxEntity.getComponent[VelocityComponent] shouldBe Some(VelocityComponent(0, model.GRAVITY_VELOCITY * elapsedTime))
    }
}