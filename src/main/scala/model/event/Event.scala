package model.event

import model.ecs.components.{PositionComponent, SpriteComponent}
import model.ecs.entities.Entity

import java.util.UUID

sealed trait Event

object Event:
  final case class Move(entityID: UUID, position: PositionComponent) extends Event
  final case class Spawn(entityID: UUID, ofType: Class[_ <: Entity], sprite: SpriteComponent, position: PositionComponent) extends Event
  final case class Tick() extends Event
