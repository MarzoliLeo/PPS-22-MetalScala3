package ecs.systems

import model.ecs.components.{ColliderComponent, PositionComponent, Size}
import model.ecs.entities.{Entity, EntityManager, PlayerEntity}
import model.ecs.systems.CollisionSystem
import model.ecs.systems.CollisionSystem.{handleCollision, isColliding}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, noException}
import org.scalatest.matchers.should.Matchers.should

class CollisionSystemTest extends AnyFlatSpec with BeforeAndAfterEach:

  var entityManager: EntityManager = _
  var entity1: Entity = _
  var entity2: Entity = _

  override def beforeEach(): Unit = {
    entityManager = EntityManager()
    entity1 = PlayerEntity()
    entity2 = PlayerEntity()
    entityManager.addEntity(entity1)
    entityManager.addEntity(entity2)
  }


  "isColliding" should "return true if two entities are colliding" in {
    val positionComponent1 = PositionComponent(10, 10)
    entity1.addComponent(positionComponent1)
    entity1.addComponent(ColliderComponent(Size(10, 10)))

    val positionComponent2 = PositionComponent(5, 5)
    entity2.addComponent(positionComponent2)
    entity2.addComponent(ColliderComponent(Size(10, 10)))

    entity1 = entityManager.entities.filter(_.id == entity1.id).head
    entity2 = entityManager.entities.filter(_.id == entity2.id).head

    assert(isColliding(entity1, entity2))
  }

  "handleCollision" should "change the positions of the first entity that collides" in {
    val positionComponent1 = PositionComponent(10, 10)
    entity1.addComponent(positionComponent1)
    entity1.addComponent(ColliderComponent(Size(10, 10)))

    val positionComponent2 = PositionComponent(5, 5)
    entity2.addComponent(positionComponent2)
    entity2.addComponent(ColliderComponent(Size(10, 10)))

    entity1 = entityManager.entities.filter(_.id == entity1.id).head
    entity2 = entityManager.entities.filter(_.id == entity2.id).head

    handleCollision(entity1, entity2)

    val newPosition1 = entity1.getComponent(classOf[PositionComponent]).get.asInstanceOf[PositionComponent]

    newPosition1.x should be(10)
    newPosition1.y should be(15)
  }