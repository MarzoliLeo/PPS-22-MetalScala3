package view

import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.*
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.stage.{Stage, WindowEvent}
import model.ecs.components.*
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.{PlayerEntity, SlugEntity}
import model.ecs.entities.weapons.{AmmoBoxEntity, MachineGunEntity}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.*
import model.engine.Engine
import view.{GameView, View}

trait MainMenu extends View:
  def createEntity(entity: Entity, components: Component*): Entity =
    components.foldLeft(entity) { (e, component) =>
      e.addComponent(component)
    }

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

private class MainMenuImpl(parentStage: Stage) extends MainMenu:

  val loader: FXMLLoader = FXMLLoader(getClass.getResource("/main.fxml"))
  val root: Pane = loader.load[javafx.scene.layout.GridPane]()

  private val entityManager = EntityManager()
  private val systemManager = SystemManager(entityManager)
  private val gameEngine = Engine()

  // Gestione dei pulsanti.
  getButton(root, "Start").setOnAction((_: ActionEvent) => handleStartButton())
  getButton(root, "Exit").setOnAction((_: ActionEvent) => handleExitButton())
  parentStage.setOnCloseRequest((* : WindowEvent) => handleWindowCloseRequest())

  def handleExitButton(): Unit =
    parentStage.close()
    gameEngine.stop()
    System.exit(0)

  private def handleWindowCloseRequest(): Unit = {
    parentStage.close()
    gameEngine.stop()
    System.exit(0)
  }

  /*.addEntity(createPlayerEntity(50,700))
      .addEntity(createEnemyEntity(900, 250))
      .addEntity(createEnemyEntity(1100, 700))
      .addEntity(createBoxEntity(250, 700))
      .addEntity(createBoxEntity(400, 400))
      .addEntity(createBoxEntity(500, 300))
      .addEntity(createBoxEntity(750, 150))
      .addEntity(createBoxEntity(650, 700))
      .addEntity(createBoxEntity(750, 700))
      .addEntity(createBoxEntity(900, 500))
      .addEntity(createMachineGunEntity(750,50))
      .addEntity(createSlugEntity(500,700))
  * */

  def handleStartButton(): Unit =
    val gameView = GameView(parentStage, Set(entityManager, gameEngine))
    entityManager
      .addEntity(
        createEntity(
          PlayerEntity(),
          playerComponents(PositionComponent(0, GUIHEIGHT)): _*
        )
      )
      .addEntity {
        createEntity(
          BoxEntity(),
          boxComponents(PositionComponent(400, GUIHEIGHT)): _*
        )
      }
//      .addEntity(
//        createEntity(
//          EnemyEntity(),
//          enemyComponents.collect {
//            case pc: PositionComponent => pc.copy(800, GUIHEIGHT)
//            case other                 => other
//          }.toSeq: _*
//        )
//      )
      .addEntity(
        createEntity(
          EnemyEntity(),
          enemyComponents(PositionComponent(1100, GUIHEIGHT)): _*
        )
      )
      .addEntity(
        createEntity(
          MachineGunEntity(),
          machineGunWeaponComponents(PositionComponent(600, GUIHEIGHT)): _*
        )
      )
      .addEntity(
        createEntity(
          AmmoBoxEntity(),
          ammoBoxComponents(PositionComponent(900, GUIHEIGHT)): _*
        )
      )
    systemManager
      .addSystem(InputSystem())
      .addSystem(PositionUpdateSystem())
      .addSystem(GravitySystem())
      .addSystem(BulletMovementSystem())
      .addSystem(AISystem())
      .addSystem(SpriteSystem())
    parentStage.getScene.setRoot(gameView)
    gameEngine.start()


  private def createPlayerEntity(positionInTheGUI: Int): Entity =
    PlayerEntity()
      .addComponent(PlayerComponent())
      .addComponent(GravityComponent(model.GRAVITY_VELOCITY))
      .addComponent(PositionComponent(positionInTheGUI, GUIHEIGHT))
      .addComponent(
        SizeComponent(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE)
      )
      .addComponent(CollisionComponent(scala.collection.mutable.Set()))
      .addComponent(BulletComponent(StandardBullet()))
      .addComponent(VelocityComponent(0, 0))
      .addComponent(DirectionComponent(RIGHT))
      .addComponent(JumpingComponent(false))
      .addComponent(SpriteComponent(model.s_MarcoRossi))

  private def createBoxEntity(positionInTheGUI: Int): Entity =
    BoxEntity()
      .addComponent(GravityComponent(model.GRAVITY_VELOCITY))
      .addComponent(PositionComponent(positionInTheGUI, GUIHEIGHT))
      .addComponent(
        SizeComponent(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE)
      )
      .addComponent(VelocityComponent(0, 0))
      .addComponent(DirectionComponent(RIGHT))
      .addComponent(JumpingComponent(false))
      .addComponent(SpriteComponent(model.s_Box))

  private def createEnemyEntity(positionInTheGUI: Int): Entity =
    EnemyEntity()
      .addComponent(GravityComponent(model.GRAVITY_VELOCITY))
      .addComponent(PositionComponent(positionInTheGUI, GUIHEIGHT))
      .addComponent(
        SizeComponent(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE)
      )
      .addComponent(BulletComponent(EnemyBullet()))
      .addComponent(VelocityComponent(0, 0))
      .addComponent(DirectionComponent(RIGHT))
      .addComponent(JumpingComponent(false))
      .addComponent(SpriteComponent(model.s_EnemyCrab))
      .addComponent(AIComponent())

  private def createMachineGunEntity(positionInTheGUI: Int): Entity =
    MachineGunEntity()
      .addComponent(GravityComponent(model.GRAVITY_VELOCITY))
      .addComponent(PositionComponent(positionInTheGUI, GUIHEIGHT))
      .addComponent(DirectionComponent(RIGHT))
      .addComponent(SpriteComponent(model.s_Weapon_H))
      .addComponent(
        SizeComponent(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE)
      )
      .addComponent(VelocityComponent(0, 0))
      .addComponent(JumpingComponent(false))


  override def startButton: Button = getButton(root, "Start")

  override def exitButton: Button = getButton(root, "Exit")

object MainMenu:
  def apply(parentStage: Stage): MainMenu =
    MainMenuImpl(parentStage)
