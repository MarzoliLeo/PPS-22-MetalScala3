package view

import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.*
import javafx.scene.shape.Box
import javafx.stage.{Stage, WindowEvent}
import model.*
import model.ecs.entities.EntityManager
import model.ecs.systems.*
import model.engine.Engine
import view.{GameView, View}

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

  // Gestione dei pulsanti.
  getButton(root, "Start").setOnAction((_: ActionEvent) => handleStartButton())
  getButton(root, "Exit").setOnAction((_: ActionEvent) => handleExitButton())
  parentStage.setResizable(false)
  parentStage.setOnCloseRequest((_ : WindowEvent) => handleWindowCloseRequest())

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
    val gameView = GameView(parentStage, Set(gameEngine))
    createGame(gameEngine)
    parentStage.getScene.setRoot(gameView)
    gameEngine.start()

  override def startButton: Button = getButton(root, "Start")

  override def exitButton: Button = getButton(root, "Exit")


object MainMenu:
  def apply(parentStage: Stage, gameEngine: Engine): MainMenu =
    MainMenuImpl(parentStage, gameEngine)
