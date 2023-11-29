package view

import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.{Background, BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize, GridPane, Pane}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.stage.{Stage, WindowEvent}
import model.ecs.components.*
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.MachineGunEntity
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.systems.*
import model.engine.Engine
import model.{GUIHEIGHT, HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE, s_Logo}
import view.{GameView, View}

trait MainMenu extends View:
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

  //Gestione dei pulsanti.
  getButton(root, "Start").setOnAction((_: ActionEvent) => handleStartButton())
  getButton(root, "Exit").setOnAction((_: ActionEvent) => handleExitButton())
  parentStage.setOnCloseRequest((event: WindowEvent) =>
    handleWindowCloseRequest()
  )

  def handleExitButton(): Unit =
    parentStage.close()
    gameEngine.stop()
    System.exit(0)

  private def handleWindowCloseRequest(): Unit = {
    parentStage.close()
    gameEngine.stop()
    System.exit(0)
  }

  def handleStartButton(): Unit =
    val gameView = GameView(parentStage, Set(entityManager, gameEngine))
    entityManager
      .addEntity(createPlayerEntity(250))
      .addEntity(createBoxEntity(400))
      //.addEntity(createEnemyEntity(800))
      .addEntity(createEnemyEntity(1100))
      .addEntity(createMachineGunEntity(600))
    systemManager
      .addSystem(InputSystem())
      .addSystem(GravitySystem())
      .addSystem(PositionUpdateSystem())
      .addSystem(BulletMovementSystem())
      .addSystem(AISystem())
      .addSystem(SpriteSystem())
    parentStage.getScene.setRoot(gameView)
    gameEngine.start()


  def createPlayerEntity(positionInTheGUI: Int): Entity =
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

  def createBoxEntity(positionInTheGUI: Int): Entity =
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

  def createEnemyEntity(positionInTheGUI: Int): Entity =
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

  def createMachineGunEntity(positionInTheGUI: Int): Entity =
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
