package model.ecs.systems
import javafx.scene.input.KeyCode
import model.ecs.components.{Component, PositionComponent, GravityComponent}
import model.ecs.entities.{BoxEntity, Entity, EntityManager, PlayerEntity}
import model.inputsQueue
import model.utilities.Empty

object Systems {

  private def moveEntity(entity: Entity, dx: Int, dy: Int): Unit =
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .getOrElse(PositionComponent(0, 0))
      .asInstanceOf[PositionComponent]
      // for immutability
    if (currentPosition.x < 0) {
      entity.replaceComponent(
        PositionComponent(0, currentPosition.y)
      )
    } else if (currentPosition.x + 100 + model.INPUT_MOVEMENT_VELOCITY/*Dimensione del Box (Sarà poi quella del player)*/ > model.GUIWIDTH) {
      entity.replaceComponent(
        PositionComponent(model.GUIWIDTH - 100 -model.INPUT_MOVEMENT_VELOCITY/*Dimensione del Box (Sarà poi quella del player)*/, currentPosition.y )
      )
    } else entity.replaceComponent(
        PositionComponent(currentPosition.x + dx, currentPosition.y + dy)
    )


  val passiveMovementSystem: EntityManager => Unit = manager =>
    manager
      .getEntitiesByClass(classOf[BoxEntity])
      .foreach(entity => moveEntity(entity, 1, 0))

  val inputMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesByClass(classOf[BoxEntity]).foreach { entity =>
      inputsQueue.peek match {
        case Some(command) =>
          command match {
            case KeyCode.W => moveEntity(entity, 0, -model.JUMP_MOVEMENT_VELOCITY)
            case KeyCode.A => moveEntity(entity, -model.INPUT_MOVEMENT_VELOCITY, 0)
            case KeyCode.S => moveEntity(entity, 0, model.INPUT_MOVEMENT_VELOCITY)
            case KeyCode.D => moveEntity(entity, model.INPUT_MOVEMENT_VELOCITY, 0)
          }
        case None => ()
      }
      inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }
  
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
            .getOrElse(GravityComponent(model.GRAVITY_VELOCITY))
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

        })

}
