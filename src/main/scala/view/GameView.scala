package view

import javafx.application.Platform
import javafx.scene.layout.FlowPane
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import javafx.scene.{Node, Scene}
import javafx.stage.Stage
import model.ecs.components.*
import model.ecs.entities.{BoxEntity, Entity, EntityManager}
import model.ecs.observer.{Observable, Observer}
import model.entityManager
import model.input.BasicInputHandler
import java.util.UUID

trait GameView extends View

private class GameViewImpl(primaryStage: Stage) extends GameView with BasicInputHandler with Observer[Component] {
  val root: FlowPane = FlowPane()
  private var entityIdToView: Map[UUID, Node] = Map()

  //Creazione della scena di gioco (Diversa da quella del Menù).
  private val scene: Scene = Scene(root, model.GUIWIDTH, model.GUIHEIGHT)
  scene.setOnKeyPressed(handleInput)
  primaryStage.setScene(scene)


  private val boxes: List[BoxEntity] = entityManager
    .getEntitiesWithComponent(classOf[VisibleComponent])
    .filter(_.hasComponent(classOf[PositionComponent]))
    .collect { case box: BoxEntity => box }

  boxes.foreach(box => {
    box.addObserver(this)
    createBoxView(box)
  })


  private def removeOldView()(f: => Unit): Unit =
    Platform.runLater(() => {
      entityIdToView
        .foreach((_, view) => root.getChildren.remove(view))
      f
    })

  override def update(component: Component): Unit =
    removeOldView() {
      component match {
        case position: PositionComponent =>
          boxes
            .filter(
              _.getComponent(classOf[PositionComponent]).contains(position)
            )
            .foreach(createBoxView)
        case color: ColorComponent =>
          boxes
            .filter(_.getComponent(classOf[ColorComponent]).contains(color))
            .foreach(createBoxView)
        case _ => ()
      }
      updateView()
    }

  private def updateView(): Unit =
    Platform.runLater(() =>
      entityIdToView.foreach((_, view) => {
        if (!root.getChildren.contains(view)) {
          root.getChildren.add(view)
        }
      })
    )

  private def createBoxView(entity: Entity): Unit = {
    val box = Box(100, 100, 100)
    val position = entity
      .getComponent(classOf[PositionComponent])
      .get
      .asInstanceOf[PositionComponent]
    val color = entity
      .getComponent(classOf[ColorComponent])
      .fold(Color.BLACK)(_.asInstanceOf[ColorComponent].color)
    box.setTranslateX(position.x)
    box.setTranslateY(position.y)
    box.setMaterial(PhongMaterial(color))
    entityIdToView = entityIdToView + (entity.id -> box)
    updateView()
  }

}

object GameView {
  def apply(primaryStage: Stage): GameView = new GameViewImpl(primaryStage)
}
