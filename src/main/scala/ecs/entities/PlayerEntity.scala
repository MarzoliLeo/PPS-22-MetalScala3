package ecs.entities

import ecs.components.Component

final case class PlayerEntity(components: Component*) extends Entity(components: _*)