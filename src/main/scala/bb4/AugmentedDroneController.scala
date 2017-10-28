package bb4

import bb4.Global.{enemies, knownMinerals}
import cwinter.codecraft.core.api.{Drone, DroneController, MineralCrystal}


class AugmentedDroneController extends DroneController {


  override def onMineralEntersVision(mineral: MineralCrystal) = {
    if (mineral.size > 0 && !mineral.harvested)
      knownMinerals += mineral
  }

  override def onDroneEntersVision(drone: Drone): Unit = {
    if (drone.isEnemy) {
      enemies += drone
    }
  }
}
