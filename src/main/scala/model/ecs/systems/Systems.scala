package model.ecs.systems

import javafx.scene.input.KeyCode
import model.*
import model.ecs.components.*
import model.ecs.entities.*
import model.ecs.entities.environment.BoxEntity
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.weapons.{BulletEntity, MachineGunEntity, WeaponEntity}
import model.ecs.systems.CollisionChecker.{boundaryCheck, getCollidingEntity, isOutOfHorizontalBoundaries}
import model.ecs.systems.PositionUpdateSystem.getUpdatedPosition
import model.event.Event
import model.event.observer.Observable
import model.input.commands.*
import model.utilities.Empty

import java.io.FileInputStream
import java.util.{Timer, TimerTask}



object Systems extends Observable[Event]:

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


  val AISystem: Long => Unit = elapsedTime => {
    import alice.tuprolog._
    import utilities.Scala2P._
    import model.*

    val prologFile = new java.io.File("src/main/resources/EnemyAI.pl")
    val engine = new Prolog()
    engine.setTheory(new Theory(new FileInputStream(prologFile)))

    val playerPosition: PositionComponent = EntityManager()
      .getEntitiesByClass(classOf[PlayerEntity])
      .head
      .getComponent[PositionComponent].get


    EntityManager()
      .getEntitiesWithComponent(classOf[AIComponent])
      .foreach(entity => {
        val enemyPosition = entity.getComponent[PositionComponent].get
        val currentVelocity = entity.getComponent[VelocityComponent].get

        val daemonThread = new Thread(new Runnable {
          override def run(): Unit = {
            val randomInt = scala.util.Random.nextInt(3) + 1

            val query = new Struct("move_toward_player",
              randomInt,
              (playerPosition.x, playerPosition.y),
              (enemyPosition.x, enemyPosition.y),
              new Var()
            )

            try {
              val s = engine.solve(query).getSolution

              if (randomInt == 3) {
                Command.shoot(entity)
              } else {
                val newEnemyX = extractTerm(s, 3)

                val newEnemyVelocity = VelocityComponent(
                  currentVelocity.x + (enemyPosition.x - newEnemyX) * elapsedTime * 0.001,
                  currentVelocity.y
                )

                newEnemyVelocity match {
                  case VelocityComponent(x, _) if x > 0 => entity.replaceComponent(DirectionComponent(LEFT))
                  case _ => entity.replaceComponent(DirectionComponent(RIGHT))
                }

                entity.replaceComponent(newEnemyVelocity)

                val newEnemyPosition = PositionComponent(x = newEnemyX, y = enemyPosition.y)
                entity.replaceComponent(newEnemyPosition)
              }
            } catch {
              case e: Exception => e match {
                case e: NoSolutionException => println("Prolog query failed: No.")
                case e: MalformedGoalException => println("Prolog query failed: Malformed.")
                case e: NoMoreSolutionException => println("Prolog query failed: No more solutions.")
                case _ => println("Raised exception: " + e)
              }
            }
          }
        })

        AItimeElapsedSinceLastExecution += AIexecutionSpeed

        //println("timeElapsedSinceLastExecution: " + AItimeElapsedSinceLastExecution)

        if (AItimeElapsedSinceLastExecution >= AIexecutionInterval) {
          daemonThread.setDaemon(true)
          daemonThread.start()
          AItimeElapsedSinceLastExecution = 0
        }
      })
  }




