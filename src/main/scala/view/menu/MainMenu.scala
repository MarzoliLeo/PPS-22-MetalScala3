package view.menu

import ecs.components.PositionComponent
import ecs.entities.Entity
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}
import javafx.scene.shape.Box
import javafx.stage.Stage
import view.View
import javafx.scene.paint.{Color, PhongMaterial}

trait MainMenu extends View:
  def getButton(root: Pane, buttonText: String): Button =
    root.getChildren
      .filtered {
        case btn: Button if btn.getText == buttonText => true
        case _                                        => false
      }
      .get(0)
      .asInstanceOf[Button]

  def startButton: Button

  def exitButton: Button

  def handleStartButton(): Unit

  def handleExitButton(): Unit

  /*def CANCELLAMITIPREGO(entities: List[Entity]): Unit*/

private class MainMenuImpl(parentStage: Stage, nextPane: Pane) extends MainMenu:

  val loader: FXMLLoader = FXMLLoader(getClass.getResource("/main.fxml"))
  val root: GridPane = loader.load[javafx.scene.layout.GridPane]()
  getButton(root, "Start").setOnAction((_: ActionEvent) => handleStartButton())
  getButton(root, "Exit").setOnAction((_: ActionEvent) => handleExitButton())

  def handleStartButton(): Unit = parentStage.getScene.setRoot(nextPane)

  def handleExitButton(): Unit = parentStage.close()

  override def getContent: Pane = root

  override def startButton: Button = getButton(root, "Start")

  override def exitButton: Button = getButton(root, "Exit")

  /*  override def CANCELLAMITIPREGO(entities: List[Entity]): Unit =
      entities.foreach { e =>
        e.getComponent(classOf[Position]) match
          case Some(pos) => val box = Box(50,50, 50)
            val material = PhongMaterial(Color.RED)
            box.setMaterial(material)
            root.add(box,0,0)
            box.setTranslateX(pos.asInstanceOf[Position].x)
            box.setTranslateY(pos.asInstanceOf[Position].y)
          case None =>
      }
  */
object MainMenu:
  def apply(parentStage: Stage, nextPane: Pane): MainMenu =
    MainMenuImpl(parentStage, nextPane)
