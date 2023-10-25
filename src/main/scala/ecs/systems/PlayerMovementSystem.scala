package ecs.systems
import ecs.components.Position
import ecs.entities.{Entity, EntityManager, PlayerEntity}

object PlayerMovementSystem extends System:
  def apply(): System = this

  override def update(entityManager: EntityManager): Unit =
    entityManager
      .getEntitiesByClass(classOf[PlayerEntity])
      .foreach(x => {
        val currentPosition = x.getComponent(classOf[Position]).asInstanceOf[Position]
        x.replaceComponent(
          Position(
            currentPosition.x + 1,
            currentPosition.y
          )
        )
      })
