package model.ecs.systems

import model.ecs.components.{Component, GravityComponent, PositionComponent}
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
          if (currentPosition.x < 0) {
            entity.replaceComponent(
              PositionComponent(0, currentPosition.y)
            )
          } else if (currentPosition.x + 100 /*Dimensione del Box*/ > model.GUIWIDTH) {
            entity.replaceComponent(
              PositionComponent(model.GUIWIDTH - 100 , currentPosition.y /*Dimensione del Box*/)
            )
          } else entity.replaceComponent(
            PositionComponent(currentPosition.x + 1, currentPosition.y)
          )
        })

  val gravitySystem: EntityManager => Unit =
    manager =>
      manager
        .getEntitiesByClass(classOf[BoxEntity])
        .foreach(entity => {
          val currentPosition: PositionComponent = entity
            .getComponent(classOf[PositionComponent])
            .getOrElse(PositionComponent(0, 0))
            .asInstanceOf[PositionComponent]

          val gravityToApply: GravityComponent = entity
            .getComponent(classOf[GravityComponent])
            .getOrElse(GravityComponent(1))
            .asInstanceOf[GravityComponent]

          if (currentPosition.y < 0) {
              entity.replaceComponent(
                PositionComponent(currentPosition.x, 0)
              )
          } else if (currentPosition.y + 100 /*Dimensione del Box*/ > model.GUIHEIGHT) {
            entity.replaceComponent(
              PositionComponent(currentPosition.x, model.GUIHEIGHT - 100 /*Dimensione del Box*/)
            )
          } else entity.replaceComponent(
            PositionComponent(currentPosition.x, currentPosition.y + gravityToApply.gravity)
          )

          /*print("Applying gravity to entity\n")
          print("Y Position: " + currentPosition.y + "\n")*/
        })

}
