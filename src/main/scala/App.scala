import App.main
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.stage.Stage
import view.menu.MainMenu
import ecs.entities.EntityManager
import ecs.systems.SystemManager
import engine.Engine

class App extends Application:
  @Override
  def start(primaryStage: Stage): Unit =
    val GAME_TITLE = "Metal Scala 3"
    val WINDOW_WIDTH = 800
    val WINDOW_HEIGHT = 600
    primaryStage.setTitle(GAME_TITLE)
    val nextPane = GridPane()
    val mainMenu = MainMenu(primaryStage, nextPane)
    //TODO metterlo nel tasto start.
    val gameEngine = Engine(mainMenu)


    primaryStage
      .setScene(
          Scene(
              mainMenu,
              WINDOW_WIDTH,
              WINDOW_HEIGHT
      )
  )
    primaryStage.show()

    gameEngine.start()

object App:
  def main(args: Array[String]): Unit =
    Application.launch(classOf[App], args: _*)
