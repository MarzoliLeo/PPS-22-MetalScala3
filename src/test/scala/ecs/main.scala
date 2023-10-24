package ecs

import org.junit.Test
import org.junit.Assert.*

class EntityTest {

  val player: Entity = Entity(Position(0, 0))

  @Test
  def entityCreationTest(): Unit =
    assertTrue(player.hasComponent(classOf[Position]))
    assertEquals(player.getComponent(classOf[Position]).get, Position(0,0))

  @Test
  def removeComponentTest(): Unit =
    player.removeComponent(classOf[Position])
    assertEquals(player.getComponent(classOf[Position]), None)

  @Test
  def replaceComponentTest(): Unit =
    val newPosition = Position(1,1)
    player.replaceComponent(newPosition)
    assertEquals(player.getComponent(classOf[Position]).get, newPosition)
}




