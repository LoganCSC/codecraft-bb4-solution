package bb4

import cwinter.codecraft.core.api._
import cwinter.codecraft.util.maths.Vector2
import Global._
import scala.util.Random



class Harvester(mothership: Mothership) extends AugmentedDroneController {

  /** The currently harvested mineral, if any */
  var currentMineral: Option[MineralCrystal] = None
  var message = ""
  var fleeingTicks: Int = 0
  var fleeing: Boolean = false
  var goalPosition: Option[Vector2] = None

  override def onTick(): Unit = {

    val nearbyEnemy = dronesInSight.find(d => d.isEnemy && d.missileBatteries > 0)
    if (nearbyEnemy.isDefined) {
      val dir = (position - nearbyEnemy.get.position).orientation
      moveInDirection(dir)  // flee
      fleeing = true
      message = "fleeing!"
    } else {
      if (availableStorage == 0) {
        moveTo(mothership)
        message = "returning to mother"
      }
      else if (!isHarvesting) {
        if (currentMineral.nonEmpty) {
          claimedMinerals -= currentMineral.get
          currentMineral = None
          message = "done harvesting"
        }
        if (!isMoving) moveSomewhere()
      }
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
    }

    showText(message)
  }

  private def moveSomewhere() = {
    val closestMineral = getClosestAvailableMineral(position)
    if (closestMineral.isDefined) {
      moveTo(closestMineral.get)
      message = "moving toward mineral at " + closestMineral.get.position
    } else {
      if (goalPosition.isEmpty) {
        goalPosition = Some(closestUnvisitedPosition(position))
      }
      moveTo(goalPosition.get)
    }
  }

  override def onArrivesAtPosition(): Unit = {
    if (goalPosition.isDefined && position == goalPosition.get) {
      goalPosition = None
    }
  }

  override def onArrivesAtMineral(mineral: MineralCrystal) = {
    harvest(mineral)
    currentMineral = Some(mineral)
    message = "harvesting"
    claimedMinerals += mineral
  }

  override def onArrivesAtDrone(drone: Drone) = {
    if (drone.isInstanceOf[Mothership]) {
      giveResourcesTo(drone)
      message = "transerring to mother"
    }
  }

  override def onSpawn(): Unit = {
    harvesters += this
  }

  override def onDeath(): Unit = {
    harvesters -= this
  }
}

