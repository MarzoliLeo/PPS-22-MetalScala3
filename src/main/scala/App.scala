import App.main
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.stage.Stage
import model.ecs.components.{ColorComponent, PositionComponent, VisibleComponent}
import model.ecs.entities.{BoxEntity, EntityManager}
import model.ecs.systems.SystemManager
import model.engine.Engine
import model.entityManager
import model.input.InputHandler
import view.GameView
import view.menu.MainMenu

class App extends Application:
  override def start(primaryStage: Stage): Unit =
    val GAME_TITLE = "Metal Scala 3"
    val WINDOW_WIDTH = 800
    val WINDOW_HEIGHT = 600
    primaryStage.setTitle(GAME_TITLE)

    val mainMenu = MainMenu(primaryStage)

    primaryStage
      .setScene(
        Scene(
          mainMenu,
          WINDOW_WIDTH,
          WINDOW_HEIGHT
        )
      )
    primaryStage.show()

object App:
  def main(args: Array[String]): Unit =
    Application.launch(classOf[App], args: _*)
