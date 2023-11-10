package view

import javafx.application.Platform
import javafx.scene.layout.{FlowPane, Pane}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Rectangle}
import javafx.scene.{Node, Scene}
import javafx.stage.Stage
import model.ecs.components.*
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionSystem.getBoundingBox
import model.event.Event
import model.event.Event.{Move, Spawn, Tick}
import model.event.observer.{Observable, Observer}
import model.input.BasicInputHandler

import java.util.UUID

trait GameView extends View

private class GameViewImpl(
    primaryStage: Stage,
    observables: Set[Observable[Event]]
) extends GameView
    with BasicInputHandler
    with Observer[Event] {
  val root: Pane = Pane()
  private var entityIdToView: Map[UUID, Node] = Map()

  // Creazione della scena di gioco (Diversa da quella del Menù).
  private val scene: Scene = Scene(root, model.GUIWIDTH, model.GUIHEIGHT)
  scene.setOnKeyPressed(handleInput)
  primaryStage.setScene(scene)
  observables.foreach(_.addObserver(this))

  // FOR DEBUG
//  private var entityIdToBoundingBox: Map[UUID, Rectangle] = Map()

/*  // FOR DEBUG
  private def createBoundingBox(
      entity: Entity,
      position: PositionComponent
  ): Rectangle = {

    val boundingBox =
      entity.getBoundingBox // Assuming getBoundingBox is accessible

    // first argument is width, second is height, third is x offset, fourth is y offset
    val rectangle = Rectangle(
      boundingBox.width,
      boundingBox.height,
      position.x,
      position.y
    )

    rectangle.setStroke(Color.BLACK)
    rectangle.setFill(Color.TRANSPARENT)
    rectangle.toFront() // Brings the rectangle to the front

    rectangle.setTranslateX(position.x)
    rectangle.setTranslateY(position.y)
    rectangle
  }
*/
  override def update(subject: Event): Unit =
    Platform.runLater { () =>
      subject match
        case Spawn(entityId, _, position) =>
          val targetEntity = EntityManager().entities
            .filter(_.id == entityId)
            .head
          val entityColor = targetEntity
            .getComponent(classOf[ColorComponent])
            .get
            .asInstanceOf[ColorComponent]
            .color
          entityIdToView =
            entityIdToView + (entityId -> createBoxView(position, entityColor))
//          entityIdToBoundingBox =
//            entityIdToBoundingBox + (entityId -> createBoundingBox(
//              targetEntity,
//              position
//            ))
        case Move(entityId, position) =>
          val targetEntity = EntityManager().entities
            .filter(_.id == entityId)
            .head
          val box = entityIdToView(entityId)
          box.setTranslateX(position.x)
          box.setTranslateY(position.y)

          /*val boundingBox = entityIdToBoundingBox(entityId)
          boundingBox.setTranslateX(position.x)
          boundingBox.setTranslateY(position.y)*/
        case Tick() =>
          entityIdToView.foreach((_, view) => root.getChildren.remove(view))
          entityIdToView.foreach((_, view) => root.getChildren.add(view))
          /*entityIdToBoundingBox.foreach((_, view) =>
            root.getChildren.remove(view)
          )
          entityIdToBoundingBox.foreach((_, view) => root.getChildren.add(view))*/
    }

  private def createBoxView(position: PositionComponent, color: Color): Node =
    val box = Box(100, 100, 100)
    box.setTranslateX(position.x)
    box.setTranslateY(position.y)
    box.setMaterial(PhongMaterial(color))
    box
}

object GameView {
  def apply(
      primaryStage: Stage,
      observables: Set[Observable[Event]]
  ): GameView =
    new GameViewImpl(primaryStage, observables)
}
