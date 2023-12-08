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

  override def update(subject: Event): Unit = Platform.runLater { () =>
    subject match
      case Tick(entities) =>
        resetEntities()
        processEntities(entities)
        addSpritesToRoot()
      case _ => ()
  }

  /** Resets all entities by removing them from the root node and clearing the
    * entity map.
    */
  private def resetEntities(): Unit = {
    entityIdToView.values.foreach(root.getChildren.remove)
    entityIdToView = Map()
  }

  /** Process the given entities by performing certain operations on them.
    *
    * @param entities
    *   the sequence of entities to be processed
    */
  private def processEntities(entities: Seq[Entity]): Unit = {
    entities.foreach { entity =>
      (
        entity.getComponent[PositionComponent],
        entity.getComponent[SpriteComponent],
        entity.getComponent[DirectionComponent]
      ) match
        case (Some(position), Some(sprite), Some(direction)) =>
          processEntity(entity)
          renderSpriteView(entity, sprite, position, direction)
        case _ => ()
    }
  }

  /** Processes a entity according to the type of entity.
    *
    * @param entity
    *   The entity to process.
    */
  private def processEntity(entity: Entity): Unit = {
    entity match
      case playerEntity: PlayerEntity =>
        val ammoTextValue = playerEntity
          .getComponent[SpecialWeaponAmmoComponent]
          .map(c => s"Ammo: ${c.ammo}")
          .getOrElse("Ammo: 0")
        ammoText.setText(ammoTextValue)
        val bombTextValue = playerEntity
          .getComponent[BombAmmoComponent]
          .map(c => s"Bomb: ${c.ammo}")
          .getOrElse("Bomb: 0")
        bombText.setText(bombTextValue)
      case _ => ()
  }

  /** Private method for working with a sprite view.
    *
    * @param entity
    *   The entity being worked on.
    * @param sprite
    *   The sprite component of the entity.
    * @param position
    *   The position component of the entity.
    * @param direction
    *   The direction component of the entity.
    */
  private def renderSpriteView(
      entity: Entity,
      sprite: SpriteComponent,
      position: PositionComponent,
      direction: DirectionComponent
  ): Unit = {
    val spriteView = createSpriteView(sprite, position)
    entityIdToView += (entity.id -> spriteView)
    spriteView.setTranslateX(position.x)
    spriteView.setTranslateY(position.y)
    val scaleXValue = direction.d match
      case RIGHT if entity.isInstanceOf[EnemyBulletEntity] => -1
      case LEFT if entity.isInstanceOf[EnemyBulletEntity]  => 1
      case RIGHT                                           => 1
      case LEFT                                            => -1
    spriteView.setScaleX(scaleXValue)
  }

  /** Creates a sprite view using the provided sprite component and position
    * component.
    *
    * @param spriteComponent
    *   The sprite component containing the sprite path.
    * @param position
    *   The position component containing the x and y coordinates.
    * @return
    *   The created sprite view as a Node.
    */
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

  /** Adds all the sprites to the root node.
    *
    * This method iterates through each value in `entityIdToView` map and adds
    * the corresponding view node to the root node.
    *
    * @since 1.0.0
    */
  private def addSpritesToRoot(): Unit = {
    entityIdToView.values.foreach(root.getChildren.add)
  }

  /** Creates a new Text object at the given coordinates.
    *
    * @param x
    *   The x-coordinate of the Text object.
    * @param y
    *   The y-coordinate of the Text object.
    * @return
    *   The newly created Text object.
    */
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
      observables: Set[Observable[Event]]
  ): GameView =
    new GameViewImpl(primaryStage, observables)
}
