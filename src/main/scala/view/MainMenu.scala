package view

import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}
import javafx.stage.Stage

object MainMenu:

  /**
   * Creates a new stage with the main menu
   * @param btnMap Map of button text to event handler to assign an Event Handler to a button based on its text
   * @return The Pane of the main menu
   */
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