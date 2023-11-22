package model.ecs.systems

import model.ecs.components.{CollisionComponent, PositionComponent, SizeComponent}
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.WeaponEntity
import model.ecs.entities.{Entity, EntityManager}

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object CollisionSystem {

  def isOverlapping(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): Boolean = {
    horizontalOverlap(pos1, size1, pos2, size2) > 0 && verticalOverlap(pos1, size1, pos2, size2) > 0
  }

  private def horizontalOverlap(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): Double = {
    val left1 = pos1.x
    val right1 = pos1.x + size1.width

    val left2 = pos2.x
    val right2 = pos2.x + size2.width

    val overlap = Math.min(right1, right2) - Math.max(left1, left2)
    if (overlap < 0) 0 else overlap
  }
  private def verticalOverlap(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): Double = {
    val top1 = pos1.y
    val bottom1 = pos1.y + size1.height

    val top2 = pos2.y
    val bottom2 = pos2.y + size2.height

    val overlap = Math.min(bottom1, bottom2) - Math.max(top1, top2)
    if (overlap < 0) 0 else overlap
  }
}
