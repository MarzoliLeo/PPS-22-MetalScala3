package engine

import ecs.systems
import ecs.systems.SystemManager
import engine.GameStatus.*

trait Engine extends GameEngine {
  // TODO init method

  def start(): Unit
  def stop(): Unit
  def pause(): Unit
  def resume(): Unit
  def getStatus(): GameStatus

}

object Engine {
  // TODO apply method with parameters
  def apply(systemManager: SystemManager): Engine = new EngineImpl(
    systemManager
  )
  private class EngineImpl(systemManager: SystemManager) extends Engine {
    private val Fps = 60
    private val gameLoop = GameLoop(Fps, this)
    override def tick(): Unit = systemManager.updateAll()

    override def start(): Unit = {
      gameLoop.status match {
        case Stopped =>
          gameLoop.start() // .start() its because gameloop is a thread.
        case _ => print("GameLoop already started")
      }
    }
    override def stop(): Unit = {
      gameLoop.halt()
      // TODO mediator.unsubscribe(this)
    }
    override def pause(): Unit = gameLoop.pause()
    override def resume(): Unit = gameLoop.unPause()

    // Method for testing purposes
    def getStatus(): GameStatus = gameLoop.status

  }
}
