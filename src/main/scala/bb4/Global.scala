package bb4

import cwinter.codecraft.core.api.{Drone, MineralCrystal}
import cwinter.codecraft.util.maths.Vector2

import scala.util.Random

/** Maintain global state that can be queried by any drone */
object Global {

  val RND = new Random()

  /** Crystals that we have seen so far */
  var knownMinerals: Set[MineralCrystal] = Set()

  /** Of those we have see, these are the ones that are actively being harvested */
  var claimedMinerals: Set[MineralCrystal] = Set()

  var mothers: Set[Mothership] = Set()
  var enemies: Set[Drone] = Set()
  var harvesters: Set[Harvester] = Set()
  var maxHarvesters = 3  // this can change
  var nSoldiers = 0

  def getClosestAvailableMineral(pos: Vector2): Option[MineralCrystal] = {
    val availableMinerals = (knownMinerals -- claimedMinerals).filter(!_.harvested)
    if (availableMinerals.isEmpty) {
      None
    } else {
      val closest = availableMinerals.minBy(m => (pos - m.position).lengthSquared)
      claimedMinerals += closest
      println("claimed: " + claimedMinerals.size + " known: " + knownMinerals.size)
      Some(closest)
    }
  }

  def getClosestHarvester(pos: Vector2): Option[Drone] = {
    val candidates = harvesters.filter(!_.isDead)
    if (candidates.isEmpty) {
      None
    } else {
      val closest = candidates.minBy(m => (pos - m.lastKnownPosition).lengthSquared)
      Some(closest)
    }
  }

  def getClosestEnemy(pos: Vector2): Option[Drone] = {
    val candidates = enemies.filter(!_.isDead)
    if (candidates.isEmpty) {
      None
    } else {
      val closest = candidates.minBy(m => (pos - m.lastKnownPosition).lengthSquared)
      Some(closest)
    }
  }

  def getRandomMother: Mothership = {
    mothers.toList(RND.nextInt(mothers.size))
    //mothers(RND.nextInt(mothers.length))
  }
}
