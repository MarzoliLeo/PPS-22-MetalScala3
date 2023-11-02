import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import model.ecs.components.{GravityComponent, PositionComponent}
import model.ecs.entities.{EntityManager, PlayerEntity}
import model.ecs.systems.SystemManager
import model.ecs.systems.Systems.{gravitySystem, inputMovementSystem, passiveMovementSystem}
import model.event.observer.Observable
import model.utilities.{Cons, Empty, Stack}

import scala.collection.mutable

package object model:
  val GUIWIDTH: Int = 1500
  val GUIHEIGHT: Int = 800

  val INPUT_MOVEMENT_VELOCITY = 35
  val JUMP_MOVEMENT_VELOCITY = 250
  val GRAVITY_VELOCITY = 10

  var inputsQueue: Stack[KeyCode] = Empty

