package view

import javafx.animation.{ParallelTransition, PathTransition, TranslateTransition}
import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.FlowPane
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.*
import javafx.scene.{Node, Scene}
import javafx.stage.Stage
import javafx.util.Duration
import model.ecs.components.*
import model.ecs.entities.{Entity, EntityManager}
import model.event.Event
import model.event.Event.{Jump, Move, Spawn, Tick}
import model.event.observer.{Observable, Observer}
import model.input.BasicInputHandler

import java.util.UUID

trait GameView extends View

private class GameViewImpl(primaryStage: Stage, observables: Set[Observable[Event]]) extends GameView with BasicInputHandler with Observer[Event] {
  val root: FlowPane = FlowPane()
  private var entityIdToView: Map[UUID, Node] = Map()
  private var isMoving = false


  //Creazione della scena di gioco (Diversa da quella del Menù).
  private val scene: Scene = Scene(root, model.GUIWIDTH, model.GUIHEIGHT)
  scene.setOnKeyPressed(handleInput)
  primaryStage.setScene(scene)
  observables.foreach(_.addObserver(this))

  override def update(subject: Event): Unit =
    Platform.runLater { () =>
      subject match
        case Spawn(entity, ofType, sprite, position) =>
          entityIdToView = entityIdToView + (entity -> createSpriteView(sprite, position))
        case Move(entity, position, duration) =>
          val entityToShow = entityIdToView(entity)
          if (!isMoving) {
            val moveTransition = moveAnimation(entityToShow, position, duration)
            moveTransition.setOnFinished(_ => isMoving = false)
            moveTransition.play()
            isMoving = true
          }
          //entityToShow.setTranslateX(position.x)
          entityToShow.setTranslateY(position.y)
        case Tick() =>
          entityIdToView.foreach((_, view) => root.getChildren.remove(view))
          entityIdToView.foreach((_, view) => root.getChildren.add(view))
        case Jump(entity, jumpHeight, duration) =>
          val entityToShow = entityIdToView(entity)
          val startY = entityToShow.getTranslateY
          val jumpTransition = jumpAnimation(entityToShow, startY, jumpHeight, duration)
          jumpTransition.play()

    }

  //TODO lo userò per creare il terreno di gioco.
  private def createBoxView(position: PositionComponent): Node =
    val box = Box(100, 100, 100)
    box.setTranslateX(position.x)
    box.setTranslateY(position.y)
    box.setMaterial(PhongMaterial(Color.BLACK))
    box

  private def createSpriteView(spriteComponent: SpriteComponent, position: PositionComponent): Node = {
    val imageView = new ImageView(new Image(spriteComponent.spritePath))
    imageView.setFitWidth(model.fixedSpriteWidth)
    imageView.setFitHeight(model.fixedSpriteHeight)
    imageView.setPreserveRatio(true)
    imageView.setTranslateX(position.x)
    imageView.setTranslateY(position.y)
    imageView
  }

  private def jumpAnimation(entity: Node, startYPosition: Double, jumpHeight: Double, durationSeconds: Double): TranslateTransition= {
    val numerOfCyclesPerAnimation = 2
    val translateYTransition = new TranslateTransition(Duration.seconds(durationSeconds), entity)
    translateYTransition.setToY(startYPosition - jumpHeight)
    translateYTransition.setCycleCount(numerOfCyclesPerAnimation)
    translateYTransition.setAutoReverse(true)
    translateYTransition
    }

  private def moveAnimation(entity: Node, positionComponent: PositionComponent, durationSeconds: Double): TranslateTransition = {
    val translateTransition = new TranslateTransition(Duration.seconds(durationSeconds), entity)
    translateTransition.setFromX(entity.getTranslateX)
    translateTransition.setToX(positionComponent.x)
    //translateTransition.setFromY(entity.getTranslateY)
    //translateTransition.setToY(positionComponent.y)
    translateTransition.setCycleCount(1)
    translateTransition.setAutoReverse(false)
    translateTransition
  }

}


object GameView {
  def apply(primaryStage: Stage, observables: Set[Observable[Event]]): GameView =
    new GameViewImpl(primaryStage, observables)
}
