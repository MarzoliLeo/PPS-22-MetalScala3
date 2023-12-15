package model.ecs.entities

import model.ecs.collision_handlers.CollisionHandler
import model.ecs.components.Component

import java.util.UUID
import scala.reflect.ClassTag

trait Entity extends CollisionHandler:

  val id: UUID = UUID.randomUUID()
  private var signature: Set[Component] = Set()

  def replaceComponent(component: Component): Entity =
    if hasComponent(component.getClass) then
      removeComponent(component.getClass)
      addComponent(component)
      this
    else this

  def addComponent(component: Component): Entity =
    signature += component
    this

  def removeComponent(componentType: Class[_ <: Component]): Entity =
    signature = signature.filterNot(c => c.getClass == componentType)
    this

  def hasComponent(componentType: Class[_ <: Component]): Boolean =
    signature.exists(c => c.getClass == componentType)

  def getComponent[T <: Component: ClassTag]: Option[T] =
    signature.collectFirst {
      case component
          if summon[ClassTag[T]].runtimeClass.isInstance(component) =>
        component.asInstanceOf[T]
    }

  def isSameEntity(entity: Entity): Boolean =
    entity.id == id

