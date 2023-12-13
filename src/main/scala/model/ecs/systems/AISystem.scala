package model.ecs.systems

import alice.tuprolog.{MalformedGoalException, NoMoreSolutionException, NoSolutionException, Prolog, Struct, Term, Theory, Var}
import javafx.scene.Node
import model.*
import model.ecs.components.*
import model.ecs.entities.player.PlayerEntity
import model.ecs.entities.{Entity, EntityManager}
import model.input.commands.Command
import model.utilities.Scala2P.*

import java.io.FileInputStream
import java.util.UUID
import java.util.concurrent.{ExecutorService, Executors}

trait AISystem extends SystemWithElapsedTime {
  private val engine = new Prolog()
  private val threadPool: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_ENEMIES)
  private val prologFile = new java.io.File("src/main/resources/EnemyAI.pl")
  engine.setTheory(new Theory(new FileInputStream(prologFile)))

  @volatile private var stopped: Boolean = false

  override def update(elapsedTime: Long): Unit = {
    EntityManager.getEntitiesByClass(classOf[PlayerEntity]) match {
      case Nil => // player morto.
      case playerEntity :: _ =>
        val playerPosition = playerEntity.getComponent[PositionComponent].get

        AItimeElapsedSinceLastExecution += AIexecutionSpeed

        if (AItimeElapsedSinceLastExecution >= AIexecutionInterval) {
          EntityManager
            .getEntitiesWithComponent(classOf[AIComponent])
            .foreach(entity => {
              val enemyPosition = entity.getComponent[PositionComponent].get
              val enemyVelocity = entity.getComponent[VelocityComponent].get

              val task = new AISystemTask(engine, entity, playerPosition, enemyPosition, enemyVelocity, elapsedTime)
              threadPool.submit(task)
            })

          AItimeElapsedSinceLastExecution = 0
        }
    }
  }
}

class AISystemTask(
                    engine: Prolog,
                    entity: Entity,
                    playerPosition: PositionComponent,
                    enemyPosition: PositionComponent,
                    enemyVelocity: VelocityComponent,
                    elapsedTime: Long
                  ) extends Runnable {

  override def run(): Unit = {
    try {
      val randomInt = scala.util.Random.nextInt(3) + 1

      val query = new Struct(
        "move_toward_player",
        randomInt,
        (playerPosition.x, playerPosition.y),
        (enemyPosition.x, enemyPosition.y),
        new Var()
      )

      val solution = engine.solve(query).getSolution

      if (randomInt == 3) {
        Command.shoot(entity)
      } else {
        handleMoveSolution(solution, entity, enemyPosition, enemyVelocity, elapsedTime)
      }
    } catch {
      case e: Exception =>
        handleException(e)
    }
  }

  private def handleMoveSolution(solution: Term, entity: Entity, enemyPosition: PositionComponent, enemyVelocity: VelocityComponent, elapsedTime: Long): Unit = {
    try {
      val prologPositionX = extractTerm(solution, 3)
      val newEnemyVelocity = VelocityComponent(
        enemyVelocity.x + (enemyPosition.x - prologPositionX) * elapsedTime,
        enemyVelocity.y
      )

      newEnemyVelocity match {
        case VelocityComponent(x, 0) if x > 0 =>
          entity.replaceComponent(DirectionComponent(RIGHT))
        case VelocityComponent(x, 0) if x < 0 =>
          entity.replaceComponent(DirectionComponent(LEFT))
        case _ => ()
      }

      entity.replaceComponent(CollisionComponent())
      entity.replaceComponent(newEnemyVelocity)

      val proposedPosition = PositionComponent(prologPositionX, enemyPosition.y)
      val handledPosition: Option[PositionComponent] = entity.handleCollision(proposedPosition)

      handledPosition.foreach(entity.replaceComponent)
    } catch {
      case e: Exception =>
        handleException(e)
    }
  }

  private def handleException(e: Exception): Unit = {
    e match {
      case _: NoSolutionException =>
        println("Prolog query failed: No.")
      case _: MalformedGoalException =>
        println("Prolog query failed: Malformed.")
      case _: NoMoreSolutionException =>
        println("Prolog query failed: No more solutions.")
      case _ =>
        println("Raised exception: " + e)
    }
  }
}

object AISystem {
  def apply(): AISystem = new AISystem {}
}
