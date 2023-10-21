package view

import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}
import javafx.stage.Stage

object MainMenu:
  def apply(primaryStage: Stage): Pane =
    val loader: FXMLLoader = FXMLLoader(getClass.getResource("/main.fxml"))
    val root: GridPane = loader.load[javafx.scene.layout.GridPane]()
    val onButtonClicked: PartialFunction[Button, Unit] = {
      case button if button.getText == "Start" => button.setOnAction((_: ActionEvent) => println("Hello World"))
      case button if button.getText == "Exit" => button.setOnAction((_: ActionEvent) => primaryStage.close())
    }
    root.getChildren
      .filtered(_.isInstanceOf[Button])
      .forEach(x => onButtonClicked(x.asInstanceOf[Button]))
    root