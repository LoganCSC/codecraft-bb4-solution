package experiment1

import cwinter.codecraft.core.api._
import cwinter.codecraft.util.maths.Vector2
import Global._
import scala.util.Random



class Harvester(mothership: Mothership) extends DroneController {

  override def onTick(): Unit = {
    if (!isMoving && !isHarvesting) {
      if (availableStorage == 0) moveTo(mothership)
      else {
        val candidateMinerals = knownMinerals.filter(m => m.size > 0 && !m.harvested)
        if (candidateMinerals.isEmpty) {
          val randomDirection = Vector2(2 * math.Pi * Random.nextDouble())
          val targetPosition = position + 400 * randomDirection
          moveTo(targetPosition)
        } else {
          // instead of moving randomly go to known mineral
          val targetMineral = candidateMinerals.minBy(m => (m.position - position).lengthSquared)
          moveTo(targetMineral)
          knownMinerals = candidateMinerals - targetMineral
          println("new num minerals = " + knownMinerals.size)
        }
      }
    }
  }

  override def onMineralEntersVision(mineral: MineralCrystal) = {
    if (mineral.size > 0 && !mineral.harvested) {
      knownMinerals += mineral
      if (availableStorage > 0) moveTo(mineral)
    }
  }

  override def onDroneEntersVision(drone: Drone): Unit = {
    if (drone.isEnemy) {
      enemies += drone
    }
  }

  override def onArrivesAtMineral(mineral: MineralCrystal) = harvest(mineral)

  override def onArrivesAtDrone(drone: Drone) = giveResourcesTo(drone)

  override def onDeath(): Unit = {
    Global.harvesters -= this
  }
}

