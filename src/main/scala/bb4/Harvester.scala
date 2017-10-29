package bb4

import cwinter.codecraft.core.api._
import cwinter.codecraft.util.maths.Vector2
import Global._
import scala.util.Random



class Harvester(mothership: Mothership) extends AugmentedDroneController {

  /** The currently harvested mineral, if any */
  var currentMineral: Option[MineralCrystal] = None
  var message = ""

  override def onTick(): Unit = {

    if (availableStorage == 0) {
      moveTo(mothership)
      message = "returning to mother"
    }
    if (!isHarvesting) {
      if (currentMineral.nonEmpty) {
        claimedMinerals -= currentMineral.get
        currentMineral = None
        message = "done harvesting"
      }

      if (!isMoving) {
        val closestMineral = getClosestAvailableMineral(position)
        if (closestMineral.isDefined) {
          moveTo(closestMineral.get)
          message = "moving toward mineral at " + closestMineral.get.position
        } else {
          val randomDirection = Vector2(2 * math.Pi * Random.nextDouble())
          val targetPosition = position + 400 * randomDirection
          moveTo(targetPosition)
          message = "toward  " + targetPosition
        }
      }
    }

    showText(message)
  }

  override def onArrivesAtMineral(mineral: MineralCrystal) = {
    harvest(mineral)
    //assert(isHarvesting)
    //println("harvesting")
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

