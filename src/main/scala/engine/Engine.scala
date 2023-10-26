package engine

import ecs.entities.{EntityManager, PlayerEntity}
import ecs.systems
import ecs.systems.SystemManager
import ecs.systems.Systems.playerMovementSystem
import engine.GameStatus.*
import ecs.components.Position
import view.menu.MainMenu

trait Engine extends GameEngine {
  def start(): Unit
  def stop(): Unit
  def pause(): Unit
  def resume(): Unit
  def getStatus(): GameStatus

}

object Engine {
  def apply(mainMenu: MainMenu): Engine = new EngineImpl(mainMenu)
  private class EngineImpl(mainMenu: MainMenu) extends Engine {
    private val Fps = 60
    private val gameLoop = GameLoop(Fps, this)
    private val entityManager = EntityManager()
    private val systemManager = SystemManager(entityManager)
    override def tick(): Unit =
      systemManager.updateAll()
      mainMenu.CANCELLAMITIPREGO(entityManager.entities)
    private def init(): Unit = {
      entityManager.addEntity(PlayerEntity(Position(50, 50)))
      systemManager.addSystem(playerMovementSystem)
    }

    override def start(): Unit = {
      init()
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
