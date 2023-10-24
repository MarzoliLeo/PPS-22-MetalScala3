package engine

import engine.GameStatus._

trait Engine extends GameEngine {
  //TODO init method

  def start(): Unit
  def stop(): Unit
  def pause(): Unit
  def resume(): Unit
  def getStatus(): GameStatus

}

object Engine {
  //TODO apply method with parameters
  def apply(): Engine = new EngineImpl()
  private class EngineImpl extends Engine {
    private val Fps = 60
    private val gameLoop = GameLoop(Fps, this)

    override def tick(): Unit =
      //TODO coordinator.updateSystems()
      //TODO INSERIMENTO DELLA LOGICA ECS Systems in modo che tutte le meccaniche vengano richiamate sempre.
      print("Ho fatto l'update dei system \n")

    override def start(): Unit = {
      gameLoop.status match {
        case Stopped => gameLoop.start() //.start() its because gameloop is a thread.
        case _ => print("GameLoop already started")
      }
    }
    override def stop(): Unit = {
      gameLoop.halt()
      //TODO mediator.unsubscribe(this)
    }
    override def pause(): Unit = gameLoop.pause()
    override def resume(): Unit = gameLoop.unPause()

    //Method for testing purposes
    def getStatus() : GameStatus = gameLoop.status

  }
}


