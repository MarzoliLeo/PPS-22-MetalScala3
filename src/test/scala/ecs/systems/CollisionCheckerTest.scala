package ecs.systems

import model.ecs.collision_handlers.CollisionChecker
import model.ecs.components.{CollisionComponent, PositionComponent, SizeComponent}
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.{Entity, EntityManager}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CollisionCheckerTest
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {
  override def beforeEach(): Unit =
    EntityManager.entities.foreach(EntityManager.removeEntity)

  "getCollidingEntity" should "return the colliding entity" in {
    val entity1 = BoxEntity()
    entity1.addComponent(PositionComponent(0, 0))
    entity1.addComponent(SizeComponent(10, 10))
    entity1.addComponent(CollisionComponent())

    val entity2 = BoxEntity()
    entity2.addComponent(PositionComponent(10, 0))
    entity2.addComponent(SizeComponent(10, 10))
    entity2.addComponent(CollisionComponent())

    EntityManager.addEntity(entity1)
    EntityManager.addEntity(entity2)

    val newPosition = PositionComponent(5, 0)

    CollisionChecker.getCollidingEntity(entity1, newPosition) shouldEqual Some(
      entity2
    )
  }

  it should "return None when there is no colliding entity" in {
    val entity1 = BoxEntity()
    entity1.addComponent(PositionComponent(0, 0))
    entity1.addComponent(SizeComponent(10, 10))
    entity1.addComponent(CollisionComponent())

    val entity2 = BoxEntity()
    entity2.addComponent(
      PositionComponent(30, 30)
    ) // Adjusted position to avoid collision
    entity2.addComponent(SizeComponent(10, 10))
    entity2.addComponent(CollisionComponent())

    EntityManager.addEntity(entity1)
    EntityManager.addEntity(entity2)

    val newPosition = PositionComponent(15, 0)

    CollisionChecker.getCollidingEntity(entity1, newPosition) shouldEqual None
  }

  it should "not consider the entity itself as a colliding entity" in {
    val entity1 = BoxEntity()
    entity1.addComponent(PositionComponent(0, 0))
    entity1.addComponent(SizeComponent(10, 10))
    entity1.addComponent(CollisionComponent())

    EntityManager.addEntity(entity1)

    val newPosition = PositionComponent(0, 0)

    CollisionChecker.getCollidingEntity(entity1, newPosition) shouldEqual None
  }
}
