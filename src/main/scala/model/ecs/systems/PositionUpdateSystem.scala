package model.ecs.systems

import model.FRICTION_FACTOR
import model.ecs.components.*
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{MachineGunEntity, PlayerBulletEntity}
import model.ecs.entities.{Entity, EntityManager}

trait PositionUpdateSystem extends SystemWithElapsedTime

private case class PositionUpdateSystemImpl() extends PositionUpdateSystem:
  override def update(elapsedTime: Long): Unit =
    EntityManager
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[VelocityComponent],
        classOf[JumpingComponent]
      )
      .foreach(entity =>
        val currentPosition: PositionComponent =
          entity.getComponent[PositionComponent].get
        given VelocityComponent =
          entity.getComponent[VelocityComponent].get
        entity.replaceComponent(getUpdatedVelocity)

        val proposedPosition = currentPosition.getUpdatedPosition(elapsedTime)
        entity.handleCollision(proposedPosition) match
          case Some(handledPosition) => entity.replaceComponent(handledPosition)
          case None                  => ()
      )

  private def getUpdatedVelocity(using
      velocity: VelocityComponent
  ): VelocityComponent = {
    val newHorizontalVelocity = velocity.x * FRICTION_FACTOR match
      case x if -0.1 < x && x < 0.1 => 0.0
      case x                        => x
    VelocityComponent(newHorizontalVelocity, velocity.y)
  }

object PositionUpdateSystem:
  def apply(): PositionUpdateSystem = PositionUpdateSystemImpl()
