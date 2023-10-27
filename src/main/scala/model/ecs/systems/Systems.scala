package model.ecs.systems

import model.ecs.components.{Component, PositionComponent}
import model.ecs.entities.{BoxEntity, EntityManager, PlayerEntity}

object Systems {

  val playerMovementSystem: EntityManager => Unit =
    manager =>
      manager
        .getEntitiesByClass(classOf[BoxEntity])
        .foreach(entity => {
          val currentPosition: PositionComponent = entity
            .getComponent(classOf[PositionComponent])
            .getOrElse(PositionComponent(0, 0))
            .asInstanceOf[PositionComponent]
          // for immutability
          entity.replaceComponent(
            PositionComponent(currentPosition.x + 1, currentPosition.y)
          )
        })

  // TODO nuovi systems qua sotto...
}
