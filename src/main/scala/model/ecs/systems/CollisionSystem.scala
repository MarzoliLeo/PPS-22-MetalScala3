package model.ecs.systems

import model.ecs.components.{PositionComponent, SizeComponent}
import model.ecs.entities.EntityManager
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

object CollisionSystem {
  enum OverlapType {
    case None, SameX, SameY, Both
  }

  def isOverlapping(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): OverlapType = {
    val overlapX = isOverlappingX(pos1, size1, pos2, size2)
    val overlapY = isOverlappingY(pos1, size1, pos2, size2)

    (overlapX, overlapY) match {
      case (OverlapType.SameX, OverlapType.SameY) => OverlapType.Both
      case (OverlapType.SameX, _) => OverlapType.SameX
      case (_, OverlapType.SameY)   => OverlapType.SameY
      case _                           => OverlapType.None
    }
  }

  private def isOverlappingX(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): OverlapType = {
    val left1 = pos1.x
    val right1 = pos1.x + size1.width

    val left2 = pos2.x
    val right2 = pos2.x + size2.width

    if (!(left1 >= right2 || right1 <= left2)) OverlapType.SameX
    else OverlapType.None
  }

  private def isOverlappingY(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): OverlapType = {
    val top1 = pos1.y
    val bottom1 = pos1.y + size1.height

    val top2 = pos2.y
    val bottom2 = pos2.y + size2.height

    if (!(top1 >= bottom2 || bottom1 <= top2)) OverlapType.SameY
    else OverlapType.None
  }
}
