package model.input

import javafx.scene.input.KeyEvent

trait InputHandler:
  def handleInput(keyEvent: KeyEvent): Unit
