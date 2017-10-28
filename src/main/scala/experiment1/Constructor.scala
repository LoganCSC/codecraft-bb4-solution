package experiment1

import Global.RND
import cwinter.codecraft.util.maths.Vector2

import scala.util.Random


class Constructor extends Mothership {

  override def onTick(): Unit = {
    if (!isConstructing) {

      val r = RND.nextDouble()
      if (r < 0.5) {
        val h = new Harvester(Global.getRandomMother)
        buildDrone(h, storageModules = 2)
        Global.harvesters += h
      }
      else  {
        buildDrone(new Soldier, missileBatteries = 2, shieldGenerators = 1)
        Global.nSoldiers += 1
      }

      if (!isMoving) {
        val randomDirection = Vector2(2 * math.Pi * Random.nextDouble())
        val targetPosition = position + 100 * randomDirection
        moveTo(targetPosition)
      }
    }
  }
}
