package ecs.entities

import ecs.components.PositionComponent
import ecs.entities.Entity
import org.scalatest.Assertions.*
import org.scalatest.funsuite.AnyFunSuite

class EntityTest extends AnyFunSuite {

  val player: Entity = PlayerEntity(PositionComponent(0, 0))

  test("entity has position component") {
    assert(player.hasComponent(classOf[PositionComponent]))
    assert(player.getComponent(classOf[PositionComponent]).contains(PositionComponent(0, 0)))
  }

  test("remove position component") {
    player.removeComponent(classOf[PositionComponent])
    assert(!player.hasComponent(classOf[PositionComponent]))
  }

  test("replace position component") {
    val newPosition = PositionComponent(1, 1)
    player.replaceComponent(newPosition)
    assert(player.getComponent(classOf[PositionComponent]).contains(newPosition))
  }
}