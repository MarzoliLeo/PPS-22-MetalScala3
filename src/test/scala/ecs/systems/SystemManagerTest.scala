package ecs.systems

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ecs.entities.EntityManager
import org.mockito.Mockito._

class SystemManagerTest extends AnyFlatSpec with Matchers {
  "A SystemManager" should "be able to add and remove a system" in {
    val entityManager = mock(classOf[EntityManager])
    val systemManager = SystemManager(entityManager)
    val system1 = PlayerMovementSystem()

    systemManager.addSystem(system1)
    systemManager.systems should contain (system1)

    systemManager.removeSystem(system1)
    systemManager.systems should not contain system1
  }

  it should "call update on all systems when updateAll is called" in {
    val entityManager = mock(classOf[EntityManager])
    val systemManager = SystemManager(entityManager)
    val system1 = mock(classOf[System])
    val system2 = mock(classOf[System])

    systemManager.addSystem(system1)
    systemManager.addSystem(system2)

    systemManager.updateAll()

    verify(system1).update(entityManager)
    verify(system2).update(entityManager)
  }
}