package ecs

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.Assertions._

class EntityTest extends AnyFunSuite {

  val player: Entity = Entity(Position(0, 0))

  test("entity has position component") {
    assert(player.hasComponent(classOf[Position]))
    assert(player.getComponent(classOf[Position]) == Position(0, 0))
  }

  test("remove position component") {
    player.removeComponent(classOf[Position])
    assert(!player.hasComponent(classOf[Position]))
  }

  test("replace position component") {
    val newPosition = Position(1, 1)
    player.replaceComponent(newPosition)
    assert(player.getComponent(classOf[Position]) == newPosition)
  }
}