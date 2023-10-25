package ecs.systems

import ecs.entities.{Entity, EntityManager}

trait System:
  def update(entityManager: EntityManager): Unit
