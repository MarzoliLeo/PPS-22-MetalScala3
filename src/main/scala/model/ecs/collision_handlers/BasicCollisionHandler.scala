package model.ecs.collision_handlers

import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}
import model.ecs.components.{JumpingComponent, PositionComponent, SizeComponent, VelocityComponent}
import model.ecs.entities.Entity
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}

/**
 * The BasicCollisionHandler trait implements collision handling logic for entities.
 * It extends the CollisionHandler trait and requires the entity to mix in the Entity trait.
 */
trait BasicCollisionHandler extends CollisionHandler:
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
      velocity: VelocityComponent,
      collisionSize: Double
  ): JumpingComponent =
    if (
      (currentPosition.y + collisionSize >= model.GUIHEIGHT && velocity.y >= 0)
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
   * Handles a special collision with an optional colliding entity.
   *
   * @param collidingEntity the optional colliding entity involved in the collision
   */
  protected def handleSpecialCollision(collidingEntity: Option[Entity]): Unit = {}

  def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent] =
    for
      currentPosition <- getComponent[PositionComponent]
      velocity <- getComponent[VelocityComponent]
      collidingEntity = getCollidingEntity(this, proposedPosition)
    yield
      val sizeComponent = getComponent[SizeComponent]
        .getOrElse(throw new Exception("No SizeComponent found"))
      val updatedJumpingComponent = updateJumpingComponent(
        currentPosition,
        proposedPosition,
        velocity,
        sizeComponent.height
      )
      replaceComponent(updatedJumpingComponent)

      handleSpecialCollision(collidingEntity)

      PositionComponent(
        boundaryCheck(
          getFinalPosition(
            PositionComponent(proposedPosition.x, currentPosition.y),
            currentPosition
          ).x,
          model.GUIWIDTH,
          sizeComponent.width
        ),
        boundaryCheck(
          getFinalPosition(
            PositionComponent(currentPosition.x, proposedPosition.y),
            currentPosition
          ).y,
          model.GUIHEIGHT,
            sizeComponent.height
        )
      )

