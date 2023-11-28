package model.ecs.systems

trait System

/**
 * A system that does not need to know the elapsed time since the last update.
 */
trait SystemWithoutTime extends System:
  def update(): Unit

/**
 * A system that needs to know the elapsed time since the last update.
 */
trait SystemWithElapsedTime extends System:
  def update(elapsedTime: Long): Unit




