package model.ecs.components

import javafx.scene.paint.Color

import scala.annotation.targetName
import scala.math.sqrt

sealed trait Component
case class SizeComponent(width: Double, height: Double) extends Component
case class PositionComponent(x: Double, y: Double) extends Component:
  @targetName("sum")
  def +(v: VelocityComponent): PositionComponent =
    PositionComponent(x + v.x, y + v.y)
case class GravityComponent(gravity: Double) extends Component
case class JumpingComponent(isJumping: Boolean) extends Component
case class SpriteComponent(spritePath: String) extends Component
case class DirectionComponent(d: Direction) extends Component
case class AIComponent(foolishness: Double) extends Component
case class ColorComponent(color: Color) extends Component

case class PlayerComponent() extends Component
case class VelocityComponent(var x: Double, var y: Double) extends Component:

  def this(to: PositionComponent, from: PositionComponent) =
    this(to.x - from.x, to.y - from.y)

  @targetName("sum")
  def +(v: VelocityComponent): VelocityComponent =
    VelocityComponent(x + v.x, y + v.y)

  @targetName("multiply")
  def *(fact: Double): VelocityComponent =
    VelocityComponent(x * fact, y * fact)
