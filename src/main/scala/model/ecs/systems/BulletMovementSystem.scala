package model.ecs.systems

import model.ecs.components.{PositionComponent, VelocityComponent}
import model.ecs.entities.EntityManager
import model.ecs.entities.weapons.BulletEntity
import model.ecs.systems.PositionUpdateSystem.getUpdatedPosition

object BulletMovementSystem:
  val bulletMovementSystem: Long => Unit = elapsedTime =>
    EntityManager().getEntitiesByClass(classOf[BulletEntity]).foreach {
      bullet =>
        given position: PositionComponent =
          bullet.getComponent[PositionComponent].get
        given velocity: VelocityComponent =
          bullet.getComponent[VelocityComponent].get
        val proposedPosition = getUpdatedPosition(elapsedTime)
        bullet.handleCollision(proposedPosition) match
          case Some(handledPosition) => bullet.replaceComponent(handledPosition)
          case None => ()
    }
