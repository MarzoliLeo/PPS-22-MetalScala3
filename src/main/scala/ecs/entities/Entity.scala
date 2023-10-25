package ecs.entities

import ecs.Component

trait Entity(components: Component*) {
  private final type ComponentType = Class[_ <: Component]

  private var signature: Map[ComponentType, Component] = Map()

  components.foreach(addComponent)

  private def addComponent(component: Component): Entity = {
    signature = signature + (component.getClass -> component)
    this
  }

  def removeComponent(componentType: ComponentType): Entity = {
    signature = signature - componentType
    this
  }

  def replaceComponent(component: Component): Entity = {
    removeComponent(component.getClass)
    addComponent(component)
    this
  }

  def getComponent(componentType: ComponentType): Component = {
    if (hasComponent(componentType)) {
      signature(componentType)
    } else {
      throw new IllegalArgumentException(
        s"entity does not contain an instance of $componentType"
      )
    }
  }

  def hasComponent(componentType: ComponentType): Boolean =
    signature.keys.exists(k => k == componentType)
}