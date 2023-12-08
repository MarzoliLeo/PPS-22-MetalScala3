package view

import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.*
import javafx.scene.text.{Font, Text}
import javafx.scene.{Node, Scene}
import javafx.stage.Stage
import javafx.util.Duration
import model.ecs.components.*
import model.ecs.entities.Entity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.EnemyBulletEntity
import model.engine.Engine
import model.event.Event
import model.event.Event.*
import model.event.observer.{Observable, Observer}
import model.input.CommandsStackHandler
import model.input.commands.Command

import java.util.UUID

trait GameView extends View

private class GameViewImpl(
    primaryStage: Stage,
    observables: Set[Observable[Event]]
) extends GameView
    with CommandsStackHandler
    with Observer[Event] {

  val root: Pane = Pane()

  // Create the ammo text
  private val ammoText: Text = createText(10, 30)
  // Create the bomb text
  private val bombText: Text = createText(10, 60)
  // Creazione della scena di gioco (Diversa da quella del Menù).
  private val scene: Scene = Scene(root, model.GUIWIDTH, model.GUIHEIGHT)

  scene.setOnKeyPressed { k =>
    k.getCode match
      case KeyCode.LEFT  => handleInput(Command.left)
      case KeyCode.RIGHT => handleInput(Command.right)
      case KeyCode.UP    => handleInput(Command.jump)
      case KeyCode.SPACE => handleInput(Command.shoot)
      case KeyCode.DOWN  => handleInput(Command.crouch)
      case KeyCode.B     => handleInput(Command.bomb)
      case _             =>
  }
  scene.setOnKeyReleased { k =>
    k.getCode match
      case KeyCode.DOWN => handleInput(Command.standUp)
      case _            =>
  }

  // Load the background image
  private val backgroundImage = new Image(model.s_GameBackground)
  // Create a BackgroundImage object
  private val background = new Background(
    new BackgroundImage(
      backgroundImage,
      BackgroundRepeat.NO_REPEAT,
      BackgroundRepeat.NO_REPEAT,
      BackgroundPosition.DEFAULT,
      new BackgroundSize(
        BackgroundSize.AUTO,
        BackgroundSize.AUTO,
        false,
        false,
        true,
        true
      )
    )
  )
  private var entityIdToView: Map[UUID, Node] = Map()
  // Set the background of the root using the Background object
  root.setBackground(background)
  primaryStage.setScene(scene)
  observables.foreach(_.addObserver(this))

  override def update(subject: Event): Unit =
    Platform.runLater { () =>
      subject match
        case Tick(entities) =>
          entityIdToView.foreach((_, view) =>
            root.getChildren.remove(view)
          ) // Reset delle entità di ECS.
          entityIdToView =
            Map() // Solo per il reset delle entità che vengono rimosse (in questo caso Bullet).
          entities.foreach(entity =>
            if entity.hasComponent(classOf[PositionComponent])
              && entity.hasComponent(classOf[SpriteComponent])
              && entity.hasComponent(classOf[DirectionComponent])
            then
              val position = entity
                .getComponent[PositionComponent]
                .get
              val sprite = entity
                .getComponent[SpriteComponent]
                .get
              val direction = entity.getComponent[DirectionComponent].get

              entity match
                case playerEntity: PlayerEntity =>
                  playerEntity.getComponent[SpecialWeaponAmmoComponent] match
                    case Some(ammoComponent) =>
                      val ammo = ammoComponent.ammo
                      ammoText.setText(s"Ammo: $ammo")
                    case None => ammoText.setText("Ammo: 0")
                  playerEntity.getComponent[BombAmmoComponent] match
                    case Some(ammoComponent) =>
                      val ammo = ammoComponent.ammo
                      bombText.setText(s"Bomb: $ammo")
                    case None => bombText.setText("Bomb: 0")
                case _ => ()

              entityIdToView = entityIdToView + (entity.id -> createSpriteView(
                sprite,
                position
              ))
              val entityToShow = entityIdToView(entity.id)
              entityToShow.setTranslateX(position.x)
              entityToShow.setTranslateY(position.y)
              direction.d match
                case RIGHT =>
                  entityToShow.setScaleX(1)
                case LEFT =>
                  entityToShow.setScaleX(-1)

              if entity.isInstanceOf[EnemyBulletEntity] then
                direction.d match
                  case RIGHT =>
                    entityToShow.setScaleX(-1)
                  case LEFT =>
                    entityToShow.setScaleX(1)
          )
          entityIdToView.foreach((_, view) => root.getChildren.add(view))
    }

  private def createSpriteView(
      spriteComponent: SpriteComponent,
      position: PositionComponent
  ): Node = {
    val imageView = new ImageView(new Image(spriteComponent.spritePath))
    imageView.setFitWidth(model.fixedSpriteWidth)
    imageView.setFitHeight(model.fixedSpriteHeight)
    imageView.setPreserveRatio(true)
    imageView.setTranslateX(position.x)
    imageView.setTranslateY(position.y)
    imageView
  }

  private def createText(x: Double, y: Double): Text = {
    val text = Text()
    text.setFont(Font.font("Arial", 20))
    text.setFill(Color.BLACK)
    text.setX(x)
    text.setY(y)
    root.getChildren.add(text)
    text
  }
}

object GameView {
  def apply(
      primaryStage: Stage,
      observables: Set[Observable[Event]]): GameView =
    new GameViewImpl(primaryStage, observables)
}
