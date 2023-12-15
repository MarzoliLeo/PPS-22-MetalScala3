package model.ecs.systems

import model.ecs.entities.EntityManager
import model.ecs.systems.{SystemManager, SystemWithElapsedTime, SystemWithoutTime}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

object TestSystemWithoutTime extends SystemWithoutTime {
  def update(): Unit = ()
}

object TestSystemWithElapsedTime extends SystemWithElapsedTime {
  def update(elapsedTime: Long): Unit = ()
}

class SystemManagerTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  var manager: SystemManager = _

  override def beforeEach(): Unit = {
    manager = SystemManager(EntityManager)
    super.beforeEach() // To be stackable, must call super.beforeEach()
  }

  "A SystemManager" should "add a system without time" in {
    manager.addSystem(TestSystemWithoutTime)
    manager.systems should contain(TestSystemWithoutTime)
  }

  it should "add a system with elapsed time" in {
    manager.addSystem(TestSystemWithElapsedTime)
    manager.systems should contain(TestSystemWithElapsedTime)
  }

  it should "remove a system without time" in {
    manager.addSystem(TestSystemWithoutTime)
    manager.removeSystem(TestSystemWithoutTime)
    manager.systems should not contain TestSystemWithoutTime
  }

  it should "remove a system with elapsed time" in {
    manager.addSystem(TestSystemWithElapsedTime)
    manager.removeSystem(TestSystemWithElapsedTime)
    manager.systems should not contain TestSystemWithElapsedTime
  }
}