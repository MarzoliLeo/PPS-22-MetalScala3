package model.ecs.systems

import model.ecs.components.{ColliderComponent, Component, PositionComponent}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.Systems.boundaryCheck

object CollisionSystem {

  // Define the MovementAxis enum
  enum MovementAxis:
    case Horizontal, Vertical

  case class BoundingBox(
      left: Double,
      right: Double,
      top: Double,
      bottom: Double
  ) {
    def width: Double = right - left
    def height: Double = bottom - top
  }

  extension (entity: Entity)

    private def getPositionComponent: Option[PositionComponent] =
      entity.getComponent[PositionComponent]

    private def getColliderComponent: Option[ColliderComponent] =
      entity.getComponent[ColliderComponent]

    def getBoundingBox: BoundingBox =
      val size = entity.getColliderComponent.get.size
      val position = entity.getPositionComponent.get
      BoundingBox(
        position.x,
        position.x + size.width,
        position.y,
        position.y + size.height
      )

    def isColliding: Boolean =
      entity.getCollidingEntities.nonEmpty

    def wouldCollide(
        proposedPosition: PositionComponent,
        movementAxis: MovementAxis
    ): Boolean =
      val size = entity.getColliderComponent.get.size
      val proposedBoundingBox = BoundingBox(
        proposedPosition.x,
        proposedPosition.x + size.width,
        proposedPosition.y,
        proposedPosition.y + size.height
      )

      val wouldCollide = entity.getCollidingEntities.exists { otherEntity =>
        val otherBoundingBox = otherEntity.getBoundingBox
        movementAxis match {
          case MovementAxis.Horizontal =>
            isIntersectingHorizontally(proposedBoundingBox, otherBoundingBox)
          case MovementAxis.Vertical =>
            isIntersectingVertically(proposedBoundingBox, otherBoundingBox)
        }
      }

      // print the bounding boxes of the colliding entities
      //   if wouldCollide then
      //        println(
      //          s"entity: ${entity.getBoundingBox}, other: ${entity.getCollidingEntities.head.getBoundingBox}"
      //        )
      wouldCollide && isEntityWithinBounds(
        entity,
        entity.getCollidingEntities.head
      )

    private def getCollidingEntities: List[Entity] =
      val boundingBox = entity.getBoundingBox
      EntityManager().entities
        .filter(_.hasComponent(classOf[ColliderComponent]))
        .filter { otherEntity =>
        !entity.isSameEntity(otherEntity) && isIntersecting(
          boundingBox,
          otherEntity.getBoundingBox
        )
      }

  private def isEntityWithinBounds(
      entityA: Entity,
      entityB: Entity
  ): Boolean = {
    val positionA = entityA
      .getComponent[PositionComponent]
      .get
    val positionB = entityB
      .getComponent[PositionComponent]
      .get
    val sizeA = entityA
      .getComponent[ColliderComponent]
      .get
      .size
    val sizeB = entityB
      .getComponent[ColliderComponent]
      .get
      .size

    val checkedX =
      boundaryCheck(positionA.x, positionB.x + sizeB.width, sizeA.width)
    val checkedY =
      boundaryCheck(positionA.y, positionB.y + sizeB.height, sizeA.height)

    checkedX == positionA.x && checkedY == positionA.y
  }

  private def isIntersecting(box1: BoundingBox, box2: BoundingBox): Boolean =
    isIntersectingHorizontally(box1, box2) && isIntersectingVertically(
      box1,
      box2
    )

  private def isIntersectingHorizontally(
      box1: BoundingBox,
      box2: BoundingBox
  ): Boolean =
    // If the right side of box1 is to the right of the left side of box2
    (box1.right > box2.left) && (box1.left < box2.right)

  private def isIntersectingVertically(
      box1: BoundingBox,
      box2: BoundingBox
  ): Boolean =
    // If the bottom side of box1 is below the top side of box2
    // and the top side of box1 is above the bottom side of box2
    (box1.bottom >= box2.top) && (box1.top <= box2.bottom)
}
