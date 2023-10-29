import javafx.scene.paint.Color
import model.ecs.components.{ColorComponent, PositionComponent, VisibleComponent}
import model.ecs.entities.{BoxEntity, EntityManager}
import model.ecs.systems.SystemManager
import model.ecs.systems.Systems.{gravitySystem, playerMovementSystem}

package object model:
  val GUIWIDTH: Int = 1500
  val GUIHEIGHT: Int = 800

  val entityManager: EntityManager = EntityManager()
    .addEntity(
      BoxEntity()
        .addComponent(PositionComponent(100, 100))
        .addComponent(ColorComponent(Color.RED))
        .addComponent(VisibleComponent())
    )

  val systemManager: SystemManager =
    SystemManager(entityManager)
      .addSystem(playerMovementSystem)
      .addSystem(gravitySystem)
