package model.ecs.systems

import model.ecs.entities.EntityManager
import model.ecs.entities.enemies.EnemyEntity

final case class GameOverSystem() extends SystemWithoutTime {
    override def update(): Unit = {
        if (EntityManager.getEntitiesByClass(classOf[EnemyEntity]).isEmpty) {
            val thread = Thread(() => {
                Thread.sleep(2000)
                println("Game Over")
                System.exit(0)
            })
            thread.start()
        }
    }
}