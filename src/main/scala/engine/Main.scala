package engine

object Main {
  def main(args: Array[String]): Unit = {
    val gameEngine: Engine = Engine.apply()
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