package model.ecs.entities

import model.ecs.components.{Component, PositionComponent, SpriteComponent}
import model.event.Event
import model.event.Event.Spawn
import model.event.observer.Observable

import scala.collection.immutable.{AbstractSeq, LinearSeq}

trait EntityManager extends Observable[Event]:
  def entities: List[Entity]
  def addEntity(entity: Entity): EntityManager
  def removeEntity(entity: Entity): EntityManager
  def getEntitiesWithComponent(types: Class[_ <: Component]*): List[Entity]
  def getEntitiesByClass[T <: Entity](entityClass: Class[T]): List[Entity]

private case class EntityManagerImpl() extends EntityManager:

  var entities: List[Entity] = List.empty
  override def getEntitiesWithComponent(types: Class[_ <: Component]*): List[Entity] =
    entities.filter(e => types.forall(t => e.hasComponent(t)))

  override def getEntitiesByClass[T <: Entity](entityClass: Class[T]): List[Entity] =
    entities.filter(_.getClass == entityClass)

  override def addEntity(entity: Entity): EntityManager =
    //Imposto il Frontend (View).
    notifyObservers {
      Spawn (
        entity.id,
        entity.getClass,
        entity.getComponent[SpriteComponent].get,
        entity.getComponent[PositionComponent].get
      )
    }
    entities = entities :+ entity
    this

  override def removeEntity(entity: Entity): EntityManager =
    entities = entities.filter(!_.isSameEntity(entity))
    this

object EntityManager {
  private var singleton: Option[EntityManager] = None
  def apply(): EntityManager =
    if(singleton.isEmpty){
      singleton = Some(EntityManagerImpl())
    }
    singleton.get
    
}
