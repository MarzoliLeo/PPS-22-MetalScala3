package model.event

import model.ecs.components.{PositionComponent, SpriteComponent}
import model.ecs.entities.Entity

import java.util.UUID

sealed trait Event

object Event:
  //final case class Move(entityID: UUID, sprite: SpriteComponent, position: PositionComponent, duration: Double) extends Event
  //final case class Spawn(entityID: UUID, ofType: Class[_ <: Entity], sprite: SpriteComponent, position: PositionComponent) extends Event
  final case class Tick() extends Event
  //final case class Jump(entityID: UUID, sprite: SpriteComponent, position: PositionComponent, jumpHeight: Double, duration: Double) extends Event
  //final case class Gravity(entityID: UUID, position: PositionComponent) extends Event
