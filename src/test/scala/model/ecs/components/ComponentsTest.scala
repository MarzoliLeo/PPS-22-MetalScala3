package model.ecs.components

import model.ecs.components.{PositionComponent, VelocityComponent}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ComponentsTest extends AnyFlatSpec with Matchers {
  "A PositionComponent" should "update its position correctly given a VelocityComponent and elapsed time" in {
    val position = PositionComponent(0, 0)
    val velocity = VelocityComponent(1, 1)
    val updatedPosition = position.getUpdatedPosition(1000)(using velocity)
    updatedPosition shouldBe PositionComponent(1, 1)
  }

  it should "add a VelocityComponent correctly" in {
    val position = PositionComponent(0, 0)
    val velocity = VelocityComponent(1, 1)
    val newPosition = position + velocity
    newPosition shouldBe PositionComponent(1, 1)
  }

  "A VelocityComponent" should "add another VelocityComponent correctly" in {
    val v1 = VelocityComponent(1, 1)
    val v2 = VelocityComponent(2, 2)
    val sum = v1 + v2
    sum shouldBe VelocityComponent(3, 3)
  }

  it should "multiply by a factor correctly" in {
    val velocity = VelocityComponent(1, 1)
    val product = velocity * 2
    product shouldBe VelocityComponent(2, 2)
  }
}
