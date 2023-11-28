package model.ecs.components

import javafx.scene.paint.Color
import model.ecs.entities.Entity

import scala.annotation.targetName
import scala.math.sqrt

/** Represents a generic component in the ECS system.
  */
sealed trait Component

case class BulletComponent(bullet: Bullet) extends Component

case class CollisionComponent(entities: scala.collection.mutable.Set[Entity])
    extends Component

/** An Entity has this component if it's a trigger
  */
case class TriggerComponent() extends Component

/** Represents the size of an entity.
  * @param width
  *   The width of the entity.
  * @param height
  *   The height of the entity.
  */
case class SizeComponent(width: Double, height: Double) extends Component

/** Represents the position of an entity.
  * @param x
  *   The x-coordinate of the entity.
  * @param y
  *   The y-coordinate of the entity.
  */
case class PositionComponent(x: Double, y: Double) extends Component:
  def getUpdatedPosition(
      elapsedTime: Long,
      velocity: VelocityComponent
  ): PositionComponent = {
    val newPositionX = this.x + velocity.x * elapsedTime * 0.001
    val newPositionY = this.y + velocity.y * elapsedTime * 0.001

    PositionComponent(newPositionX, newPositionY)
  }

  /** Adds a velocity component to the current position component.
    * @param v
    *   The velocity component to add.
    * @return
    *   The new position component after adding the velocity.
    */
  @targetName("sum")
  def +(v: VelocityComponent): PositionComponent =
    PositionComponent(x + v.x, y + v.y)

/** Represents the gravity applied to an entity.
  * @param gravity
  *   The gravity value.
  */
case class GravityComponent(gravity: Double) extends Component

/** Represents the jumping state of an entity.
  * @param isJumping
  *   Indicates if the entity is currently jumping.
  */
case class JumpingComponent(isJumping: Boolean) extends Component

/** Represents the sprite path of an entity.
  * @param spritePath
  *   The path to the sprite image file.
  */
case class SpriteComponent(spritePath: String) extends Component

/** Represents the direction of an entity.
  * @param d
  *   The direction of the entity.
  */
case class DirectionComponent(d: Direction) extends Component
case class AIComponent() extends Component

/** Represents the color of an entity.
  * @param color
  *   The color of the entity.
  */
case class ColorComponent(color: Color) extends Component

/** Represents a player entity.
  */
case class PlayerComponent() extends Component

/** Represents the health of an entity.
  * @param currentHealth
  *   The current health value.
  */
case class HealthComponent(var currentHealth: Int) extends Component

/** Represents the velocity of an entity.
  * @param x
  *   The x-component of the velocity.
  * @param y
  *   The y-component of the velocity.
  */
case class VelocityComponent(var x: Double, var y: Double) extends Component:
  /** Constructs a velocity component from two position components.
    * @param to
    *   The target position component.
    * @param from
    *   The source position component.
    */
  def this(to: PositionComponent, from: PositionComponent) =
    this(to.x - from.x, to.y - from.y)

  /** Adds a velocity component to the current velocity component.
    * @param v
    *   The velocity component to add.
    * @return
    *   The new velocity component after adding the other velocity.
    */
  @targetName("sum")
  def +(v: VelocityComponent): VelocityComponent =
    VelocityComponent(x + v.x, y + v.y)

  @targetName("multiply")
  def *(fact: Double): VelocityComponent =
    VelocityComponent(x * fact, y * fact)

  /** Returns a string representation of the velocity component.
    *
    * @return
    *   The string representation of the velocity component.
    */
  override def toString: String = s"VelocityComponent($x,$y)"

