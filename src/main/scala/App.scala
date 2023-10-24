import javafx.application.Application
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.stage.Stage
import view.menu.MainMenu

class App extends Application:
  @Override
  def start(primaryStage: Stage): Unit =
    val GAME_TITLE = "Metal Scala 3"
    val WINDOW_WIDTH = 800
    val WINDOW_HEIGHT = 600
    primaryStage.setTitle(GAME_TITLE)

    primaryStage
      .setScene(Scene(
        MainMenu(primaryStage, Stage()),
        WINDOW_WIDTH,
        WINDOW_HEIGHT
      ))
    primaryStage.show()

object App:
  def main(args: Array[String]): Unit = Application.launch(classOf[App], args: _*)