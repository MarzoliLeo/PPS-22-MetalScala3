package ecs.entities

import ecs.Component

final case class PlayerEntity(components: Component*) extends Entity(components: _*)