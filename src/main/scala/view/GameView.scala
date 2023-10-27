package view

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.layout.{FlowPane, Pane}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box
import model.ecs.components.*
import model.ecs.entities.{BoxEntity, Entity, EntityManager}
import model.ecs.observer.{Observable, Observer}

import java.util.UUID

trait GameView extends View

private class GameViewImpl(entities: EntityManager)
    extends GameView
    with Observer[Component]:

  val root: FlowPane = FlowPane()

  private var entityIdToView: Map[UUID, Node] = Map()

  private val displayableEntities: List[Entity] =
    entities
      .getEntitiesWithComponent(classOf[VisibleComponent])
      .filter(_.hasComponent(classOf[PositionComponent]))

  private val boxes: List[BoxEntity] = displayableEntities
    .collect { case box: BoxEntity => box }

  // we observe each box entity to update the view when a box entity changes
  boxes.foreach(_.addObserver(this))

  for (box <- boxes) {
    createBoxView(box)
  }

  override def getContent: Pane = root

  override def update(component: Component): Unit = {
    removeOldView() {
      component match {
        case position: PositionComponent =>
          boxes
            .collect {
              case box
                  if box.hasComponent(classOf[PositionComponent]) && box
                    .getComponent(classOf[PositionComponent])
                    .get
                    .isSameComponent(position) =>
                box
            }
            .foreach(createBoxView)
          updateView()
        case color: ColorComponent =>
          boxes
            .collect {
              case box
                  if box.hasComponent(classOf[ColorComponent]) && box
                    .getComponent(classOf[ColorComponent])
                    .get
                    .isSameComponent(color) =>
                box
            }
            .foreach(createBoxView)
          updateView()
        case _ => ()
      }
    }
  }

  private def updateView(): Unit = {
    Platform.runLater(() =>
      entityIdToView.foreach((_, view) => {
        if (!root.getChildren.contains(view)) {
          root.getChildren.add(view)
        }
      })
    )
  }

  private def createBoxView(entity: Entity): Unit = {
    val box = Box(100, 100, 100)
    val position = getPosition(entity)
    val color = getColor(entity)
    box.setTranslateX(position.x)
    box.setTranslateY(position.y)
    box.setMaterial(PhongMaterial(color))
    entityIdToView = entityIdToView + (entity.id -> box)
  }

  private def getPosition(entity: Entity): PositionComponent =
    entity.getComponent(classOf[PositionComponent]) match {
      case Some(component: PositionComponent) => component
      // fixme
      case _ => PositionComponent(0, 0)
    }

  private def getColor(entity: Entity): Color =
    entity.getComponent(classOf[ColorComponent]) match {
      case Some(ColorComponent(color)) => color
      case _                           => Color.BLACK
    }

  private def removeOldView()(f: => Unit): Unit = {
    Platform.runLater(() => {
      entityIdToView.foreach((*, view) => root.getChildren.remove(view))
      f
    })
  }

object GameView {
  def apply(entities: EntityManager): GameView = GameViewImpl(
    entities
  )
}
