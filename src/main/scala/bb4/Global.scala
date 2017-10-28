package bb4

import cwinter.codecraft.core.api.{Drone, MineralCrystal}

import scala.util.Random

/** Maintain global state that can be queried by any drone */
object Global {

  val RND = new Random()

  var knownMinerals: Set[MineralCrystal] = Set()
  var mothers: Seq[Mothership] = Seq()
  var enemies: Set[Drone] = Set()
  var harvesters: Set[Harvester] = Set()
  var nSoldiers = 0

  def getRandomMother: Mothership = {
    mothers(RND.nextInt(mothers.length))
  }
}
