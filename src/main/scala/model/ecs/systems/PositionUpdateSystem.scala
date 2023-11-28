package model.ecs.systems

import model.FRICTION_FACTOR
import model.ecs.components.{
  JumpingComponent,
  PositionComponent,
  SpriteComponent,
  VelocityComponent
}
import model.ecs.entities.{EnemyEntity, Entity, EntityManager}
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{BulletEntity, MachineGunEntity}
import model.ecs.systems.PositionUpdateSystem.getUpdatedPosition

object PositionUpdateSystem:
  def getUpdatedPosition(elapsedTime: Long)(using
      position: PositionComponent,
      velocity: VelocityComponent
  ): PositionComponent = {

    val newPositionX = position.x + velocity.x * elapsedTime * 0.001
    val newPositionY = position.y + velocity.y * elapsedTime * 0.001

    PositionComponent(newPositionX, newPositionY)
  }

  private def getUpdatedVelocity(entity: Entity)(using
      velocity: VelocityComponent
  ): VelocityComponent = {
    val newHorizontalVelocity = velocity.x * FRICTION_FACTOR match {
      case x if -0.1 < x && x < 0.1 => 0.0
      case x                        => x
    }
    entity match {
      case _: PlayerEntity =>
        val sprite = velocity match {
          case VelocityComponent(0, 0)           => model.marcoRossiSprite
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

  def apply(): Long => Unit = elapsedTime =>
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

        val proposedPosition = getUpdatedPosition(elapsedTime)
        val handledPosition: Option[PositionComponent] =
          entity.handleCollision(proposedPosition)

        handledPosition match
          case Some(handledPosition) => entity.replaceComponent(handledPosition)
          // keep the current position
          case None => ()
      )
