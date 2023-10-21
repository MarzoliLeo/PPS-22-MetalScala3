import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import view.MainMenu

class App extends Application:
  @Override def start(primaryStage: Stage): Unit =
    val gameTitle = "Metal Scala 3"
    primaryStage.setTitle(gameTitle)
    primaryStage.setScene(Scene(MainMenu(primaryStage), 800, 600))
    primaryStage.show()

object App:
  def main(args: Array[String]): Unit = Application.launch(classOf[App], args: _*)