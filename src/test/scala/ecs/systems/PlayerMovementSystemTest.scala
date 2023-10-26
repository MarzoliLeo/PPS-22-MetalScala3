package ecs.systems

import ecs.components.PositionComponent
import ecs.entities.{EntityManager, PlayerEntity}
import org.scalatest.flatspec.AnyFlatSpec

class PlayerMovementSystemTest extends AnyFlatSpec:
  val entityManager: EntityManager = EntityManager()
  private val playerEntity: PlayerEntity = PlayerEntity(PositionComponent(0, 0))
  entityManager.addEntity(playerEntity)

//  "PlayerMovementSystem"