package ecs.systems

import ecs.entities.{EntityManager, PlayerEntity}
import ecs.components.Position
import ecs.systems.SystemManager

object Systems {
  
  val playerMovementSystem: EntityManager => Unit =
    manager => manager.getEntitiesByClass(classOf[PlayerEntity])
      .foreach(x => {
        val currentPosition = x.getComponent(classOf[Position]).asInstanceOf[Position]
        //for immutability
        x.replaceComponent(Position(currentPosition.x + 1, currentPosition.y))
      })
    
    //TODO nuovi systems qua sotto...
}
