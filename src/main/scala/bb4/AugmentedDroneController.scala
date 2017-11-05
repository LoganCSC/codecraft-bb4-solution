package bb4

import bb4.Global.{enemies, knownMinerals}
import cwinter.codecraft.core.api.{Drone, DroneController, MineralCrystal}


object AugmentedDroneController {
  /** number of time steps to flee for */
  var FLEE_DURATION = 100
}

class AugmentedDroneController extends DroneController {

  protected var fleeingTicks: Int = 0
  protected var fleeing: Boolean = false
  var message = ""

  override def onMineralEntersVision(mineral: MineralCrystal) = {
    if (mineral.size > 0 && !mineral.harvested)
      knownMinerals += mineral
  }

  override def onDroneEntersVision(drone: Drone): Unit = {
    if (drone.isEnemy) {
      enemies += drone
    }
  }

  override def onTick(): Unit = {
    if (fleeing) {
      fleeingTicks += 1
      message = "fleeing " + fleeingTicks
      if (fleeingTicks > 100) {
        fleeingTicks = 0
        fleeing = false
        message = ""
        moveSomewhere()
      }
    }
    showText(message)
  }

  def moveSomewhere(): Unit = {
    // no where by default
  }

  protected def flee(nearbyEnemy: Drone): Unit = {
    val dir = (position - nearbyEnemy.position).orientation
    moveInDirection(dir)  // flee
    fleeing = true
    message = "fleeing!"
  }

  /**
    * Fight or flight
    * @return true if facing overwhelming odds
    */
  protected def inTrouble(): Boolean = {
    val enemiesInSight: Set[Drone] = dronesInSight.filter(e => e.isEnemy && e.missileBatteries > 0)
    val eMissileStr: Int = enemiesInSight.map(_.missileBatteries).sum
    val eHitPoints: Int = enemiesInSight.map(_.hitpoints).sum
    val myStrength = calcStrength(hitpoints, missileBatteries)
    val enemyStr = calcStrength(eHitPoints, eMissileStr)
    enemyStr > (myStrength -1)
  }

  private def calcStrength(hp: Int, missileBatteries: Int): Int = missileBatteries * 5 + hp
}
