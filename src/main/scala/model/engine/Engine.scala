package model.engine

import GameStatus.*
import javafx.scene.paint.Color
import model.Fps
import model.ecs.components.PositionComponent
import model.ecs.entities.EntityManager
import model.ecs.entities.player.PlayerEntity
import model.ecs.systems.{DeathSystem, SystemManager}
import model.event.Event
import model.event.Event.{GameOver, Tick}
import model.event.observer.Observable
import view.MainMenu

trait Engine extends GameEngine with Observable[Event]{
  def init(): Unit
  def start(): Unit
  def stop(): Unit
  def pause(): Unit
  def resume(): Unit
  def getStatus(): GameStatus

}

object Engine {
  def apply(): Engine = new EngineImpl()
  private class EngineImpl() extends Engine {
    private val systemManager = SystemManager(EntityManager)
    private val gameLoop = GameLoop(Fps, this)
    private val deathSystem = DeathSystem(this) // Passa l'istanza di Engine

    override def tick(elapsedTime: Long): Unit =
      systemManager.updateAll(elapsedTime)
      notifyObservers(Tick(EntityManager.entities))

    override def init(): Unit = {
      gameLoop.status match {
        case Stopped =>
          gameLoop.start() // .start() its because gameloop is a thread.
        case _ => print("GameLoop already started")
      }
    }


    override def start(): Unit = {
      gameLoop.unPause() // Riprendi il ciclo di gioco
    }

    override def stop(): Unit = {
      notifyObservers(GameOver())
      gameLoop.pause()
    }

    //TODO sono inutili per ora.
    override def pause(): Unit =
      gameLoop.pause()

    override def resume(): Unit =
      gameLoop.unPause()

    // Method for testing purposes
    override def getStatus(): GameStatus = gameLoop.status

  }
}
