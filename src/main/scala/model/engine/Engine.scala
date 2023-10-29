package model.engine

import model.ecs.systems.Systems.playerMovementSystem
import GameStatus.*
import javafx.scene.paint.Color
import model.ecs.components.{ColorComponent, PositionComponent}
import model.ecs.entities.{BoxEntity, EntityManager, PlayerEntity}
import model.ecs.systems.SystemManager
import model.systemManager
import view.menu.MainMenu

trait Engine extends GameEngine {
  def start(): Unit
  def stop(): Unit
  def pause(): Unit
  def resume(): Unit
  def getStatus(): GameStatus

}

object Engine {
  def apply(): Engine = new EngineImpl()
  private class EngineImpl() extends Engine {
    private val Fps = 60
    private val gameLoop = GameLoop(Fps, this)

    override def tick(): Unit =
      systemManager.updateAll()

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
