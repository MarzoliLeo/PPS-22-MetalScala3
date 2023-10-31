package model.ecs.systems
import javafx.scene.input.KeyCode
import model.ecs.components.{Component, GravityComponent, PositionComponent}
import model.ecs.entities.{Entity, EntityManager, PlayerEntity}
import model.ecs.observer.Observable
import model.event.Event
import model.event.Event.Move
import model.inputsQueue
import model.utilities.Empty

// TODO: notify Observers when updates are done
// TODO: apply DRY principle when possible
object Systems extends Observable[Event] {

  private def moveEntity(entity: Entity, dx: Int, dy: Int): Unit =
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .get
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
      .getEntitiesWithComponent(classOf[PositionComponent])
      .foreach(entity => moveEntity(entity, 1, 0))

  val inputMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesByClass(classOf[PlayerEntity]).foreach { entity =>
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
        .getEntitiesWithComponent(classOf[PositionComponent], classOf[GravityComponent])
        .foreach(entity => {
          val currentPosition: PositionComponent = entity
            .getComponent(classOf[PositionComponent])
            .get
            .asInstanceOf[PositionComponent]

          val gravityToApply: GravityComponent = entity
            .getComponent(classOf[GravityComponent])
            .get
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
