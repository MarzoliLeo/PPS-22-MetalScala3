import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import model.ecs.components.{Component, GravityComponent, PositionComponent}
import model.ecs.entities.{Entity, EntityManager, PlayerEntity}
import model.ecs.systems.SystemManager
import model.ecs.systems.Systems.{gravitySystem, inputMovementSystem}
import model.event.observer.Observable
import model.utilities.{Cons, Empty, Stack}

import scala.collection.mutable
import scala.reflect.{ClassTag, classTag}

package object model:
  val GUIWIDTH: Int = 1500
  val GUIHEIGHT: Int = 800

  val Fps = 60

  val FRICTION_FACTOR = 0.50 // Define a friction factor between 0 and 1

  val fixedSpriteWidth = 100.0 // Desired fixed width
  val fixedSpriteHeight = 100.0 // Desired fixed height

  val INPUT_MOVEMENT_VELOCITY = 100
  val MOVEMENT_DURATION = 0.1
  val JUMP_MOVEMENT_VELOCITY = 400
  val JUMP_DURATION = 0.3
  //val GRAVITY_VELOCITY = 10
  val GRAVITY_VELOCITY = 0.5
  var isGravityEnabled = true
  var isTouchingGround = false

  val playerSpriteList: List[String] = List("sprites/MarcoRossi.png",
                              "sprites/MarcoRossiMove.png",
                              "sprites/MarcoRossiJump.png")

  val VERTICAL_COLLISION_SIZE: Double = fixedSpriteHeight
  val HORIZONTAL_COLLISION_SIZE: Double = fixedSpriteWidth

  var inputsQueue: Stack[KeyCode] = Empty

