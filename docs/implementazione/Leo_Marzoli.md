# Leo Marzoli

All'interno del progetto mi sono occupato di:
* Creazione di un game engine e dei suoi stati.
* Creazione di una logica per i nemici basata sul TuProlog.
* Creazione di uno sprite system per il rendering degli oggetti.
* Implementazione del pattern singleton per i manager.
* Lancio delle bombe del giocatore con aiuto di Giacomo Romagnoli.
* Creazione di un sistema per la gravità con aiuto di Lorenzo Massone.

Di seguito vengono riportati gli aspetti che ritengo più rilevanti durante lo sviluppo del codice da me sviluppato.

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

Il thread si ferma soltanto quando la sua variabile *_status* di tipo *@volatile*, definita volontariamente in questo modo in quanto garantisce immediata propagazione del suo contenuto a tutti i thread che ne fanno utilizzo, assume il valore di *Stopped*.

Il game loop al suo interno invoca continuamente il metodo *tick()* specializzato all'interno di Engine che dopo aver aggiornato lo stato dei sistemi notifica il rendering della view, fintanto che il gioco sta eseguendo: 

```scala
override def tick(elapsedTime: Long): Unit =
  SystemManager().updateAll(elapsedTime)
  notifyObservers(Tick(EntityManager.entities))
```

## Creazione dell'AI nemico in TuProlog.

Si sono presentati diversi problemi da risolvere nell'implementazione di questo task. Ne riporto alcuni di rilevanza nota:
1. Eseguire la logica dei nemici in simultanea con quella del giocatore.
2. Scrittura di una logica in un file TuProlog.
3. Implementazione di un parser per la conversione automatica di codice scala in sintassi TuProlog.

Per quanto riguarda il primo punto si è deciso di sviluppare un sistema all'interno del pattern ECS in grado di riconoscere quelle entità a cui la logica di AI doveva essere applicata. Ad ogni frame di gioco il sistema si occupa di richiamare la funzione *update()* definita nel seguente modo:
```scala
trait AISystem extends SystemWithElapsedTime {
  private val engine = new Prolog()
  private val threadPool: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_ENEMIES)
  private val prologFile = new java.io.File("src/main/resources/EnemyAI.pl")
  engine.setTheory(new Theory(new FileInputStream(prologFile)))

  override def update(elapsedTime: Long): Unit = {
    EntityManager.getEntitiesByClass(classOf[PlayerEntity]) match {
      case Nil => // player morto.
      case playerEntity :: _ =>
        val playerPosition = playerEntity.getComponent[PositionComponent].get

        AItimeElapsedSinceLastExecution += AIexecutionSpeed

        if (AItimeElapsedSinceLastExecution >= AIexecutionInterval) {
          EntityManager
            .getEntitiesWithComponent(classOf[AIComponent])
            .foreach(entity => {
              val enemyPosition = entity.getComponent[PositionComponent].get
              val enemyVelocity = entity.getComponent[VelocityComponent].get

              val task = new AISystemTask(engine, entity, playerPosition, enemyPosition, enemyVelocity, elapsedTime)
              threadPool.submit(task)
            })

          AItimeElapsedSinceLastExecution = 0
        }
    }
  }
}
...

```

Si noti all'interno di questa sezione di codice due cose:
* L'utilizzo di un *Executor Service* di tipo *FixedThreadPool* inizializzato con un numero di thread pari a quello del numero di Enemies. Ho scelto di svilupparlo in questo modo per fare sì che ogni nemico eseguisse la sua logica all'interno di un task indipendente generato e gestito dall'executor service. Essendo in un contesto di programmazione asincrona il valore di ritorno dei task è una Future e dunque il risultato verrà ritornato soltanto quando questo risulterà "pronto" senza andare ad aggravare sulle performance del main thread.
* L'istanziazzione dei campi *engine*, *prologFile* e l'utilizzo di *engine.setTheory(new Theory(new FileInputStream(prologFile)))*. Questo perché il sistema sviluppato utilizza, come già detto, un file prolog per il calcolo della logica di movimento dei nemici, simulando una macchina a stati finiti.

Descrivendo ulteriormente questo ultimo punto l'implementazione in *TuProlog* è avvenuta nel seguente modo:
1. Scrittura del file in *TuProlog*:
   
```Prolog
move_toward_player(0, _, _, ENEMY_X, ENEMY_Y, ENEMY_X) :- !.
move_toward_player(RANDOMNESS, (PLAYER_X, PLAYER_Y), (ENEMY_X, ENEMY_Y), NEW_ENEMY_X) :-
    RANDOMNESS = 1 ->
    (
        ENEMY_X < PLAYER_X ->
            NEW_ENEMY_X is ENEMY_X + 40.0
        ;
            NEW_ENEMY_X is ENEMY_X - 40.0
    );
    RANDOMNESS = 2 ->
    (
        %Decido di stare fermo e non fare niente.
        ENEMY_X < PLAYER_X ->
            NEW_ENEMY_X is ENEMY_X
        ;
            NEW_ENEMY_X is ENEMY_X
    );
    RANDOMNESS = 3 ->
    (
       %Sto effettuando lo shooting del bullet quindi rimango fermo nella mia posizione.
       ENEMY_X < PLAYER_X ->
           NEW_ENEMY_X is ENEMY_X
       ;
           NEW_ENEMY_X is ENEMY_X
    ).
```
Questo file TuProlog definisce due regole, una per il caso base tramite l'utilizzo della regola di taglio, che appunto "taglia" nell'albero delle soluzioni tutte quelle dopo che questa regola risulta vera. La seconda invece è il caso iterativo, dove si definiscono 3 comportamenti differenti sulla base di un valore di randomness generato casualmente in scala e passato come argomento. I casi possibili sono: il nemico si muove nella direzione del giocatore, il nemico rimane fermo nella sua posizione, il nemico dovrà sparare e di conseguenza rimane fermo nella sua posizione.

2. Inserito all'interno del sistema il collegamento al *File*, l'oggetto engine sarà utilizzato per eseguire query e ottenere soluzioni da programmi *TuProlog*, viene settata la *Theory* del TuProlog sul file appena creato:

```Scala
trait AISystem extends SystemWithElapsedTime {
  private val engine = new Prolog()
  private val prologFile = new java.io.File("src/main/resources/EnemyAI.pl")
  engine.setTheory(new Theory(new FileInputStream(prologFile)))
...
```

3. Definizione di un sistema di parsing automatico per le query tramite l'utilizzo di *impliciti*:
```Scala 
object Scala2P {
  def extractTerm(t: Term, i: scala.Int): Term = t.asInstanceOf[Struct].getArg(i).getTerm
  implicit def termToDouble(t: Term): scala.Double = t.toString.toDouble
  implicit def intToTerm(i: scala.Int): Term = Term.createTerm(i.toString)
  implicit def doubleToTerm(d: scala.Double): Term = Term.createTerm(d.toString)
  implicit def stringToTerm(s: String): Term = Term.createTerm(s)
  implicit def setToTerm[T](s: Set[T]): Term = s.mkString("[", ",", "]")
  implicit def ListToTerm[T](l: List[T]): Term =
    val termList = l.map {
      case innerList: List[_] => ListToTerm(innerList) // Recursive call for nested lists
      case term: Term => term
      case other => other.toString
    }
    Term.createTerm(termList.mkString("[", ",", "]"))
  implicit def tuple2Term[T1, T2](tuple2: (T1, T2)): Term = Term.createTerm(tuple2.toString())
}
```
4. Svolegere una query al file TuProlog all'interno del task Runnable nel sistema, passando come parametri il nome della regola a cui fare riferimento, un valore di randomess, la posizione del player, la posizione del nemico e una variabile il cui valore è da definire in modo da salvare il risultato:
```Scala
override def run(): Unit = {
    try {
      val randomInt = scala.util.Random.nextInt(3) + 1

      val query = new Struct(
        "move_toward_player",
        randomInt,
        (playerPosition.x, playerPosition.y),
        (enemyPosition.x, enemyPosition.y),
        new Var()
      )

      val solution = engine.solve(query).getSolution

      if (randomInt == 3) {
        Command.shoot(entity)
      } else {
        handleMoveSolution(solution, entity, enemyPosition, enemyVelocity, elapsedTime)
      }
    } catch {
      case e: Exception =>
        handleException(e)
    }
```

La query in caso di successo ritorna un valore salvato all'interno della variabile *solution* e questo valore viene estratto tramite l'utilizzo della seguente riga:
```Scala
val prologPositionX = extractTerm(solution, 3)
```
Dove il 3 sta ad indicare la posizione del parametro utilizzato come risultato, nella definizione della regola, nel file TuProlog.

Questo valore verrà aggiunto alla velocità e alla posizione del nemico risultando in un movimento.

[Home.](../index.md)
