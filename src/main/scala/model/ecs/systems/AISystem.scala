package model.ecs.systems

import javafx.scene.Node
import model.ecs.entities.{Entity, EntityManager}
import java.util.concurrent.{Executors, ExecutorService}
import model.*
import model.ecs.components.*
import model.ecs.entities.player.PlayerEntity
import model.input.commands.Command

import java.io.FileInputStream
import java.util.UUID

trait AISystem extends SystemWithElapsedTime

private case class AISystemImpl() extends AISystem {
  private val threadPool: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_ENEMIES) //Numero di Thread pari al numero di nemici.

  override def update(elapsedTime: Long): Unit = {
    import alice.tuprolog.*
    import utilities.Scala2P.*

    val prologFile = new java.io.File("src/main/resources/EnemyAI.pl")
    val engine = new Prolog()
    engine.setTheory(new Theory(new FileInputStream(prologFile)))

    val playerPosition: PositionComponent = EntityManager()
      .getEntitiesByClass(classOf[PlayerEntity])
      .head
      .getComponent[PositionComponent].get

    AItimeElapsedSinceLastExecution += AIexecutionSpeed

    if (AItimeElapsedSinceLastExecution >= AIexecutionInterval) {
      EntityManager()
        .getEntitiesWithComponent(classOf[AIComponent])
        .foreach(entity => {
          val enemyPosition = entity.getComponent[PositionComponent].get
          val enemyVelocity = entity.getComponent[VelocityComponent].get

          val task = new Runnable {
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
                }
                else {

                  val prologPositionX = extractTerm(s, 3)

                  //Velocity.
                  val newEnemyVelocity = VelocityComponent(
                    enemyVelocity.x + (enemyPosition.x - prologPositionX) * elapsedTime,
                    enemyVelocity.y
                  )

                  //Direction.
                  newEnemyVelocity match {
                    case VelocityComponent(x, 0) if x > 0 => entity.replaceComponent(DirectionComponent(RIGHT))
                    case VelocityComponent(x, 0) if x < 0 => entity.replaceComponent(DirectionComponent(LEFT))
                    case _ => ()
                  }

                  entity.replaceComponent(newEnemyVelocity)

                  //Position.
                  val proposedPosition = PositionComponent(prologPositionX, enemyPosition.y)
                  val handledPosition: Option[PositionComponent] = entity.handleCollision(proposedPosition)
                  handledPosition match
                    case Some(handledPosition) => entity.replaceComponent(handledPosition)
                    // keep the current position
                    case None => ()

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
          }

          threadPool.submit(task)
        })

      AItimeElapsedSinceLastExecution = 0
    }
  }
}





object AISystem {
  def apply(): AISystem = AISystemImpl()
}
