package model.ecs.collision_handlers

import model.ecs.components.{JumpingComponent, PositionComponent, SizeComponent, VelocityComponent}
import model.ecs.entities.Entity
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

/** The BasicCollisionHandler trait implements collision handling logic for
  * entities. It extends the CollisionHandler trait and requires the entity to
  * mix in the Entity trait.
  */
trait BasicCollisionHandler extends CollisionHandler:
  self: Entity =>

  private def getFinalPosition(
      proposedPosition: PositionComponent,
      currentPosition: PositionComponent
  ): PositionComponent =
    getCollidingEntity(this, proposedPosition)
      .fold(proposedPosition)(_ => currentPosition)

  private def getUpdatedJumpingComponent(
      currentPosition: PositionComponent,
      proposedPosition: PositionComponent,
      velocity: VelocityComponent,
      collisionSize: Double
  ): JumpingComponent =
    val isOnGround =
      currentPosition.y + collisionSize >= model.GUIHEIGHT && velocity.y >= 0
    val isColliding = getCollidingEntity(
      this,
      PositionComponent(currentPosition.x, proposedPosition.y)
    ).isDefined
    if (isOnGround || isColliding) JumpingComponent(false)
    else
      getComponent[JumpingComponent].getOrElse(
        throw new Exception("No JumpingComponent found")
      )

  protected def handleSpecialCollision(
      collidingEntity: Option[Entity]
  ): Unit = {}

  def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent] =
    for
      currentPosition <- getComponent[PositionComponent]
      velocity <- getComponent[VelocityComponent]
      sizeComponent <- getComponent[SizeComponent]
    yield
      replaceComponent {
        getUpdatedJumpingComponent(currentPosition, proposedPosition, velocity, sizeComponent.height)
      }
      handleSpecialCollision {
        getCollidingEntity(this, proposedPosition)
      }

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
