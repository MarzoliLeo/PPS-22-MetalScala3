package engine

import org.junit.Test
import org.junit.Assert.*

class EngineTest {

  val engine = Engine.apply()

  engine.start()
  @Test
  def testStart() = {
    // Assert engine is running
    assertEquals(GameStatus.Running, engine.getStatus())
  }

  @Test
  def testStop() = {
    engine.stop()

    // Assert engine is stopped
    assertEquals(GameStatus.Stopped, engine.getStatus())
  }

  @Test
  def testPause() = {
    engine.pause()

    // Assert engine is stopped
    assertEquals(GameStatus.Paused, engine.getStatus())
  }

  @Test
  def testResume() = {
    engine.pause()
    engine.resume()

    // Assert engine is stopped
    assertEquals(GameStatus.Running, engine.getStatus())
  }

}
