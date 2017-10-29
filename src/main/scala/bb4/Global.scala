package bb4

import cwinter.codecraft.core.api.{Drone, MineralCrystal}
import cwinter.codecraft.util.maths.Vector2
import scala.util.Random
import cwinter.codecraft.core.api.GameConstants.DroneVisionRange

/** Maintain global state that can be queried by any drone */
object Global {

  val RND = new Random()
  val GRID_SIZE: Int = DroneVisionRange

  /** Crystals that we have seen so far */
  var knownMinerals: Set[MineralCrystal] = Set()

  /** Of those we have see, these are the ones that are actively being harvested */
  var claimedMinerals: Set[MineralCrystal] = Set()

  var mothers: Set[Mothership] = Set()
  var enemies: Set[Drone] = Set()
  var harvesters: Set[Harvester] = Set()
  var maxHarvesters = 3  // this can change
  var nSoldiers = 0
  var unvisitedLocations: Set[Vector2] = Set()
  var allLocations: List[Vector2] = _

  /** One time initialization */
  def initialize(mother: Mothership): Unit = {
    allLocations = genWorldLocations(mother)
  }

  private def genWorldLocations(mother: Mothership): List[Vector2] = {
    val rect = mother.worldSize
    val xDim = math.ceil(rect.width / GRID_SIZE).toInt
    val yDim = math.ceil(rect.height / GRID_SIZE).toInt
    val xOffset = (0.5 * rect.width / GRID_SIZE).toInt
    val yOffset = (0.5 * rect.height / GRID_SIZE).toInt

    val g = Array.tabulate[Vector2](xDim, yDim) {
      (x, y) => new  Vector2((x - xOffset + 0.5F) * GRID_SIZE , (y - yOffset + 0.5F) * GRID_SIZE)
    }
    //val locations = scala.util.Random.shuffle((for (vpos <- g; pos <- vpos) yield pos).toList)
    val locations = (for (vpos <- g; pos <- vpos) yield pos).toList
    locations
  }

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
    enemies = enemies.filter(!_.isDead)
    if (enemies.isEmpty) {
      None
    } else {
      val visibleEnemies = enemies.filter(m => m.isVisible)
      val closest = if (visibleEnemies.nonEmpty) visibleEnemies.minBy(m => (pos - m.position).lengthSquared)
                   else enemies.minBy(m => (pos - m.lastKnownPosition).lengthSquared)
      Some(closest)
    }
  }

  /** @return somewhere no friendly drone has been before */
  def closestUnvisitedPosition(position: Vector2): Vector2 = {
    if (allLocations.isEmpty) {
      allLocations = genWorldLocations(mothers.head) // repopulate
    }
    allLocations.minBy(p => (p - position).lengthSquared)
  }

  /** @return somewhere no friendly drone has been before */
  def nextUnvisitedPosition(): Vector2 = {
    if (allLocations.isEmpty) {
      allLocations = genWorldLocations(mothers.head) // repopulate
    }
    val next = allLocations.head
    allLocations = allLocations.tail
    next
  }

  def getRandomMother: Mothership = {
    mothers.toList(RND.nextInt(mothers.size))
    //mothers(RND.nextInt(mothers.length))
  }
}
