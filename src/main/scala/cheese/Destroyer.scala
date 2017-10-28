package cheese

import cwinter.codecraft.core.api.{Drone, DroneController, MineralCrystal}
import cwinter.codecraft.util.maths.Vector2

class Destroyer(targetPos: Vector2) extends DroneController {
  override def onSpawn(): Unit = {
    moveTo(targetPos)
  }
  override def onMineralEntersVision(mineralCrystal: MineralCrystal): Unit = ()
  override def onTick(): Unit = {
    for (d <- dronesInSight.find(d => d.isEnemy && isInMissileRange(d))) {
      fireMissilesAt(d)
    }
  }
  override def onArrivesAtPosition(): Unit = ()
  override def onDeath(): Unit = ()
  override def onDroneEntersVision(drone: Drone): Unit = ()
}
