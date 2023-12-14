package model.ecs.systems

import model.ecs.components.*
import model.ecs.entities.{Entity, EntityManager}

trait JumpingSystem extends SystemWithoutTime

private case class JumpingSystemImpl() extends JumpingSystem:

  override def update(): Unit =
    EntityManager
      .getEntitiesWithComponent(
        classOf[JumpingComponent]
      )
      .foreach { entity =>
        val currentPosition: PositionComponent = entity
          .getComponent[PositionComponent]
          .getOrElse(
            throw new IllegalStateException(
              "JumpingSystem: PositionComponent not found"
            )
          )
        val currentVelocity: VelocityComponent = entity
          .getComponent[VelocityComponent]
          .getOrElse(
            throw new IllegalStateException(
              "JumpingSystem: GravityComponent not found"
            )
          )
        val size: SizeComponent = entity
          .getComponent[SizeComponent]
          .getOrElse(
            throw new IllegalStateException(
              "JumpingSystem: SizeComponent not found"
            )
          )
        if isOnGround(currentPosition, currentVelocity, size) then entity.replaceComponent(JumpingComponent(false))
      }

  private def isOnGround(
      currentPosition: PositionComponent,
      currentVelocity: VelocityComponent,
      size: SizeComponent
  ): Boolean = {
    val entityLowerBound = currentPosition.y + size.height
    entityLowerBound >= model.GUIHEIGHT && currentVelocity.y >= 0
  }

object JumpingSystem:
  def apply(): JumpingSystem = JumpingSystemImpl()
