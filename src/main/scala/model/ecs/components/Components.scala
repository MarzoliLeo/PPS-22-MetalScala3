package model.ecs.components

import javafx.scene.paint.Color

import java.util.UUID

sealed trait Component
case class Size(width: Double, height: Double)
case class PositionComponent(x: Double, y: Double) extends Component
case class GravityComponent(gravity: Double) extends Component
case class ColliderComponent(size: Size) extends Component