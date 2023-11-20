package model.ecs.entities

import model.ecs.components.Component

import scala.reflect.ClassTag

trait Entity:

  private var signature: Set[Component] = Set()

  val id: java.util.UUID = java.util.UUID.randomUUID()

  def addComponent(component: Component): Entity =
    signature += component
    this

  def removeComponent(componentType: Class[_ <: Component]): Entity =
    signature = signature.filterNot(c => c.getClass == componentType)
    this

  def replaceComponent(component: Component): Entity =
    removeComponent(component.getClass)
    addComponent(component)
    this

  def getComponent[T <: Component: ClassTag]: Option[T] =
    signature.collectFirst {
      case component
          if summon[ClassTag[T]].runtimeClass.isInstance(component) =>
        component.asInstanceOf[T]
    }

  def hasComponent(componentType: Class[_ <: Component]): Boolean =
    signature.exists(c => c.getClass == componentType)

  def isSameEntity(entity: Entity): Boolean =
    entity.id == id

  override def toString: String =
    s"Entity(id: $id, components: $signature)"
