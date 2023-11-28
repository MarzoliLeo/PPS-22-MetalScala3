package model.ecs.systems

import model.{GRAVITY_VELOCITY, VERTICAL_COLLISION_SIZE}
import model.ecs.components.{GravityComponent, PositionComponent, VelocityComponent}
import model.ecs.entities.EntityManager

object GravitySystem:
  val gravitySystem: Long => Unit = elapsedTime =>
    if (model.isGravityEnabled) {
      EntityManager()
        .getEntitiesWithComponent(
          classOf[PositionComponent],
          classOf[GravityComponent],
          classOf[VelocityComponent]
        )
        .foreach { entity =>
          val position = entity.getComponent[PositionComponent].get
          val velocity = entity.getComponent[VelocityComponent].get
          val isTouchingGround =
            position.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0
          if isTouchingGround then
            entity.replaceComponent(VelocityComponent(velocity.x, 0))
          else
            entity.replaceComponent(
              velocity + VelocityComponent(0, entity.getComponent[GravityComponent].get.gravity * elapsedTime)
            )
        }
    }
