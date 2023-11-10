package model.ecs.components

import javafx.scene.paint.Color

sealed trait Component
case class Size(width: Double, height: Double)
case class PositionComponent(x: Double, y: Double) extends Component
case class GravityComponent(gravity: Double) extends Component

case class SpriteComponent(spritePath: List[String]) extends Component


case class DirectionComponent(d: Direction) extends Component

case class VelocityComponent(x: Double, y: Double) extends Component
case class ColliderComponent(size: Size) extends Component
case class ColorComponent(color: Color) extends Component
case class PlayerComponent() extends Component
