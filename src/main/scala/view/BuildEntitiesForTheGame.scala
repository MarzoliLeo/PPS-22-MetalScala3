package view

import ecs.components.{ColorComponent, PositionComponent, VisibleComponent}
import ecs.entities.{BoxEntity, EntityManager}
import javafx.scene.paint.Color


trait BuildEntitiesForTheGame:
  def build(entityManager: EntityManager): EntityManager


object BuildEntitiesForTheGame:
  def apply() : BuildEntitiesForTheGame = new BuildEntitiesForTheGameImpl()
  private class BuildEntitiesForTheGameImpl extends BuildEntitiesForTheGame {
    override def build(entityManager: EntityManager): EntityManager = {
      val entityManager = EntityManager()
        .addEntity(
          BoxEntity()
            .addComponent(PositionComponent(100, 100))
            .addComponent(VisibleComponent())
            .addComponent(ColorComponent(Color.YELLOW))
        )
      //TODO qui andranno tutte le entità che andranno mostrate nella view assieme alle loro components.
      return entityManager
    }

  }




