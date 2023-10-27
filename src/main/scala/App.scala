import App.main
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.layout.*
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.stage.Stage
import model.ecs.components.{ColorComponent, PositionComponent, VisibleComponent}
import model.ecs.entities.{BoxEntity, EntityManager}
import model.ecs.systems.SystemManager
import model.engine.Engine
import model.entityManager
import view.{BuildEntitiesForTheGame, GameView}
import view.menu.MainMenu

class App extends Application:
  @Override
  def start(primaryStage: Stage): Unit =
    val GAME_TITLE = "Metal Scala 3"
    val WINDOW_WIDTH = 800
    val WINDOW_HEIGHT = 600
    primaryStage.setTitle(GAME_TITLE)

    //references useful for modularity.
    //val buildEntitiesForTheGame : BuildEntitiesForTheGame = BuildEntitiesForTheGame()

    //Creation of the entities to be displayed in the game.
    //val gameView = GameView(buildEntitiesForTheGame.build(entityManager))
    val gameView = GameView(entityManager)

    val mainMenu = MainMenu(primaryStage, gameView)

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
