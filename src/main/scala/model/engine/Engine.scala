package model.engine

import GameStatus.*
import javafx.scene.paint.Color
import model.ecs.components.PositionComponent
import model.ecs.entities.{EntityManager, PlayerEntity}
import model.ecs.systems.SystemManager
import model.event.Event
import model.event.Event.Tick
import model.event.observer.Observable
import view.MainMenu

trait Engine extends GameEngine with Observable[Event]{
  def start(): Unit
  def stop(): Unit
  def pause(): Unit
  def resume(): Unit
  def getStatus(): GameStatus

}

object Engine {
  def apply(systemManager: SystemManager): Engine = new EngineImpl(systemManager)
  private class EngineImpl(systemManager: SystemManager) extends Engine {
    private val Fps = 60
    private val gameLoop = GameLoop(Fps, this)

    // TODO: delete debug print
    override def tick(elapsedTime: Long): Unit =
      systemManager.updateAll(elapsedTime)
      notifyObservers(Tick())

    override def start(): Unit = {
      // init()
      gameLoop.status match {
        case Stopped =>
          gameLoop.start() // .start() its because gameloop is a thread.
        case _ => print("GameLoop already started")
      }
    }

    override def stop(): Unit = {
      gameLoop.halt()
    }
    override def pause(): Unit =
      gameLoop.pause()
    override def resume(): Unit =
      gameLoop.unPause()

    // Method for testing purposes
    def getStatus(): GameStatus = gameLoop.status

  }
}
