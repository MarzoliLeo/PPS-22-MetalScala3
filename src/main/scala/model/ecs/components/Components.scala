package model.ecs.components

import javafx.scene.paint.Color

import java.util.UUID

sealed trait Component {
    private val uuid: UUID = UUID.randomUUID()

    // check the uuid of the component
    def isSameComponent(component: Component): Boolean = {
        this.uuid == component.uuid
    }
}

case class PositionComponent(x: Int, y: Int) extends Component

case class VisibleComponent() extends Component

case class ColorComponent(color: Color) extends Component