package model.engine

trait GameEngine:

  /** Advances the game engine by one tick.
    *
    * This will update all game logic and render the next frame. Should be
    * called every frame.
    *
    * @example
    *   ```
    *   gameEngine.tick()
    *   ```
    */
  def tick(elapsedTime: Long): Unit
