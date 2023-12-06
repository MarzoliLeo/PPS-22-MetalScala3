package model.event

import model.ecs.components.{PositionComponent, SpriteComponent}
import model.ecs.entities.Entity

import java.util.UUID

sealed trait Event

object Event:
  final case class Tick(entities: List[Entity]) extends Event
  final case class GameOver() extends Event
