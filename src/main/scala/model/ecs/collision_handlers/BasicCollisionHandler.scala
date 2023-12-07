package model.ecs.collision_handlers

import model.ecs.collision_handlers.CollisionHandler
import model.ecs.components.*
import model.ecs.entities.Entity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.PlayerBulletEntity
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}
import model.{GRAVITY_VELOCITY, HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

/** The BasicCollisionHandler trait implements collision handling logic for
  * entities. It extends the CollisionHandler trait and requires the entity to
  * mix in the Entity trait.
  */
trait BasicCollisionHandler extends CollisionHandler:
  self: Entity =>

  def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent] =
    for
      currentPosition <- getComponent[PositionComponent]
      _ <- getComponent[VelocityComponent]
      sizeComponent <- getComponent[SizeComponent]
    yield
      handleSpecialCollision {
        getCollidingEntity(this, proposedPosition)
      }

      val finalX = getFinalPosition(
        PositionComponent(proposedPosition.x, currentPosition.y),
        currentPosition
      ).x
      val finalY = getFinalPosition(
        PositionComponent(currentPosition.x, proposedPosition.y),
        currentPosition
      ).y

      PositionComponent(
        boundaryCheck(
          finalX,
          model.GUIWIDTH,
          sizeComponent.width
        ),
        boundaryCheck(
          finalY,
          model.GUIHEIGHT,
          sizeComponent.height
        )
      )

  /** Returns the final position based on the proposed position and the current
    * position.
    *
    * @param proposedPosition
    *   The proposed position to check for collisions.
    * @param currentPosition
    *   The current position to use if collisions occur.
    * @return
    *   The final position after considering collisions. If a collision occurs,
    *   the current position is returned; otherwise, the proposed position is
    *   returned.
    */
  private def getFinalPosition(
      proposedPosition: PositionComponent,
      currentPosition: PositionComponent
  ): PositionComponent =
    getCollidingEntity(this, proposedPosition).fold(proposedPosition)(_ =>
      currentPosition
    )

  protected def handleSpecialCollision(
      collidingEntity: Option[Entity]
  ): Unit = ()
