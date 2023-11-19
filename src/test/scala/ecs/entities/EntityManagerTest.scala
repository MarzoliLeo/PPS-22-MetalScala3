package ecs.entities

import model.ecs.components.{Component, PositionComponent}
import model.ecs.entities.EntityManager
import model.ecs.entities.player.PlayerEntity
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EntityManagerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  var manager: EntityManager = _

  before {
    manager = EntityManager()
    manager.entities.foreach(manager.removeEntity)
  }

  "An EntityManager" should "add entity" in {
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    manager.addEntity(entity1)
    manager.addEntity(entity2)

    assert(manager.entities.length == 2)
    assert(manager.entities.contains(entity1))
    assert(manager.entities.contains(entity2))
  }

  it should "remove entity" in {
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    manager.addEntity(entity1)
    manager.addEntity(entity2)
    manager.removeEntity(entity1)

    assert(manager.entities.length == 1)
  }

  it should "get entities with a component" in {
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    manager.addEntity(entity1)
    manager.addEntity(entity2)

    manager.getEntitiesWithComponent(
      classOf[PositionComponent]
    ) should contain theSameElementsAs List(entity1, entity2)
  }

  it should "get entities by class" in {
    val entity1 = PlayerEntity().addComponent(PositionComponent(0, 0))
    val entity2 = PlayerEntity().addComponent(PositionComponent(1, 1))

    manager.addEntity(entity1)
    manager.addEntity(entity2)

    manager.getEntitiesByClass(
      classOf[PlayerEntity]
    ) should contain theSameElementsAs List(entity1, entity2)
  }
}