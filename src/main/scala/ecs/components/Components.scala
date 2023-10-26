package ecs.components

import javafx.scene.paint.Color

sealed trait Component

//Component for the position of the entity in the game world.
case class PositionComponent(var x: Int, var y: Int) extends Component

//Component for enabling the rendering of the entity.
case class VisibleComponent() extends Component

//Component for the color of the entity.
case class ColorComponent(color: Color) extends Component
