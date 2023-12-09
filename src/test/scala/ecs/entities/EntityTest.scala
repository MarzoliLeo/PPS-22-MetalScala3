package ecs.entities

import model.ecs.components.PositionComponent
import model.ecs.entities.Entity
import model.ecs.entities.player.PlayerEntity
import org.scalatest.Assertions.*
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EntityTest extends AnyFunSuite with Matchers with BeforeAndAfter{

  var player: Entity = _

  before {
    player = PlayerEntity().addComponent(PositionComponent(0, 0))
  }

  test("entity has position component") {
    assert(player.hasComponent(classOf[PositionComponent]))
    assert(player.getComponent[PositionComponent].contains(PositionComponent(0, 0)))
  }

  test("remove position component") {
    player.removeComponent(classOf[PositionComponent])
    assert(!player.hasComponent(classOf[PositionComponent]))
  }

  test("replace position component") {
    val newPosition = PositionComponent(1, 1)
    player.replaceComponent(newPosition)
    assert(player.getComponent[PositionComponent].contains(newPosition))
  }
}