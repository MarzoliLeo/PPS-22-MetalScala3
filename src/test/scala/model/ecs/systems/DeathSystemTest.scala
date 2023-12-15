package model.ecs.systems

import model.ecs.entities.enemies.EnemyEntity
import model.engine.Engine
import model.ecs.entities.{Entity, EntityManager}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.engine.GameStatus.{Running, Stopped}
import model.engine.Engine

class DeathSystemTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  var engine: Engine = _

  before {
    engine = Engine()
    engine.start()
    EntityManager.entities.foreach(EntityManager.removeEntity)
  }

  "A GameOverSystem" should "stop the engine when there are no PlayerEntity instances" in {
    val gameOverSystem = DeathSystem(engine)
    val entity = EnemyEntity()

    EntityManager.addEntity(entity)

    gameOverSystem.update()

    engine.getStatus() shouldBe Stopped
  }

  it should "stop the engine when there are no EnemyEntity instances" in {
    val gameOverSystem = DeathSystem(engine)
    val entity = PlayerEntity()

    EntityManager.addEntity(entity)

    gameOverSystem.update()

    engine.getStatus() shouldBe Stopped
  }

  it should "not stop the engine when there are both PlayerEntity and EnemyEntity instances" in {
    val gameOverSystem = DeathSystem(engine)
    val playerEntity = PlayerEntity()
    val enemyEntity = EnemyEntity()

    EntityManager.addEntity(playerEntity)
    EntityManager.addEntity(enemyEntity)

    gameOverSystem.update()

    engine.getStatus() shouldBe Running
  }
}