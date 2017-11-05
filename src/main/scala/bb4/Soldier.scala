package bb4

import cwinter.codecraft.util.maths.Vector2
import bb4.Global._
import cwinter.codecraft.core.api.Drone


class Soldier extends AugmentedDroneController {

  var positionGoal: Option[Vector2] = None

  override def onTick(): Unit = {

    if (!isMoving)
      determineNextMove()
    super.onTick()
  }

  override def onDroneEntersVision(drone: Drone): Unit = {
    // if there is an enemy drone in sight, that take precedence
    if (drone.isEnemy) {
      fightOrFlight(drone)
    }
    super.onDroneEntersVision(drone)
  }

  override def onArrivesAtPosition(): Unit = {
    if (positionGoal.isDefined && (position - positionGoal.get).length < 3)
      positionGoal = None
    message = "arrived"
  }

  override def onArrivesAtDrone(drone: Drone): Unit = {
    message = "arrived at drone"
  }

  private def determineNextMove(): Unit = {
    val closestEnemy = getClosestEnemy(position)

    if (closestEnemy.isDefined) {
      val e = closestEnemy.get
      if (!e.isDead && e.isVisible) {
        fightOrFlight(e)
      } else if (e.lastKnownPosition != position) {
        moveTo(e.lastKnownPosition)
        positionGoal = Some(e.lastKnownPosition)
        message = "moving toward closer enemy: " + e.lastKnownPosition
      }
    }
    if (!isMoving) {
      moveSomewhere()
    }
  }

  private def fightOrFlight(enemy: Drone): Unit = {
    if (missileCooldown <= 0 && isInMissileRange(enemy)) {
      fireMissilesAt(enemy)
    }
    if (inTrouble()) {
      flee(enemy)
    } else {
      moveInDirection(enemy.position - this.position)
      message = "moving toward weaker enemy"
    }
  }

  override def moveSomewhere(): Unit = {
    if (positionGoal.isEmpty) {
      positionGoal = Some(closestUnvisitedPosition(position))
      message = "toward next unvisited"
    } else {
      message = "toward prev unvisited"
    }
    moveTo(positionGoal.get)
  }


  override def onSpawn(): Unit = {
    Global.nSoldiers += 1
  }

  override def onDeath(): Unit = {
    Global.nSoldiers -= 1
  }
}

