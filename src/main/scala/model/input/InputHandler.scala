package model.input

import javafx.scene.input.KeyEvent
import model.ecs.entities.Entity

trait InputHandler:
  def handleInput(command: Entity => Unit): Unit
