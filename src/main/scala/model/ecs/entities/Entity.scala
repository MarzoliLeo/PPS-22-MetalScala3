package model.ecs.entities

import java.util.UUID
import model.ecs.components.Component
import model.ecs.observer.Observable

trait Entity() extends Observable[Component]:
  private final type ComponentType = Class[_ <: Component]
  private var signature: Map[ComponentType, Component] = Map()
  val id: UUID = UUID.randomUUID()


  def addComponent(component: Component): Entity = {
    signature = signature + (component.getClass -> component)
    notifyObservers(component)
    this
  }

  def removeComponent(componentType: ComponentType): Entity = {
    signature.get(componentType).foreach { component =>
      signature = signature - componentType
      notifyObservers(component)
    }
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

  def isSameEntity(entity: Entity): Boolean = entity.id == id