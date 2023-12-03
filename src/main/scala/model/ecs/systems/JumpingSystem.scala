package model.ecs.systems

import model.ecs.components.{GravityComponent, JumpingComponent, PositionComponent, SizeComponent, VelocityComponent}
import model.ecs.entities.{Entity, EntityManager}

trait JumpingSystem extends SystemWithoutTime


private case class JumpingSystemImpl() extends JumpingSystem:

  override def update(): Unit =
    EntityManager()
      .getEntitiesWithComponent(
        classOf[JumpingComponent]
      )
      .foreach { entity =>
        val currentPosition: PositionComponent = entity
          .getComponent[PositionComponent]
          .getOrElse(
            throw new IllegalStateException("JumpingSystem: PositionComponent not found")
          )
        val currentVelocity: VelocityComponent = entity
          .getComponent[VelocityComponent]
          .getOrElse(
            throw new IllegalStateException("JumpingSystem: GravityComponent not found")
          )
        val size: SizeComponent = entity
          .getComponent[SizeComponent]
          .getOrElse(
            throw new IllegalStateException("JumpingSystem: JumpingComponent not found")
          )
        
        val isOnGround = currentPosition.y + size.height >= model.GUIHEIGHT && currentVelocity.y >= 0
        if (isOnGround)
          entity.replaceComponent(GravityComponent(0))
          Some(JumpingComponent(false))
      }



object JumpingSystem:
  def apply(): JumpingSystem = JumpingSystemImpl()