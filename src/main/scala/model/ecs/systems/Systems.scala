package model.ecs.systems

import javafx.scene.input.KeyCode
import model.ecs.components.*
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionSystem.{MovementAxis, wouldCollide}
import model.event.Event
import model.event.Event.Move
import model.event.observer.Observable
import model.inputsQueue
import model.utilities.Empty

object Systems extends Observable[Event]:

  private def updatePositionAndNotify(
      entity: Entity,
      positionComponent: PositionComponent
  ): Unit = {
    entity.replaceComponent(positionComponent)
    notifyObservers(Move(entity.id, positionComponent))
  }

  /** Applies a boundary check to a position value, ensuring it stays within the
    * bounds of the system.
    * @param pos
    *   The position value to check.
    * @param max
    *   The maximum value allowed for the position.
    * @param size
    *   The size of the object being checked.
    * @return
    *   The new position value after the boundary check has been applied.
    */
  def boundaryCheck(pos: Double, max: Double, size: Double): Double =
    math.max(0.0, math.min(pos, max - size))


  private def moveEntity(entity: Entity, dx: Double, dy: Double): Unit = {
    val currentPosition = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]
    val collider = entity
      .getComponent(classOf[ColliderComponent])
      .get
      .asInstanceOf[ColliderComponent]

    val movementAxis =
      if (dx != 0) MovementAxis.Horizontal else MovementAxis.Vertical
    val proposedPosition = PositionComponent(
      boundaryCheck(
        currentPosition.x + dx,
        model.GUIWIDTH,
        collider.size.width
      ),
      boundaryCheck(
        currentPosition.y + dy,
        model.GUIHEIGHT,
        collider.size.height
      )
    )

    if (!entity.wouldCollide(proposedPosition, movementAxis)) {
      updatePositionAndNotify(entity, proposedPosition)
    } else {
      // Handle the collision case here if needed
    }
  }

  private def handleInput(command: KeyCode, entity: Entity): Unit =
    command match {
      case KeyCode.W => moveEntity(entity, 0, -model.JUMP_MOVEMENT_VELOCITY)
      case KeyCode.A => moveEntity(entity, -model.INPUT_MOVEMENT_VELOCITY, 0)
      case KeyCode.S => moveEntity(entity, 0, model.INPUT_MOVEMENT_VELOCITY)
      case KeyCode.D => moveEntity(entity, model.INPUT_MOVEMENT_VELOCITY, 0)
      case _         => println("[INPUT] Invalid key")
    }

  val passiveMovementSystem: EntityManager => Unit = manager =>
    manager
      .getEntitiesWithComponent(classOf[PositionComponent])
      .foreach(entity => moveEntity(entity, 1.0, 0))

  val inputMovementSystem: EntityManager => Unit = manager =>
    manager.getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
      entity =>
        inputsQueue.peek.foreach(command => handleInput(command, entity))
        inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }

  val gravitySystem: EntityManager => Unit = manager =>
    manager
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[GravityComponent],
        classOf[ColliderComponent]
      )
      .foreach { entity =>
        val currentPosition = entity
          .getComponent(classOf[PositionComponent])
          .get
          .asInstanceOf[PositionComponent]
        val gravityComp = entity
          .getComponent(classOf[GravityComponent])
          .get
          .asInstanceOf[GravityComponent]
        val collider = entity
          .getComponent(classOf[ColliderComponent])
          .get
          .asInstanceOf[ColliderComponent]

        val newPosition = PositionComponent(
          currentPosition.x,
          boundaryCheck(
            currentPosition.y + gravityComp.gravity,
            model.GUIHEIGHT,
            collider.size.height
          )
        )

        if (!entity.wouldCollide(newPosition, MovementAxis.Vertical)) {
          updatePositionAndNotify(entity, newPosition)
        }
      }
