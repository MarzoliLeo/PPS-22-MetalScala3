package model.ecs.systems

import javafx.scene.input.KeyCode
import model.*
import model.ecs.components.*
import model.ecs.entities.*
import model.ecs.systems.CollisionSystem.OverlapType
import model.ecs.systems.CollisionSystem.OverlapType.Both
import model.ecs.systems.Systems.updatePosition
import model.event.Event
import model.event.observer.Observable
import model.input.commands.*
import model.utilities.Empty

import java.io.FileInputStream

object Systems extends Observable[Event]:

  /** Applies a boundary check to a position value, ensuring it stays within the
    * bounds of the system.
    *
    * @param pos
    *   The position value to check.
    * @param max
    *   The maximum value allowed for the position.
    * @param size
    *   The size of the object being checked.
    * @return
    *   The new position value after the boundary check has been applied.
    */
  def boundaryCheck(pos: Double, max: Double, size: Double): Double =
    math.max(0.0, math.min(pos, max - size))

  val bulletMovementSystem: Long => Unit = elapsedTime =>
    EntityManager().getEntitiesByClass(classOf[BulletEntity]).foreach {
      bullet =>
        {
          val pos = bullet.getComponent[PositionComponent].get
          val vel = bullet.getComponent[VelocityComponent].get
          val newPositionX = pos.x + vel.x * elapsedTime * 0.001
          val newPositionY = pos.y + vel.y * elapsedTime * 0.001
          // Calculate the new position based on the velocity and elapsed time
          val newPosition = PositionComponent(newPositionX, newPositionY)
          bullet.replaceComponent(newPosition)

        }
    }

  val inputMovementSystem: Long => Unit = * =>
    EntityManager().getEntitiesWithComponent(classOf[PlayerComponent]).foreach {
      entity =>
        inputsQueue.peek match
          case Some(command) => command(entity)
          case None          => ()
        inputsQueue = inputsQueue.pop.getOrElse(Empty)
    }

  val gravitySystem: Long => Unit = elapsedTime =>
    if (model.isGravityEnabled) {
      EntityManager()
        .getEntitiesWithComponent(
          classOf[PositionComponent],
          classOf[GravityComponent],
          classOf[VelocityComponent]
        )
        .foreach { entity =>
          val position = entity.getComponent[PositionComponent].get
          val velocity = entity.getComponent[VelocityComponent].get
          val isTouchingGround =
            position.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0
          if isTouchingGround then
            entity.replaceComponent(VelocityComponent(velocity.x, 0))
          else
            entity.replaceComponent(
              velocity + VelocityComponent(0, GRAVITY_VELOCITY * elapsedTime)
            )
        }
    }

  def updatePosition(entity: Entity, elapsedTime: Long): PositionComponent = {
    val currentPosition = entity
      .getComponent[PositionComponent]
      .getOrElse(throw new Exception("Position not found"))
    val velocity = entity
      .getComponent[VelocityComponent]
      .getOrElse(throw new Exception("Velocity not found"))

    val tmpPositionX: PositionComponent = PositionComponent(
      currentPosition.x + velocity.x * elapsedTime * 0.001,
      currentPosition.y
    )
    val tmpPositionY = PositionComponent(
      currentPosition.x,
      currentPosition.y + velocity.y * elapsedTime * 0.001
    )

    val newPositionX =
      if checkCollision(entity, tmpPositionX).isEmpty then tmpPositionX.x
      else currentPosition.x

    val newPositionY =
      if checkCollision(entity, tmpPositionY).isEmpty then tmpPositionY.y
      else
        entity.replaceComponent(JumpingComponent(false))
        currentPosition.y

    PositionComponent(
      boundaryCheck(newPositionX, model.GUIWIDTH, HORIZONTAL_COLLISION_SIZE),
      boundaryCheck(newPositionY, model.GUIHEIGHT, VERTICAL_COLLISION_SIZE)
    )
  }

  /** Checks if the entity collides with another entity in the new position
    *
    * @param entity
    *   the entity to check
    * @param newPosition
    *   the new position of the entity
    * @return
    *   the entity that collides with the entity passed as parameter
    */
  private def checkCollision(
      entity: Entity,
      newPosition: PositionComponent
  ): Option[Entity] = {
    val potentialCollisions = EntityManager().getEntitiesWithComponent(
      classOf[PositionComponent],
      classOf[SizeComponent]
    )
    val size = entity.getComponent[SizeComponent].get

    potentialCollisions.find { otherEntity =>
      if (!otherEntity.isSameEntity(entity)) {
        val overlap: OverlapType = CollisionSystem.isOverlapping(
          newPosition,
          size,
          otherEntity.getComponent[PositionComponent].get,
          otherEntity.getComponent[SizeComponent].get
        )
        overlap == OverlapType.Both
      } else false
    }
  }

  private def updateVelocity(entity: Entity): VelocityComponent = {
    val velocity = entity
      .getComponent[VelocityComponent]
      .getOrElse(throw new Exception("Velocity not found"))

    val newHorizontalVelocity =
      if -0.1 < velocity.x * FRICTION_FACTOR && velocity.x * FRICTION_FACTOR < 0.1
      then 0.0
      else velocity.x * FRICTION_FACTOR

    velocity match
      case VelocityComponent(0, 0) =>
        entity.replaceComponent(SpriteComponent(model.marcoRossiSprite))
      case VelocityComponent(_, y) if y != 0 =>
        entity.replaceComponent(
          SpriteComponent(model.marcoRossiJumpSprite)
        )
      case VelocityComponent(x, y) if x != 0 && y == 0 =>
        entity.replaceComponent(
          SpriteComponent(model.marcoRossiMoveSprite)
        )

    VelocityComponent(newHorizontalVelocity, velocity.y)
  }

  private def updateJumpingState(entity: Entity): Unit = {
    if entity.hasComponent(classOf[PlayerComponent])
    then
      val currentPosition = entity
        .getComponent[PositionComponent]
        .getOrElse(throw new Exception("Position not found"))
      val velocity = entity
        .getComponent[VelocityComponent]
        .getOrElse(throw new Exception("Velocity not found"))
      val isEntityTouchingGround = currentPosition.y + VERTICAL_COLLISION_SIZE >= model.GUIHEIGHT && velocity.y >= 0
      if (isEntityTouchingGround)
        model.isGravityEnabled = false
        entity.replaceComponent(JumpingComponent(false))
      else
        model.isGravityEnabled = true
        entity.getComponent[JumpingComponent].get
  }

  val playerPositionUpdateSystem: Long => Unit = elapsedTime =>
    EntityManager()
      .getEntitiesWithComponent(
        classOf[PositionComponent],
        classOf[VelocityComponent],
        classOf[JumpingComponent]
      )
      .foreach( entity =>

        val newPosition = updatePosition(entity, elapsedTime)
        val newVelocity = updateVelocity(entity)

        entity.replaceComponent(newVelocity)
        updateJumpingState(entity)
        entity.replaceComponent(newPosition)

      )


  val AIMoveTowardPlayerSystem: Long => Unit = elapsedTime =>
    import alice.tuprolog._
    import utilities.Scala2P._

    val prologFile = new java.io.File("src/main/resources/EnemyAI.pl")
    val engine = new Prolog()
    engine.setTheory(new Theory(new FileInputStream(prologFile)))


    val playerPosition: PositionComponent = EntityManager()
      .getEntitiesByClass(classOf[PlayerEntity])
      .head
      .getComponent[PositionComponent].get


    EntityManager()
      .getEntitiesWithComponent(
        classOf[AIComponent]
      )
      .foreach( entity =>
        val enemyPosition = entity.getComponent[PositionComponent].get
        //val randomDouble = scala.util.Random.nextDouble()

        val query = new Struct("move_toward_player",
          0.4,
          (playerPosition.x, playerPosition.y),
          (enemyPosition.x, enemyPosition.y),
          new Var(),
          new Var()
        )

        try {
          //Facendo l'update della posizione dell'enemy.
          val s = engine.solve(query).getSolution

          print("NEW_ENEMY_X: " + extractTerm(s, 3) + "\n") //NEW_ENEMY_X
          print("NEW_ENEMY_Y: " + extractTerm(s, 4) + "\n") //NEW_ENEMY_Y

          val newEnemyX = extractTerm(s, 3)
          val newEnemyY = extractTerm(s, 4)

/*
          val newEnemyVelocity = VelocityComponent(newEnemyX, newEnemyY)
          entity.replaceComponent(newEnemyVelocity)*/

          /*
          val newEnemyPosition = PositionComponent(x = newEnemyX , y = newEnemyY )
          entity.replaceComponent(newEnemyPosition)*/

        } catch {
          case e: Exception => e match
            case e: NoSolutionException => println ("Prolog query failed: No.")
            case e: MalformedGoalException => println ("Prolog query failed: Malformed.")
            case e: NoMoreSolutionException => println ("Prolog query failed: No more solutions.")
            case _ => println("Raised exception: " + e)
        }
      )