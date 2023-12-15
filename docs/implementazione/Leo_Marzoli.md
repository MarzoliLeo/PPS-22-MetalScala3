# Leo Marzoli

All'interno del progetto mi sono occupato di:
* Creazione di un game engine e dei suoi stati.
* Creazione di una logica per i nemici basata sul Prolog.
* Creazione di uno sprite system per il rendering degli oggetti.
* Implementazione del pattern singleton per i manager.
* Lancio delle bombe del giocatore con aiuto di Giacomo Romagnoli.
* Creazione di un sistema per la gravità con aiuto di Lorenzo Massone.

## Implementazione del game engine.

Il problema sorto durante lo sviluppo dell'engine ha riguardato la necessità di implementare un motore di gioco in grado di interlacciarsi con il calcolo della logica in ECS e il suo rendering tramite la View, facendo così da ponte separando le due responsabilità. Il tutto doveva essere gestito non aggravando sulle performance del main thread. Dunque, per risolvere questo problema si è deciso di implementare il *Game Loop* nel seguente modo:

```scala

trait GameLoop extends Thread:
  ...

private[engine] object GameLoop:
  ...

  private case class GameLoopImpl(override var fps: Int, gameEngine: GameEngine) extends GameLoop {
    private val millisecond = 1000
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
        val elapsedTime: Long = currentTime - lastTime
        
        gameEngine.tick(elapsedTime)

        // Wait for the next tick (calculating the time to wait).
        val tickTime = System.currentTimeMillis() - currentTime
        val timeTaken: Long = (millisecond / fps) - tickTime
        if (timeTaken > 0) {
          Thread.sleep(timeTaken)
        } else {
          print("Slowing tickness\n")
        }
        lastTime = currentTime
      }
    }

...

```

Il game loop inizia ad eseguire nel momento in cui l'interfaccia Engine invoca il suo metodo *start()* definito nel seguente modo:

```scala
override def start(): Unit = 
  gameLoop.status match {
    case Stopped =>
      gameLoop.start()
    case _ => print("GameLoop already started")
  }

```

Il thread si ferma soltanto quando la sua variabile *_status* di tipo *@volatile*, in quanto garantisce immediata propagazione del suo contenuto a tutti i thread che ne fanno utilizzo, assume il valore di *Stopped*.

Il game loop al suo interno invoca continuamente il metodo *tick()* specializzato all'interno di Engine che dopo aver aggiornato lo stato dei sistemi notifica il rendering della view, fintanto che il gioco sta eseguendo: 

```scala
override def tick(elapsedTime: Long): Unit =
  SystemManager().updateAll(elapsedTime)
  notifyObservers(Tick(EntityManager.entities))
```

