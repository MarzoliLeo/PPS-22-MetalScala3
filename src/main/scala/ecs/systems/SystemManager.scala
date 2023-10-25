package ecs.systems

import ecs.entities.EntityManager

trait SystemManager:
  var systems: List[System]
  def addSystem(system: System): Unit
  def removeSystem(system: System): Unit
  def updateAll(): Unit

private class SystemManagerImpl(var entityManager: EntityManager)
    extends SystemManager:

  var systems: List[System] = List()

  addSystem(PlayerMovementSystem())

  override def addSystem(system: System): Unit =
    systems = systems :+ system

  override def removeSystem(system: System): Unit =
    systems = systems.filterNot(_ == system)

  override def updateAll(): Unit =
    systems.foreach(_.update(entityManager))


object SystemManager:
  def apply(entityManager: EntityManager): SystemManager = SystemManagerImpl(
    entityManager
  )
