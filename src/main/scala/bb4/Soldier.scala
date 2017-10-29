package bb4

import cwinter.codecraft.util.maths.Vector2
import bb4.Global._
import scala.util.Random


class Soldier extends AugmentedDroneController {

  override def onTick(): Unit = {

    val closestEnemy = getClosestEnemy(position)

    if (closestEnemy.isDefined) {  // !moving?
      val e = closestEnemy.get
      if (position != e.lastKnownPosition)
        moveTo(e.lastKnownPosition)
    }
    for (d <- dronesInSight.find(d => d.isEnemy && isInMissileRange(d))) {
      fireMissilesAt(d)
    }

    if (!isMoving) {
      val closeHarveseter = getClosestHarvester(position)
      if (closeHarveseter.isDefined && !alliesInSight.contains(closeHarveseter.get)) { // protect harvesters
        moveTo(closeHarveseter.get) // sometimes npe
      } else {
        val randomDirection = Vector2(2 * math.Pi * Random.nextDouble())
        val targetPosition = position + 300 * randomDirection
        moveTo(targetPosition)
      }
    }
  }

  override def onSpawn(): Unit = {
    Global.nSoldiers += 1
  }

  override def onDeath(): Unit = {
    Global.nSoldiers -= 1
  }
}

