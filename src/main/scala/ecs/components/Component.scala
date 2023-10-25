package ecs

sealed trait Component

case class Position(x: Int, y: Int) extends Component