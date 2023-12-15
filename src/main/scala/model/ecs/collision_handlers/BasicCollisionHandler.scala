package model.ecs.collision_handlers

import model.ecs.collision_handlers.CollisionHandler
import model.ecs.components.*
import model.ecs.entities.Entity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.PlayerBulletEntity
import CollisionChecker.{boundaryCheck, getCollidingEntity}
import model.{
  GRAVITY_VELOCITY,
  HORIZONTAL_COLLISION_SIZE,
  VERTICAL_COLLISION_SIZE
}
import model.ecs.entities.weapons.EnemyBulletEntity
import model.ecs.entities.enemies.EnemyEntity

/** The BasicCollisionHandler trait implements collision handling logic for
  * entities. It extends the CollisionHandler trait and requires the entity to
  * mix in the Entity trait.
  */
trait BasicCollisionHandler extends CollisionHandler:
  self: Entity =>

  def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent] =
    for
      currentPosition <- getComponent[PositionComponent]
      _ <- getComponent[VelocityComponent]
      sizeComponent <- getComponent[SizeComponent]
    yield
      handleSpecialCollision {
        getCollidingEntity(this, proposedPosition)
      }

      val finalX = getFinalPosition(
        PositionComponent(proposedPosition.x, currentPosition.y),
        currentPosition
      ).x
      val finalY = getFinalPosition(
        PositionComponent(currentPosition.x, proposedPosition.y),
        currentPosition
      ).y

      PositionComponent(
        boundaryCheck(
          finalX,
          model.GUIWIDTH,
          sizeComponent.width
        ),
        boundaryCheck(
          finalY,
          model.GUIHEIGHT,
          sizeComponent.height
        )
      )

  private def getFinalPosition(
      proposedPosition: PositionComponent,
      currentPosition: PositionComponent
  ): PositionComponent =
    if canPassThrough(proposedPosition) then proposedPosition
    else currentPosition

  private def canPassThrough(
      proposedPosition: PositionComponent
  ): Boolean =
    getCollidingEntity(this, proposedPosition) match
      case Some(_: PlayerBulletEntity) if this.isInstanceOf[PlayerEntity] =>
        true
      case Some(_: PlayerEntity) if this.isInstanceOf[PlayerBulletEntity] =>
        true
      case Some(_: EnemyEntity) if this.isInstanceOf[EnemyBulletEntity] => true
      case Some(_: EnemyBulletEntity) if this.isInstanceOf[EnemyEntity] => true
      case None                                                         => true
      case _                                                            => false

  protected def handleSpecialCollision(
      collidingEntity: Option[Entity]
  ): Unit = ()
