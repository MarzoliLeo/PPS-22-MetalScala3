package model.ecs.collision_handlers
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}
import model.ecs.components.{JumpingComponent, PositionComponent, SpriteComponent, VelocityComponent}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.weapons.{PlayerBulletEntity, WeaponEntity}
import model.ecs.systems.CollisionChecker
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}

trait PlayerCollisionHandler extends CollisionHandler:
  self: Entity =>

  private def getFinalPosition(
      proposedPosition: PositionComponent,
      currentPosition: PositionComponent
  ): PositionComponent =
    getCollidingEntity(this, proposedPosition)
      // if there is a colliding entity, do not change the position
      .map(_ => currentPosition)
      // otherwise, change the position
      .getOrElse(proposedPosition)

  private def updateJumpingComponent(
      currentPosition: PositionComponent,
      proposedPosition: PositionComponent,
      velocity: VelocityComponent
  ): JumpingComponent =
    if (
      (currentPosition.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0)
      || getCollidingEntity(
        this,
        PositionComponent(currentPosition.x, proposedPosition.y)
      ).isDefined
    )
      JumpingComponent(false)
    else
      getComponent[JumpingComponent].getOrElse(
        throw new Exception("No JumpingComponent found")
      )

  override def handleCollision(
      proposedPosition: PositionComponent
  ): Option[PositionComponent] =
    for {
      currentPosition <- getComponent[PositionComponent]
      velocity <- getComponent[VelocityComponent]
      collidingEntity = getCollidingEntity(this, proposedPosition)
    } yield {
      val updatedJumpingComponent =
        updateJumpingComponent(currentPosition, proposedPosition, velocity)
      replaceComponent(updatedJumpingComponent)

      collidingEntity match {
        case Some(entity) if entity.isInstanceOf[WeaponEntity] =>
          println("Collided with WeaponEntity, updating bullets.")
          EntityManager().removeEntity(entity)
          EntityManager().getEntitiesByClass(classOf[PlayerBulletEntity]).foreach(
            entity =>
              entity.replaceComponent(SpriteComponent(model.s_BigBullet))
          )
        case _ =>
          println("Bullet destroyed inside PlayerCollision!")
      }

      PositionComponent(
        boundaryCheck(
          getFinalPosition(
            PositionComponent(proposedPosition.x, currentPosition.y),
            currentPosition
          ).x,
          model.GUIWIDTH,
          HORIZONTAL_COLLISION_SIZE
        ),
        boundaryCheck(
          getFinalPosition(
            PositionComponent(currentPosition.x, proposedPosition.y),
            currentPosition
          ).y,
          model.GUIHEIGHT,
          VERTICAL_COLLISION_SIZE
        )
      )
    }