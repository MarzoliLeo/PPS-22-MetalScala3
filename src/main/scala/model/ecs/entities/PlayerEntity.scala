package model.ecs.entities

import model.ecs.components.Component

case class PlayerEntity(components: Component*) extends Entity(components: _*)