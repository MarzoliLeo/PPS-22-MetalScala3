package model.ecs.entities

import model.ecs.components.{Component, PositionComponent, SpriteComponent}
import model.event.Event
import model.event.observer.Observable
import scala.collection.immutable.{AbstractSeq, LinearSeq}

trait EntityManager:
  def addEntity(entity: Entity): EntityManager
  def removeEntity(entity: Entity): EntityManager
  def getEntitiesWithComponent(types: Class[_ <: Component]*): List[Entity]
  def getEntitiesByClass[T <: Entity](entityClass: Class[T]): List[Entity]

object EntityManager extends EntityManager:
  var entities: List[Entity] = List.empty

  override def getEntitiesWithComponent(
      types: Class[_ <: Component]*): List[Entity] =
    entities.filter(e => types.forall(t => e.hasComponent(t)))

  override def getEntitiesByClass[T <: Entity](
      entityClass: Class[T]): List[Entity] =
    entities.filter(_.getClass == entityClass)

  override def addEntity(entity: Entity): EntityManager = {
    entities = entities :+ entity
    this
  }

  override def removeEntity(entity: Entity): EntityManager = {
    entities = entities.filter(!_.isSameEntity(entity))
    this
  }