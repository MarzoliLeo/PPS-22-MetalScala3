package model.ecs.entities

import model.ecs.components.Component
import model.ecs.observer.Observable

trait EntityManager:
  def entities: List[Entity]
  def addEntity(entity: Entity): EntityManager
  def removeEntity(entity: Entity): EntityManager
  def getEntitiesWithComponent[T <: Component](componentClass: Class[T]): List[Entity]
  def getEntitiesByClass[T <: Entity](entityClass: Class[T]): List[Entity]

private case class EntityManagerImpl(entities: List[Entity] = List.empty)
    extends EntityManager:

  override def getEntitiesWithComponent[T <: Component](componentClass: Class[T]): List[Entity] = {
    entities.filter(_.getComponent(componentClass).isDefined)
  }

  override def getEntitiesByClass[T <: Entity](entityClass: Class[T]): List[Entity] =
    entities.filter(_.getClass == entityClass)

  override def addEntity(entity: Entity): EntityManager =
    EntityManagerImpl(entity :: entities)

  override def removeEntity(entity: Entity): EntityManager =
    EntityManagerImpl(entities.filterNot(_ eq entity))

object EntityManager {
  def apply(): EntityManager = EntityManagerImpl()
}
