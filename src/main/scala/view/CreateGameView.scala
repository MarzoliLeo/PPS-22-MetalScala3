package view

import model.ecs.components.*
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.{PlayerEntity, SlugEntity}
import model.ecs.entities.weapons.{AmmoBoxEntity, MachineGunEntity}
import model.ecs.systems.*
import model.engine.Engine
import model.{
  ammoBoxComponents,
  boxComponents,
  enemyComponents,
  machineGunWeaponComponents,
  playerComponents,
  slugComponents
}

trait CreateGameView:
  def createGame(gameEngine: Engine): Unit =
    EntityManager
      .addEntity(
        createEntity(
          PlayerEntity(),
          playerComponents(PositionComponent(50, 700)): _*
        )
      )
      .addEntity(
        createEntity(
          EnemyEntity(),
          enemyComponents(PositionComponent(900, 400)): _*
        )
      )
      .addEntity(
        createEntity(
          EnemyEntity(),
          enemyComponents(PositionComponent(1100, 700)): _*
        )
      )
      .addEntity(
        createEntity(
          BoxEntity(),
          boxComponents(PositionComponent(250, 700)): _*
        )
      )
      .addEntity(
        createEntity(
          BoxEntity(),
          boxComponents(PositionComponent(400, 400)): _*
        )
      )
      .addEntity(
        createEntity(
          BoxEntity(),
          boxComponents(PositionComponent(500, 300)): _*
        )
      )
      .addEntity(
        createEntity(
          BoxEntity(),
          boxComponents(PositionComponent(750, 150)): _*
        )
      )
      .addEntity(
        createEntity(
          BoxEntity(),
          boxComponents(PositionComponent(650, 700)): _*
        )
      )
      .addEntity(
        createEntity(
          BoxEntity(),
          boxComponents(PositionComponent(750, 700)): _*
        )
      )
      .addEntity(
        createEntity(
          BoxEntity(),
          boxComponents(PositionComponent(900, 500)): _*
        )
      )
      .addEntity(
        createEntity(
          BoxEntity(),
          boxComponents(PositionComponent(1000, 500)): _*
        )
      )
    val randomPositionTupleForGunEntity = generateRandomPosition()
    EntityManager.addEntity(
      createEntity(
        MachineGunEntity(),
        machineGunWeaponComponents(
          PositionComponent(
            randomPositionTupleForGunEntity._1,
            randomPositionTupleForGunEntity._2
          )
        ): _*
      )
    )
    val randomPositionTupleForAmmoBoxEntity = generateRandomPosition()
    EntityManager
      .addEntity(
        createEntity(
          AmmoBoxEntity(),
          ammoBoxComponents(
            PositionComponent(
              randomPositionTupleForAmmoBoxEntity._1,
              randomPositionTupleForAmmoBoxEntity._2
            )
          ): _*
        )
      )
      .addEntity(
        createEntity(
          SlugEntity(),
          slugComponents(PositionComponent(500, 700)): _*
        )
      )
    SystemManager(EntityManager)
      .addSystem(InputSystem())
      .addSystem(JumpingSystem())
      .addSystem(GravitySystem())
      .addSystem(PositionUpdateSystem())
      .addSystem(BulletMovementSystem())
      .addSystem(AISystem())
      .addSystem(SpriteSystem())
      .addSystem(GameOverSystem(gameEngine))

  private def createEntity(entity: Entity, components: Component*): Entity =
    components.foldLeft(entity) { (e, component) =>
      e.addComponent(component)
    }

  private def generateRandomPosition(): (Int, Int) =
    val randomInt = scala.util.Random.nextInt(model.randomPositions.size)
    val randomPositionTuple = model.randomPositions(randomInt)
    model.randomPositions = model.randomPositions.patch(randomInt, Nil, 1)
    randomPositionTuple
