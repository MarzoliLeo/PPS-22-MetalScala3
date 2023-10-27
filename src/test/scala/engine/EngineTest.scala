package engine

import model.ecs.entities.EntityManager
import model.ecs.systems.SystemManager
import model.engine.{Engine, GameStatus}
import org.junit.Test
import org.junit.Assert.*

class EngineTest {

  val entityManager: EntityManager = EntityManager()
  val systemManager: SystemManager = SystemManager(entityManager)
  val engine: Engine = Engine()

  engine.start()
  @Test
  def testStart(): Unit = {
    // Assert engine is running
    assertEquals(GameStatus.Running, engine.getStatus())
  }

  @Test
  def testStop(): Unit = {
    engine.stop()

    // Assert engine is stopped
    assertEquals(GameStatus.Stopped, engine.getStatus())
  }

  @Test
  def testPause(): Unit = {
    engine.pause()

    // Assert engine is stopped
    assertEquals(GameStatus.Paused, engine.getStatus())
  }

  @Test
  def testResume(): Unit = {
    engine.pause()
    engine.resume()

    // Assert engine is stopped
    assertEquals(GameStatus.Running, engine.getStatus())
  }

}
