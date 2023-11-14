package model.input


import model.ecs.entities.Entity
import model.inputsQueue

trait CommandsStackHandler extends InputHandler:
  override def handleInput(command: Entity => Unit): Unit = {
    val newStack = inputsQueue.push(command)
    inputsQueue = newStack
  }