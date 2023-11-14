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
import model.ecs.entities.{EntityManager, PlayerEntity}
import model.ecs.systems.Systems.{bulletMovementSystem, gravitySystem, inputMovementSystem, positionUpdateSystem}
import model.ecs.systems.{CollisionSystem, SystemManager, Systems}
import model.engine.Engine
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}
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
        PlayerEntity()
          .addComponent(PlayerComponent())
          .addComponent(PositionComponent(100, 600))
          .addComponent(GravityComponent(model.GRAVITY_VELOCITY))
          .addComponent(
            ColliderComponent(
              Size(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE)
            )
          )
          .addComponent(JumpingComponent(false))
          .addComponent(VelocityComponent(0, 0))
          .addComponent(SpriteComponent(model.playerSpriteList))
          .addComponent(ColorComponent(Color.BLUE))
          .addComponent(DirectionComponent(RIGHT))
      )
    /* .addEntity(
        PlayerEntity()
          .addComponent(PositionComponent(400, 100))
          .addComponent(ColorComponent(Color.RED))
          .addComponent(GravityComponent(model.GRAVITY_VELOCITY))
          .addComponent(ColliderComponent(Size(HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE)))
      ) */
    systemManager
      .addSystem(inputMovementSystem)
      .addSystem(gravitySystem)
      .addSystem(positionUpdateSystem)
    // .addSystem(collisionSystem)
    // .addSystem(bulletMovementSystem)
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
