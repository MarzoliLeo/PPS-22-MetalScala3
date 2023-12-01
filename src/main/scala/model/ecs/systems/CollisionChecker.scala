package model.ecs.systems

import model.ecs.components.{PositionComponent, SizeComponent}
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.WeaponEntity
import model.ecs.entities.{Entity, EntityManager}
import model.{GUIWIDTH, HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object CollisionChecker {

  /** Checks if the entity collides with another entity in the new position
    *
    * @param entity
    *   the entity to check
    * @param newPosition
    *   the new position of the entity
    * @return
    *   the entity that collides with the entity passed as parameter
    */
  def getCollidingEntity(
      entity: Entity,
      newPosition: PositionComponent
  ): Option[Entity] = {
    val potentialCollisions = EntityManager().getEntitiesWithComponent(
      classOf[PositionComponent],
      classOf[SizeComponent]
    )
    val size = entity.getComponent[SizeComponent].get

    // [ATTENTION] We are hypothesizing that there is at most one collision
    potentialCollisions.find { otherEntity =>
      if (!otherEntity.isSameEntity(entity)) {
        isOverlapping(
          newPosition,
          size,
          otherEntity.getComponent[PositionComponent].get,
          otherEntity.getComponent[SizeComponent].get
        )
      } else false
    }
  }

  /** Determines whether `entity1` is immediately above another entity
    *
    * @param entity1
    *   The first entity to compare.
    * @return
    *   `true` if `entity1` is immediately above another entity, `false`
    *   otherwise.
    */
  def isImmediatelyAboveAPlatform(entity1: Entity): Option[Entity] = {
    val entity1Position = entity1.getComponent[PositionComponent].get

    getCollidingEntity(
      entity1,
      PositionComponent(
        entity1Position.x,
        entity1Position.y + 5
      )
    ) match
      case Some(boxEntity: BoxEntity) => Some(boxEntity)
      case Some(_) => None
      case _ => None
  }

  /** Applies a boundary check to a currentPosition value, ensuring it stays
    * within the bounds of the system. It ensures that 'pos' is not less than
    * 0.0 and not greater than 'max - size'.
    *
    * @param pos
    *   The currentPosition value to check.
    * @param max
    *   The maximum value allowed for the currentPosition.
    * @param size
    *   The size of the object being checked.
    * @return
    *   The new currentPosition value after the boundary check has been applied.
    */
  def boundaryCheck(pos: Double, max: Double, size: Double): Double =
    math.max(0.0, math.min(pos, max - size))

  def isOutOfHorizontalBoundaries(
      newPosition: PositionComponent
  ): Boolean = {
    (newPosition.x + HORIZONTAL_COLLISION_SIZE >= GUIWIDTH) ||
    (newPosition.x <= 0)
  }

  private def isOverlapping(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): Boolean = {
    isOverlappingX(pos1, size1, pos2, size2) && isOverlappingY(
      pos1,
      size1,
      pos2,
      size2
    )
  }

  private def isOverlappingX(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): Boolean = {
    val left1 = pos1.x
    val right1 = pos1.x + size1.width

    val left2 = pos2.x
    val right2 = pos2.x + size2.width

    !(left1 >= right2 || right1 <= left2)
  }

  private def isOverlappingY(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): Boolean = {
    val top1 = pos1.y
    val bottom1 = pos1.y + size1.height

    val top2 = pos2.y
    val bottom2 = pos2.y + size2.height

    !(top1 >= bottom2 || bottom1 <= top2)
  }
}
