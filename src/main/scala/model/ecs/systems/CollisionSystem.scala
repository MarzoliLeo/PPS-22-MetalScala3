package model.ecs.systems

import model.ecs.components.{ColliderComponent, Component, PositionComponent}
import model.ecs.entities.{Entity, EntityManager}

object CollisionSystem {
  private case class BoundingBox(
      left: Double,
      right: Double,
      top: Double,
      bottom: Double
  )

  extension (entity: Entity)
    private def retrieveComponent[T <: Component](
        componentClass: Class[T]
    ): Option[T] =
      entity.getComponent(componentClass).asInstanceOf[Option[T]]

    private def getPositionComponent: Option[PositionComponent] =
      retrieveComponent[PositionComponent](classOf[PositionComponent])

    private def getColliderComponent: Option[ColliderComponent] =
      retrieveComponent[ColliderComponent](classOf[ColliderComponent])

    private def getBoundingBox: BoundingBox =
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

    def wouldCollide(proposedPosition: PositionComponent): Boolean =
      val size = entity.getColliderComponent.get.size
      val proposedBoundingBox = BoundingBox(
        proposedPosition.x,
        proposedPosition.x + size.width,
        proposedPosition.y,
        proposedPosition.y + size.height
      )

      entity.getCollidingEntities.exists { otherEntity =>
        val boundingBox = otherEntity.getBoundingBox
        isIntersecting(proposedBoundingBox, boundingBox)
      }

    private def getCollidingEntities: List[Entity] =
      val boundingBox = entity.getBoundingBox
      EntityManager().entities.filter { otherEntity =>
        !entity.isSameEntity(otherEntity) && isIntersecting(
          boundingBox,
          otherEntity.getBoundingBox
        )
      }

  private def isIntersecting(box1: BoundingBox, box2: BoundingBox): Boolean =
    (box1.right > box2.left) && (box1.left < box2.right) && (box1.bottom > box2.top) && (box1.top < box2.bottom)
}
