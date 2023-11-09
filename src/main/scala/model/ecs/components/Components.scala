package model.ecs.components


sealed trait Component
case class Size(width: Double, height: Double)
case class PositionComponent(x: Double, y: Double) extends Component
case class GravityComponent(gravity: Double) extends Component
case class ColliderComponent(size: Size) extends Component
case class PlayerComponent() extends Component