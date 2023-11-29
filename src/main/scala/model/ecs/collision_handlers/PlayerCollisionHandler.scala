package model.ecs.collision_handlers
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}
import model.ecs.components.{JumpingComponent, PositionComponent, VelocityComponent}
import model.ecs.entities.Entity
import model.ecs.systems.CollisionChecker
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}

trait PlayerCollisionHandler extends CollisionHandler:
  self: Entity =>

  private def getFinalPosition(
      proposedPosition: PositionComponent,
      currentPosition: PositionComponent
  ): PositionComponent =
    getCollidingEntity(this, proposedPosition)
      // if there is a colliding entity, do not change the position
      .map(_ => currentPosition)
      // otherwise, change the position
      .getOrElse(proposedPosition)

  private def updateJumpingComponent(
      currentPosition: PositionComponent,
      proposedPosition: PositionComponent,
      velocity: VelocityComponent
  ): JumpingComponent =
    if (
      (currentPosition.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0)
      || getCollidingEntity(
        this,
        PositionComponent(currentPosition.x, proposedPosition.y)
      ).isDefined
    )
      JumpingComponent(false)
    else
      getComponent[JumpingComponent].getOrElse(
        throw new Exception("No JumpingComponent found")
      )

  /**
   * Handles the collision of the PlayerEntity with the proposed position.
   * @param proposedPosition
   *   The proposed position before the collision is handled.
   *  @return
   *   An optional PositionComponent representing the new position after
   *   handling the collision. Returns None if no position update is necessary.
   */
  override def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent] =
    // TODO: check if the collision is with a weapon:
    //  if so, delete the weapon entity and change the type of bullets shot

    for {
      currentPosition <- getComponent[PositionComponent]
      velocity <- getComponent[VelocityComponent]
    } yield {
      val updatedJumpingComponent =
        updateJumpingComponent(currentPosition, proposedPosition, velocity)
      replaceComponent(updatedJumpingComponent)

      PositionComponent(
        boundaryCheck(
          getFinalPosition(
            PositionComponent(proposedPosition.x, currentPosition.y),
            currentPosition
          ).x,
          model.GUIWIDTH,
          HORIZONTAL_COLLISION_SIZE
        ),
        boundaryCheck(
          getFinalPosition(
            PositionComponent(currentPosition.x, proposedPosition.y),
            currentPosition
          ).y,
          model.GUIHEIGHT,
          VERTICAL_COLLISION_SIZE
        )
      )
    }
