package ecs.entities

import model.ecs.components.{Component, PositionComponent}
import model.ecs.entities.{EntityManager, PlayerEntity}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EntityManagerTest extends AnyFlatSpec with Matchers:

  "An EntityManager" should "add entity" in {
    var entityManager = EntityManager()
    val entity1 = PlayerEntity(PositionComponent(0, 0))
    val entity2 = PlayerEntity(PositionComponent(1, 1))

    entityManager = entityManager.addEntity(entity1)
    entityManager = entityManager.addEntity(entity2)

    assert(entityManager.entities.length == 2)
    assert(entityManager.entities.contains(entity1))
    assert(entityManager.entities.contains(entity2))
  }

  it should "remove entity" in {
    var entityManager = EntityManager()
    val entity1 = PlayerEntity(PositionComponent(0, 0))
    val entity2 = PlayerEntity(PositionComponent(1, 1))

    entityManager = entityManager.addEntity(entity1)
    entityManager = entityManager.addEntity(entity2)
    entityManager = entityManager.removeEntity(entity1)

    assert(entityManager.entities.length == 1)
    assert(!entityManager.entities.contains(entity1))
    assert(entityManager.entities.contains(entity2))
  }

  it should "get entities with a component" in {
    var entityManager = EntityManager()
    val entity1 = PlayerEntity(PositionComponent(0, 0))
    val entity2 = PlayerEntity(PositionComponent(1, 1))

    entityManager = entityManager addEntity entity1
    entityManager = entityManager addEntity entity2

    entityManager.getEntitiesWithComponent(
      classOf[PositionComponent]
    ) should contain theSameElementsAs List(entity1, entity2)
  }

  it should "get entities by class" in {
    var entityManager = EntityManager()
    val entity1 = PlayerEntity(PositionComponent(0, 0))
    val entity2 = PlayerEntity(PositionComponent(1, 1))

    entityManager = entityManager.addEntity(entity1)
    entityManager = entityManager.addEntity(entity2)

    entityManager.getEntitiesByClass(
      classOf[PlayerEntity]
    ) should contain theSameElementsAs List(entity1, entity2)
  }
