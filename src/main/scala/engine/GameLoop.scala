package engine

import engine.GameStatus._

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
        while (_status != Stopped) {
          if (_status == Paused) {
            this.synchronized {
              print("[Wait] Lo stato del sistema è: " + _status + "\n")
              wait()
            }
          }
          val start = System.currentTimeMillis()

          //Update all the systems.
          gameEngine.tick()

          //Wait for the next tick (calculating the time to wait).
          val tickTime = System.currentTimeMillis() - start
          //print("[AfterTick] Lo stato del sistema è: " + _status + "\n")
          val deltaTime = (millisecond/fps) - tickTime
          if (deltaTime > 0) {
            Thread.sleep(deltaTime)
          } else print("Slowing tickness\n")
        }
      }

    override def pause(): GameStatus = this.synchronized {
      print("[Pause] Lo stato del sistema è:" + _status + "\n")
      _status match {
        case Running => _status = Paused; print("pausing\n")
        case Paused | Stopped => print("Not running, can't pause\n")
      }
      _status
    }

    override def unPause(): GameStatus = this.synchronized {
      print("[unPause] Lo stato del sistema è:" + _status + "\n")
      _status match {
        case Paused => _status = Running; print("unpausing\n"); notifyAll()
        case Running | Stopped => print("Not paused, can't unpause\n")
      }
      _status
    }

    override def halt(): GameStatus = this.synchronized {
      print("[Halt] Lo stato del sistema è:" + _status + "\n")
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



