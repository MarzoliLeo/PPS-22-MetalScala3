package model.ecs.systems

import model.ecs.components.{
  PlayerComponent,
  PositionComponent,
  SpriteComponent,
  VelocityComponent
}
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.{Entity, EntityManager}
import model.engine.Engine
import model.event.observer.Observable
import model.ecs.entities.enemies.EnemyEntity

trait GameOverSystem extends SystemWithoutTime

private case class DeathSystemImpl(engine: Engine) extends GameOverSystem:
  override def update(): Unit =
    if isGameOver() then gameOver() else ()

  private def isGameOver(): Boolean =
    !EntityManager.entities.exists(
      _.isInstanceOf[PlayerEntity]
    ) || !EntityManager.entities.exists(_.isInstanceOf[EnemyEntity])

  private def gameOver(): Unit =
    engine.stop()
    Thread.sleep(1000)
    System.exit(0)

object GameOverSystem:
  def apply(engine: Engine): GameOverSystem = DeathSystemImpl(engine)
