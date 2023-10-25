package ecs.entities

import ecs.components.Component

trait EntityManager {
  def entities: List[Entity]
  def addEntity(entity: Entity): EntityManager
  def removeEntity(entity: Entity): EntityManager
  def getEntitiesWithComponent[T <: Component](
      componentClass: Class[T]
  ): List[Entity]
  def getEntitiesByClass[T <: Entity](entityClass: Class[T]): List[T]
}

private case class EntityManagerImpl(entities: List[Entity] = List.empty)
    extends EntityManager {
  override def addEntity(entity: Entity): EntityManager = {
    copy(entities = entity +: entities)
  }

  override def removeEntity(entity: Entity): EntityManager = {
    copy(entities = entities.filterNot(_ == entity))
  }

  override def getEntitiesWithComponent[T <: Component](
      componentClass: Class[T]
  ): List[Entity] = {
    entities.filter(_.hasComponent(componentClass))
  }

  override def getEntitiesByClass[T <: Entity](
      entityClass: Class[T]
  ): List[T] = {
    entities.collect {
      case entity: T if entity.getClass == entityClass => entity
    }
  }
}

object EntityManager {
  def apply(): EntityManager = EntityManagerImpl()
}
