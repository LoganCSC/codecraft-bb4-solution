package bb4

import Global._
import cwinter.codecraft.util.maths.Vector2

import scala.util.Random


class Constructor extends Mothership {

  override def onTick(): Unit = {
    if (!isConstructing) {
      val r = RND.nextDouble()
      if (r < 0.5) {
        val h = new Harvester(Global.getRandomMother)
        buildDrone(h, storageModules = 2)
      }
      else  {
        buildDrone(new Soldier, missileBatteries = 2, shieldGenerators = 1)
      }

      if (!isMoving) {
        moveTo(nextUnvisitedPosition())
      }
    }
  }

  override def onSpawn(): Unit = {
    mothers += this
  }

  override def onDeath(): Unit = {
    mothers -= this
  }
}
