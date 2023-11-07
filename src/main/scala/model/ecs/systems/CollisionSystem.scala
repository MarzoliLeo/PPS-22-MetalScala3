package model.ecs.systems

import model.ecs.components.{ColliderComponent, Component, PositionComponent}
import model.ecs.entities.{Entity, EntityManager}

case object CollisionSystem:
  extension (entity: Entity)
    private def retrieveComponent[T <: Component](
        componentClass: Class[T]
    ): Option[T] =
      entity.getComponent(componentClass).asInstanceOf[Option[T]]

    private def getPositionComponent: Option[PositionComponent] =
      retrieveComponent[PositionComponent](classOf[PositionComponent])

    private def getColliderComponent: Option[ColliderComponent] =
      retrieveComponent[ColliderComponent](classOf[ColliderComponent])

  def isColliding(entity1: Entity, entity2: Entity): Boolean =
    val size1 = entity1.getColliderComponent.get.size
    val size2 = entity2.getColliderComponent.get.size

    val position1 = entity1.getPositionComponent.get
    val position2 = entity2.getPositionComponent.get

    val x1 = position1.x // Get the x-coordinate of entity1
    val y1 = position1.y // Get the y-coordinate of entity1
    val x2 = position2.x // Get the x-coordinate of entity2
    val y2 = position2.y // Get the y-coordinate of entity2

    // Check for collision based on bounding boxes
    val left1 = x1
    val right1 = x1 + size1.width
    val top1 = y1
    val bottom1 = y1 + size1.height

    val left2 = x2
    val right2 = x2 + size2.width
    val top2 = y2
    val bottom2 = y2 + size2.height

    // Check if the bounding boxes of the two entities intersect
    (right1 > left2) && (left1 < right2) && (bottom1 > top2) && (top1 < bottom2)

  def handleCollision(pair: (Entity, Entity)): Unit = pair match {
    case (entity1, entity2) =>
      (for {
        pos1 <- entity1.getPositionComponent
        col1 <- entity1.getColliderComponent
        pos2 <- entity2.getPositionComponent
        col2 <- entity2.getColliderComponent
      } yield (pos1, col1, pos2, col2)) match {
        case Some((position1, collider1, position2, collider2)) =>
          println("Handling collision")
          val overlapX =
            (position1.x + collider1.size.width / 2) - (position2.x + collider2.size.width / 2)
          val overlapY =
            (position1.y + collider1.size.height / 2) - (position2.y + collider2.size.height / 2)

          val newPosition1 = if (Math.abs(overlapX) > Math.abs(overlapY)) {
            if (overlapX > 0)
              // If the overlap is positive in the X direction, move the entity to the right to resolve the collision.
              position1.copy(x = position1.x + Math.abs(overlapX))
            else
              // If the overlap is negative in the X direction, move the entity to the left to resolve the collision.
              position1.copy(x = position1.x - Math.abs(overlapX))
          } else {
            if (overlapY > 0)
              // If the overlap is positive in the Y direction, move the entity down to resolve the collision.
              position1.copy(y = position1.y + Math.abs(overlapY))
            else
              // If the overlap is negative in the Y direction, move the entity up to resolve the collision.
              position1.copy(y = position1.y - Math.abs(overlapY))
          }

          entity1.replaceComponent(newPosition1)
        case _ =>
      }
  }
  def apply(entities: EntityManager): Unit =
    val collidingPairs = for {
      List(entity1, entity2) <- entities
        .getEntitiesWithComponent(classOf[ColliderComponent])
        .combinations(2)
      if !entity1.isSameEntity(entity2) && isColliding(entity1, entity2)
    } yield (entity1, entity2)
    if collidingPairs.nonEmpty then println("Collision!")
    collidingPairs.foreach(handleCollision)
