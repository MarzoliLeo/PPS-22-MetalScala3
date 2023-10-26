package ecs.systems

import ecs.entities.{BoxEntity, EntityManager, PlayerEntity}
import ecs.components.PositionComponent
import ecs.systems.SystemManager

object Systems {
  
  val playerMovementSystem: EntityManager => Unit =
    manager => manager.getEntitiesByClass(classOf[BoxEntity])
      .foreach(x => {
        val currentPosition = x.getComponent(classOf[PositionComponent]).asInstanceOf[PositionComponent]
        //for immutability
        x.replaceComponent(PositionComponent(currentPosition.x + 1, currentPosition.y))
      })


  //TODO nuovi systems qua sotto...
}
