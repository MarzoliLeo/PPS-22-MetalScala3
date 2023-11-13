package model.input.commands

import model.ecs.components.*
import model.ecs.entities.{BulletEntity, Entity, EntityManager, PlayerEntity}
import model.ecs.systems.CollisionSystem.{MovementAxis, wouldCollide}
import model.ecs.systems.Systems.{boundaryCheck, gravitySystem, notifyObservers}
import model.event.Event.{Jump, Move}

// The base command class defines the common interface for all
// concrete commands.
trait Command {
  def execute(entity: Entity, elapsedTime: Long): Unit
}

case class JumpCommand(duration: Double) extends Command {
  override def execute(entity: Entity, elapsedTime: Long): Unit = {
    val velocity = entity
      .getComponent(classOf[VelocityComponent])
      .get
      .asInstanceOf[VelocityComponent]
    val position = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]

    // Only allow the entity to jump if it's not already jumping
    val jumping = entity
      .getComponent(classOf[JumpingComponent])
      .getOrElse(JumpingComponent(false))
      .asInstanceOf[JumpingComponent]

    if (!jumping.isJumping) {
      // Set the vertical velocity to the jump velocity
      val newVelocity = VelocityComponent(velocity.x, -model.JUMP_MOVEMENT_VELOCITY)
      entity.replaceComponent(newVelocity)

      // Set the jumping component to true
      entity.replaceComponent(JumpingComponent(true))
    }
  }
}

/* case class MoveCommand(dx: Double, dy: Double) extends Command {
  override def execute(entity: Entity, elapsedTime: Long): Unit = {
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]
    val newVelocity = VelocityComponent(dx, dy)

    val proposedPosition =
      currentPosition + newVelocity * (elapsedTime / 100.0)

    val currentSprite = entity
      .getComponent(classOf[SpriteComponent])
      .get
      .asInstanceOf[SpriteComponent]

    if currentSprite.spritePath.nonEmpty then
        entity.replaceComponent(proposedPosition)
        entity.replaceComponent(newVelocity)
        notifyObservers(
          Move(
            entity.id,
            currentSprite,
            proposedPosition,
            model.MOVEMENT_DURATION
          )
        )
  }
} */

case class MoveCommand(dx: Double, dy: Double) extends Command {
  override def execute(entity: Entity, elapsedTime: Long): Unit = {
    val velocity = entity
      .getComponent(classOf[VelocityComponent])
      .get
      .asInstanceOf[VelocityComponent]

    // Change the direction of the velocity
    val newVelocity = VelocityComponent(dx, velocity.y)
    entity.replaceComponent(newVelocity)
  }
}

case class ShootCommand() extends Command {
  override def execute(entity: Entity, elapsedTime: Long): Unit = {
    val pos = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]
    val dir = entity
      .getComponent(classOf[DirectionComponent])
      .get
      .asInstanceOf[DirectionComponent]
    val xVelocity = dir.d match
      case RIGHT => 20
      case LEFT  => -20
    EntityManager().addEntity(
      BulletEntity()
        .addComponent(PositionComponent(pos.x, pos.y))
        .addComponent(VelocityComponent(xVelocity, 0))
    )
  }
}

case object InvalidCommand extends Command {
  def execute(entity: Entity, elapsedTime: Long): Unit = {
    println("[INPUT] Invalid key")
  }
}

//TODO potenziale save,pause,resume command?
