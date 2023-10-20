import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.stage.Stage

class MainApp extends Application:
  @Override def start(primaryStage: Stage): Unit =
    val loader = FXMLLoader(getClass.getResource("main.fxml"))
    val root: GridPane = loader.load[javafx.scene.layout.GridPane]()

    // a PartialFunction to add the handlers to the buttons
    val onButtonClicked: PartialFunction[Button, Unit] = {
      case button if button.getText == "Start" => button.setOnAction((_: ActionEvent) => println("Hello World"))
      case button if button.getText == "Exit" => button.setOnAction((_: ActionEvent) => primaryStage.close())
    }

    root.getChildren
      .filtered(_.isInstanceOf[Button])
      .forEach(x => onButtonClicked(x.asInstanceOf[Button]))

    primaryStage.setTitle("Hello World")
    primaryStage.setScene(new Scene(root, 640, 480))
    primaryStage.show()

object MainApp:
  def main(args: Array[String]): Unit = Application.launch(classOf[MainApp], args *)