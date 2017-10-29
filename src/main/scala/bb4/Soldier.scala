package bb4

import cwinter.codecraft.util.maths.Vector2
import bb4.Global._


class Soldier extends AugmentedDroneController {

  var msg = ""
  var positionGoal: Option[Vector2] = None

  override def onTick(): Unit = {

    val closestEnemy = getClosestEnemy(position)

    if (closestEnemy.isDefined) {
      val e = closestEnemy.get
      if (e.isVisible && isInMissileRange(e)) {
        fireMissilesAt(e)
        moveTo(e)
      } else if (e.lastKnownPosition != position) {
        val vec = e.lastKnownPosition - position
        val distToNewGoal = vec.lengthSquared
        val distToLastGoal = if (positionGoal.isEmpty) Double.PositiveInfinity
                             else (positionGoal.get - position).lengthSquared
        if (distToNewGoal < distToLastGoal) {
          moveTo(e.lastKnownPosition)
          //positionGoal = Some(e.lastKnownPosition)
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
    if (positionGoal.isDefined && position == positionGoal.get)
      positionGoal = None
    msg = "arrived"
    //moveSomewhere()
  }

  private def moveSomewhere(): Unit = {
    val r = RND.nextDouble()
    if (r < 0.2) {
      // move to protect harvesters
      val closeHarveseter = getClosestHarvester(position)
      if (positionGoal.isEmpty &&closeHarveseter.isDefined && !alliesInSight.contains(closeHarveseter.get)) {
        positionGoal = Some(closeHarveseter.get.position)
        moveTo(positionGoal.get)
        msg = "moving toward harvester"
      }
    } else {
      if (positionGoal.isEmpty) {
        positionGoal = Some(nextUnvisitedPosition())
        msg = "toward next unvisited"
      } else {
        msg = "toward prev unvisited"
      }
      moveTo(positionGoal.get)
    }
  }

  override def onSpawn(): Unit = {
    Global.nSoldiers += 1
  }

  override def onDeath(): Unit = {
    Global.nSoldiers -= 1
  }
}

