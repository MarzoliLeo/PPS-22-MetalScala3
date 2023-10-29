package view

import javafx.scene.layout.{FlowPane, Pane}

trait View:
  def root: Pane

object View:
  given Conversion[View, Pane] = _.root

