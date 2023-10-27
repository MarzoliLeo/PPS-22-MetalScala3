package view

import javafx.scene.paint.Color
import model.ecs.components.{ColorComponent, PositionComponent, VisibleComponent}
import model.ecs.entities.{BoxEntity, EntityManager}

trait BuildEntitiesForTheGame:
  def build(entityManager: EntityManager): EntityManager

object BuildEntitiesForTheGame:
  def apply(): BuildEntitiesForTheGame = BuildEntitiesForTheGameImpl()
  private class BuildEntitiesForTheGameImpl extends BuildEntitiesForTheGame:
    override def build(entityManager: EntityManager): EntityManager =
      val entityManager = EntityManager()
        .addEntity(
          BoxEntity()
            .addComponent(PositionComponent(100, 100))
            .addComponent(VisibleComponent())
            .addComponent(ColorComponent(Color.RED))
        )
      // TODO qui andranno tutte le entità che andranno mostrate nella view assieme alle loro components.
      entityManager
