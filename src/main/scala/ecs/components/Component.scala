package ecs.components

import javafx.scene.paint.Color

sealed trait Component

case class PositionComponent(x: Int, y: Int) extends Component
case class DisplayableComponent() extends Component
case class ColorComponent(color: Color) extends Component
