package ecs

trait Entity:
  final protected type ComponentType = Class[_ <: Component]
  def addComponent(component: Component): Entity
  def removeComponent(componentType: ComponentType): Entity
  def replaceComponent(component: Component): Entity
  def hasComponent(componentType: ComponentType): Boolean
  def getComponent(componentType: ComponentType): Option[Component]

object Entity:

  def apply(components: Component*): Entity =
    val entity = new EntityImpl()
    components.foreach(c => entity.addComponent(c))
    entity
    
  private class EntityImpl() extends Entity:
    private var signature: Map[ComponentType, Component] = Map()

    override def addComponent(component: Component): Entity =
      signature = signature + (component.getClass -> component)
      this

    override def removeComponent(componentType: ComponentType): Entity =
      signature = signature - componentType
      this

    override def replaceComponent(component: Component): Entity =
      removeComponent(component.getClass)
      addComponent(component)
      this

    override def getComponent(componentType: ComponentType): Option[Component] =
      signature.get(componentType)


    override def hasComponent(componentType: ComponentType): Boolean =
      signature.keys.exists( k => k == componentType)
