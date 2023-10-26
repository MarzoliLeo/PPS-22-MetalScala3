package ecs.entities

import ecs.components.Component

trait EntityManager {
  def entities: List[Entity]
  def addEntity(entity: Entity): EntityManager
  def removeEntity(entity: Entity): EntityManager
  def getEntitiesWithComponent[T <: Component](
      componentClass: Class[T]
  ): List[Entity]
  def getEntitiesByClass[T <: Entity](
      entityClass: Class[T]
  ): List[T]
}

private case class EntityManagerImpl(entities: List[Entity] = List.empty)
    extends EntityManager {

  override def getEntitiesWithComponent[T <: Component](
      componentClass: Class[T]
  ): List[Entity] = {
    entities.filter(_.getComponent(componentClass).isDefined)
  }

  override def getEntitiesByClass[T <: Entity](
      entityClass: Class[T]
  ): List[T] = {
    entities.flatMap {
      case entity: T => Some(entity)
      case _         => None
    }
  }

  override def addEntity(entity: Entity): EntityManager =
    EntityManagerImpl(entity :: entities)

  override def removeEntity(entity: Entity): EntityManager =
    EntityManagerImpl(entities.filterNot(_ == entity))
}

object EntityManager {
  def apply(): EntityManager = EntityManagerImpl()
}
