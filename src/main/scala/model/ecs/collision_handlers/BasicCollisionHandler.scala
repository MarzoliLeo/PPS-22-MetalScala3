package model.ecs.collision_handlers
import model.ecs.components.{PositionComponent, VelocityComponent}
import model.ecs.entities.Entity
import model.ecs.systems.CollisionChecker
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

trait BasicCollisionHandler extends CollisionHandler:
  self: Entity =>

  private def getFinalPosition(
      proposedCoordinate: Double,
      currentCoordinate: Double,
      getCoordinate: Double => PositionComponent
  ): Double =
    if getCollidingEntity(this, getCoordinate(proposedCoordinate)).isEmpty
    then proposedCoordinate
    else currentCoordinate

  override def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent] =
    val currentPos = getComponent[PositionComponent].getOrElse(
      throw new Exception(
        "No position component in handleCollision for ${this}"
      )
    )
    val finalPositionX = getFinalPosition(
      proposedPosition.x,
      currentPos.x,
      PositionComponent(_, currentPos.y)
    )
    val finalPositionY = getFinalPosition(
      proposedPosition.y,
      currentPos.y,
      PositionComponent(currentPos.x, _)
    )

    Some(
      PositionComponent(
        boundaryCheck(
          finalPositionX,
          model.GUIWIDTH,
          HORIZONTAL_COLLISION_SIZE
        ),
        boundaryCheck(
          finalPositionY,
          model.GUIHEIGHT,
          VERTICAL_COLLISION_SIZE
        )
      )
    )
