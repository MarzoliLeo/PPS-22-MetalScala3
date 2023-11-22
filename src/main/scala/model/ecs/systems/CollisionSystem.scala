package model.ecs.systems

import model.ecs.components.{PositionComponent, SizeComponent}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.WeaponEntity

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object CollisionSystem {

  /** Checks if the entity collides with another entity in the new position
    *
    * @param entity
    *   the entity to check
    * @param newPosition
    *   the new position of the entity
    * @return
    *   the entity that collides with the entity passed as parameter
    */
  def checkCollision(
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
        CollisionSystem.isOverlapping(
          newPosition,
          size,
          otherEntity.getComponent[PositionComponent].get,
          otherEntity.getComponent[SizeComponent].get
        )
      } else false
    }
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

    (!(left1 >= right2 || right1 <= left2))
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

    (!(top1 >= bottom2 || bottom1 <= top2))
  }
}
