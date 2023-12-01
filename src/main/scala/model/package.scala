import model.ecs.components.*
import model.ecs.entities.Entity
import model.utilities.{Cons, Empty, Stack}

package object model:
  // Game
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
  var isGravityEnabled = true
  var isTouchingGround = false

  val VERTICAL_COLLISION_SIZE = 100
  val HORIZONTAL_COLLISION_SIZE = 100
  var inputsQueue: Stack[Entity => Unit] = Empty

  // Weapon variables
  val ammoBoxRefill = 10

  // Variabili di AI.
  val NUMBER_OF_ENEMIES = 2
  var AItimeElapsedSinceLastExecution = 0
  val AIexecutionInterval = 20
  val AIexecutionSpeed = 1

  // Sprites
  val s_MarcoRossi = "sprites/MarcoRossi.png"
  val s_MarcoRossiMove = "sprites/MarcoRossiMove.png"
  val s_MarcoRossiJump = "sprites/MarcoRossiJump.png"
  val s_MarcoRossiJumpingMoving = "sprites/MarcoRossiJumpingMoving.png"
  val s_BigBullet = "sprites/BigBullet.png"
  val s_SmallBullet = "sprites/SmallBullet.png"
  val s_Weapon_H = "sprites/Weapon_H.png"
  val s_EnemyCrab = "sprites/EnemyCrab.png"
  val s_EnemyCrabMoving = "sprites/EnemyCrabMoving.png"
  val s_Box = "sprites/Box.png"
  val s_Logo = "sprites/Logo.jpg"
  val s_GameBackground = "sprites/Background1230x700.png"
  val s_AmmoBox = "sprites/Munitions.png"

  // Components
  private val defaultComponents: Seq[Component] = Seq(
    PositionComponent(0, 0),
    GravityComponent(model.GRAVITY_VELOCITY),
    SizeComponent(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE),
    VelocityComponent(0, 0),
    DirectionComponent(RIGHT),
    JumpingComponent(false)
  )

  private def createComponents(pos: PositionComponent, additionalComponents: Component*): Seq[Component] = {
    defaultComponents.collect {
      case pc: PositionComponent => pc.copy(x = pos.x, y = pos.y)
      case other => other
    } ++ additionalComponents
  }

  val playerComponents: PositionComponent => Seq[Component] = pos =>
    createComponents(pos, PlayerComponent(), SpriteComponent(s_MarcoRossi), BulletComponent(StandardBullet()))

  val enemyComponents: PositionComponent => Seq[Component] = pos =>
    createComponents(pos, AIComponent(), SpriteComponent(s_EnemyCrab), BulletComponent(EnemyBullet()))

  val boxComponents: PositionComponent => Seq[Component] = pos =>
    createComponents(pos, SpriteComponent(s_Box))

  val machineGunWeaponComponents: PositionComponent => Seq[Component] = pos =>
    createComponents(pos, SpriteComponent(s_Weapon_H))

  val ammoBoxComponents: PositionComponent => Seq[Component] = pos =>
    createComponents(pos, SpriteComponent(s_AmmoBox), AmmoComponent(ammoBoxRefill))