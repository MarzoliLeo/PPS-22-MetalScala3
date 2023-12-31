import App.main
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.stage.Stage
import model.ecs.components.PositionComponent
import model.ecs.entities.EntityManager
import model.ecs.systems.SystemManager
import model.engine.Engine
import model.input.InputHandler
import view.{GameView, MainMenu}

class App extends Application:
  @Override
  def start(primaryStage: Stage): Unit =
    val GAME_TITLE = "Metal Scala 3"
    val WINDOW_WIDTH = model.GUIWIDTH
    val WINDOW_HEIGHT = model.GUIHEIGHT
    val gameEngine = Engine()
    
    primaryStage.setTitle(GAME_TITLE)

    //La creazione della GameView avviene dentro il pulsante start di Main Menu.
    val mainMenu = MainMenu(primaryStage, gameEngine)

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
