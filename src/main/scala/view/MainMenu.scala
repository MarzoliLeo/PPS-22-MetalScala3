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
import model.*
import model.ecs.components.*
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
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

  def handleStartButton(): Unit =
    val gameView = GameView(parentStage, Set(entityManager, gameEngine))
    entityManager
      .addEntity(
        createEntity(
          PlayerEntity(),
          playerComponents.collect {
            case pc: PositionComponent => pc.copy(250, GUIHEIGHT)
            case other                 => other
          }.toSeq: _*
        )
      )
      .addEntity {
        createEntity(
          BoxEntity(),
          boxComponents.collect {
            case pc: PositionComponent => pc.copy(400, GUIHEIGHT)
            case other                 => other
          }.toSeq: _*
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
          enemyComponents.collect {
            case pc: PositionComponent => pc.copy(1100, GUIHEIGHT)
            case other                 => other
          }.toSeq: _*
        )
      )
      .addEntity(
        createEntity(
          MachineGunEntity(),
          machineGunWeaponComponents.collect {
            case pc: PositionComponent => pc.copy(600, GUIHEIGHT)
            case other                 => other
          }.toSeq: _*
        )
      )
      .addEntity(
        createEntity(
          AmmoBoxEntity(),
          ammoBoxComponents.collect {
            case pc: PositionComponent => pc.copy(900, GUIHEIGHT)
            case other                 => other
          }.toSeq: _*
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

  override def startButton: Button = getButton(root, "Start")

  override def exitButton: Button = getButton(root, "Exit")

object MainMenu:
  def apply(parentStage: Stage): MainMenu =
    MainMenuImpl(parentStage)
