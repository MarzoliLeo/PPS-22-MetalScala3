package model.ecs.entities

import model.ecs.components.Component
import model.ecs.observer.Observable

import scala.collection.immutable.{AbstractSeq, LinearSeq}

trait EntityManager:
  def entities: List[Entity]
  def addEntity(entity: Entity): EntityManager
  def removeEntity(entity: Entity): EntityManager
  def getEntitiesWithComponent(types: Class[_ <: Component]*): List[Entity]
  def getEntitiesByClass[T <: Entity](entityClass: Class[T]): List[Entity]

private case class EntityManagerImpl(entities: List[Entity] = List.empty)
    extends EntityManager:

  override def getEntitiesWithComponent(types: Class[_ <: Component]*): List[Entity] =
    entities.filter(e => types.forall(t => e.hasComponent(t)))

  override def getEntitiesByClass[T <: Entity](entityClass: Class[T]): List[Entity] =
    entities.filter(_.getClass == entityClass)

  override def addEntity(entity: Entity): EntityManager =
    EntityManagerImpl(entity :: entities)

  override def removeEntity(entity: Entity): EntityManager =
    EntityManagerImpl(entities.filterNot(_ eq entity))

object EntityManager {
  def apply(): EntityManager = EntityManagerImpl()
}
