package view

import javafx.scene.layout.Pane

trait View:
  def getContent: Pane

object View:
  given Conversion[View, Pane] = _.getContent

