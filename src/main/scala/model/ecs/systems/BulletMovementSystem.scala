package model.ecs.systems

import model.ecs.components.{PositionComponent, VelocityComponent}
import model.ecs.entities.EntityManager
import model.ecs.entities.weapons.{BombEntity, EnemyBulletEntity, PlayerBulletEntity}

trait BulletMovementSystem extends SystemWithElapsedTime

private class BulletMovementSystemImpl extends BulletMovementSystem:
  override def update(elapsedTime: Long): Unit =
    val playerBullets = EntityManager().getEntitiesByClass(classOf[PlayerBulletEntity])
    val enemyBullets = EntityManager().getEntitiesByClass(classOf[EnemyBulletEntity])
    val bombs = EntityManager().getEntitiesByClass(classOf[BombEntity])
    (playerBullets ++ enemyBullets ++ bombs).foreach {
      bullet =>
        given position: PositionComponent =
          bullet.getComponent[PositionComponent].get

        given velocity: VelocityComponent =
          bullet.getComponent[VelocityComponent].get

        bullet.handleCollision(position.getUpdatedPosition(elapsedTime)) match
          case Some(handledPosition) => bullet.replaceComponent(handledPosition)
          case None => ()
    }


object BulletMovementSystem:
  def apply(): BulletMovementSystem = BulletMovementSystemImpl()