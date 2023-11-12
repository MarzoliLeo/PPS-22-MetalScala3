package model.input.commands

import model.ecs.components.*
import model.ecs.entities.{BulletEntity, Entity, EntityManager, PlayerEntity}
import model.ecs.systems.CollisionSystem.MovementAxis
import model.ecs.systems.Systems.{boundaryCheck, notifyObservers}
import model.ecs.systems.CollisionSystem.wouldCollide
import model.event.Event.{Jump, Move}

// The base command class defines the common interface for all
// concrete commands.
trait Command {
  def execute(entity: Entity, manager: EntityManager): Unit
}

case class JumpCommand(duration: Double) extends Command {
  override def execute(entity: Entity, manager: EntityManager): Unit = {
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]

    val currentSprite = entity
      .getComponent(classOf[SpriteComponent])
      .get
      .asInstanceOf[SpriteComponent]

    entity.replaceComponent(PositionComponent(currentPosition.x, currentPosition.y - model.JUMP_MOVEMENT_VELOCITY))

    try {
      model.isGravityEnabled = false
      notifyObservers(
        Jump(
          entity.id,
          currentSprite,
          currentPosition,
          model.JUMP_MOVEMENT_VELOCITY,
          duration
        )
      )
    } finally {
      model.isGravityEnabled = true
    }
  }
}

case class MoveCommand(dx: Double, dy: Double) extends Command {
  override def execute(entity: Entity, manager: EntityManager): Unit = {
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]
    val collider = entity
      .getComponent(classOf[ColliderComponent])
      .get
      .asInstanceOf[ColliderComponent]

    val movementAxis = if (dx != 0) MovementAxis.Horizontal else MovementAxis.Vertical
    val proposedPosition = PositionComponent(
      boundaryCheck(
        currentPosition.x + dx,
        model.GUIWIDTH,
        collider.size.width
      ),
      boundaryCheck(
        currentPosition.y + dy,
        model.GUIHEIGHT,
        collider.size.height
      )
    )

    val currentSprite = entity
      .getComponent(classOf[SpriteComponent])
      .get
      .asInstanceOf[SpriteComponent]

    if (!entity.wouldCollide(proposedPosition, movementAxis)) {
      if currentSprite.spritePath.nonEmpty then
        entity.replaceComponent(proposedPosition)
        notifyObservers(
          Move(
            entity.id,
            currentSprite,
            proposedPosition,
            model.MOVEMENT_DURATION
          )
        )
    } else {
      // Handle the collision case here if needed
    }
  }
}

case class ShootCommand() extends Command {
  override def execute(entity: Entity, manager: EntityManager): Unit = {
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
      case LEFT => -20
    manager.addEntity(
      BulletEntity()
        .addComponent(PositionComponent(pos.x, pos.y))
        .addComponent(VelocityComponent(xVelocity, 0))
    )
  }
}

case object InvalidCommand extends Command {
  def execute(entity: Entity, manager: EntityManager): Unit = {
    println("[INPUT] Invalid key")
  }
}


//TODO potenziale save,pause,resume command?