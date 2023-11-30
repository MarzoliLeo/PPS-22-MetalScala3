package model.ecs.systems

import model.ecs.components.{GravityComponent, JumpingComponent, PositionComponent, VelocityComponent}
import model.ecs.entities.EntityManager
import model.ecs.systems.CollisionChecker.isImmediatelyAboveAPlatform
import model.{GRAVITY_VELOCITY, VERTICAL_COLLISION_SIZE}

trait GravitySystem extends SystemWithElapsedTime

private class GravitySystemImpl extends GravitySystem:
  override def update(elapsedTime: Long): Unit =
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
          if isTouchingGround
          then entity.replaceComponent(VelocityComponent(velocity.x, 0))
          else if isImmediatelyAboveAPlatform(entity).isDefined then
            entity.replaceComponent(GravityComponent(0))
            entity.replaceComponent(JumpingComponent(false))
            // non si accumula più

            entity.replaceComponent(VelocityComponent(velocity.x, 0))
          else
            entity.replaceComponent(GravityComponent(GRAVITY_VELOCITY))
            entity.replaceComponent(
              velocity + VelocityComponent(
                0,
                entity.getComponent[GravityComponent].get.gravity * elapsedTime
              )
            )
        }
    }

object GravitySystem:
  def apply(): GravitySystem = GravitySystemImpl()
