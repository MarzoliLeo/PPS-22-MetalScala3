package model.input

import javafx.scene.input.{KeyCode, KeyEvent}
import jdk.internal.util.xml.impl.Input
import model.inputsQueue

trait BasicInputHandler extends InputHandler:
  override def handleInput(keyEvent: KeyEvent): Unit = {
    val newStack = inputsQueue.push(keyEvent.getCode)
    //println("Current stack is: " + newStack.toString)
    inputsQueue = newStack
  }