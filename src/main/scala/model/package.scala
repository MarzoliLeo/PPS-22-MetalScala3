import model.ecs.entities.Entity
import model.utilities.{Cons, Empty, Stack}

package object model:
  val GUIWIDTH = 1230
  val GUIHEIGHT = 700
  val Fps = 60
  val FRICTION_FACTOR = 0.5 // Define a friction factor between 0 and 1
  val fixedSpriteWidth = 100 // Desired fixed width
  val fixedSpriteHeight = 100 // Desired fixed height
  val INPUT_MOVEMENT_VELOCITY = 1000
  val MOVEMENT_DURATION = 0.1
  val JUMP_MOVEMENT_VELOCITY = 500
  val BULLET_VELOCITY = 1000
  val GRAVITY_VELOCITY = 0.5
  val CLUTCHFACTOR = 2
  var isGravityEnabled = true
  var isTouchingGround = false

  val VERTICAL_COLLISION_SIZE = 100
  val HORIZONTAL_COLLISION_SIZE = 100
  var inputsQueue: Stack[Entity => Unit] = Empty

  //Variabili di AI.
  val NUMBER_OF_ENEMIES = 2
  var AItimeElapsedSinceLastExecution = 0
  val AIexecutionInterval = 20
  val AIexecutionSpeed = 1

  //Sprites
  val s_MarcoRossi = "sprites/MarcoRossi.png"
  val s_MarcoRossiMove = "sprites/MarcoRossiMove.png"
  val s_MarcoRossiJump = "sprites/MarcoRossiJump.png"
  val s_MarcoRossiJumpingMoving = "sprites/MarcoRossiJumpingMoving.png"
  val s_MarcoRossiCluch = "sprites/MarcoRossiCluch.png"
  val s_BigBullet = "sprites/BigBullet.png"
  val s_SmallBullet = "sprites/SmallBullet.png"
  val s_Weapon_H = "sprites/Weapon_H.png"
  val s_EnemyCrab = "sprites/EnemyCrab.png"
  val s_EnemyCrabMoving = "sprites/EnemyCrabMoving.png"
  val s_Box = "sprites/Box.jpg"
  val s_Logo = "sprites/Logo.jpg"
  val s_GameBackground ="sprites/Background1230x700.png"
  



