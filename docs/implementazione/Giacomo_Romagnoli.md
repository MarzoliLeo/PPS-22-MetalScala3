# Giacomo Romagnoli

Nel corso dello sviluppo mi sono occupato principalmente di:

* Implementare il concetto di entità come definito in ECS.
* Implementare il sistema dei comandi.
* Implementare il sistema per il movimento dei proiettili, comprensivo di componenti e logica.
* Implementare la logica di rendering dell'interfaccia grafica.

Per i primi due punti il lavoro è stato prevalentemente autonomo, mentre per proiettili e rendering ho collaborato a stretto contatto rispettivamente con: Lorenzo Massone e Leo Marzoli.
Di seguito una vista in dettaglio degli aspetti implementativi salienti.

## Entity
La combinazione di reflection, generici e impliciti nel metodo ```getComponent``` permettono di restituire un tipo di dato generico specificato in input, nella fattispecie uno specifico componente.
Questa strategia implementativa permette di restituire una sotto classe di ```Component```, più maneggevole rispetto all'interfaccia ```Component``` che necessita di un cast.
```
  def getComponent[T <: Component: ClassTag]: Option[T] =
    signature.collectFirst {
      case component
          if summon[ClassTag[T]].runtimeClass.isInstance(component) =>
        component.asInstanceOf[T]
    }
```
## Command
I comandi sono stati implementati come funzioni ```Entity => Unit``` facenti parte di un modulo ```Command```.
```
  object Command:
    def jump(entity: Entity): Unit = ...
    def left(entity: Entity): Unit = ...
    def right(entity: Entity): Unit = ...
    ...
```
Alla pressione di un tasto un listener associa il tasto al commando via pattern matching, e impila in uno ```Stack```.
```
  scene.setOnKeyPressed { k =>
      k.getCode match
        case KeyCode.LEFT  => handleInput(Command.left)
        case KeyCode.RIGHT => handleInput(Command.right)
        case KeyCode.UP    => handleInput(Command.jump)
        case KeyCode.SPACE => handleInput(Command.shoot)
        case KeyCode.DOWN  => handleInput(Command.crouch)
        case KeyCode.B     => handleInput(Command.bomb)
        case _             =>
    }
```
```
  trait CommandsStackHandler extends InputHandler:
    override def handleInput(command: Entity => Unit): Unit = 
      val newStack = inputsQueue.push(command)
      inputsQueue = newStack
```
Durante l'iterazione del game loop i comandi vengono eseguiti.
```
  override def update(): Unit =
      EntityManager.getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
        entity =>
          inputsQueue.peek match
            case Some(command) => command(entity)
            case None => ()
          inputsQueue = inputsQueue.pop.getOrElse(Empty)
      }
```


[Home.](../index.md)
