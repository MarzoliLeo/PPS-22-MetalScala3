package view

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, WindowEvent}
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}
import model.ecs.entities.EntityManager
import model.ecs.systems.SystemManager
import model.engine.Engine


trait GameOverView extends View with CreateGameView:

  def getButton(root: Pane, buttonText: String): Button =
    root.getChildren
      .filtered {
        case btn: Button if btn.getText == buttonText => true
        case _ => false
      }
      .get(0)
      .asInstanceOf[Button]

  def startButton: Button

  def exitButton: Button

  def handleStartButton(): Unit

  def handleExitButton(): Unit

private class GameOverViewImpl(primaryStage: Stage, gameEngine: Engine) extends GameOverView:

  val loader: FXMLLoader = FXMLLoader(getClass.getResource("/gameOver.fxml"))
  val root: Pane = loader.load[javafx.scene.layout.GridPane]()

  // Gestione dei pulsanti.
  getButton(root, "Start").setOnAction((_: ActionEvent) => handleStartButton())
  getButton(root, "Exit").setOnAction((_: ActionEvent) => handleExitButton())
  primaryStage.setOnCloseRequest((* : WindowEvent) => handleWindowCloseRequest())

  val scene = Scene(root, model.GUIWIDTH, model.GUIHEIGHT)
  primaryStage.setTitle("GameOver")
  primaryStage.setScene(scene)
  primaryStage.show()

  def handleStartButton(): Unit =
    val gameView = GameView(primaryStage, Set(EntityManager(), gameEngine), gameEngine)
    createGame(gameEngine)
    primaryStage.getScene.setRoot(gameView)
    gameEngine.start()

  def handleExitButton(): Unit =
    primaryStage.close()
    gameEngine.stop()
    System.exit(0)

  private def handleWindowCloseRequest(): Unit = {
    primaryStage.close()
    gameEngine.stop()
    System.exit(0)
  }

  override def startButton: Button = getButton(root, "Start")
  override def exitButton: Button = getButton(root, "Exit")

object GameOverView:
  def apply(primaryStage: Stage, gameEngine: Engine): GameOverView = new GameOverViewImpl(primaryStage, gameEngine)

