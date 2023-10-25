package ecs.systems

import ecs.components.Position
import ecs.entities.{EntityManager, PlayerEntity}
import org.scalatest.flatspec.AnyFlatSpec

class PlayerMovementSystemTest extends AnyFlatSpec:
  val entityManager: EntityManager = EntityManager()
  private val playerEntity: PlayerEntity = PlayerEntity(Position(0, 0))
  entityManager.addEntity(playerEntity)

//  "PlayerMovementSystem"