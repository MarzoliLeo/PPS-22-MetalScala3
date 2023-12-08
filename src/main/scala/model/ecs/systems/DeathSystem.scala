package model.ecs.systems

import model.ecs.components.{PlayerComponent, PositionComponent, SpriteComponent, VelocityComponent}
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.{Entity, EntityManager}
import model.engine.Engine
import model.event.observer.Observable

trait DeathSystem extends SystemWithoutTime

private case class DeathSystemImpl(engine: Engine) extends DeathSystem:
  override def update(): Unit =
    EntityManager.entities.find(_.isInstanceOf[PlayerEntity]) match
      case Some(entity)
          if entity.isInstanceOf[PlayerEntity] => // player ancora vivo.
      case _ => engine.stop()

object DeathSystem:
  def apply(engine: Engine): DeathSystem = DeathSystemImpl(engine)
