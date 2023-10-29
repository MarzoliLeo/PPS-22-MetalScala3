package model.ecs.systems

import model.ecs.components.GravityComponent
import model.ecs.entities.{BoxEntity, EntityManager}

trait SystemManager:
  protected type System = EntityManager => Unit

  var systems: List[System]
  def addSystem(system: System): SystemManager
  def removeSystem(system: System): SystemManager
  def updateAll(): Unit

private class SystemManagerImpl(var entityManager: EntityManager ) extends SystemManager:
  var systems: List[System] = List()

  override def addSystem(system: System): SystemManager =
    systems = systems :+ system
    this

  override def removeSystem(system: System): SystemManager =
    systems = systems.filterNot(_ == system)
    this

  override def updateAll(): Unit =
    systems.foreach(_(entityManager))


object SystemManager:
  def apply(entityManager: EntityManager): SystemManager = SystemManagerImpl(
    entityManager
  )
