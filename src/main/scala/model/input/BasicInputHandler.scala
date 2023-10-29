package model.input

import javafx.scene.input.{KeyCode, KeyEvent}
import jdk.internal.util.xml.impl.Input

trait BasicInputHandler() extends InputHandler:
  override def handleInput(keyEvent: KeyEvent): Unit = keyEvent.getCode match
    case KeyCode.W => println("W")
    case KeyCode.A => println("A")
    case KeyCode.S => println("S")
    case KeyCode.D => println("D")
