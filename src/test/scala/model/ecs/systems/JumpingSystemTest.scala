package model.ecs.systems

import model.ecs.components.{
  JumpingComponent,
  PositionComponent,
  SizeComponent,
  VelocityComponent
}
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.{Entity, EntityManager}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter

class JumpingSystemTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  "A JumpingSystem" should "set JumpingComponent to false when an entity with JumpingComponent reaches the ground" in {
    val jumpingSystem = JumpingSystem()
    val entity = BoxEntity()
      .addComponent(JumpingComponent(true))
      .addComponent(PositionComponent(0, model.GUIHEIGHT))
      .addComponent(VelocityComponent(0, 10))
      .addComponent(SizeComponent(10, 10))

    EntityManager.addEntity(entity)

    jumpingSystem.update()

    entity.getComponent[JumpingComponent].get.isJumping shouldBe false
  }

  it should "throw IllegalStateException when an entity with JumpingComponent does not have PositionComponent" in {
    val jumpingSystem = JumpingSystemImpl()
    val entity = BoxEntity()
      .addComponent(JumpingComponent(false))

    EntityManager.addEntity(entity)

    assertThrows[IllegalStateException] {
      jumpingSystem.update()
    }
  }
}
