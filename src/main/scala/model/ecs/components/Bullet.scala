package model.ecs.components

sealed trait Bullet

case class StandardBullet() extends Bullet
case class EnemyBullet() extends Bullet
case class MachineGunBullet() extends Bullet
