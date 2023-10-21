package view

import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}
import javafx.stage.Stage

object MainMenu:
  def apply(btnMap: Map[String, EventHandler[ActionEvent]]): Pane =
    val loader: FXMLLoader = FXMLLoader(getClass.getResource("/main.fxml"))
    val root: Pane = loader.load()
    root
      .getChildren
      .stream()
      .filter(_.isInstanceOf[Button])
      .map(_.asInstanceOf[Button])
      .forEach { button =>
        btnMap
          .get(button.getText)
          .foreach { handler =>
            button.setOnAction(handler)
          }
      }
    root