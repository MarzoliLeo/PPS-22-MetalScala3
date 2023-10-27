import javafx.scene.paint.Color
import model.ecs.components.{ColorComponent, PositionComponent, VisibleComponent}
import model.ecs.entities.{BoxEntity, EntityManager}
import model.ecs.systems.SystemManager
import model.ecs.systems.Systems.playerMovementSystem

package object model:
  val entityManager = EntityManager()
    .addEntity(
      BoxEntity()
        .addComponent(PositionComponent(100, 100))
        .addComponent(ColorComponent(Color.RED))
        .addComponent(VisibleComponent())
    )
  val systemManager: SystemManager =
    SystemManager(entityManager).addSystem(playerMovementSystem)
