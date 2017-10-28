package experiment1

import cwinter.codecraft.core.api._
import cwinter.codecraft.util.maths.Vector2
import experiment1.Global.enemies

import scala.util.Random


class Soldier extends DroneController {

  override def onTick(): Unit = {
    val droneEnemies = dronesInSight.filter(_.isEnemy)
    if (droneEnemies.nonEmpty) {
      enemies ++= droneEnemies
      println("num enemies = " + enemies.size)
    }

    enemies.headOption match {
      case Some(enemy) =>
        moveTo(enemy.lastKnownPosition)
        if (enemy.isVisible && missileCooldown <= 0 && isInMissileRange(enemy))
          fireMissilesAt(enemy)
      case None =>
        if (!isMoving) {
          enemies = enemies.filter(!_.isDead)
          if (enemies.nonEmpty) {
            moveTo(enemies.head)
            //enemies = enemies.tail
          } else if (Global.harvesters.nonEmpty) {
            //println("moving to h = " + Global.harvesters.head)
            moveTo(Global.harvesters.head)
          } else {
            val randomDirection = Vector2(2 * math.Pi * Random.nextDouble())
            val targetPosition = position + 300 * randomDirection
            moveTo(targetPosition)
          }
        }
    }
  }

  override def onDroneEntersVision(drone: Drone): Unit = {
    if (drone.isEnemy) {
      enemies += drone
    }
  }

  override def onDeath(): Unit = {
    Global.nSoldiers -= 1
  }
}

