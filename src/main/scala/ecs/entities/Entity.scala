package ecs.entities

import ecs.components.Component

trait Entity(components: Component*) {
  private final type ComponentType = Class[_ <: Component]
  private var signature: Map[ComponentType, Component] = Map()

  components.foreach(addComponent)

  def addComponent(component: Component): Entity = {
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

  // todo: add type parameter to make it more idiomatic and type safe
  def getComponent(componentType: ComponentType): Option[Component] =
    signature.get(componentType)

  def hasComponent(componentType: ComponentType): Boolean =
    signature.contains(componentType)
}