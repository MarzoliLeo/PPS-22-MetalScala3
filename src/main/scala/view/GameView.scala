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
        case Spawn(entityID, ofType, sprite, position) =>
          entityIdToView = entityIdToView + (entityID -> createSpriteView(sprite,0, position))
        case Move(entityID, sprite, position, duration) =>
          val entityToShow = entityIdToView(entityID)
//          if (!isAnimationMovingOn && model.isTouchingGround) {
//            reUpdateView(entityID, sprite, 1, position)
//            val moveTransition = moveAnimation(entityToShow, position, duration)
//            moveTransition.setOnFinished(_ =>
//              isAnimationMovingOn = false
//              reUpdateView(entityID, sprite, 0, position)
//            )
//            moveTransition.play()
//            isAnimationMovingOn = true
//            model.isTouchingGround = false
//          }
          //CODICE ORIGINALE: lo tengo perché così faccio il paragone fra l'animazione e non.
          entityToShow.setTranslateX(position.x)
          entityToShow.setTranslateY(position.y)
        case Tick() =>
          entityIdToView.foreach((_, view) => root.getChildren.remove(view))
          entityIdToView.foreach((_, view) => root.getChildren.add(view))
        case Jump(entityID, sprite, position, jumpHeight, duration) =>
          reUpdateView(entityID, sprite, 2, position)
          // Ricordarsi che la jump animation prende il nuovo sprite che ho appena creato (dentro reUpdateView),
          // e non quello originale.
          val entityToMakeJump = entityIdToView(entityID)
//          if(!isAnimationJumpingOn && model.isTouchingGround) {
//            val jumpTransition = jumpAnimation(entityToMakeJump, position.y, jumpHeight, duration)
//            jumpTransition.setOnFinished(_ =>
//              isAnimationJumpingOn = false
//              reUpdateView(entityID, sprite, 0, position)
//            )
//            jumpTransition.play()
//            isAnimationJumpingOn = true
//            model.isTouchingGround = false
//          }
          entityToMakeJump.setTranslateY(position.y - jumpHeight)
        case Gravity(entityID, position) =>
          val entityToShow = entityIdToView(entityID)
          entityToShow.setTranslateY(position.y)

    }

  //TODO lo userò per creare il terreno di gioco.
  private def createBoxView(position: PositionComponent): Node =
    val box = Box(100, 100, 100)
    box.setTranslateX(position.x)
    box.setTranslateY(position.y)
    box.setMaterial(PhongMaterial(Color.RED))
    box


  private def jumpAnimation(node: Node, startYPosition: Double, jumpHeight: Double, durationSeconds: Double): TranslateTransition= {
    val numerOfCyclesPerAnimation = 2
    val translateYTransition = new TranslateTransition(Duration.seconds(durationSeconds), node)
    translateYTransition.setToY(startYPosition - jumpHeight)
    translateYTransition.setCycleCount(numerOfCyclesPerAnimation)
    translateYTransition.setAutoReverse(true)
    translateYTransition
    }

  private def moveAnimation(node: Node, positionComponent: PositionComponent, durationSeconds: Double): TranslateTransition = {
    val translateTransition = new TranslateTransition(Duration.seconds(durationSeconds), node)
    translateTransition.setFromX(node.getTranslateX)
    translateTransition.setToX(positionComponent.x)
    translateTransition.setCycleCount(1)
    translateTransition.setAutoReverse(false)
    translateTransition
  }

  private def createSpriteView(spriteComponent: SpriteComponent, index: Int, position: PositionComponent): Node = {
    val imageView = new ImageView(new Image(spriteComponent.spritePath(index)))
    imageView.setFitWidth(model.fixedSpriteWidth)
    imageView.setFitHeight(model.fixedSpriteHeight)
    imageView.setPreserveRatio(true)
    imageView.setTranslateX(position.x)
    imageView.setTranslateY(position.y)
    imageView
  }

  private def reUpdateView(entityID : UUID, sprite: SpriteComponent, index: Int, position: PositionComponent) : Unit = {
    //TODO sto facendo il remove qua perché l'evento Move va più veloce di Tick() il che si traduce che se non fai questa remove qua,
    // hai 300 sprite nella stessa scena di gioco e non uno sprite che viene sostituito nella stessa posizione.
    entityIdToView.foreach((_, view) => root.getChildren.remove(view))
    entityIdToView = entityIdToView + (entityID -> createSpriteView(sprite, index, position))
    entityIdToView.foreach((_, view) => root.getChildren.add(view))
  }

}


object GameView {
  def apply(
      primaryStage: Stage,
      observables: Set[Observable[Event]]
  ): GameView =
    new GameViewImpl(primaryStage, observables)
}
