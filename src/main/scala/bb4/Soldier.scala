package bb4

import cwinter.codecraft.util.maths.Vector2
import bb4.Global._
import cwinter.codecraft.core.api.Drone


class Soldier extends AugmentedDroneController {

  var msg = ""
  var positionGoal: Option[Vector2] = None

  override def onTick(): Unit = {

    val closestEnemy = getClosestEnemy(position)

    if (closestEnemy.isDefined) {
      val e = closestEnemy.get
      if (!e.isDead && e.isVisible && isInMissileRange(e)) {
        fireMissilesAt(e)
        moveTo(e)
      } else if (e.lastKnownPosition != position) {
        val vec = e.lastKnownPosition - position
        val distToNewGoal = vec.lengthSquared
        val distToLastGoal = if (positionGoal.isEmpty) Double.PositiveInfinity
                             else (positionGoal.get - position).lengthSquared
        if (distToNewGoal < distToLastGoal) {
          moveTo(e.lastKnownPosition)
          positionGoal = Some(e.lastKnownPosition)
          msg = "moving toward closer enemy: " + e.lastKnownPosition
        }
      }
    }

    if (!isMoving) {
      moveSomewhere()
    }
    showText(msg)
  }

  override def onArrivesAtPosition(): Unit = {
    if (positionGoal.isDefined && (position - positionGoal.get).length < 3)
      positionGoal = None
    msg = "arrived"
    moveSomewhere()
  }

  override def onArrivesAtDrone(drone: Drone): Unit = {
    msg = "arrived at drone"
    moveSomewhere()
  }

  private def moveSomewhere(): Unit = {
    if (positionGoal.isEmpty) {
      positionGoal = Some(nextUnvisitedPosition())
      msg = "toward next unvisited"
    } else {
      msg = "toward prev unvisited"
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

