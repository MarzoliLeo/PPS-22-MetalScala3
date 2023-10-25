package engine

import ecs.entities.EntityManager
import ecs.systems.SystemManager

object Main {
  def main(args: Array[String]): Unit = {
    val entityManager = EntityManager()
    val systemManager: SystemManager = SystemManager(entityManager)
    val gameEngine: Engine = Engine(systemManager)
    //Prova 1.
    gameEngine.start()
    gameEngine.pause()
    gameEngine.resume()
    gameEngine.stop()
    print("--------------------------------------------------------------")
/*   //Prova 2.
    gameEngine.start()
    gameEngine.pause()
    gameEngine.pause()
    gameEngine.resume()
    gameEngine.stop()
    print("--------------------------------------------------------------")
   //Prova 3.
    gameEngine.start()
    gameEngine.resume()
    gameEngine.pause()
    gameEngine.stop()*/
  }
}