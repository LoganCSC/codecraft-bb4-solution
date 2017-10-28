package bb4

import cwinter.codecraft.core.api._

import scala.util.Random
import Global._
import cwinter.codecraft.util.maths.Vector2


class Mothership extends AugmentedDroneController {

  mothers :+= this

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
      harvesters += h
    }
    else if (nSoldiers < 2) {
      buildDrone(new Soldier, missileBatteries = 3, shieldGenerators = 2)
      nSoldiers += 1
    }
    else {
      val newMother = new Constructor
      buildDrone(newMother, missileBatteries = 1, shieldGenerators = 1, constructors = 2, storageModules = 2)
      mothers :+= newMother
      maxHarvesters += 2
    }
  }

  override def onDeath() {
    mothers = mothers.filter(_ != this)
  }

}

