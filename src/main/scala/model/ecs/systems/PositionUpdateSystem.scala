package model.ecs.systems

import model.FRICTION_FACTOR
import model.ecs.components.*
import model.ecs.entities.{EnemyEntity, Entity, EntityManager}
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{BulletEntity, MachineGunEntity}

trait PositionUpdateSystem extends SystemWithElapsedTime

private class PositionUpdateSystemImpl() extends PositionUpdateSystem:

  override def update(elapsedTime: Long): Unit =
    EntityManager()
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[VelocityComponent],
        classOf[JumpingComponent]
      )
      .foreach(entity =>
        given currentPosition: PositionComponent =
          entity.getComponent[PositionComponent].get

        given currentVelocity: VelocityComponent =
          entity.getComponent[VelocityComponent].get

        entity.replaceComponent(getUpdatedVelocity(entity))

        val proposedPosition = currentPosition.getUpdatedPosition(elapsedTime)
        val handledPosition: Option[PositionComponent] =
          entity.handleCollision(proposedPosition)

        handledPosition match
          case Some(handledPosition) => entity.replaceComponent(handledPosition)
          // keep the current position
          case None => ()
      )

  private def getUpdatedVelocity(entity: Entity)(using velocity: VelocityComponent
  ): VelocityComponent = {
    val newHorizontalVelocity = velocity.x * FRICTION_FACTOR match {
      case x if -0.1 < x && x < 0.1 => 0.0
      case x => x
    }
    entity match {
      case _: PlayerEntity =>
        val sprite = velocity match {
          case VelocityComponent(0, 0) => model.marcoRossiSprite
          case VelocityComponent(_, y) if y != 0 => model.marcoRossiJumpSprite
          case VelocityComponent(x, y) if x != 0 && y == 0 =>
            model.marcoRossiMoveSprite
        }
        entity.replaceComponent(SpriteComponent(sprite))
      case _: BulletEntity =>
        entity.replaceComponent(SpriteComponent("sprites/Bullet.png"))
      case _: MachineGunEntity =>
        entity.replaceComponent(SpriteComponent("sprites/h.png"))
      case _: BoxEntity =>
        entity.replaceComponent(SpriteComponent("sprites/box.jpg"))
      case _: EnemyEntity =>
        entity.replaceComponent(SpriteComponent("sprites/MarcoRossi.png"))
    }

    VelocityComponent(newHorizontalVelocity, velocity.y)
  }

object PositionUpdateSystem:
  def apply(): PositionUpdateSystem = PositionUpdateSystemImpl()

