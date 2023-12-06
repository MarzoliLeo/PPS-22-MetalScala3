package model.engine

import GameStatus._

trait GameLoop extends Thread {
  def fps: Int //get
  def fps_=(fps: Int): Unit //set
  def status: GameStatus
  def pause(): GameStatus
  def unPause(): GameStatus
  def halt(): GameStatus
}

private[engine] object GameLoop {
  def apply(fps: Int, gameEngine: GameEngine): GameLoop = new GameLoopImpl(fps, gameEngine)

  private class GameLoopImpl(override var fps: Int, val gameEngine: GameEngine) extends GameLoop {
    private val millisecond = 1000
    //volatile makes the variable thread safe and fast to updates.
    @volatile private var _status: GameStatus = Stopped

    override def run(): Unit = {
      _status = Running
        var lastTime = System.currentTimeMillis()
        while (_status != Stopped) {
          if (_status == Paused) {
            this.synchronized {
              print("[Wait] Lo stato del sistema è: " + _status + "\n")
              wait()
            }
          }
          val currentTime: Long = System.currentTimeMillis()
          var elapsedTime: Long = currentTime - lastTime

          //Update all the systems.
          gameEngine.tick(elapsedTime)

          //Wait for the next tick (calculating the time to wait).
          val tickTime = System.currentTimeMillis() - currentTime
          //print("[Tick] Lo stato del sistema è: " + _status + "\n")
          val timeTaken: Long = (millisecond/fps) - tickTime
          if (timeTaken > 0) {
            Thread.sleep(timeTaken)
          }
          else
          {
            print("Slowing tickness\n")
          }
          lastTime = currentTime
        }
      }

    override def pause(): GameStatus = this.synchronized {
      _status match {
        case Running => _status = Paused; print("pausing\n")
        case Paused | Stopped => print("Not running, can't pause\n")
      }
      _status
    }

    override def unPause(): GameStatus = this.synchronized {
      _status match {
        case Paused => _status = Running; print("unpausing\n"); notifyAll()
        case Running | Stopped => print("Not paused, can't unpause\n")
      }
      _status
    }

    override def halt(): GameStatus = this.synchronized {
      _status match {
        case Running | Paused => _status = Stopped; print("stopped\n"); notifyAll()
        case Stopped => print("Already stopped\n")
      }
      _status
    }

    def status: GameStatus = this.synchronized {
      _status
    }

  }
}



