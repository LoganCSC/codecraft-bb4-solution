package bb4

import scala.util.Random
import Global._
import cwinter.codecraft.util.maths.Vector2


class Mothership extends AugmentedDroneController {

  mothers += this

  override def onTick(): Unit = {

    if (!isConstructing) {
      makeNewDrone()
    }
    if (!isMoving) {
      val randomDirection = Vector2(2 * math.Pi * Random.nextDouble())
      val targetPosition = position + 50 * randomDirection
      println("moving mothership to " + targetPosition)
      moveTo(targetPosition)
    }
  }

  def makeNewDrone(): Unit = {
    if (harvesters.size < maxHarvesters) {
      val h = new Harvester(getRandomMother)
      buildDrone(h, storageModules = 1)
    }
    else if (nSoldiers < 2) {
      buildDrone(new Soldier, missileBatteries = 2, shieldGenerators = 1, engines = 1)
    }
    else {
      val r = RND.nextDouble()
      if (r < 0.5) {
        val newMother = new Constructor
        buildDrone(newMother, missileBatteries = 1, shieldGenerators = 1, constructors = 2, storageModules = 2)
        mothers += newMother
        maxHarvesters += 1
      } else {
        buildDrone(new Soldier, missileBatteries = 2, shieldGenerators = 1, engines = 1)
      }
    }
  }

  override def onDeath() {
    mothers -= this
  }
}

