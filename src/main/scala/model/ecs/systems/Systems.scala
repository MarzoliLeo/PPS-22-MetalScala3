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
          entity.replaceComponent(
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

          entity.replaceComponent(
            PositionComponent(currentPosition.x, currentPosition.y + gravityToApply.gravity)
          )
          print("Applying gravity to entity\n")
          print("Y Position: " + currentPosition.y + "\n")
        })




}
