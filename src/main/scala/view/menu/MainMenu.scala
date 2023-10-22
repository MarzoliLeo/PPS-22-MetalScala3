package view.menu

import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}
import javafx.scene.{Node, Scene}
import javafx.stage.Stage
import jdk.internal.vm.ThreadContainers.root
import view.View

trait MainMenu extends View:
  def getButton(root: Pane, buttonText: String): Button =
    root
      .getChildren
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

private class MainMenuImpl(primaryStage: Stage) extends MainMenu:
  val loader: FXMLLoader = FXMLLoader(getClass.getResource("/main.fxml"))
  val root: GridPane = loader.load[javafx.scene.layout.GridPane]()
  getButton(root, "Start").setOnAction((_: ActionEvent) => handleStartButton())
  getButton(root, "Exit").setOnAction((_: ActionEvent) => handleExitButton())

  def handleStartButton(): Unit = println("Start")

  def handleExitButton(): Unit = primaryStage.close()

  override def getContent: Pane = root

  override def startButton: Button = getButton(root, "Start")

  override def exitButton: Button = getButton(root, "Exit")

object MainMenu:
  def apply(primaryStage: Stage): MainMenu = MainMenuImpl(primaryStage)