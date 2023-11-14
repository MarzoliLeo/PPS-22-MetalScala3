package model.ecs.components

import javafx.scene.paint.Color

import scala.annotation.targetName
import scala.math.sqrt

sealed trait Component
case class Size(width: Double, height: Double) extends Component
case class PositionComponent(x: Double, y: Double) extends Component:
  @targetName("sum")
  def +(v: VelocityComponent): PositionComponent =
    PositionComponent(x + v.x, y + v.y)
case class GravityComponent(gravity: Double) extends Component
case class JumpingComponent(isJumping: Boolean) extends Component
case class SpriteComponent(spritePath: List[String]) extends Component
case class DirectionComponent(d: Direction) extends Component
case class ColliderComponent(size: Size) extends Component
case class ColorComponent(color: Color) extends Component
case class PlayerComponent() extends Component
case class VelocityComponent(var x: Double, var y: Double) extends Component:

  def this(to: PositionComponent, from: PositionComponent) =
    this(to.x - from.x, to.y - from.y)

  @targetName("sum")
  def +(v: VelocityComponent): VelocityComponent =
    VelocityComponent(x + v.x, y + v.y)

  def module(): Double = sqrt(x * x + y * y)

  def getNormalized: VelocityComponent =
    val mod = module()
    VelocityComponent(x / mod, y / mod)

  @targetName("multiply")
  def *(fact: Double): VelocityComponent =
    VelocityComponent(x * fact, y * fact)

  override def toString: String = s"VelocityComponent($x,$y)"
