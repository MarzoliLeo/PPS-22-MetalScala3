package model.ecs.systems

import model.ecs.components.GravityComponent
import model.ecs.entities.weapons.PlayerBulletEntity
import model.ecs.entities.EntityManager
import model.ecs.entities.player.PlayerEntity


trait SystemManager {
  var systems: List[System]
  def addSystem(system: System): SystemManager
  def removeSystem(system: System): SystemManager
  def updateAll(elapsedTime: Long): Unit
}

private case class SystemManagerImpl(var entityManager: EntityManager)
  extends SystemManager {
  var systems: List[System] = List()

  override def addSystem(system: System): SystemManager = {
    systems = systems :+ system
    this
  }

  override def removeSystem(system: System): SystemManager = {
    systems = systems.filterNot(_ == system)
    this
  }

  override def updateAll(elapsedTime: Long): Unit = {
    systems.foreach {
      case sys: SystemWithoutTime => sys.update()
      case sys: SystemWithElapsedTime => sys.update(elapsedTime)
    }
  }
}

object SystemManager {
  private val instance: SystemManager = SystemManagerImpl(EntityManager)

  def apply(): SystemManager = instance
}
