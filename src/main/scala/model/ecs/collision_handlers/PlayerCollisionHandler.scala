package model.ecs.collision_handlers
import model.ecs.components.*
import model.ecs.entities.enemies.EnemyEntity
import model.ecs.entities.weapons.{PlayerBulletEntity, WeaponEntity}
import model.ecs.entities.{Entity, EntityManager}
import model.ecs.systems.CollisionChecker
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity}
import model.{HORIZONTAL_COLLISION_SIZE, VERTICAL_COLLISION_SIZE}

trait PlayerCollisionHandler extends BasicCollisionHandler:
  self: Entity =>

  override protected def handleSpecialCollision(collidingEntity: Option[Entity]): Unit =
    collidingEntity match
      case Some(otherEntity) if otherEntity.isInstanceOf[WeaponEntity] =>
        EntityManager().removeEntity(otherEntity)
        EntityManager()
          .getEntitiesByClass(classOf[PlayerBulletEntity])
          .foreach(entity =>
            entity.replaceComponent(SpriteComponent(model.s_BigBullet))
          )
      case _ =>
        println("Player colliding with entity of type " + collidingEntity)