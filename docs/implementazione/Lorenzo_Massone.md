# Lorenzo Massone
I compiti da me svolti durante lo sviluppo sono i seguenti:
- Implementazione della view in JavaFX con Leo Marzoli;
- Implementazione EntityManager con Giacomo Romagnoli;
- Implementazione degli Unit Tests relativamente ad aspetti critici dell'applicativo;
- Implementazione della logica di collisione;
- Implementazione di armi e munizioni;
- Implementazione del sistema di aggiornamento della posizione.
  Di seguito una descrizione delle feature più interessanti da analizzare.
## Gestione delle collisioni
Per gestire le collisioni tra le entità presenti all'interno del gioco ho scelto di implementare un  mix-in **CollisionHandler** che utilizza un **self-type** su **Entity** per garantire che un CollisionHandler possa essere utilizzato solamente da un'entità:
```scala
trait CollisionHandler:  
  self: Entity =>  
  
  def handleCollision(  
      proposedPosition: PositionComponent  
  ): Option[PositionComponent]
```

La logica di gestione delle collisioni di default è contenuta all'interno di`BasicCollisionHandler`.  Al suo interno è implementata una logica di gestione delle collisioni che evità che due entità si sovrappongono, a meno che non vi siano casi specifici:
```scala
trait BasicCollisionHandler extends CollisionHandler:  
  self: Entity =>  
  
  def handleCollision(  
      proposedPosition: PositionComponent  
  ): Option[PositionComponent] =  
    for  
      currentPosition <- getComponent[PositionComponent]  
      _ <- getComponent[VelocityComponent]  
      sizeComponent <- getComponent[SizeComponent]  
    yield  
      handleSpecialCollision {  
        getCollidingEntity(this, proposedPosition)  
      }  
  
      val finalX = getFinalPosition(  
        PositionComponent(proposedPosition.x, currentPosition.y),  
        currentPosition  
      ).x  
      val finalY = getFinalPosition(  
        PositionComponent(currentPosition.x, proposedPosition.y),  
        currentPosition  
      ).y  
  
      PositionComponent(  
        boundaryCheck(  
          finalX,  
          model.GUIWIDTH,  
          sizeComponent.width  
        ),  
        boundaryCheck(  
          finalY,  
          model.GUIHEIGHT,  
          sizeComponent.height  
        )  
      )  
  
  private def getFinalPosition(  
      proposedPosition: PositionComponent,  
      currentPosition: PositionComponent  
  ): PositionComponent =  
    if canPassThrough(proposedPosition) then proposedPosition  
    else currentPosition  
  
  private def canPassThrough(  
      proposedPosition: PositionComponent  
  ): Boolean =  
    getCollidingEntity(this, proposedPosition) match  
      case Some(_: PlayerBulletEntity) if this.isInstanceOf[PlayerEntity] =>  
        true  
      case Some(_: PlayerEntity) if this.isInstanceOf[PlayerBulletEntity] =>  
        true  
      case Some(_: EnemyEntity) if this.isInstanceOf[EnemyBulletEntity] => true  
      case Some(_: EnemyBulletEntity) if this.isInstanceOf[EnemyEntity] => true  
      case None                                                         => true  
      case _                                                            => false  
...
```
Ulteriori logiche necessarie in caso di collisione sono implementate nei mix-in appropriati (e.g. `PlayerCollisionHandler`, `PlayerBulletCollisionHandler` , ecc...).
Per estrarre tutte le `Component` necessarie all'elaborazione e per produrre il risultato atteso in un modo particolarmente ordinato è stato utilizzato un `for yield`.
Inoltre si è scelto di implementare le logiche di collisione in questo modo per cercare di applicare quanto più possibile il **Single Responsability Principle** in modo da poter più facilmente evolvere questa sezione.
## Sistema di aggiornamento della posizione
Per aggiornare la posizione di tutte le entità è stato implementato `PositionUpdateSystem` nel seguente modo:
```scala
trait PositionUpdateSystem extends SystemWithElapsedTime  
  
private case class PositionUpdateSystemImpl() extends PositionUpdateSystem:  
  override def update(elapsedTime: Long): Unit =  
    EntityManager  
      .getEntitiesWithComponent(  
        classOf[PositionComponent],  
        classOf[VelocityComponent],  
        classOf[JumpingComponent]  
      )  
      .foreach(entity =>  
        val currentPosition: PositionComponent =  
          entity.getComponent[PositionComponent].get  
        given VelocityComponent =  
          entity.getComponent[VelocityComponent].get  
        entity.replaceComponent(getUpdatedVelocity)  
  
        val proposedPosition = currentPosition.getUpdatedPosition(elapsedTime)  
        entity.handleCollision(proposedPosition) match  
          case Some(handledPosition) => entity.replaceComponent(handledPosition)  
          case None                  => ()  
      )  
  
  private def getUpdatedVelocity(using  
      velocity: VelocityComponent  
  ): VelocityComponent = {  
    val newHorizontalVelocity = velocity.x * FRICTION_FACTOR match  
      case x if -0.1 < x && x < 0.1 => 0.0  
      case x                        => x  
    VelocityComponent(newHorizontalVelocity, velocity.y)  
  }  
  
object PositionUpdateSystem:  
  def apply(): PositionUpdateSystem = PositionUpdateSystemImpl()

``` 
In questo `System` è stato utilizzato un **given** per il `VelocityComponent` in quanto risultava essere un elemento di contesto necessario a calcolare la velocità futura dell' `Entity` (in `getUpdatedVelocity`)  e la conseguente nuova posizione (in `getUpdatedPosition`).
Oltre a ciò, il codice fa un uso abbondante di **pattern matching** per rendere il codice più leggibile e conciso.
# View
Nella definizione iniziale della struttura del modulo `view` si è scelto di strutturare le `View` affinchè  delegassero il render vero e proprio dei loro elementi a un `Pane` interno. Di conseguenza è sorta spontaneamente la necessità di implementare una **implicit conversion** da `View` a `Pane` per utilizzare più agilmente i metodi esposti da **JavaFX**:
```scala
trait View:  
  def root: Pane  
  
object View:  
  given Conversion[View, Pane] = _.root
```
Nell'implementazione della View è stato fatto uso di un file `.fxml` per semplificare il codice relativo a `MainMenu` (approccio poi non utilizzato in `GameView` in quanto scenario fortemente dinamico) risultando nel seguente codice:
```scala
trait MainMenu extends View with CreateGameView:  
  
  def getButton(root: Pane, buttonText: String): Button =  
    root.getChildren  
      .filtered {  
        case btn: Button if btn.getText == buttonText => true  
        case _                                        => false  
      }  
      .get(0)  
      .asInstanceOf[Button]  
  
  def startButton: Button  
  
  def exitButton: Button  
  
  def handleStartButton(): Unit  
  
  def handleExitButton(): Unit

private class MainMenuImpl(parentStage: Stage, gameEngine: Engine) extends MainMenu:  
  
  val loader: FXMLLoader = FXMLLoader(getClass.getResource("/main.fxml"))  
  val root: Pane = loader.load[javafx.scene.layout.GridPane]()
...  
```
## Implementazione Unit Test
Nel progetto sono presenti Unit Test riguardanti gli aspetti più critici dell'applicativo, in particolare i `System`. Per garantire un sufficiente monitoraggio del loro funzionamento, sono stati implementati diversi test utilizzando la libreria **ScalaTest** per definire in modo chiaro e conciso dei test che fossero facilmente leggibili e interpretabili da chiunque li leggesse:
```scala
class PositionUpdateSystemTest  
    extends AnyFlatSpec  
    with Matchers  
    with BeforeAndAfter {  
  val startingPosition: PositionComponent = PositionComponent(0, 0)  
  val elapsedTime = 1L  
  var positionUpdateSystem: PositionUpdateSystem = _  
  var entity: Entity = _  
  
  before {  
    positionUpdateSystem = PositionUpdateSystem()  
    entity = BoxEntity()  
  
    // Empty the EntityManager  
    EntityManager.entities.foreach(EntityManager.removeEntity)  
  
    EntityManager.addEntity(entity)  
    entity.addComponent(startingPosition)  
  }  
  
  "PositionUpdateSystem" should "update position based on velocity" in {  
    val velocityComponent = VelocityComponent(1, 1)  
    entity.addComponent(velocityComponent)  
    entity.addComponent(JumpingComponent(false))  
    entity.addComponent(SizeComponent(1, 1))  
  
    positionUpdateSystem.update(elapsedTime)  
  
    val updatedPosition = entity.getComponent[PositionComponent].get  
    updatedPosition shouldEqual startingPosition.getUpdatedPosition(  
      elapsedTime  
    )(using velocityComponent)  
  }  
  
  it should "not update position if there is no velocity" in {  
    entity.addComponent(JumpingComponent(false))  
  
    positionUpdateSystem.update(elapsedTime)  
  
    val updatedPosition = entity.getComponent[PositionComponent].get  
    updatedPosition shouldEqual startingPosition  
  }  
  
  it should "not update position if there is no jumping component" in {  
    val velocityComponent = VelocityComponent(1, 1)  
    entity.addComponent(velocityComponent)  
  
    positionUpdateSystem.update(elapsedTime)  
  
    val updatedPosition = entity.getComponent[PositionComponent].get  
    updatedPosition shouldEqual startingPosition  
  }  
}
```
In particolare sono stati usati diversi mix-in di ScalaTest :
- `AnyFlatSpec` per garantire una sintassi molto simile al linguaggio naturale, in stile **BDD**;
- `Matchers`, per usare una sintassi più naturale basata sulla keyword `should` invece di fare uso di `assert`;
- `BeforeAndAfter` per definire opportunamente le operazioni di setup necessarie a priori di ogni test.