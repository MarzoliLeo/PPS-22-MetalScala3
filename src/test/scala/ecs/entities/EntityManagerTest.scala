package ecs.entities

import model.ecs.components.{Component, PositionComponent}
import model.ecs.entities.{EntityManager, PlayerEntity}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EntityManagerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  "An EntityManager" should "add entity" in {
    var manager: EntityManager = EntityManager()
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    manager = manager.addEntity(entity1)
    manager = manager.addEntity(entity2)

    assert(manager.entities.length == 2)
    assert(manager.entities.contains(entity1))
    assert(manager.entities.contains(entity2))
  }

  it should "remove entity" in {
    var manager: EntityManager = EntityManager()
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    manager = manager.addEntity(entity1)
    manager = manager.addEntity(entity2)
    manager = manager.removeEntity(entity1)

    assert(manager.entities.length == 1)
  }

  it should "get entities with a component" in {
    var manager: EntityManager = EntityManager()
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    manager = manager addEntity entity1
    manager = manager addEntity entity2

    manager.getEntitiesWithComponent(
      classOf[PositionComponent]
    ) should contain theSameElementsAs List(entity1, entity2)
  }

  it should "get entities by class" in {
    var manager: EntityManager = EntityManager()
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    manager = manager.addEntity(entity1)
    manager = manager.addEntity(entity2)

    manager.getEntitiesByClass(
      classOf[PlayerEntity]
    ) should contain theSameElementsAs List(entity1, entity2)
  }
}