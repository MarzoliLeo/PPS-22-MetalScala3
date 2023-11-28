package model.ecs.systems

import model.ecs.entities.EntityManager

import model.*
import model.ecs.components.*
import model.ecs.entities.player.PlayerEntity
import model.input.commands.Command
import java.io.FileInputStream

trait AISystem extends SystemWithElapsedTime

private case class AISystemImpl() extends AISystem :
  override def update(elapsedTime: Long): Unit =
    import alice.tuprolog.*
    import utilities.Scala2P.*

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
                  currentVelocity.x + (enemyPosition.x - newEnemyX) * elapsedTime ,
                  currentVelocity.y
                )

                newEnemyVelocity match
                  case VelocityComponent(x,0) if x > 0 => entity.replaceComponent(DirectionComponent(RIGHT))
                  case VelocityComponent(x,0) if x < 0=> entity.replaceComponent(DirectionComponent(LEFT))
                  case _ => ()

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

object AISystem:
  def apply(): AISystem = AISystemImpl()