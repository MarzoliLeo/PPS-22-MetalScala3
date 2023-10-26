import App.main
import ecs.components.{ColorComponent, DisplayableComponent, PositionComponent}
import ecs.entities.{BoxEntity, EntityManager}
import ecs.systems.SystemManager
import engine.Engine
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.layout.*
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.stage.Stage
import view.GameView
import view.menu.MainMenu

class App extends Application:
  @Override
  def start(primaryStage: Stage): Unit =
    val GAME_TITLE = "Metal Scala 3"
    val WINDOW_WIDTH = 800
    val WINDOW_HEIGHT = 600
    primaryStage.setTitle(GAME_TITLE)

    val entityManager = EntityManager()
      .addEntity(
        BoxEntity()
          .addComponent(PositionComponent(100, 100))
          .addComponent(DisplayableComponent())
          .addComponent(ColorComponent(Color.YELLOW))
      )

    val gameView = GameView(entityManager)

    val mainMenu = MainMenu(primaryStage, gameView)
    // TODO metterlo nel tasto start.
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
