package bb4

import Global._


class Mothership extends AugmentedDroneController {

  var moved: Boolean = false

  override def onTick(): Unit = {

    if (!isConstructing) {
      if (!moved && knownMinerals.nonEmpty) {
        moveTo(knownMinerals.head)
        moved = true
        message = "moving to mineral"
      }
      if (!isMoving) {
        makeNewDrone()
        message = "building"
      }
    }

    super.onTick()
  }


  def makeNewDrone(): Unit = {
    if (harvesters.size < maxHarvesters) {
      val h = new Harvester(getRandomMother)
      buildDrone(h, storageModules = 1)
    }
    else if (nSoldiers < 2) {
      buildDrone(new Soldier, missileBatteries = 3, shieldGenerators = 1, engines = 1)
    }
    else {
      val r = RND.nextDouble()
      if (r < 0.4) {
        val newMother = new Constructor
        buildDrone(newMother, missileBatteries = 1, shieldGenerators = 1, constructors = 2, storageModules = 2)
        mothers += newMother
        maxHarvesters += 1
      } else {
        buildDrone(new Soldier, missileBatteries = 2, shieldGenerators = 1, engines = 1)
      }
    }
  }

  override def onSpawn(): Unit = {
    Global.initialize(this)
    mothers += this
  }

  override def onDeath() {
    mothers -= this
  }
}

