package view.menu

import ecs.components.Position
import view.View
import javafx.scene.layout.{GridPane, Pane}
import javafx.fxml.FXMLLoader
import javafx.stage.Stage

trait Controller extends View:
  def renderObjects(node : javafx.scene.Node, position : Position) : Unit

object Controller:
  def apply(parentStage: Stage, nextPane: Pane): Controller = ControllerImp(parentStage, nextPane)
  private class ControllerImp(parentStage: Stage, nextPane: Pane) extends Controller:
    val loader: FXMLLoader = FXMLLoader(getClass.getResource("/main.fxml"))
    val root: GridPane = loader.load[javafx.scene.layout.GridPane]()

    override def getContent: Pane = root
    // javafx.scene.Node its any renderizable object in JavaFX.
    override def renderObjects( node : javafx.scene.Node, position : Position): Unit =
      node.setTranslateX(position.x)
      node.setTranslateY(position.y)

