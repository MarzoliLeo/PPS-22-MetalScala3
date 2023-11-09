package model.ecs.systems

import javafx.geometry.BoundingBox
import model.ecs.components.{ColliderComponent, Component, PositionComponent}
import model.ecs.entities.{Entity, EntityManager}

case object CollisionSystem:
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

  private def isIntersecting(box1: BoundingBox, box2: BoundingBox): Boolean = {
    (box1.right > box2.left) && (box1.left < box2.right) && (box1.bottom > box2.top) && (box1.top < box2.bottom)
  }

  private def getBoundingBox(entity: Entity): BoundingBox = {
    val size = entity.getColliderComponent.get.size
    val position = entity.getPositionComponent.get
    val x = position.x
    val y = position.y
    BoundingBox(x, x + size.width, y, y + size.height)
  }

  def isColliding(entity1: Entity): Boolean = {
    getCollidingEntities(entity1) match {
      case Some(_) => true
      case None    => false
    }
  }

  def wouldCollide(
      entity: Entity,
      proposedPosition: PositionComponent
  ): Boolean =
    // Create a bounding box for the proposed position
    val size = entity.getColliderComponent.get.size
    val proposedBoundingBox = BoundingBox(
      proposedPosition.x,
      proposedPosition.x + size.width,
      proposedPosition.y,
      proposedPosition.y + size.height
    )

    // Check if the proposed bounding box would intersect with any other entity
    getCollidingEntities(entity) match {
      case Some(collidingEntities) =>
        collidingEntities.exists { otherEntity =>
          val boundingBox = getBoundingBox(otherEntity)
          isIntersecting(proposedBoundingBox, boundingBox)
        }
      case None => false
    }

  private def getCollidingEntities(entity: Entity): Option[List[Entity]] = {
    val collidingEntities = EntityManager().entities.filter { otherEntity =>
      val boundingBox = getBoundingBox(entity)
      !entity.isSameEntity(otherEntity) && isIntersecting(
        boundingBox,
        getBoundingBox(otherEntity)
      )
    }

    if (collidingEntities.isEmpty) None else Some(collidingEntities)
  }
