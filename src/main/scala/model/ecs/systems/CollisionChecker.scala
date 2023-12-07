package model.ecs.systems

import model.ecs.components.{PositionComponent, SizeComponent}
import model.ecs.entities.{Entity, EntityManager}
import model.{GUIWIDTH, HORIZONTAL_COLLISION_SIZE}

object CollisionChecker {

  def getCollidingEntity(
      entity: Entity,
      newPosition: PositionComponent
  ): Option[Entity] =
    val potentialEntitiesCollisions = EntityManager
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[SizeComponent]
      )
    val size = entity.getComponent[SizeComponent].get

    potentialEntitiesCollisions.find {
      case otherEntity if !otherEntity.isSameEntity(entity) =>
        isOverlapping(
          newPosition,
          size,
          otherEntity.getComponent[PositionComponent].get,
          otherEntity.getComponent[SizeComponent].get
        )
      case _ => false
    }

  private def isOverlapping(
      pos1: PositionComponent,
      size1: SizeComponent,
      pos2: PositionComponent,
      size2: SizeComponent
  ): Boolean =
    isOverlappingAxis(
      pos1.x,
      size1.width,
      pos2.x,
      size2.width
    ) && isOverlappingAxis(pos1.y, size1.height, pos2.y, size2.height)

  private def isOverlappingAxis(
      pos1: Double,
      size1: Double,
      pos2: Double,
      size2: Double
  ): Boolean = {
    val end1 = pos1 + size1
    val end2 = pos2 + size2

    !(pos1 >= end2 || end1 <= pos2)
  }

  def boundaryCheck(pos: Double, max: Double, size: Double): Double =
    math.max(0.0, math.min(pos, max - size))

  def isOutOfHorizontalBoundaries(newPosition: PositionComponent): Boolean =
    newPosition.x + HORIZONTAL_COLLISION_SIZE >= GUIWIDTH || newPosition.x <= 0
}
