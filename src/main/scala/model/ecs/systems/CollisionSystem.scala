package model.ecs.systems

import model.ecs.components.{PositionComponent, SizeComponent}
import model.ecs.entities.EntityManager

object CollisionSystem {
  def isOverlapping(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): Boolean = {
    val left1 = pos1.x
    val right1 = pos1.x + size1.width
    val top1 = pos1.y
    val bottom1 = pos1.y + size1.height

    val left2 = pos2.x
    val right2 = pos2.x + size2.width
    val top2 = pos2.y
    val bottom2 = pos2.y + size2.height

    !(left1 >= right2 || right1 <= left2 || top1 >= bottom2 || bottom1 <= top2)
  }
}
