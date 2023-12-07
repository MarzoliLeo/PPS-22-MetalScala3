package ecs.entities

import model.ecs.components.{Component, PositionComponent}
import model.ecs.entities.EntityManager
import model.ecs.entities.player.PlayerEntity
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EntityManagerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    EntityManager.entities.foreach(EntityManager.removeEntity)
  }

  "An EntityManager" should "add entity" in {
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    EntityManager.addEntity(entity1)
    EntityManager.addEntity(entity2)

    assert(EntityManager.entities.length == 2)
    assert(EntityManager.entities.contains(entity1))
    assert(EntityManager.entities.contains(entity2))
  }

  it should "remove entity" in {
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    EntityManager.addEntity(entity1)
    EntityManager.addEntity(entity2)
    EntityManager.removeEntity(entity1)

    assert(EntityManager.entities.length == 1)
  }

  it should "get entities with a component" in {
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    EntityManager.addEntity(entity1)
    EntityManager.addEntity(entity2)

    EntityManager.getEntitiesWithComponent(
      classOf[PositionComponent]
    ) should contain theSameElementsAs List(entity1, entity2)
  }

  it should "get entities by class" in {
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    EntityManager.addEntity(entity1)
    EntityManager.addEntity(entity2)

    EntityManager.getEntitiesByClass(
      classOf[PlayerEntity]
    ) should contain theSameElementsAs List(entity1, entity2)
  }
}
