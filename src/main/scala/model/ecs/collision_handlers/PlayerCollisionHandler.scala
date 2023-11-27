package model.ecs.collision_handlers
import model.ecs.components.{JumpingComponent, PositionComponent, VelocityComponent}
import model.ecs.entities.Entity
import model.ecs.systems.CollisionChecker
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

trait PlayerCollisionHandler extends CollisionHandler:
  self: Entity =>

  /** Calculates the final position for an entity based on the proposed
    * coordinate, current coordinate, and a function to retrieve the position
    * component of a specific coordinate.
    *
    * @param proposedCoordinate
    *   The proposed coordinate for the entity.
    * @param currentCoordinate
    *   The current coordinate of the entity.
    * @param getCoordinate
    *   A function that takes a coordinate and returns the position component of
    *   that coordinate.
    * @return
    *   The final position of the entity. If there is no colliding entity at the
    *   proposed coordinate, the proposed coordinate is returned. Otherwise, the
    *   current coordinate is returned.
    */
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
    val currentPosition = getComponent[PositionComponent].getOrElse(
      throw new Exception(
        "No position component in handleCollision for ${this}"
      )
    )

    val velocity = getComponent[VelocityComponent]
      .getOrElse(
        throw new Exception(
          "No velocity component in handleCollision for ${this}"
        )
      )

    val canJump =
      (currentPosition.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0) || getCollidingEntity(
        this,
        proposedPosition
      ).isDefined

    if canJump then replaceComponent(JumpingComponent(false))

    val finalPositionX = getFinalPosition(
      proposedPosition.x,
      currentPosition.x,
      PositionComponent(_, currentPosition.y)
    )
    val finalPositionY = getFinalPosition(
      proposedPosition.y,
      currentPosition.y,
      PositionComponent(currentPosition.x, _)
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
