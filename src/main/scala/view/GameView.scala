package view
import ecs.components.{ColorComponent, Component, VisibleComponent, PositionComponent}
import ecs.entities.{BoxEntity, Entity, EntityManager}
import javafx.scene.layout.{FlowPane, Pane}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.Box

trait GameView extends View

private class GameViewImpl(entities: EntityManager) extends GameView:
  val root: FlowPane = FlowPane()

  private val displayableEntities: List[Entity] =
    entities
    .getEntitiesWithComponent(classOf[VisibleComponent])
    .filter(_.hasComponent(classOf[PositionComponent]))

  private val boxes: List[Box] = displayableEntities.collect {
    case entity if entity.isInstanceOf[BoxEntity] =>
      val position = entity.getComponent(classOf[PositionComponent]) match
        case Some(component) => component.asInstanceOf[PositionComponent]
        case None            => PositionComponent(0, 0)
      val color = entity
        .getComponent(classOf[ColorComponent])
        .map(_.asInstanceOf[ColorComponent])
        .map(_.color)
        .getOrElse(Color.WHITE)
      val box = Box(100, 100, 100)
      box.setTranslateX(position.x)
      box.setTranslateY(position.y)
      box.setMaterial(PhongMaterial(color))
      box
  }

  //Add the boxes to the root pane. So it can be displayed.
  boxes.foreach(root.getChildren.add)

  override def getContent: Pane = root

object GameView:
  def apply(entities: EntityManager): GameView = GameViewImpl(
    entities: EntityManager
  )
