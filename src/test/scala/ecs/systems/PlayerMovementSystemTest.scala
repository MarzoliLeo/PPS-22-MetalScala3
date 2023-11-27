package ecs.systems

import model.ecs.components.PositionComponent
import model.ecs.entities.EntityManager
import model.ecs.entities.player.PlayerEntity
import org.scalatest.flatspec.AnyFlatSpec

class PlayerMovementSystemTest extends AnyFlatSpec:
  val entityManager: EntityManager = EntityManager()
  private val playerEntity = PlayerEntity().addComponent(PositionComponent(0, 0))
  entityManager.addEntity(playerEntity)

//  "PlayerMovementSystem"