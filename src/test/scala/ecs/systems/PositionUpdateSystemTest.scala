package ecs.systems

import model.ecs.components.{JumpingComponent, PositionComponent, SizeComponent, VelocityComponent}
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.PositionUpdateSystem
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PositionUpdateSystemTest
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {
  val startingPosition: PositionComponent = PositionComponent(0, 0)
  val elapsedTime = 1L
  var positionUpdateSystem: PositionUpdateSystem = _
  var entity: Entity = _

  override def beforeEach(): Unit = {
    positionUpdateSystem = PositionUpdateSystem()
    entity = BoxEntity()
    EntityManager.addEntity(entity)
    entity.addComponent(startingPosition)
  }

  "PositionUpdateSystem" should "update position based on velocity" in {
    val velocityComponent = VelocityComponent(1, 1)
    entity.addComponent(velocityComponent)
    entity.addComponent(JumpingComponent(false))
    entity.addComponent(SizeComponent(1, 1))

    positionUpdateSystem.update(elapsedTime)

    val updatedPosition = entity.getComponent[PositionComponent].get
    updatedPosition shouldEqual startingPosition.getUpdatedPosition(
      elapsedTime
    )(using velocityComponent)
  }

  it should "not update position if there is no velocity" in {
    entity.addComponent(JumpingComponent(false))

    positionUpdateSystem.update(elapsedTime)

    val updatedPosition = entity.getComponent[PositionComponent].get
    updatedPosition shouldEqual startingPosition
  }

  it should "not update position if there is no jumping component" in {
    val velocityComponent = VelocityComponent(1, 1)
    entity.addComponent(velocityComponent)

    positionUpdateSystem.update(elapsedTime)

    val updatedPosition = entity.getComponent[PositionComponent].get
    updatedPosition shouldEqual startingPosition
  }
}
