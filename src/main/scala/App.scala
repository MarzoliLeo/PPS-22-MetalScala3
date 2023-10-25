import javafx.application.Application
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.stage.Stage
import view.menu.MainMenu

class App extends Application:
  @Override
  def start(primaryStage: Stage): Unit =
    val GAME_TITLE = "Metal Scala 3"
    val WINDOW_WIDTH = 800
    val WINDOW_HEIGHT = 600
    primaryStage.setTitle(GAME_TITLE)

    val box = Box(50, 50, 50)
    box.setTranslateX(100)
    box.setTranslateY(100)
    box.setTranslateZ(100)
    val material = PhongMaterial(Color.RED)
    box.setMaterial(material)

    val nextPane = GridPane()
    nextPane.add(box, 0, 0)

    primaryStage
      .setScene(Scene(
        MainMenu(primaryStage, nextPane),
        WINDOW_WIDTH,
        WINDOW_HEIGHT
      ))
    primaryStage.show()

object App:
  def main(args: Array[String]): Unit = Application.launch(classOf[App], args: _*)