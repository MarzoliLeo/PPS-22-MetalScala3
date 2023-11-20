package view

import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.{GridPane, Pane}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.stage.Stage
import model.ecs.components.*
import model.ecs.entities.EntityManager
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.MachineGunEntity
import model.ecs.systems.Systems.{bulletMovementSystem, gravitySystem, inputMovementSystem, positionUpdateSystem}
import model.ecs.systems.{CollisionSystem, SystemManager, Systems}
import model.engine.Engine
import model.{GUIHEIGHT, HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}
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
  val root: GridPane = loader.load[javafx.scene.layout.GridPane]()

  private val entityManager = EntityManager()
  private val systemManager = SystemManager(entityManager)
  private val gameEngine = Engine()
  getButton(root, "Start").setOnAction((_: ActionEvent) => handleStartButton())
  getButton(root, "Exit").setOnAction((_: ActionEvent) => handleExitButton())

  def handleStartButton(): Unit =
    val gameView =
      GameView(parentStage, Set(entityManager, Systems, gameEngine))
    // Imposta il backend ECS.
    entityManager
      .addEntity(
        // Real player entity
        PlayerEntity()
          .addComponent(PlayerComponent())
          .addComponent(GravityComponent(model.GRAVITY_VELOCITY))
          .addComponent(PositionComponent(250, GUIHEIGHT))
          .addComponent(
            SizeComponent(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE)
          )
          .addComponent(BulletComponent(Bullet.StandardBullet))
          .addComponent(VelocityComponent(0, 0))
          .addComponent(DirectionComponent(RIGHT))
          .addComponent(JumpingComponent(false))
          .addComponent(SpriteComponent(model.marcoRossiSprite))
      )
      .addEntity(
        // Used for testing collisions
        BoxEntity()
          .addComponent(GravityComponent(model.GRAVITY_VELOCITY))
          .addComponent(PositionComponent(400, GUIHEIGHT))
          .addComponent(
            SizeComponent(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE)
          )
          .addComponent(VelocityComponent(0, 0))
          .addComponent(DirectionComponent(RIGHT))
          .addComponent(JumpingComponent(false))
          .addComponent(SpriteComponent("sprites/box.jpg"))
      )
      .addEntity(
        MachineGunEntity()
          .addComponent(GravityComponent(model.GRAVITY_VELOCITY))
          .addComponent(PositionComponent(800, GUIHEIGHT))
          .addComponent(DirectionComponent(RIGHT))
          .addComponent(SpriteComponent("sprites/H.png"))
          .addComponent(
            SizeComponent(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE)
          )
          .addComponent(VelocityComponent(0, 0))
          .addComponent(JumpingComponent(false))
      )
    systemManager
      .addSystem(inputMovementSystem)
      .addSystem(gravitySystem)
      .addSystem(positionUpdateSystem)
      .addSystem(bulletMovementSystem)
    parentStage.getScene.setRoot(gameView)
    gameEngine.start()

  def handleExitButton(): Unit =
    parentStage.close()
    gameEngine.stop()
    System.exit(0)

  override def startButton: Button = getButton(root, "Start")

  override def exitButton: Button = getButton(root, "Exit")

object MainMenu:
  def apply(parentStage: Stage): MainMenu =
    MainMenuImpl(parentStage)
