package ecs.entities

import ecs.components.Component

case class PlayerEntity(components: Component*) extends Entity(components: _*)