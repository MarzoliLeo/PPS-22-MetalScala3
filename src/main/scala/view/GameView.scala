package view

import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.*
import javafx.scene.{Node, Scene}
import javafx.stage.Stage
import javafx.util.Duration
import model.ecs.components.*
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionSystem.getBoundingBox
import model.event.Event
import model.event.Event.*
import model.event.observer.{Observable, Observer}
import model.input.CommandsStackHandler
import model.input.commands.Command

import java.util.UUID

trait GameView extends View

private class GameViewImpl(primaryStage: Stage, observables: Set[Observable[Event]]) extends GameView with CommandsStackHandler with Observer[Event] {
  val root: Pane = Pane()
  private var entityIdToView: Map[UUID, Node] = Map()
  private var isAnimationMovingOn = false
  private var isAnimationJumpingOn = false

  // Creazione della scena di gioco (Diversa da quella del Menù).
  private val scene: Scene = Scene(root, model.GUIWIDTH, model.GUIHEIGHT)
  scene.setOnKeyPressed { k =>
    k.getCode match
      case KeyCode.LEFT => handleInput(Command.left)
      case KeyCode.RIGHT => handleInput(Command.right)
      case KeyCode.UP => handleInput(Command.jump)
      case KeyCode.SPACE => handleInput(Command.shoot)
      case _ => ()
  }
  primaryStage.setScene(scene)
  observables.foreach(_.addObserver(this))


  override def update(subject: Event): Unit =
    Platform.runLater { () =>
      subject match
        case Tick(entities) =>
          entityIdToView.foreach((_, view) => root.getChildren.remove(view))
          entities.foreach(entity =>
            if //1° Check - Se renderizzabile
              entity.hasComponent(classOf[PositionComponent])
              && entity.hasComponent(classOf[SpriteComponent])
              && entity.hasComponent(classOf[VelocityComponent])
            then
              val position = entity.getComponent[PositionComponent].get
              val sprite = entity.getComponent[SpriteComponent].get
              val velocity = entity.getComponent[VelocityComponent].get
              entityIdToView = entityIdToView + (entity.id -> createSpriteView(sprite,0, position))
              val entityToShow = entityIdToView(entity.id)
              entityToShow.setTranslateX(position.x)
              entityToShow.setTranslateY(position.y)
              if velocity.x < 0 then
                entityToShow.setScaleX(-1)
              if velocity.x > 0 then
                entityToShow.setScaleX(1)

          )
          entityIdToView.foreach((_, view) => root.getChildren.add(view))
    }

  //TODO lo userò per creare il terreno di gioco.
  private def createBoxView(position: PositionComponent): Node =
    val box = Box(100, 100, 100)
    box.setTranslateX(position.x)
    box.setTranslateY(position.y)
    box.setMaterial(PhongMaterial(Color.RED))
    box


  private def createSpriteView(spriteComponent: SpriteComponent, index: Int, position: PositionComponent): Node = {
    val imageView = new ImageView(new Image(spriteComponent.spritePath(index)))
    imageView.setFitWidth(model.fixedSpriteWidth)
    imageView.setFitHeight(model.fixedSpriteHeight)
    imageView.setPreserveRatio(true)
    imageView.setTranslateX(position.x)
    imageView.setTranslateY(position.y)
    imageView
  }

}


object GameView {
  def apply(
      primaryStage: Stage,
      observables: Set[Observable[Event]]
  ): GameView =
    new GameViewImpl(primaryStage, observables)
}
