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
    // Only allow the entity to jump if it's not already jumping
    val jumping: JumpingComponent = entity
      .getComponent[JumpingComponent]
      .get
    if (!jumping.isJumping) {
      val velocity: VelocityComponent = entity
        .getComponent[VelocityComponent]
        .get

      // Set the vertical velocity to the jump velocity
      val newVelocity =
        VelocityComponent(velocity.x, -model.JUMP_MOVEMENT_VELOCITY)
      entity.replaceComponent(newVelocity)

      // Set the jumping component to true
      entity.replaceComponent(JumpingComponent(true))
    }
  }
}

case class MoveCommand(dx: Double, dy: Double) extends Command {
  override def execute(entity: Entity, elapsedTime: Long): Unit = {
    val velocity = entity
      .getComponent[VelocityComponent]
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
      .getComponent[PositionComponent]
      .get
    val dir = entity
      .getComponent[DirectionComponent]
      .get
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
