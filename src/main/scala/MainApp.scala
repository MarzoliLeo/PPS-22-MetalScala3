import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.stage.Stage

class MainApp extends Application:
  @Override def start(primaryStage: Stage): Unit =
    val loader = FXMLLoader(getClass.getResource("main.fxml"))
    val root: GridPane = loader.load[javafx.scene.layout.GridPane]()
    root.getChildren
      .filtered(_.isInstanceOf[Button]).forEach { btn =>
        val button = btn.asInstanceOf[Button]
        button.getText match
          case "Start" =>
            button.setOnAction((_: ActionEvent) => println("Hello World"))
          case "Exit" =>
            button.setOnAction(new EventHandler[ActionEvent] {
              override def handle(event: ActionEvent): Unit =
                primaryStage.close()
            })
      }
    primaryStage.setTitle("Hello World")
    primaryStage.setScene(new Scene(root, 640, 480))
    primaryStage.show()

object MainApp:
  def main(args: Array[String]): Unit = Application.launch(classOf[MainApp], args *)